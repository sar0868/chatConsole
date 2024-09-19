package ru.otus.java.safarov;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationProvider implements AuthenticatedProvider {
    private class User {
        private final String login;
        private final String password;
        private final String username;
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
        User admin = new User("admin", "admin", "admin");
        admin.role = Role.ADMIN;
        this.users.add(admin);
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

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username) {
        if(login.trim().length() < 3 || password.trim().length() < 6
        || username.trim().length() < 2){
            clientHandler.sendMessage("""
                    Логин должен быть 3 более символов,
                    длина пароля 6 и более символов,
                    имя пользователя длиной 2 и более символов.""");
            return false;
        }
        if (isLoginAlreadyExist(login)){
            clientHandler.sendMessage("Указанный логин уже занят.");
            return false;
        }
        if (isUserNameAlreadyExist(login)){
            clientHandler.sendMessage("Указанное имя пользователя уже занятщ.");
            return false;
        }
        users.add(new User(login, password, username));
        clientHandler.setName(username);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/regok " + username);
        return true;
    }

    private boolean isUserNameAlreadyExist(String login) {
        for (User user : users) {
            if (user.username.equals(login)){
                return true;
            }
        }
        return false;
    }

    private boolean isLoginAlreadyExist(String login) {
        for (User user : users) {
            if (user.login.equals(login)){
                return true;
            }
        }
        return false;
    }

    private Role getRole(String username){
        for (User user : users) {
            if (user.username.equals(username)){
                return user.role;
            }
        }
        return null;
    }

    @Override
    public boolean isAdmin(ClientHandler clientHandler) {
        return getRole(clientHandler.getName()) == Role.ADMIN;
    }
}

enum Role {
    ADMIN, USER
}
