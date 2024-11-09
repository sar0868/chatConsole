package ru.otus.java.safarov;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.otus.java.safarov.ServerApplication.logger;

public class AuthenticationProvider implements AuthenticatedProvider {

    private final Server server;
    private final List<User> users;
    private final Set<Department> departments;
    private final List<Group> groups;
    private ClientDAO clientDAO;
    private boolean inMemory;
//    private Map<String, List<>>

    public AuthenticationProvider(Server server) {
        this.server = server;
        this.users = new ArrayList<>();
        this.departments = new HashSet<>();
        this.groups = new ArrayList<>();
        inMemory = true;
        this.users.add(new User("qwe", "qwe", "qwe1"));
        this.users.add(new User("asd", "asd", "asd1"));
        User admin = new User("admin", "admin", "admin");
        //        admin.setRole(Role.ADMIN);
        this.users.add(admin);

    }

    @Override
    public void initialize() {
        try {
            clientDAO = new ClientDAO();
            logger.info("Сервис аутентификации запущен. DB режим");
            inMemory = false;
        } catch (SQLException e) {
            logger.info("Сервис аутентификации запущен. In memory режим");
        }
    }

    private synchronized String getUserNameByLoginAndPassword(String login, String password) {
        if (inMemory) {
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
    public synchronized boolean registration(ClientHandler clientHandler, String login, String password, String username) {
        if (login.trim().length() < 3 || password.trim().length() < 6
            || username.trim().length() < 2) {
            clientHandler.sendMessage("""
                    Логин должен быть 3 более символов,
                    длина пароля 6 и более символов,
                    имя пользователя длиной 2 и более символов.""");
            return false;
        }
        if (isLoginAlreadyExist(login)) {
            clientHandler.sendMessage("Указанный логин уже занят.");
            return false;
        }
        if (isUserNameAlreadyExist(username)) {
            clientHandler.sendMessage("Указанное имя пользователя уже занято.");
            return false;
        }
        if (inMemory) {
            users.add(new User(login, password, username));
        }
        if (!inMemory && clientDAO.addUser(clientDAO.getAll().size() + 1, new User(login, password, username)) == -1) {
            String msgError = username + " не зарегистрирован.";
            logger.info(msgError);
            clientHandler.sendMessage(msgError);
            return false;
        }
        clientHandler.setName(username);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/regok " + username);
        return true;
    }

    private synchronized boolean isUserNameAlreadyExist(String username) {
        if (inMemory) {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    return true;
                }
            }
            return false;
        }
        return clientDAO.isUserName(username);
    }


    private synchronized boolean isLoginAlreadyExist(String login) {
        if (inMemory) {
            for (User user : users) {
                if (user.getLogin().equals(login)) {
                    return true;
                }
            }
            return false;
        }
        return clientDAO.isLogin(login);
    }

