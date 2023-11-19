package com.example.do_an_thuc_tap_main.Common;

public class Common {
    public static String convertCodeToStatus(String status){
        if(status.equals("0")){
            return "Đợi đóng hàng...";
        }
        else if(status.equals("1")){
            return "Đang vận chuyển...";
        }
        else if(status.equals("2")){
            return "Đang giao hàng...";
        }
        else if(status.equals("3")){
            return "Đã giao hàng thành công!";
        }
        else{
            return "Đơn hàng thất bại!";
        }
    }
}
