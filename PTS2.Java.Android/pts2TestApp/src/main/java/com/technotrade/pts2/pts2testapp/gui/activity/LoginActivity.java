package com.technotrade.pts2.pts2testapp.gui.activity;

import com.technotrade.pts2.pts2testapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPw;
    private Button btnLogin, btnCreateAccount, btnForgetPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPw = findViewById(R.id.etPw);
        btnLogin = findViewById(R.id.btnLogin);
        btnForgetPw = findViewById(R.id.btnForgetPw);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(v -> doLogin());
        btnForgetPw.setOnClickListener(v -> {
            Toast.makeText(this, "Sending to forgot password page", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
        });
        btnCreateAccount.setOnClickListener(v -> {
            Toast.makeText(this, "Sending to create account page", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
//            startActivity(intent);
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        btnLogin.setEnabled(true);
    }

    private void doLogin(){
        String email = etEmail.getText().toString().trim();
        String password = etPw.getText().toString();

        btnLogin.setEnabled(false);
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

        // QUERY DATABASE
        // email exists? yes, authenticate user with email + password
        // else if no email, user not authenticated, no such email

        // else its correct, go to home page

        if (!(email.equals("admin@gmail.com") && password.equals("admin"))) return; // temp

        // change to home page
        Toast.makeText(this, "Success! Changing to homepage...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

}
