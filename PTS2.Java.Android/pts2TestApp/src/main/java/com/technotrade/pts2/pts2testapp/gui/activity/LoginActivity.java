package com.technotrade.pts2.pts2testapp.gui.activity;

import com.technotrade.pts2.pts2testapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TextView tvForgotPassword;
    private EditText etEmail, etPw;
    private Button btnLogin, btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (loggedIn != null) {
//            // go to HOME PAGE
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//            return;
//        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPw = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnContinue);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnLogin.setOnClickListener(v -> doLogin());
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Sending to forgot password page", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
        });
        btnCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
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
