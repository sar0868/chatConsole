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

    boolean addGroup(ClientHandler clientHandler, String title);

    List<String> getGroupTitle(ClientHandler clientHandler);

    boolean enterGroup(ClientHandler clientHandler, String groupTitle);

    boolean addRequestAddGroup(ClientHandler clientHandler, String groupTitle);

    boolean isManagerGroup(ClientHandler clientHandler);

    List<String> getListRequest(ClientHandler clientHandler, String groupTitle);

    void addUsersToGroup(ClientHandler clientHandler, List<String> addUsers);

    void removeRequestAddUserToGroup(ClientHandler clientHandler);
}
