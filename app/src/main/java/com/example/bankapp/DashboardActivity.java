package com.example.bankapp ;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        LinearLayout newRegistrationButton = findViewById(R.id.open_account_btn);
        LinearLayout approvedLoansButton = findViewById(R.id.approved_loans_btn);
        LinearLayout rejectedLoansButton = findViewById(R.id.rejected_loans_btn);
        LinearLayout failedPaymentsButton = findViewById(R.id.failed_payments_btn);

        // Setting onClick listeners for buttons
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

        // Adding leads dynamically
        addLeads();
    }

    // Method to add leads dynamically
    private void addLeads() {
        LinearLayout leadsContainer = findViewById(R.id.leads_container);
        String[] leadNumbers = {"Lead 1", "Lead 2", "Lead 3", "Lead 4", "Lead 5", "Lead 6", "Lead 7", "Lead 8", "Lead 9"};
        String[] leadStatuses = {"Status 1", "Status 2", "Status 3", "Status 4", "Status 1", "Status 2", "Status 3", "Status 4", "Status 9"};

        int marginInPixels = 16 * (int) getResources().getDisplayMetrics().density;

        // Loop to add leads dynamically
        for (int i = 0; i < leadNumbers.length; i++) {
            // Inflating the lead item layout
            View leadItem = getLayoutInflater().inflate(R.layout.lead_item_layout, null);

            TextView leadNumberTextView = leadItem.findViewById(R.id.lead_number);
            TextView leadStatusTextView = leadItem.findViewById(R.id.lead_status);

            leadNumberTextView.setText(leadNumbers[i]);
            leadStatusTextView.setText(leadStatuses[i]);

            leadsContainer.addView(leadItem);

            if (i < leadNumbers.length - 1) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) leadItem.getLayoutParams();
                params.bottomMargin = marginInPixels;
                leadItem.setLayoutParams(params);
            }
        }
    }
}
