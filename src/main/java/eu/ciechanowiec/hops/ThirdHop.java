package eu.ciechanowiec.hops;

import org.apache.jackrabbit.commons.JcrUtils;

import javax.jcr.*;
import java.io.IOException;
import java.io.InputStream;

class ThirdHop {

    public static void main(String[] args) throws RepositoryException, IOException {
        Repository repository = JcrUtils.getRepository();
        SimpleCredentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
        Session session = repository.login(credentials);

        try {
            Node root = session.getRootNode();

            // Import the XML file unless already imported
            if (!root.hasNode("importxml")) {
                System.out.println("Importing xml...");

                // Create an unstructured node under which to import the XML
                Node node = root.addNode("importxml", "nt:unstructured");

                // Import the file "text.xml" under the created node
                InputStream xml = getFileFromResourcesAsStream("test.xml");
                String absPathToNode = node.getPath();
                session.importXML(absPathToNode, xml, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
                xml.close();
                session.save();
                System.out.println("Done");
            }

            dump(root);
        } finally {
            session.logout();
        }


    }

    private static InputStream getFileFromResourcesAsStream(String fileName) {
        Class<? extends ThirdHop> currentClass = ThirdHop.class;
        ClassLoader classLoader = currentClass.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException(String.format("File '%s' wasn't found!", fileName));
        } else {
            return inputStream;
        }
    }

    /* Recursively outputs the contents of the given node */
    private static void dump(Node node) throws RepositoryException {
        // First output the node path
        System.out.println(node.getPath());
        // Skip the virtual (and large!) jcr:system subtree
        if (node.getName().equals("jcr:system")) {
            return;
        }

        // Then output the properties
        PropertyIterator properties = node.getProperties();
        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            if (property.getDefinition().isMultiple()) {
                // A multi-valued property, print all values
                Value[] values = property.getValues();
                for (int i = 0; i < values.length; i++) {
                    String propertyRepresentation = property.getPath() + " = " + values[i].getString();
                    System.out.println(propertyRepresentation);
                }
            } else {
                // A single-valued property
                String propertyRepresentation = property.getPath() + " = " + property.getString();
                System.out.println(propertyRepresentation);
            }
        }

        // Finally output all the child nodes recursively
        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            dump(nodes.nextNode());
        }
    }
}
