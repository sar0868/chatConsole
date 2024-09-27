package ru.otus.java.safarov;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO implements ClientService {
    private final String CHECK_USERNAME = "select id from Users where username = ?";
    private final String INSERT_USER = "INSERT INTO Users (id, login, password, username) values (?,?,?,?)";
    private final String INSERT_USERS_TO_ROLES = "INSERT INTO Users_to_Roles (userID, roleID) values (?, 2)";
    private final String USER_ROLE = "SELECT role from Roles r " +
            "inner join Users_to_Roles utr on r.id = utr.roleID " +
            "INNER JOIN Users u on utr.userID = u.id " +
            "where u.username = ?";
    private final String CHECK_LOGIN = "select id from Users where login = ?";
    //    private final String DATABASE_URL = "jdbc:sqlite:clients.db";
    private final String DATABASE_URL = "jdbc:postgresql://localhost:5432/chat";
    private final Connection connection;
    private final String USERS_QUERY = "select login, password, username from Users";
    private final String USER_QUERY = "select username from Users where login = ? and password = ?";

    public ClientDAO() throws SQLException {
//        connection = DriverManager.getConnection(DATABASE_URL);
        connection = DriverManager.getConnection(DATABASE_URL, "username", "passwd");

    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(USERS_QUERY)) {
                while (resultSet.next()) {
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    String username = resultSet.getString("username");
                    users.add(new User(login, password, username));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public String getUsername(String login, String password) {
        String username = null;
        try (PreparedStatement pst = connection.prepareStatement(USER_QUERY)) {
            pst.setString(1, login);
            pst.setString(2, password);
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    username = resultSet.getString("username");
                    System.out.println(username);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return username;
    }

    @Override
    public int addUser(int id, User user){
        int result = -1;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement pst = connection.prepareStatement(INSERT_USER)) {
            pst.setInt(1, id);
            pst.setString(2, user.getLogin());
            pst.setString(3, user.getPassword());
            pst.setString(4, user.getUsername());
            result = pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(result != -1 && insertUsersToRoles(id) == -1){
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return -1;
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private int insertUsersToRoles(int id){
        int result = -1;
        try (PreparedStatement pst = connection.prepareStatement(INSERT_USERS_TO_ROLES)) {
            pst.setInt(1, id);
            result = pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public boolean isLogin(String login) {
        int id = -1;
        try (PreparedStatement pst = connection.prepareStatement(CHECK_LOGIN)) {
            pst.setString(1, login);
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id != -1;
    }

    @Override
    public String getRole(String username) {
        String role = null;
        try (PreparedStatement pst = connection.prepareStatement(USER_ROLE)) {
            pst.setString(1, username);
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    role = resultSet.getString("role");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return role;
    }

    @Override
    public boolean isUserName(String username) {
        int id = -1;
        try (PreparedStatement pst = connection.prepareStatement(CHECK_USERNAME)) {
            pst.setString(1, username);
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id != -1;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
