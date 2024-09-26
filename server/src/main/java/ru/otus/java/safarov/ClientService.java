package ru.otus.java.safarov;

import java.util.List;

public interface ClientService extends AutoCloseable{
    List<User> getAll();
    boolean isAdmin(int userID);
    String getUsername(String login, String password);
    void addUser(User user);

    boolean isLogin(String login);

    String getRole(String username);
}
