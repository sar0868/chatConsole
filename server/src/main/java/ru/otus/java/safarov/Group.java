package ru.otus.java.safarov;

public class Group {
    private final int id;
    private final String title;
    private final String password;

    public Group(int id, String title, String password) {
        this.id = id;
        this.title = title;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPassword() {
        return password;
    }
}
