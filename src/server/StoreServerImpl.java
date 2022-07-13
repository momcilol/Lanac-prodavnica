package server;

import client.Store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class StoreServerImpl extends UnicastRemoteObject implements StoreServer {

    private Listeners listeners;

    protected StoreServerImpl() throws RemoteException {
        this.listeners = new Listeners();
    }

    @Override
    public List<String> checkAvailable(String product, String name) throws RemoteException {
        return listeners.findProduct(product, name);
    }

    @Override
    public void addStore(String name, Store store) throws RemoteException {
        listeners.addStoreListener(name, store);
    }

    @Override
    public void removeStore(String name) throws RemoteException {
        listeners.removeStoreListener(name);
    }

    public static void main(String[] args) {
        String name = "itshop";

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            LocateRegistry.createRegistry(1099);
            StoreServerImpl chatServer = new StoreServerImpl();
            Naming.rebind(name, chatServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server working...");
    }
}
