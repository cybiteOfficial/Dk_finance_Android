package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewRegistrationActivity2 extends AppCompatActivity {

    TextView applicationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration2);

        final String application_ID = getIntent().getStringExtra("applicationId");
        showToast("Application ID Generated");
        TextView btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        applicationID = findViewById(R.id.applicationID);
        applicationID.setText(application_ID);


        Button continueButton = findViewById(R.id.submit_button);
        ImageView homeButton = findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRegistrationActivity2.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send data to AddCustomerActivity using Intent
                Intent intent = new Intent(NewRegistrationActivity2.this, AddCustomerActivity.class);
                intent.putExtra("applicationID", application_ID);
                startActivity(intent);
                finish();

            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}