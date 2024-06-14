package com.example.bankapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardInsideActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_inside);

        Intent intent = getIntent();
        String application_id = intent.getStringExtra("application_id");

        // Get references to the TextViews
        TextView applicationId = findViewById(R.id.applicationID);
        TextView createCustomerPageBtn = findViewById(R.id.createCustomerPageBtn);
        TextView loanDetailsPageBtn = findViewById(R.id.loanDetailsPageBtn);
        TextView documentsPageBtn = findViewById(R.id.documentsPageBtn);
        TextView photographsPageBtn = findViewById(R.id.photographsPageBtn);
        TextView collateralDetailsPageBtn = findViewById(R.id.collateralDetailsPageBtn);
        TextView customerAppFormPageBtn = findViewById(R.id.customerAppFormPageBtn);

        applicationId.setText(application_id);

        // Set click listeners for each TextView
        createCustomerPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardInsideActivity.this, AddCustomerActivity.class);
                startActivity(intent);
            }
        });

        loanDetailsPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardInsideActivity.this, LoanDetailsActivity.class);
                startActivity(intent);
            }
        });

        documentsPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardInsideActivity.this, DocumentUploadActivity.class);
                intent.putExtra("application_id", application_id);
                startActivity(intent);
            }
        });

        photographsPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardInsideActivity.this, PhotographUploadActivity.class);
                intent.putExtra("application_id", application_id);
                startActivity(intent);
            }
        });

        collateralDetailsPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardInsideActivity.this, CollateralDetailsActivity.class);
                startActivity(intent);
            }
        });

        customerAppFormPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardInsideActivity.this, CAFActivity.class);
                startActivity(intent);
            }
        });
    }
}
