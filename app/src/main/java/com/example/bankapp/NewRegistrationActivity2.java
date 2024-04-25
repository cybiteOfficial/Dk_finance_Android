package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewRegistrationActivity2 extends AppCompatActivity {

    TextView applicationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration2);

        TextView btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        applicationID = findViewById(R.id.applicationID);

        // Generate and set random application ID
        String randomApplicationID = generateApplicationID();
        applicationID.setText(randomApplicationID);

        Button continueButton = findViewById(R.id.submit_button);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send data to AddCustomerActivity using Intent
                Intent intent = new Intent(NewRegistrationActivity2.this, AddCustomerActivity.class);
                intent.putExtra("applicationID", randomApplicationID); // Put the random application ID
                startActivity(intent);
            }
        });
    }

    // Generate random application ID
    private String generateApplicationID() {
        // Generate a random 3-digit number
        int randomNumber = (int) (Math.random() * 900) + 100; // Generates a random number between 100 and 999
        return "app_241884" + randomNumber; // Concatenate with the fixed prefix
    }
}