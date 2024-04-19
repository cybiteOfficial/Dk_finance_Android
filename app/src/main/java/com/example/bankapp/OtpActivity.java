package com.example.bankapp;

// OTPActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {

    private EditText otpEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpEditText = findViewById(R.id.otp_edit_text);
        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyOTP()) {
                    // If OTP is correct, start the splash screen activity
                    Intent intent = new Intent(OtpActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish(); // Close the OTP activity
                } else {
                    // Show error message or handle incorrect OTP
                }
            }
        });
    }

    private boolean verifyOTP() {
        // Implement your OTP verification logic here
        // Return true if OTP is correct, false otherwise
        return true; // For demonstration, always return true
    }
}
