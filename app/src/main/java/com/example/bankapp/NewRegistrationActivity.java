package com.example.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NewRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration);

        Button generateOTPButton = findViewById(R.id.submit_button);
        generateOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecting to OTPActivity
                startActivity(new Intent(NewRegistrationActivity.this, KycActivity1.class));
            }
        });
    }
}
