package ru.otus.java.safarov;

public class ServerApplication {
    public static void main(String[] args) {
        int PORT = 8189;
        new Server(PORT).start();
    }
}