package com.example.bankapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        LinearLayout newRegistrationButton = findViewById(R.id.open_account_btn);
        LinearLayout approvedLoansButton = findViewById(R.id.approved_loans_btn);
        LinearLayout rejectedLoansButton = findViewById(R.id.rejected_loans_btn);
        LinearLayout failedPaymentsButton = findViewById(R.id.failed_payments_btn);

        newRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, NewRegistrationActivity.class);
                startActivity(intent);
            }
        });
        approvedLoansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ApprovedLoansActivity
                Intent intent = new Intent(DashboardActivity.this, ApprovedLoansActivity.class);
                startActivity(intent);
            }
        });
        rejectedLoansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start RejectedLoansActivity
                Intent intent = new Intent(DashboardActivity.this, RejectedLoansActivity.class);
                startActivity(intent);
            }
        });
        failedPaymentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start FailedPaymentsActivity
                Intent intent = new Intent(DashboardActivity.this, FailedPaymentsActivity.class);
                startActivity(intent);
            }
        });
    }


}
