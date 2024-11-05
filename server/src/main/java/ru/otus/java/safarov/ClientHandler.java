package ru.otus.java.safarov;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ru.otus.java.safarov.ServerApplication.logger;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String name;
    private String groupTitle;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.groupTitle = "";
        new Thread(() -> {
            try {
                // цикл аутентификации и регистрации
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit")) {
                            exit();
                            break;
                        }
                        if (msg.startsWith("/auth ")) {
                            if (authClient(msg)) {
                                logger.info("Клиент {} прошел аутентификацию.", name);
                                break;
                            }
                            continue;
                        }
                        if (msg.startsWith("/register ")) {
                            if (regClient(msg)) {
                                logger.info("Клиент {} зарегистрирован.", name);
                                break;
                            }
                            continue;
                        }
                    }
                    sendMessage("Отправка и получение сообщений доступна\n" +
                                "только после аутентификации (команда: /auth login passowrd)\n" +
                                "или после регистрации (команда: /register login password username)");
                }
                // завершение аутентификации и регистрации
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit")) {
                            exit();
                            break;
                        } else if (msg.startsWith("/w ")) {
                            personalMsg(msg);
                        } else if (msg.startsWith("/activelist")) {
                            server.sendList(this);
                        } else if (msg.startsWith("/kick ")) {
                            kickUser(msg);
                        } else if (msg.startsWith("/changenick")) {
                            String oldName = getName();
                            if (changeNick(msg)) {
                                server.changeNick(this, oldName);
                                String infoMsg = "Клиент " + oldName + " изменил username на " + getName();
                                logger.info(infoMsg);
                                sendMessage(infoMsg);
                                continue;
                            }
                            String infoMsg = "Не удалось изменить имя клиента " + oldName;
                            logger.info(infoMsg);
                        } else if (msg.startsWith("/department")) {
                            if (createDepartment(msg)) {
                                String resultAddDepartment = "Отдел " + msg.trim().split("\\s+")[1] + " создан";
                                logger.info(resultAddDepartment);
                                sendMessage(resultAddDepartment);

                            }
                        } else if (msg.startsWith("/listdepartments")) {
                            getDepartments();
                        } else if (msg.startsWith("/shutdown")) {
                            if (shutdownServer()) {
                                server.shutdown();

                                disconnect();
                                System.exit(0);
                            }
                        } else if (msg.startsWith("/group ")) {
                            if (createGroup(msg)) {
                                String resultAddGroup = "Группа " + msg.trim().split("\\s+")[1] + " создана";
                                logger.info(resultAddGroup);
                                sendMessage(resultAddGroup);
                            }
                        } else if (msg.startsWith("/enter ")) {
                            enterGroup(msg);
                        } else if (msg.startsWith("/groupslist")){
                            getTitlesGroups();
                        } else if (msg.startsWith("/addgroup ")) {
                            requestAddGroup(msg);
                        } else if (msg.startsWith("/exitgroup")) {
                            exitGroup();
                        } else {
                            sendMessage("Не корректный ввод: " + msg);
                        }
                    } else {
                        server.broadcastMessage(name + ": " + msg);
                    }
                }
                disconnect();
            } catch (IOException e) {
                if (name == null) {
                    logger.info("Не аутентифицированный клиент отключился");
                } else {
                    logger.info("{} отключился", name);
                }
            } finally {
                disconnect();
            }
        }).start();
    }

    private void exitGroup() {
        if (groupTitle.isEmpty()){
            sendMessage("Вы не входили ни в одну из групп");
        }  else {
            String yuoGroup = groupTitle;
            groupTitle = "";
            sendMessage("Вы вышли из группы " + yuoGroup);
        }
    }

    private void requestAddGroup(String msg) {
        // /addgroup <имя группы>
        String[] array = msg.trim().split(("\\s+"));
        if (array.length != 2){
            sendMessage("Некорректный формат ввода команды /addgroup");
        } else {
            if(!server.getAuthenticatedProvider().addRequestAddGroup(this, array[1])){
                logger.info("Запрос на добавление группы {} не создан", array[1]);
            }
        }
     }

    private void enterGroup(String msg) {
        // /enter groupTitle password_group
        String[] array = msg.trim().split("\\s+");
        if (array.length != 3) {
            sendMessage("Некорректный формат ввода команды /enter");
        } else {
            if (groupTitle.isEmpty()) {
                server.getAuthenticatedProvider().enterGroup(this, array[1], array[2]);
                getGroup(array[1]);
            } else {
                sendMessage("Для создания группы необходимо выйти из группы " + groupTitle + " командой /exitgroup");
            }
        }
    }

    private void getGroup(String titleGroup) {
        groupTitle = titleGroup;
    }

    private void getTitlesGroups() {
        List<String> titles = server.getAuthenticatedProvider().getGroupTitle(this);
        String titlesGroups = Collections.singletonList(titles).toString();
        sendMessage("list groups: " + titlesGroups);
    }

    private boolean createGroup(String msg) {
//        /group <title> <password>
        String[] array = msg.trim().split("\\s+");
        if (array.length != 3) {
            sendMessage("Некорректный формат ввода команды /group");
            return false;
        }
        return server.getAuthenticatedProvider().addGroup(this, array[1], array[2]);
    }

    private void getDepartments() {
        Set<Department> departments = server.getAuthenticatedProvider().getDepartments();
        StringBuilder msgDepartments = new StringBuilder("departments: ");
        for (Department department : departments) {
            msgDepartments.append(department.getTitle()).append(" ");
        }
        if (msgDepartments.toString().equals("departments: ")) {
            msgDepartments.append("empty");
        }
        sendMessage(msgDepartments.toString());
    }

    private boolean createDepartment(String msg) {
        String[] array = msg.trim().split("\\s+");
        if (array.length != 3) {
            sendMessage("Некорректный формат ввода команды /department");
            return false;
        }
        if (!server.getAuthenticatedProvider().isAdmin(this)) {
            sendMessage("Вы не являетесь администратором");
            return false;
        }
        return server.getAuthenticatedProvider().addDepartment(this, array[1], array[2]);
    }

    private boolean shutdownServer() {
        if (server.getAuthenticatedProvider().isAdmin(this)) {
            server.broadcastMessage("/exitok");
            return true;
        }
        sendMessage("Вы не являетесь администратором");
        return false;
    }

    private boolean changeNick(String msg) {
        String[] array = msg.trim().split("\\s+");
        if (array.length != 2) {
            sendMessage("Некорректный формат ввода команды /auth");
            return false;
        }
        if (server.getAuthenticatedProvider().changeUsername(this, array[1])) {
            setName(array[1]);
            return true;
        }
        return false;
    }

    protected void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void exit() {
        sendMessage("/exitok");
        if (name != null) {
            logger.info("Клиенту {} отравлено сообщение о закрытии", name);
        }
    }

    private void personalMsg(String msg) {
        msg = msg.trim().replaceAll("\\s+", " ");
        String[] array = msg.split(" ");
        if (array.length < 3) {
            sendMessage("Некорректный формат ввода");
        } else {
            String recipient = array[1];
            String msgToPersonal = String.join(", ", Arrays.stream(array, 2, array.length)
                    .toArray(String[]::new));
            server.sendMessageClient(this, recipient, msgToPersonal);
        }
    }

    private boolean authClient(String msg) {
        String[] array = msg.trim().split("\\s+");
        if (array.length != 3) {
            sendMessage("Некорректный формат ввода команды /auth");
            return false;
        }
        return server.getAuthenticatedProvider().authenticate(this, array[1], array[2]);
    }

    private boolean regClient(String msg) {
        String[] array = msg.trim().split("\\s+");
        if (array.length != 4) {
            sendMessage("Некорректный формат ввода команды /register");
            return false;
        }
        return server.getAuthenticatedProvider().registration(this, array[1], array[2], array[3]);
    }

    private void kickUser(String msg) {
        if (server.getAuthenticatedProvider().isAdmin(this)) {
            String[] array = msg.trim().split("\\s+");
            if (array.length != 2) {
                sendMessage("Некорректный формат ввода команды /kick");
            } else if (server.closeUser(array[1])) {
                sendMessage("Пользователь " + array[1] + " отключен");
            } else {
                sendMessage("Пользователя " + array[1] + " нет в сети.");
            }
        } else {
            sendMessage("Вы не являетесь администратором");
        }
    }
}
