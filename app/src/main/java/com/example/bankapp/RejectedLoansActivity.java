package com.example.bankapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RejectedLoansActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_loans);

        Spinner loanStatusSpinner = findViewById(R.id.page_spinner);

        List<String> loanStatuses = new ArrayList<>();
        loanStatuses.add("Rejected Loans");
        loanStatuses.add("Pending Loans");
        loanStatuses.add("Approved Loans");

        List<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.icon_rejected_loans);
        icons.add(R.drawable.icon_pending_loans);
        icons.add(R.drawable.icon_approved_loans);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, loanStatuses, icons);
        loanStatusSpinner.setAdapter(adapter);

        loanStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        startActivity(new Intent(RejectedLoansActivity.this, ApprovedLoansActivity.class));
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(RejectedLoansActivity.this, FailedPaymentsActivity.class));
                        finish();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ImageView backButton = findViewById(R.id.back_btn);
        LinearLayout cardContainer = findViewById(R.id.card_container);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RejectedLoansActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}