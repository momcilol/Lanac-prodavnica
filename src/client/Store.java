package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Store extends Remote {
    int checkAvailable(String product) throws RemoteException;
}
