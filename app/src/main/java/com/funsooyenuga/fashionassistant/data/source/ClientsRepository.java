package com.funsooyenuga.fashionassistant.data.source;

import com.funsooyenuga.fashionassistant.data.Client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FAB THE GREAT on 03/01/2017.
 */

public class ClientsRepository implements ClientDataSource {

    private static ClientsRepository instance;

    private final ClientDataSource dataSource;

    private Map<String, Client> cachedClients;

    private List<ClientsRepositoryObserver> observers = new ArrayList<>();

    private boolean cacheAvailable;

    private ClientsRepository(ClientDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ClientsRepository getInstance(ClientDataSource dataSource) {
        if (instance == null) {
            instance = new ClientsRepository(dataSource);
        }
        return instance;
    }

    @Override
    public void clearDeliveredJobs() {

    }

    @Override
    public List<Client> getClients() {
        if (cacheAvailable()) {
            return getCachedClients();
        }

        List<Client> clients = dataSource.getClients();
        cacheClients(clients);
        return clients;
    }

    @Override
    public List<Client> getPendingClients() {
        return dataSource.getPendingClients();
    }

    private void cacheClients(List<Client> clients) {
        cachedClients = new LinkedHashMap<>();

        for (Client client : clients) {
            cachedClients.put(client.getId(), client);
        }
        cacheAvailable = true;
    }

    private List<Client> getCachedClients() {
        return new ArrayList<>(cachedClients.values());
    }

    public boolean cacheAvailable() {
        return cacheAvailable && cachedClients != null;
    }

    public Client getClient(String clientId) {
        return cachedClients.get(clientId);
    }

    @Override
    public void saveClient(Client client) {
        dataSource.saveClient(client);
        //Update repository
        cachedClients.put(client.getId(), client);

        notifyContentObservers();
    }

    @Override
    public void updateClient(Client client) {
        dataSource.updateClient(client);
        //Remove the old Client from the map and put the new client object

        cachedClients.remove(client.getId());
        cachedClients.put(client.getId(), client);

        notifyContentObservers();
    }

    @Override
    public void deleteClient(String clientId) {
        dataSource.deleteClient(clientId);
        //Update repository
        cachedClients.remove(clientId);

        notifyContentObservers();
    }

    @Override
    public void setDelivered(String clientId, boolean delivered) {
        dataSource.setDelivered(clientId, delivered);

        cachedClients.get(clientId).setDelivered(delivered);
        cachedClients.get(clientId).setDeliveryDate(null);
        notifyContentObservers();
    }

    //Content Observers
    public void addContentObserver(ClientsRepositoryObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeContentObservers(ClientsRepositoryObserver observer) {
        if (observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    public void notifyContentObservers() {
        for (ClientsRepositoryObserver observer : observers) {
            observer.onDataChange();
        }
    }

    public interface ClientsRepositoryObserver {

        void onDataChange();

    }
}
