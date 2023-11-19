package com.example.shopserverfoadmin.model;

public class User {
    private String email;
    private String name;
    private String phone;
    private String pass;
    private boolean staff;

    public User() {
    }


    public User(String email, String name, String phone, String pass) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.pass = pass;
        staff = false;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isStaff() {
        return staff;
    }


    public void setStaff(boolean staff) {
        this.staff = staff;
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
