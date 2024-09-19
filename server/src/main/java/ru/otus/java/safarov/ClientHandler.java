package ru.otus.java.safarov;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private String name;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                System.out.println("Клиент подключился");
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
                                System.out.println("Клиент " + name + " прошел аутентификацию.");
                                break;
                            }
                            continue;
                        }
                        if (msg.startsWith("/reg ")) {
                            if (regClient(msg)) {
                                System.out.println("Клиент " + name + " зарегистрирован.");
                                break;
                            }
                            continue;
                        }
                    }
                    sendMessage("""
                            Отправка и получение сообщений доступна
                            только после аутентификации (команда: /auth login passowrd)
                            или после регистрации (команда: /reg login password username)""");
                }
                // завершение аутентификации и регистрации
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit")) {
                            exit();
                            break;
                        }
                        if (msg.startsWith("/w ")) {
                            personalMsg(msg);
                            continue;
                        }

                        if (msg.startsWith("/list")) {
                            server.sendList(this);
                            continue;
                        }
                        if (msg.startsWith("/kick ")){
                            kickUser(msg);
                        }
                    } else {
                        server.broadcastMessage(name + ": " + msg);
                    }
                }
                disconnect();
            } catch (IOException e) {
                if (name == null) {
                    System.out.println("Не аутентифицированный клиент отключился");
                } else {
                    System.out.println(name + " отключился");
                }
            } finally {
                disconnect();
            }
        }).start();
    }

    private void disconnect() {
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

    private void exit() {
        sendMessage("/exitok");
        if (name != null) {
            System.out.println("Клиенту " + name + " отравлено сообщение о закрытии");
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
        server.getAuthenticatedProvider().authenticate(this, array[1], array[2]);
        return true;
    }

    private boolean regClient(String msg) {
        String[] array = msg.trim().split("\\s+");
        if (array.length != 4) {
            sendMessage("Некорректный формат ввода команды /reg");
            return false;
        }
        server.getAuthenticatedProvider().registration(this, array[1], array[2], array[3]);
        return true;
    }

    private void kickUser(String msg) {
        if (server.getAuthenticatedProvider().isAdmin(this)){
            String[] array = msg.trim().split("\\s+");
            if (array.length != 2){
                sendMessage("Некорректный формат ввода команды /kick");
            } else if (server.closeUser(array[1])){
                sendMessage("Пользователь " + array[1] + " отключен");
            } else {
               sendMessage("Пользователя " + array[1] + " нет в сети.");
            }
        } else {
            sendMessage("Вы не являетесь администратором");
        }
    }
}
