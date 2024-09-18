package ru.otus.java.safarov;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticatedProvider {
    private class User {
        private String login;
        private String password;
        private String username;
        private Role role;

        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
            this.role = Role.USER;
        }
    }

    private final Server server;
    private final List<User> users;

    public InMemoryAuthenticationProvider(Server server) {
        this.server = server;
        this.users = new ArrayList<>();
        this.users.add(new User("login1", "password1", "username1"));
        this.users.add(new User("qwe", "qwe", "qwe1"));
        this.users.add(new User("asd", "asd", "asd1"));
        this.users.add(new User("zxc", "zxc", "zxc1"));
    }

    @Override
    public void initialize() {
        System.out.println("Сервис аутентификации запущен. In memory режим");
    }

    private String getUserNameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.username;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) {
        String authName = getUserNameByLoginAndPassword(login, password);
        if (authName == null) {
            clientHandler.sendMessage("Incorrect login/password");
            return false;
        }
        if (server.isName(authName)) {
            clientHandler.sendMessage("Username is busy.");
            return false;
        }
        clientHandler.setName(authName);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/authok " + authName);
        return true;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username) {
        return false;
    }
}

enum Role {
    ADMIN, USER
}
