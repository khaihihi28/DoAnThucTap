package com.example.do_an_thuc_tap_main.Model;

public class User {
    private String email;
    private String name;
    private String phone;
    private boolean staff;

    public User() {
    }


    public User(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        staff = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
