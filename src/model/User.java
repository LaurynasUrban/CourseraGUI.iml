package model;

import java.io.Serializable;

public class User implements Serializable {

    private int id;
    private String login;
    private String psw;
    private String email;

    public User() {
    }

    public User(String login, String psw, String email) {
        this.login = login;
        this.psw = psw;
        this.email = email;
    }

    public User(int id, String login, String psw, String email) {
        this.id = id;
        this.login = login;
        this.psw = psw;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void greetUser() {
        System.out.println("Welcome back, " + login);
    }
}
