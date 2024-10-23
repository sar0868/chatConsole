package ru.otus.java.safarov;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final int port;
    private final Map<String, ClientHandler> clients;
    private final AuthenticatedProvider authenticatedProvider;

    public Server(int port) {
        this.port = port;
        clients = new HashMap<>();
        authenticatedProvider = new AuthenticationProvider(this);
        authenticatedProvider.initialize();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен. Порт: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.put(clientHandler.getName(), clientHandler);
    }

    public synchronized void changeNick(ClientHandler clientHandler, String oldUsername){
        clients.put(clientHandler.getName(), clientHandler);
        clients.remove(oldUsername);
    }

    public synchronized void broadcastMessage(String msg) {
        for (Map.Entry<String, ClientHandler> client : clients.entrySet()) {
            client.getValue().sendMessage(msg + " time: " + new Date());
        }
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler.getName());
    }

    public void sendMessageClient(ClientHandler clientHandler, String recipient, String msgToPersonal) {
        if (clients.containsKey(recipient)) {
            clients.get(recipient).sendMessage(clientHandler.getName() + ": " + msgToPersonal + " time: " +
                    new Date());
        } else {
            clientHandler.sendMessage("Клиента с ником " + recipient + " нет в сети." +
                    " time: " + new Date());
        }
    }

    public synchronized void sendList(ClientHandler clientHandler) {
        clientHandler.sendMessage(Arrays.toString(clients.keySet().toArray()));
    }

    public boolean isName(String name) {
        return clients.containsKey(name);
    }

    public boolean closeUser(String name) {
        ClientHandler closeClient = clients.get(name);
        if (closeClient == null) {
            return false;
        }
        closeClient.sendMessage("/exitok");
        clients.remove(name);
        return true;
    }
}
