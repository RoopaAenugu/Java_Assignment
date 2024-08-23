package com.wavemaker.todotask.pojo;

public class UserAuthentication {
    private String username;
    private  String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    @Override
    public String toString() {
        return "UserAuthentication{" +
                "userName='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
