package client.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DOMStorage implements Storage {


    public final String filename;
    private final Map<String, Integer> productsMap;
    private Document document;

    public DOMStorage(String filename) {
        this.filename = filename;
        this.productsMap = new HashMap<>();
        loadDocument(filename);
        loadMap();
    }


    /**
     * Loads XML document from {@code filename}
     */
    public void loadDocument(String filename) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.parse(filename);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void loadMap() {
        Element root = document.getDocumentElement();
        NodeList products = root.getElementsByTagName("proizvod");

        for (int i = 0; i < products.getLength(); i++) {

            Element product = (Element) products.item(i);
            int amount = Integer.parseInt(product.getAttribute("kolicina"));
            this.productsMap.put(product.getTextContent().trim(), amount);
        }
    }


    /**
     * Overloading method of {@link #saveDocument(String)}
     */
    public void saveDocument() {
        saveDocument(filename);
    }

    /**
     * Saves XML document to {@code filename}
     */
    public void saveDocument(String filename) {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();

//            System.out.println(writer.writeToString(document));

            String content = writer.writeToString(document);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_16);
            outputStreamWriter.write(content);
            outputStreamWriter.close();

        } catch (ClassCastException | ClassNotFoundException | InstantiationException | IllegalAccessException |
                 IOException ex) {
            ex.printStackTrace();
        }

    }


    @Override
    public int amount(String product) {
        return productsMap.get(product);
    }

    @Override
    public boolean take(String product) {
        if (productsMap.containsKey(product) && productsMap.get(product) > 0) {
            productsMap.put(product, productsMap.get(product) - 1);
            updateDocument(product);
            saveDocument();
            return true;
        }
        return false;
    }

    private void updateDocument(String product) {
        Element root = document.getDocumentElement();
        NodeList products = root.getElementsByTagName("proizvod");

         for (int i = 0; i < products.getLength(); i++) {
            Element tmp = (Element) products.item(i);
            if (tmp.getTextContent().equals(product)) {
                int amount = Integer.parseInt(tmp.getAttribute("kolicina"));
                tmp.setAttribute("kolicina", String.valueOf(amount));
                break;
            }
        }
    }

}