package server;

import client.Store;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface StoreServer extends Remote {
    List<String> checkAvailable(String product, String name) throws RemoteException;
    void addStore(String name, Store store) throws RemoteException;
    void removeStore(String name) throws RemoteException;

}