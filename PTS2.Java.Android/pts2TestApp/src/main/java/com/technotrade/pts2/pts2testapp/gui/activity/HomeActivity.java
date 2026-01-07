package com.technotrade.pts2.pts2testapp.gui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.technotrade.pts2.pts2testapp.R;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUser, tvMoney;
    private Button btnProfile, btnConnect, btnTopup, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get login info
        // eg: FirebaseUser currentUser = auth.getCurrentUser();
        //        if (currentUser == null) // show error message and return to login

        tvUser = findViewById(R.id.tvUser);
        tvMoney = findViewById(R.id.tvMoney);
        btnProfile = findViewById(R.id.btnProfile);
        btnConnect = findViewById(R.id.btnConnect);
        btnTopup = findViewById(R.id.btnTopup);
        btnLogout = findViewById(R.id.btnLogout);

        btnProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Loading profile page...", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//            startActivity(intent);
        });

        // btnConnect send to connect page

        btnTopup.setOnClickListener(v -> {
            Toast.makeText(this, "Sending to wallet top-up...", Toast.LENGTH_SHORT).show();
        });
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            finish();
            // + whatever logout requirements
        });
    }
}
