package ru.otus.java.safarov;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
//        requestName();
        new Thread(() -> {
            try {
                System.out.println("Клиент с порта " + socket.getLocalPort() + " подключился");
//                цикл аутентификации
                while (true){
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit")) {
                            exit(msg);
                            break;
                        }
                        if (msg.startsWith("/auth")) {
                            authClient(msg);
//                            msg = msg.trim().replaceAll("\\s+", " ");
//                            String[] array = msg.split(" ");
//                            String recipient = array[1];
//                            String msgToPersonal = array[2];
//                            server.sendMessageClient(this, recipient, msgToPersonal);
                            continue;
                        }
                        if (msg.startsWith("/list")) {
                            server.sendList(this);
                            continue;
                        }
                    }
                    server.broadcastMessage(name + ": " + msg);
                }
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit ")) {
                            exit(msg);
//                            sendMessage("/exitok");
//                            System.out.println("Клиенту " + name + " отравлено сообщение о закрытии");
                            break;
                        }
                        if (msg.startsWith("/w ")) {
                            personalMsg(msg);
//                            msg = msg.trim().replaceAll("\\s+", " ");
//                            String[] array = msg.split(" ");
//                            String recipient = array[1];
//                            String msgToPersonal = array[2];
//                            server.sendMessageClient(this, recipient, msgToPersonal);
                            continue;
                        }
                        if (msg.startsWith("/list")) {
                            server.sendList(this);
                            continue;
                        }
                    }
                    server.broadcastMessage(name + ": " + msg);
                }
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
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

    public void requestName() {
        try {
            while (true) {
                out.writeUTF("Укажите Ваше имя: ");
                String answer = in.readUTF();
                if (!server.isName(answer)) {
                    name = answer;
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setName(String name) {
        this.name = name;
    }

    public void sendErrorName() {
        try {
            out.writeUTF("Имя " + name + " уже используется.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestName();
    }

    private void exit(String msg) {
        sendMessage("/exitok");
        System.out.println("Клиенту " + name + " отравлено сообщение о закрытии");
    }

    private void personalMsg(String msg){
        msg = msg.trim().replaceAll("\\s+", " ");
        String[] array = msg.split(" ");
        if (array.length < 3){
//            server.sendMessageClient(this, );
        }
        String recipient = array[1];
        String msgToPersonal = array[2];
        server.sendMessageClient(this, recipient, msgToPersonal);
    }

    private void authClient(String msg) {
//        msg = msg.trim().replaceAll("\\s+", " ");
        String[] array = msg.split("\\s+");

        String recipient = array[1];
        String msgToPersonal = array[2];
        server.sendMessageClient(this, recipient, msgToPersonal);
    }
}
