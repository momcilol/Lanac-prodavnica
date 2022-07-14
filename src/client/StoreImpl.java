package client;

import client.xml.DOMStorage;
import client.xml.SAXStorage;
import client.xml.Storage;
import server.StoreServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.stream.Collectors;

public class StoreImpl extends UnicastRemoteObject implements Store {

    private final String name;
    private StoreServer storeServer;
    private Storage storage;

    public StoreImpl(String name) throws RemoteException {
        this.name = name;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("SAX[s] or DOM[otherwise]?");
        String repository = "res/" + name + ".xml";

        try {
            String xmlReader = br.readLine();
            if (xmlReader != null && xmlReader.equalsIgnoreCase("s")) {
                this.storage = new SAXStorage(repository);
                System.out.println("SAX");
            } else {
                this.storage = new DOMStorage(repository);
                System.out.println("DOM");
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.storage = new DOMStorage(repository);
            System.out.println("DOM");
        }
    }

    public void start(String storeAddress) {
        connectToServer(storeAddress);
        run();
        System.exit(0);
    }

    private void connectToServer(String storeAddress) {
        try {
            this.storeServer = (StoreServer) Naming.lookup("//" + storeAddress + "/itshop");
            this.storeServer.addStore(name, this);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        System.out.println("""
                Dobrodosli nazad!
                Klijent prepoznaje sledece komande:
                                
                - ? proizvod - Ispisuje kolicinu datog proizvoda raspolozivog u prodavnici, ako
                               prodavnica ima bar jedan takav proizvod. Ako prodavnica ne
                               raspolaze datim proizvodom, ispisuje u kojim prodavnicama se taj
                               proizvod moze pronaci.
                                
                - ! proizvod - Prodaje jedan komad naznacenog proizvoda.
                                
                - prazan unos - Zatvaranje programa
                """);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.print(">> ");
            String message = br.readLine();
            while (message != null && !message.isBlank()) {
                message = message.trim();
                String[] tokens = message.split(" ");

                if (tokens.length > 1) processCommand(tokens[0], tokens[1]);
                System.out.print(">> ");
                message = br.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                this.storeServer.removeStore(name);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void processCommand(String command, String product) throws RemoteException {
        switch (command) {
            case "?" -> {
                int tmp = storage.amount(product);
                if (tmp > 0) {
                    System.out.println(tmp);
                } else {
                    String output = storeServer.checkAvailable(product, name).stream()
                            .collect(Collectors.joining(
                                    "\n\t",
                                    "Prodavnice u kojima mozete naci proizvod: \n\t",
                                    ""
                            ));

                    System.out.println(output);
                }
            }
            case "!" -> System.out.println(storage.take(product) ? "Prodato!" : "Nema na raspolaganju!");
            default -> System.out.println("Nepoznata komanda!");
        }
    }

    @Override
    public int checkAvailable(String product) throws RemoteException {
        return storage.amount(product);
    }

    public static void main(String[] args) {

        try {
            StoreImpl store = new StoreImpl(args[0]);
            store.start(args[1]);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
