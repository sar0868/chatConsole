package ru.otus.java.safarov;

public interface AuthenticatedProvider {
    void initialize();

    boolean authenticate(ClientHandler clientHandler, String login, String password);

    boolean registration(ClientHandler clientHandler, String login, String password, String username);

    boolean isAdmin(ClientHandler clientHandler);
}
