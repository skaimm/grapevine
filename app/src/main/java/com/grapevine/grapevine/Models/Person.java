package com.grapevine.grapevine.Models;

public class Person {

    String email;
    String password;

    public Person(int personid, String email, String password, String ptype) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
