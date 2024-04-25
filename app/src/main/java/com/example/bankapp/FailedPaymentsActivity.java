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

public class FailedPaymentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_payments);

        Spinner loanStatusSpinner = findViewById(R.id.page_spinner);

        List<String> loanStatuses = new ArrayList<>();
        loanStatuses.add("Failed Payments");
        loanStatuses.add("Approved Loans");
        loanStatuses.add("Rejected Loans");

        List<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.icon_failed_payments);
        icons.add(R.drawable.icon_approved_loans);
        icons.add(R.drawable.icon_rejected_loans);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, loanStatuses, icons);
        loanStatusSpinner.setAdapter(adapter);

        loanStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        startActivity(new Intent(FailedPaymentsActivity.this, ApprovedLoansActivity.class));
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(FailedPaymentsActivity.this, RejectedLoansActivity.class));
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
                Intent intent = new Intent(FailedPaymentsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Add 6 cards dynamically with dummy data
//        for (int i = 0; i < 6; i++) {
//            View cardView = getLayoutInflater().inflate(R.layout.card_layout, null);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            if (i != 0) {
//                params.topMargin = dpToPx(12);
//            }
//            cardView.setLayoutParams(params);
//
//            cardContainer.addView(cardView);
//
//            TextView applicationIdTextView = cardView.findViewById(R.id.application_id_text);
//            TextView actualIdTextView = cardView.findViewById(R.id.actual_id_text);
//            TextView customerNameTextView = cardView.findViewById(R.id.customer_name_text);
//            TextView customerTypeTextView = cardView.findViewById(R.id.customer_type_text);
//            TextView loanAmountTitleTextView = cardView.findViewById(R.id.loan_amount_title_text);
//            TextView loanAmountTextView = cardView.findViewById(R.id.loan_amount_text);
//            TextView loanStatusTextView = cardView.findViewById(R.id.loan_status_text);
//
//            applicationIdTextView.setText("Application ID: ");
//            actualIdTextView.setText("12345" + (i + 1));
//            customerNameTextView.setText("Customer Name " + (i + 1));
//            customerTypeTextView.setText("Individual");
//            loanAmountTextView.setText("₹10,000" + (i + 1));
//            loanStatusTextView.setText("Failed");
//            loanStatusTextView.setTextColor(Color.parseColor("#E95537"));
//            loanStatusTextView.setBackgroundResource(R.drawable.status_failed_background);
//        }
//    }

//    private int dpToPx(int dp) {
//        float density = getResources().getDisplayMetrics().density;
//        return Math.round((float) dp * density);
//    }
    }
}
