package com.technotrade.pts2.pts2testapp.gui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.technotrade.pts2.pts2testapp.R;

public class CreateAccountActivity extends AppCompatActivity {

    LinearLayout layoutEmail, layoutVerification, layoutDetails;
    Button btnContinue, btnCancel;
    TextView tvResendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        layoutEmail = findViewById(R.id.layoutEmail);
        layoutVerification = findViewById(R.id.layoutVerification);
        layoutDetails = findViewById(R.id.layoutDetails);

        btnContinue = findViewById(R.id.btnContinue);
        btnCancel = findViewById(R.id.btnCancel);
        tvResendCode = findViewById(R.id.tvResend);

        btnContinue.setOnClickListener(v -> OnContinue());
        btnCancel.setOnClickListener(v -> finish());
        tvResendCode.setOnClickListener(v -> OnResendCode());
    }

    private void OnContinue(){
        if (layoutEmail.getVisibility() == View.VISIBLE) { // step 1
            layoutEmail.setVisibility(View.GONE);
            layoutVerification.setVisibility(View.VISIBLE);

            // CHECK IF ITS AN AVAILABLE EMAIL
        }
        else if (layoutVerification.getVisibility() == View.VISIBLE) { // step 2
            layoutVerification.setVisibility(View.GONE);
            layoutDetails.setVisibility(View.VISIBLE);

            // Can ignore this step if verification done by database
            // CHECK IF VERIFICATION IS CORRECT
        }
        else { // step 3
            CreateAccount();
        }
    }

    private void OnResendCode(){
        // RESEND CODE
        Toast.makeText(this, "Resending code...", Toast.LENGTH_SHORT).show();
    }

    private void CreateAccount(){
        Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show();
        finish(); // returns to login page to login
    }
}
