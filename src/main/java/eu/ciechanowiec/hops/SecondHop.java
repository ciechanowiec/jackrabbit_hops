package eu.ciechanowiec.hops;

import org.apache.jackrabbit.commons.JcrUtils;

import javax.jcr.*;

class SecondHop {

    public static void main(String[] args) throws RepositoryException {
        Repository repository = JcrUtils.getRepository();
        SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
        Session session = repository.login(credentials);
        try {
            Node root = session.getRootNode();

            // Store content
            Node hello = root.addNode("hello");
            Node universe = hello.addNode("universe");
            universe.setProperty("message", "Hello, Universe!");
            session.save();

            // Retrieve content
            Node node = root.getNode("hello/universe");
            System.out.println(node.getPath());
            System.out.println(node.getProperty("message").getString());

            // Remove content
            root.getNode("hello").remove();
            session.save();

        } finally {
            session.logout();
        }
    }
}
