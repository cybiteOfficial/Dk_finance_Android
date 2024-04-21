package com.example.bankapp;

// OTPActivity.java
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {

    private EditText otpEditText; // Add this line

    private EditText editTextDigit1;
    private EditText editTextDigit2;
    private EditText editTextDigit3;
    private EditText editTextDigit4;
    private Button submit_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Initialize otpEditText
        otpEditText = findViewById(R.id.editTextDigit1); // You can choose any of the editTextDigit fields to initialize it

        editTextDigit1 = findViewById(R.id.editTextDigit1);
        editTextDigit2 = findViewById(R.id.editTextDigit2);
        editTextDigit3 = findViewById(R.id.editTextDigit3);
        editTextDigit4 = findViewById(R.id.editTextDigit4);

        // Setup EditText listeners
        setupEditTextListeners();

        // Find the submit_otp button by its ID
        submit_otp = findViewById(R.id.submit_otp);

        TextView change_number = findViewById(R.id.change_number);
        change_number.setPaintFlags(change_number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        TextView resend_btn = findViewById(R.id.resend_btn);
        resend_btn.setPaintFlags(change_number.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Set click listener for the submit_otp button after finding it
        submit_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyOTP()) {
                    // If OTP is correct, navigate to the DashboardActivity
                    Intent intent = new Intent(OtpActivity.this, NewRegistrationActivity2.class);
                    startActivity(intent);

                    // Close the current activity to prevent navigating back to the OTP screen
                    finish();
                } else {
                    // Show error message or handle incorrect OTP
                    // For example, display a toast message indicating incorrect OTP
                    Toast.makeText(OtpActivity.this, "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean verifyOTP() {
        // Get the OTP entered by the user from the EditText
        String enteredOTP = editTextDigit1.getText().toString().trim() +
                editTextDigit2.getText().toString().trim() +
                editTextDigit3.getText().toString().trim() +
                editTextDigit4.getText().toString().trim();

        // Implement your OTP verification logic here
        // For demonstration, let's assume the correct OTP is "1234"
        String correctOTP = "1234";

        // Compare the entered OTP with the correct OTP
        return enteredOTP.equals(correctOTP);
    }

    private void setupEditTextListeners() {
        editTextDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    // Move focus to the next EditText
                    editTextDigit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    // Move focus to the next EditText
                    editTextDigit3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    // Move focus to the next EditText
                    editTextDigit4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
