package com.example.shopserverfoadmin.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireBaseHelper {
    ///------------------ACOUNT---------------------------

    //Check Login
    public static boolean isUserLoggedIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null;
    }
    //get user
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //get uid user(id)
    public static String getCurrentUserUid() {
        FirebaseUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    //checkAdmin
    public static void checkIsAdmin(OnIsAdminCheckListener listener) {
        DatabaseReference isAdminReference = FirebaseDatabase.getInstance().getReference("User").child(getCurrentUserUid()).child("staff");
        isAdminReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean staff = false;
                if (snapshot.exists() && snapshot.getValue() instanceof Boolean) {
                    staff = (boolean) snapshot.getValue();
                }
                listener.onIsAdminCheck(staff);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onIsAdminCheck(false);
            }
        });
    }

    // Interface để trả kết quả kiểm tra về
    public interface OnIsAdminCheckListener {
        void onIsAdminCheck(boolean isStaff);
    }


}
