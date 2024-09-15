package ru.otus.java.safarov;

public class ServerApplication {
    public static void main(String[] args) {
        int PORT = 8888;
        new Server(PORT).start();
    }
}