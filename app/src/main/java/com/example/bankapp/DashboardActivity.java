package com.example.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String userID = getIntent().getStringExtra("userId");
        LinearLayout newRegistrationButton = findViewById(R.id.open_account_btn);
        LinearLayout approvedLoansButton = findViewById(R.id.approved_loans_btn);
        LinearLayout rejectedLoansButton = findViewById(R.id.rejected_loans_btn);
        LinearLayout failedPaymentsButton = findViewById(R.id.failed_payments_btn);
        LinearLayout leadsButton = findViewById(R.id.leads_btn);

        // Setting onClick listeners for buttons
        newRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, NewRegistrationActivity.class);
                intent.putExtra("userId", userID);
                startActivity(intent);
            }
        });
        approvedLoansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ApprovedLoansActivity.class);
                startActivity(intent);
            }
        });
        rejectedLoansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, RejectedLoansActivity.class);
                startActivity(intent);
            }
        });
        failedPaymentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, FailedPaymentsActivity.class);
                startActivity(intent);
            }
        });
        leadsButton.setOnClickListener(new View.OnClickListener() { // Added
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LeadsActivity.class);
                startActivity(intent);
            }
        });

    }
}
