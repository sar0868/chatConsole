package ru.otus.java.safarov;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationProvider implements AuthenticatedProvider {

    private final Server server;
    private final List<User> users;
    private ClientDAO clientDAO;
    private boolean inMemory;

    public AuthenticationProvider(Server server) {
        this.server = server;
        this.users = new ArrayList<>();
        inMemory = true;
//        this.users.add(new User("qwe", "qwe", "qwe1"));
//        this.users.add(new User("asd", "asd", "asd1"));
//        User admin = new User("admin", "admin", "admin");
//        admin.setRole(Role.ADMIN);
//        this.users.add(admin);
    }

    @Override
    public void initialize() {
        try {
            clientDAO = new ClientDAO();
            System.out.println("Сервис аутентификации запущен. DB режим");
            inMemory = false;
        } catch (SQLException e) {
            System.out.println("Сервис аутентификации запущен. In memory режим");
        }
    }

    private String getUserNameByLoginAndPassword(String login, String password) {
        if (inMemory){
            for (User user : users) {
                if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                    return user.getUsername();
                }
            }
            return null;
        }
        return clientDAO.getUsername(login, password);
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
        if (isUserNameAlreadyExist(username)){
            clientHandler.sendMessage("Указанное имя пользователя уже занято.");
            return false;
        }
        if (inMemory){
            users.add(new User(login, password, username));
        }
        if (!inMemory && clientDAO.addUser(clientDAO.getAll().size()+1,new User(login, password, username)) == -1){
            String msgError = username + " не зарегистрирован.";
            System.out.println(msgError);
            clientHandler.sendMessage(msgError);
            return false;
        }
        clientHandler.setName(username);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/regok " + username);
        return true;
    }

    private boolean isUserNameAlreadyExist(String username) {
        if(inMemory){
            for (User user : users) {
                if (user.getUsername().equals(username)){
                    return true;
                }
            }
            return false;
        }
        return clientDAO.isUserName(username);
    }

    private boolean isLoginAlreadyExist(String login) {
        if(inMemory){
            for (User user : users) {
                if (user.getLogin().equals(login)){
                    return true;
                }
            }
            return false;
        }
        return clientDAO.isLogin(login);
    }

    private Role getRole(String username){
        if (inMemory){
            for (User user : users) {
                if (user.getUsername().equals(username)){
                    return user.getRole();
                }
            }
            return null;
        }

        if(clientDAO.getRole(username).equals("ADMIN")){
            return Role.ADMIN;
        }
        return Role.USER;
    }

    @Override
    public boolean isAdmin(ClientHandler clientHandler) {
        return getRole(clientHandler.getName()) == Role.ADMIN;
    }
}

enum Role {
    ADMIN, USER
}
