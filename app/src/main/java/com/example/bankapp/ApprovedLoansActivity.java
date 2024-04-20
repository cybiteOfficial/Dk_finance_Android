package com.example.bankapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ApprovedLoansActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_loans);

        Spinner loanStatusSpinner = findViewById(R.id.page_spinner);

        List<String> loanStatuses = new ArrayList<>();
        loanStatuses.add("Approved loans");
        loanStatuses.add("Rejected Loans");
        loanStatuses.add("Failed Payments");

        List<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.icon_approved_loans);
        icons.add(R.drawable.icon_rejected_loans);
        icons.add(R.drawable.icon_failed_payments);
        // Add more icons corresponding to loan statuses

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, loanStatuses, icons);
        loanStatusSpinner.setAdapter(adapter);

        loanStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        // Handle Rejected Loans
                        // Start RejectedLoansActivity
                        startActivity(new Intent(ApprovedLoansActivity.this, RejectedLoansActivity.class));
                        finish(); // Finish current activity
                        break;
                    case 2:
                        // Handle Failed Payments
                        // Start FailedPaymentsActivity
                        startActivity(new Intent(ApprovedLoansActivity.this, FailedPaymentsActivity.class));
                        finish(); // Finish current activity
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing if nothing is selected
            }
        });


        ImageView backButton = findViewById(R.id.back_btn);
        LinearLayout cardContainer = findViewById(R.id.card_container);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the DashboardActivity
                Intent intent = new Intent(ApprovedLoansActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Add 6 cards dynamically with dummy data
        for (int i = 0; i < 6; i++) {
            View cardView = getLayoutInflater().inflate(R.layout.card_layout, null);

            // Add spacing between cards
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (i != 0) {
                params.topMargin = dpToPx(12); // Add 12dp spacing between cards
            }
            cardView.setLayoutParams(params);

            cardContainer.addView(cardView);

            // Find views in the card layout
            TextView applicationIdTextView = cardView.findViewById(R.id.application_id_text);
            TextView actualIdTextView = cardView.findViewById(R.id.actual_id_text);
            TextView customerNameTextView = cardView.findViewById(R.id.customer_name_text);
            TextView customerTypeTextView = cardView.findViewById(R.id.customer_type_text);
            TextView loanAmountTitleTextView = cardView.findViewById(R.id.loan_amount_title_text);
            TextView loanAmountTextView = cardView.findViewById(R.id.loan_amount_text);
            TextView loanStatusTextView = cardView.findViewById(R.id.loan_status_text);

            // Set dummy data
            applicationIdTextView.setText("Application ID: ");
            actualIdTextView.setText("12345" + (i + 1));
            customerNameTextView.setText("Customer Name " + (i + 1));
            customerTypeTextView.setText("Individual");
            loanAmountTextView.setText("₹10,000" + (i + 1));
            loanStatusTextView.setText("Completed");
            loanStatusTextView.setTextColor(Color.parseColor("#4CA6A8"));
            loanStatusTextView.setBackgroundResource(R.drawable.status_completed_background);
        }


    }
    // Function to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
