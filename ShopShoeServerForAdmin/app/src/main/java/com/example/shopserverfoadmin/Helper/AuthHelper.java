package com.example.shopserverfoadmin.Helper;

import androidx.annotation.NonNull;

import com.example.shopserverfoadmin.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    //login ảo
    public static void loginAo(String key, OnLoginCompleteListener listener) {
        getUserForKey(key, new UserCallback() {
            @Override
            public void onCallback(User user) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPass())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser userFirebase = mAuth.getCurrentUser();
                                if (user != null) {
                                    listener.onLoginSuccess(userFirebase.getUid());
                                } else {
                                }
                            } else {
                            }
                        });
            }
        });

    }

    public static void getUserForKey(String key, UserCallback userCallback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("User").child(key);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Đối tượng User có thể được chuyển đổi từ dataSnapshot
                    User user = dataSnapshot.getValue(User.class);
                    userCallback.onCallback(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }
    public interface UserCallback {
        void onCallback(User user);
    }

    // Interface để trả kết quả đăng nhập về
    public interface OnLoginCompleteListener {
        void onLoginSuccess(String uid);

        void onLoginFailure(String errorMessage);
    }

    // Đăng ký
    public static void signUpHelper(String email, String password, String name, String phone, String pass, OnRegistrationCompleteListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("User");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            User account = new User(email, name, phone, pass);
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
