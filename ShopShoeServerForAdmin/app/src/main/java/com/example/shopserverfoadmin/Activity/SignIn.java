package com.example.shopserverfoadmin.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopserverfoadmin.Helper.AuthHelper;
import com.example.shopserverfoadmin.Helper.FireBaseHelper;
import com.example.shopserverfoadmin.Home;
import com.example.shopserverfoadmin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference mData;
    EditText edtEmail, edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangNhap();
            }
        });
    }
    private void dangNhap(){
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Vui lòng chờ...");
        mDialog.show();
        AuthHelper.signInHelper(email, password, new AuthHelper.OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(String uid) {
                mDialog.dismiss();
                FireBaseHelper.checkIsAdmin(new FireBaseHelper.OnIsAdminCheckListener() {
                    @Override
                    public void onIsAdminCheck(boolean isStaff) {
                        if(isStaff){
                            Toast.makeText(SignIn.this, "Đăng nhập thành công!!!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignIn.this, Home.class);
                            intent.putExtra("localKey", FireBaseHelper.getCurrentUserUid().toString());
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(SignIn.this, "Tài khoản của bạn chưa được cấp quyền admin!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onLoginFailure(String errorMessage) {
                mDialog.dismiss();
                Toast.makeText(SignIn.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}