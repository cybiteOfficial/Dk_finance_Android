package com.example.bankapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
        LinearLayout allBtn = findViewById(R.id.all_btn);
        LinearLayout leadsButton = findViewById(R.id.leads_btn);

        ImageView logoutBtn = findViewById(R.id.logout);
        // Setting onClick listeners for buttons

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });
        newRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, NewRegistrationActivity.class);
                intent.putExtra("userId", userID);
                startActivity(intent);
            }
        });
//        approvedLoansButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, ApprovedLoansActivity.class);
//                startActivity(intent);
//            }
//        });
//        rejectedLoansButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, RejectedLoansActivity.class);
//                startActivity(intent);
//            }
//        });
//        failedPaymentsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, FailedPaymentsActivity.class);
//                startActivity(intent);
//            }
//        });
        leadsButton.setOnClickListener(new View.OnClickListener() { // Added
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, LeadsActivity.class);
                startActivity(intent);
            }
        });

//        allBtn.setOnClickListener(new View.OnClickListener() { // Added
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DashboardActivity.this, AllActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Do you really want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void logout() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        showToast("Logged out successfully");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}