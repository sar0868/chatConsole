package ru.otus.java.safarov;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface ClientService extends AutoCloseable{
    List<User> getAll();
    String getUsername(String login, String password);
    int addUser(int id, User user) throws SQLException;

    boolean isLogin(String login);

    String getRole(String username);

    boolean isUserName(String username);

    boolean setUserName(String username, String newUsername);

    int getUserID(String username);

    int addDepartment(Department department, String username);

    int getMaxID(String table);

    int insertUsersToDepartment(int userId, int departmentID);

    boolean isDepartment(String title);

    Set<Department> getDepartments();

    int getGroupID(String title);

    int addGroup(String title, String password, String username);

    int insertUsersToGroups(int userId, int groupID);

    List<String> getGroupTitle();

    boolean updateDateVisit(String authName);

    boolean addDateVisit(String username);

    boolean isMemberGroup(String username, String groupTitle);

    boolean addRequestAddGroup(String username, String groupTitle);

//    int getMaxGroupID();
}
