package server;

import client.Store;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Listeners {

    private Map<String, Store> storeListeners;

    public Listeners() {
        this.storeListeners = new HashMap<>();
    }

    public void addStoreListener(String name, Store store) {
        this.storeListeners.put(name, store);
    }

    public void removeStoreListener(String name) {
        this.storeListeners.remove(name);
    }

    public void deleteStoreLisneners() {
        this.storeListeners.clear();
    }

    public int countStoreListeners() {
        return this.storeListeners.size();
    }

    public List<String> findProduct(String product, String name) {
        return this.storeListeners.entrySet().stream()
                .filter(e -> {
                    try {
                        return !e.getKey().equals(name) && e.getValue().checkAvailable(product) > 0;
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
