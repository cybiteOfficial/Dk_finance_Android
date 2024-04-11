package com.example.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button newRegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize the "New Registration" button
        newRegistrationButton = findViewById(R.id.new_registration_button);

        // Set OnClickListener for the "New Registration" button
        newRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start NewRegistrationActivity
                Intent intent = new Intent(DashboardActivity.this, NewRegistrationActivity.class);
                startActivity(intent);
            }
        });

        // Add OnClickListener for other buttons if needed
    }
}