    private synchronized Role getRole(String username) {
        if (inMemory) {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    return user.getRole();
                }
            }
            return null;
        }

        if (clientDAO.getRole(username).equals("ADMIN")) {
            return Role.ADMIN;
        }
        return Role.USER;
    }

    @Override
    public synchronized boolean isAdmin(ClientHandler clientHandler) {
        return getRole(clientHandler.getName()) == Role.ADMIN;
    }

    @Override
    public synchronized boolean changeUsername(ClientHandler clientHandler, String username) {
        if (inMemory) {
            for (User user : users) {
                if (user.getUsername().equals(clientHandler.getName())) {
                    user.setUsername(username);
                    return true;
                }
            }
        }
        return clientDAO.setUserName(clientHandler.getName(), username);
    }

    @Override
    public synchronized boolean addDepartment(ClientHandler clientHandler, String title, String login) {
        if (isTitleAlreadyExist(title)) {
            clientHandler.sendMessage("Указанный отдел уже существует.");
            return false;
        }
        if (!isLoginAlreadyExist(login)) {
            clientHandler.sendMessage("Нет пользователя с таким логином.");
            return false;
        }
        if (inMemory) {
            departments.add(new Department(title));
            return true;
        }
        if (clientDAO.addDepartment(new Department(title), clientHandler.getName()) == -1) {
            String msgError = title + " не создан.";
            logger.info(msgError);
            clientHandler.sendMessage(msgError);
            return false;
        }
        clientHandler.sendMessage("/departmentok " + title);
        return true;
    }

    private synchronized boolean isTitleAlreadyExist(String title) {
        if (inMemory) {
            for (Department department : departments) {
                if (department.getTitle().equals(title)) {
                    return true;
                }
            }
            return false;
        }
        return clientDAO.isDepartment(title);
    }

    @Override
    public synchronized boolean addGroup(ClientHandler clientHandler, String title) {
        if (title.trim().length() < 2) {
            clientHandler.sendMessage(" Название группы должно быть более 2 символов");
            return false;
        }
        if (isGroupAlreadyExist(title) != -1) {
            clientHandler.sendMessage("Указанная группа уже существует.");
            return false;
        }

        if (inMemory) {
            for (Group group : groups) {
                if (group.getTitle().equals(title)) {
                    clientHandler.sendMessage("Указанная группа уже существует.");
                    return false;
                }
            }
            groups.add(new Group(nextIDGroup(), title));
            return true;
        }
        if (clientDAO.addGroup(title, clientHandler.getName()) == -1) {
            String msgError = title + " не создан.";
            logger.info(msgError);
            clientHandler.sendMessage(msgError);
            return false;
        }
        clientHandler.sendMessage("/groupok " + title);
        return true;
    }

    private synchronized int nextIDGroup() {
        int id = 0;
        for (Group group : groups) {
            int el = group.getId();
            if (id > el) {
                id = el;
            }
        }
        return id + 1;
    }

    private synchronized int isGroupAlreadyExist(String title) {
        if (inMemory) {
            for (Group group : groups) {
                if (group.getTitle().equals(title)) {
                    return 1;
                }
            }
            return -1;
        }
        return clientDAO.getGroupID(title);
    }

    @Override
    public synchronized Set<Department> getDepartments() {
        return clientDAO.getDepartments();
    }

    @Override
    public synchronized List<String> getGroupTitle(ClientHandler clientHandler) {
        List<String> titleGroups = new ArrayList<>();
        if (inMemory) {
            for (Group group : groups) {
                titleGroups.add(group.getTitle());
            }
        } else {
            titleGroups = clientDAO.getGroupTitle();

        }
        return titleGroups;
    }

    @Override
    public synchronized boolean enterGroup(ClientHandler clientHandler, String groupTitle) {
        //есть ли группа и является ли пользователем членом группы, если да, то вход
        //иначе сообщение "Вы не являетесь членом группы, можете отправить запрос на
        //добавление в группу /requestaddgroup <имя группы>"
        int groupID = isGroupAlreadyExist(groupTitle);
        if (groupID == -1) {
            clientHandler.sendMessage("Группы " + groupTitle + " не существует");
            return false;
        }
        if (!isMemberGroup(clientHandler.getName(), groupID)) {
            clientHandler.sendMessage("Вы не являетесь членом группы, можете отправить запрос на " +
                                      "добавление в группу /requestaddgroup <имя группы>");
            return false;
        }
        clientHandler.setGroupTitle(groupTitle);
        server.subscribeGroup(clientHandler);
        clientHandler.sendMessage("Вы вошли в группу " + groupTitle);
        return true;
    }

    @Override
    public synchronized List<String> getUsersToGroup(String groupTitle) {
        return clientDAO.getUsersToGroup(groupTitle);
    }

    @Override
    public synchronized boolean isManagerGroup(ClientHandler clientHandler) {
        return clientHandler.getName().equals(clientDAO.getUsernameManagerGroup(clientHandler.getGroupTitle()));
    }

    @Override
    public synchronized boolean addRequestAddGroup(ClientHandler clientHandler, String groupTitle) {
        // есть ли такая группа, не является ли пользователь уже членом группы, есть ли уже запрос на добавление
        // создать запрос (сделать все одним запросом или собирать данные)
        int groupID = isGroupAlreadyExist(groupTitle);
        if (groupID == -1) {
            clientHandler.sendMessage("Группы " + groupTitle + " не существует");
            return false;
        }
        if (isMemberGroup(clientHandler.getName(), groupID)) {
            clientHandler.sendMessage("Вы уже являетесь членом группы " + groupTitle +
                                      ".\nДля входа в группу введите /enter " + groupTitle);
            return false;
        }
        if (isExistRequest(clientHandler.getName(), groupID)) {
            clientHandler.sendMessage("Вы уже направляли запрос на добавление в группу " + groupTitle +
                                      ". Ваш запрос еще не рассмотрен.");
            return false;
        }
        return clientDAO.addRequestAddGroup(clientHandler.getName(), groupID);
    }

    @Override
    public synchronized List<String> getListRequest(ClientHandler clientHandler, String groupTitle) {
        return clientDAO.getUsernameSentRequest(groupTitle);
    }

    private synchronized boolean isExistRequest(String username, int groupID) {
        return clientDAO.isExistRequestAddGroup(username, groupID);
    }


    private synchronized boolean isMemberGroup(String username, int groupID) {
        return clientDAO.isMemberGroup(username, groupID);
    }

    @Override
    public synchronized void addUsersToGroup(ClientHandler clientHandler, List<String> addUsers) {
        int groupID = clientDAO.getGroupID(clientHandler.getGroupTitle());
        for (String username : addUsers) {
            int userID = clientDAO.getUserID(username);
            clientDAO.insertUsersToGroups(userID, groupID);
        }
    }

    @Override
    public synchronized void removeRequestAddUserToGroup(ClientHandler clientHandler) {
        if (clientDAO.deleteRequestAddUserToGroup(clientHandler.getGroupTitle()) == 0) {
            logger.info("Failed clear request add users to group");
        }
    }

    @Override
    public synchronized void addMsgToGroup(String groupTitle, List<String> users, String msg) {
        for (String user : users) {
            clientDAO.addMessageForUserToGroup(groupTitle, user, msg);
        }
    }

    @Override
    public synchronized List<String> getListMsgForGroup(ClientHandler clientHandler) {
        return clientDAO.getListMsgForGroup(clientHandler.getGroupTitle(), clientHandler.getName());
    }
}
