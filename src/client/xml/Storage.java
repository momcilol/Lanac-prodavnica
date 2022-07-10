package client.xml;

public interface Storage {
    boolean loadFile(String filename);

    int amount(String product);

    int take(String product);

}
