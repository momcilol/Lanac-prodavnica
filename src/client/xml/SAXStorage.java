package client.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class SAXStorage extends DefaultHandler implements Storage {

    private final String filename;

    private final Map<String, Integer> productsMap;

    private final Stack<String> path = new Stack<>();

    private int amount;


    public SAXStorage(String filename) {
        this.filename = filename;
        this.productsMap = new HashMap<>();
        parseDocument(filename);
    }

    /**
     * Parse XML file from {@code filename}
     */
    public void parseDocument(String filename) {

        // Get a factory
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {

            // Get a new instance of parser
            SAXParser parser = factory.newSAXParser();

            // Parse the file and also register this class for call backs
            parser.parse(filename, this);

            // Print any errors
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
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
            String content = this.productsMap.entrySet().stream()
                    .map(e -> "<proizvod kolicina=\"" + e.getValue() + "\">\n"
                            + "\t\t" + e.getKey() + "\n"
                            + "\t</proizvod>")
                    .collect(Collectors.joining(
                            "\n\n\t",
                            """
                                    <?xml version="1.0" encoding="UTF-16"?><!DOCTYPE list SYSTEM "products.dtd">
                                    <magacin>
                                    \t""",
                            "\n</magacin>"
                    ));

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_16);
            outputStreamWriter.write(content);
            outputStreamWriter.close();

        } catch (ClassCastException | IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        // Add new note to the path
        path.push(qName);

        // Set movie fields
        if (qName.equals("proizvod")) {
            amount = Integer.parseInt(attributes.getValue("kolicina"));
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        path.pop();

        if (qName.equals("proizvod")) {
            amount = 0;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        // Get current node and text content
        String node = path.peek();
        String text = new String(ch, start, length);

        // Print title and year
        if (node.equals("proizvod")) {
            this.productsMap.put(text, amount);
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
            saveDocument();
            return true;
        }
        return false;
    }
}


