package com.example.do_an_thuc_tap_main.Model;

import java.util.List;

public class Requetst {
    private String phone;
    private String name;
    private String address;
    private String total;
    private List<Order> shoes;

    private String Uid;

    private String status;

    public Requetst(){
    }

    public Requetst(String phone, String name, String address, String total, List<Order> shoes, String uid, String status) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.shoes = shoes;
        Uid = uid;
        this.status = status; //0: Đợi đóng hàng - 1: Đang vận chuyển - 2: đang giao - 3: đã giao thành công - 4: giao hàng thất bại
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getShoes() {
        return shoes;
    }

    public void setShoes(List<Order> shoes) {
        this.shoes = shoes;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
