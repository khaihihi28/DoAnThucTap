package com.example.do_an_thuc_tap_main.Helper;

import com.example.do_an_thuc_tap_main.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthHelper {
    // Đăng nhập
    public static void signInHelper(String email, String password, OnLoginCompleteListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            listener.onLoginSuccess(user.getUid());
                        } else {
                            listener.onLoginFailure("Đăng nhập thành công!!!.");
                        }
                    } else {
                        listener.onLoginFailure("Đăng nhập thất bại. Kiểm tra lại email và mật khẩu.");
                    }
                });
    }

    // Interface để trả kết quả đăng nhập về
    public interface OnLoginCompleteListener {
        void onLoginSuccess(String uid);

        void onLoginFailure(String errorMessage);
    }

    // Đăng ký
    public static void signUpHelper(String email, String password, String name, String phone, OnRegistrationCompleteListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("User");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            User account = new User(email, name, phone);
                            mDatabase.child(uid).setValue(account);
                            listener.onRegistrationSuccess(uid);
                        } else {
                            listener.onRegistrationFailure("Đăng ký thành công nhưng không thể lấy thông tin người dùng.");
                        }
                    } else {
                        listener.onRegistrationFailure("Đăng ký thất bại. Kiểm tra lại thông tin đăng ký.");
                    }
                });
    }

    // Interface để trả kết quả đăng ký về
    public interface OnRegistrationCompleteListener {
        void onRegistrationSuccess(String uid);

        void onRegistrationFailure(String errorMessage);
    }

    //Dang xuat
    public static void signOutHelper() {
        FirebaseAuth.getInstance().signOut();
    }
}
