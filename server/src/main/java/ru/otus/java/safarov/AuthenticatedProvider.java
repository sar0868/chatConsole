package ru.otus.java.safarov;

import java.util.List;
import java.util.Set;

public interface AuthenticatedProvider {
    void initialize();

    boolean authenticate(ClientHandler clientHandler, String login, String password);

    boolean registration(ClientHandler clientHandler, String login, String password, String username);

    boolean isAdmin(ClientHandler clientHandler);

    boolean changeUsername(ClientHandler clientHandler, String username);

    boolean addDepartment(ClientHandler clientHandler, String title, String login);


    Set<Department> getDepartments();

    boolean addGroup(ClientHandler clientHandler, String title, String password);

    List<String> getGroupTitle(ClientHandler clientHandler);

    void enterGroup(ClientHandler clientHandler, String groupTitle, String password);
}
