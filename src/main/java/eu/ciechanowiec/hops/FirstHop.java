package eu.ciechanowiec.hops;

import org.apache.jackrabbit.commons.JcrUtils;

import javax.jcr.GuestCredentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

class FirstHop {

    public static void main(String[] args) throws RepositoryException {
        Repository repository = JcrUtils.getRepository();
        Session session = repository.login(new GuestCredentials());
        try {
            String user = session.getUserID();
            String repoName = repository.getDescriptor(Repository.REP_NAME_DESC);
            System.out.println("Logged in as '" + user + "' to a '" + repoName + "' repository.");
        } finally {
            session.logout();
        }
    }
}		
