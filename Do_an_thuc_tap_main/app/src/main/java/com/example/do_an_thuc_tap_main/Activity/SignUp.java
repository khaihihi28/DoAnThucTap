package com.example.do_an_thuc_tap_main.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.do_an_thuc_tap_main.Helper.AuthHelper;
import com.example.do_an_thuc_tap_main.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText edtEmail, edtPhone, edtName, edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Account");

        anhXa();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKy();
            }
        });

    }
    private void dangKy(){
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String name = edtName.getText().toString();
        String phone = edtPhone.getText().toString();
        ProgressDialog mDialog = new ProgressDialog(SignUp.this);
        mDialog.setMessage("Vui lòng chờ...");
        mDialog.show();
        AuthHelper.signUpHelper(email, password, name, phone, new AuthHelper.OnRegistrationCompleteListener() {
            @Override
            public void onRegistrationSuccess(String uid) {
                mDialog.dismiss();
                Toast.makeText(SignUp.this, "Đăng ký thành công!!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onRegistrationFailure(String errorMessage) {
                mDialog.dismiss();
                Toast.makeText(SignUp.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void anhXa(){
        edtName = (EditText) findViewById(R.id.edtName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
    }
}