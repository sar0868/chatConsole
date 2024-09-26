package ru.otus.java.safarov;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBAuthenticatedProvider implements AuthenticatedProvider{

    private final Server server;
    private final List<User> users;
    private ClientDAO clientDAO;

    public DBAuthenticatedProvider(Server server) {
        this.server = server;
        this.users = new ArrayList<>();
    }


    @Override
    public void initialize() {
        try {
            clientDAO = new ClientDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String authName = getUserNameByLoginAndPassword(login, password);
        if (authName == null) {
            clientHandler.sendMessage("Некорректный логин/пароль");
            return false;
        }
        if (server.isName(authName)) {
            clientHandler.sendMessage("Имя пользователя занято.");
            return false;
        }
        clientHandler.setName(authName);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/authok " + authName);
        return true;
    }

    private String getUserNameByLoginAndPassword(String login, String password) {

        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                return user.getUsername();
            }
        }
        return null;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username) {
        return false;
    }

    @Override
    public boolean isAdmin(ClientHandler clientHandler) {
        return false;
    }
}
