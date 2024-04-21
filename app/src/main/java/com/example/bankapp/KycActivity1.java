package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class KycActivity1 extends AppCompatActivity {

    EditText firstName, lastName, phoneNumber, emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_1);

        // Adding underline to the "Save" button text
        TextView btnSave = findViewById(R.id.btn_save);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Initialize EditText fields
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailId = findViewById(R.id.emailId);

        setListeners();
    }

    private void setListeners() {
        // "Save" button listener
        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate fields
                if (validateFields()) {
                    // Save data
                    saveData();
                }
            }
        });

        // "Submit" button listener
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()) {
                    // Move to KycActivity2
                    startActivity(new Intent(KycActivity1.this, KycActivity2.class));

                }
            }
        });

        // "Home" icon listener
        ImageView homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to DashboardActivity
                startActivity(new Intent(KycActivity1.this, DashboardActivity.class));
                finish();
            }
        });
    }

    private boolean validateFields() {
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String phoneNumberText = phoneNumber.getText().toString().trim();
        String emailText = emailId.getText().toString().trim();

        if (TextUtils.isEmpty(firstNameText)) {
            firstName.setError("First name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastNameText)) {
            lastName.setError("Last name is required");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumberText)) {
            phoneNumber.setError("Phone number is required");
            return false;
        }

        if (TextUtils.isEmpty(emailText)) {
            emailId.setError("Email is required");
            return false;
        } else if (!isValidEmail(emailText)) {
            emailId.setError("Enter a valid email address");
            return false;
        }

        return true;
    }

    private void saveData() {
        // You can implement the logic to save data here
        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
