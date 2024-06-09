package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeadsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    ImageView backBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leads);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        backBtn = findViewById(R.id.back_btn);
        progressBar = findViewById(R.id.progressBar);

        // Set click listener for back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to DashboardActivity
                Intent intent = new Intent(LeadsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Adding leads dynamically
        getLeads();
    }


    // Method to fetch leads using GET request
    private void getLeads() {
        showProgressBar(); // Show progress bar when fetching leads
        String accessToken = sharedPreferences.getString("accessToken", "");
        String url = BASE_URL + "api/v1/leads?sort=-created_at"; // Assuming server supports sorting by creation date

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Hide progress bar on failure
                hideProgressBar();
                // Handle failure
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Display an error message or retry logic if needed
                        Toast.makeText(LeadsActivity.this, "Failed to fetch leads", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (responseBody.contains("User found.")) {
                    // Process JSON data here
                    try {
                        Gson gson = new Gson();
                        LeadResponse leadResponse = gson.fromJson(responseBody, LeadResponse.class);
                        if (!leadResponse.isError() && leadResponse.getData() != null) {
                            final LeadData[] leads = leadResponse.getData();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayLeads(leads, accessToken); // Pass access token to displayLeads
                                    hideProgressBar(); // Hide progress bar after fetching leads
                                }
                            });
                        } else {
                            // Hide progress bar on error
                            hideProgressBar();
                            // Handle error if needed
                            final String errorMessage = leadResponse.getMessage();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LeadsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Hide progress bar on exception
                        hideProgressBar();
                        // Handle exception
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LeadsActivity.this, "Failed to fetch leads", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Hide progress bar on unsuccessful response
                    hideProgressBar();
                    // Handle unsuccessful response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display an error message or retry logic if needed
                            Toast.makeText(LeadsActivity.this, "Failed to fetch leads", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    private String formatAmount(String amount) {
        if (amount == null) {
            return ""; // or any default value you prefer
        }

        // Remove commas from the amount string
        String amt = amount.replace(",", "");

        // Parse the string to a long value
        long loanAmount = 0;
        try {
            loanAmount = Long.parseLong(amt);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle parsing error if needed
        }

        // Create a DecimalFormat object to format the number
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

        // Format the number with commas
        return formatter.format(loanAmount);
    }


    // Method to dynamically display fetched leads
    private void displayLeads(LeadData[] leads, String accessToken) { // Add accessToken parameter
        LinearLayout leadsContainer = findViewById(R.id.leads_container);
        leadsContainer.removeAllViews(); // Clear existing views

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = leads.length - 1; i >= 0; i--) {
            LeadData lead = leads[i];
            View leadItem = inflater.inflate(R.layout.lead_item_layout, leadsContainer, false); // Move the creation inside the loop
            TextView leadNumberTextView = leadItem.findViewById(R.id.lead_number);
            TextView NameTextView = leadItem.findViewById(R.id.customer_name_text);
            TextView loanAmount = leadItem.findViewById(R.id.loan_amount_text);
            TextView customerType = leadItem.findViewById(R.id.customer_type_text);

            // Set lead data
            leadNumberTextView.setText(lead.getLead_id());
            String fullName = lead.getFirst_name() + " " + lead.getLast_name();
            NameTextView.setText(fullName);
            String loanAmtUnformatted = lead.getLoan_amount();
            String formattedLoanAmount = formatAmount(loanAmtUnformatted);

            // Set formatted loan amount
            loanAmount.setText(formattedLoanAmount);

            String custType = lead.getCustomer_type();
            if (Objects.equals(custType, "home_loan")) {
                customerType.setText("Normal");
            }

            // Add lead item to container
            leadsContainer.addView(leadItem);

            // Add click listener to lead item
            leadItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show lead details in a custom dialog
                    showLeadDetailDialog(lead, accessToken); // Pass accessToken to showLeadDetailDialog
                }
            });
        }
    }


    private void showLeadDetailDialog(LeadData lead, String accessToken) { // Add accessToken parameter
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.lead_detail_dialog, null);

        // Find views in the custom dialog layout
        TextView leadIdTextView = dialogView.findViewById(R.id.lead_id);
        TextView leadNameTextView = dialogView.findViewById(R.id.lead_name);
        TextView mobileNumberTextView = dialogView.findViewById(R.id.mobile_number);
        TextView productTypeTextView = dialogView.findViewById(R.id.product_type);
        TextView caseTagTextView = dialogView.findViewById(R.id.case_tag);
        TextView createdAtTextView = dialogView.findViewById(R.id.created_at);
        TextView customerTypeTextView = dialogView.findViewById(R.id.customer_type);
        Button kycBtn = dialogView.findViewById(R.id.kycButton);
        Button otpBtn = dialogView.findViewById(R.id.otpSendBtn);

        leadIdTextView.setText("Lead ID: " + lead.getLead_id());
        String fullName = lead.getFirst_name() + " " + lead.getLast_name();
        leadNameTextView.setText("Name: " + fullName);
        mobileNumberTextView.setText("Mobile Number: " + lead.getMobile_number());
        if(Objects.equals(lead.getProduct_type(), "normal")){
            productTypeTextView.setText("Product Type: Normal");
        }

        if(Objects.equals(lead.getCase_tag(), "normal")){
            caseTagTextView.setText("Case Tag: Normal");
        }
        createdAtTextView.setText("Created At: " + formatDate(lead.getCreated_at()));
        String custType = lead.getCustomer_type();
        Log.d(lead.getMobile_number(),"Mobile no");
        if (Objects.equals(custType, "home_loan")) {
            customerTypeTextView.setText("Customer Type: Normal");
        }

        // Check if KYC is done for the lead
        checkKYCStatus(lead.getLead_id(), new KYCCheckListener() {
            @Override
            public void onKYCStatusChecked(boolean isKYCDone, boolean isKyc1done, boolean isKyc2done, String UUID) {
                String myString = String.valueOf(isKyc1done);
                Log.i("kyc1", myString);


                if (isKYCDone && (!isKyc1done || !isKyc2done)) {
                    String str = String.valueOf(isKyc1done);
                    Log.i("kyc1", myString);



                    // If KYC is not done, show KYC button
                    kycBtn.setVisibility(View.VISIBLE);
                    kycBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!isKyc1done){

                                // Start KYC activity
                                Intent intent = new Intent(LeadsActivity.this, KycUpdate1.class);
                                intent.putExtra("firstName", lead.getFirst_name());
                                intent.putExtra("lastName", lead.getLast_name());
                                intent.putExtra("phoneNumber", lead.getMobile_number());
                                intent.putExtra("leadId", lead.getLead_id());
                                intent.putExtra("kyc_id", UUID);
                                intent.putExtra("accessToken", accessToken); // Pass accessToken to KYC activity
                                startActivity(intent);
                                finish();
                            }

                            else if(isKyc1done && !isKyc2done){

                                // Start KYC activity
                                Intent intent = new Intent(LeadsActivity.this, KycActivity2.class);
                                intent.putExtra("leadId", lead.getLead_id());
                                intent.putExtra("accessToken", accessToken); // Pass accessToken to KYC activity
                                intent.putExtra("kyc_id", UUID);
                                intent.putExtra("phoneNumber", lead.getMobile_number());
                                startActivity(intent);
                                finish();

                            }
                        }
                    });
                }

                else{
                    otpBtn.setVisibility(View.VISIBLE);
                    otpBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // move to otp activity
                            Intent intent = new Intent(LeadsActivity.this, OtpActivity.class);
                            intent.putExtra("leadId", lead.getLead_id());
                            intent.putExtra("phoneNumber", lead.getMobile_number());
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }, accessToken);

        // Create and show the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView);
        builder.setBackground(getResources().getDrawable(R.drawable.lead_dialogue_background)); // Add custom background
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // Method to check KYC status for a lead
    private void checkKYCStatus(String leadId, KYCCheckListener listener, String accessToken) {
        String url = BASE_URL + "api/v1/kyc?lead_id=" + leadId;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
                Log.e("KYC Status", "Failed to check KYC status: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify listener with KYC not done
                        listener.onKYCStatusChecked(true, false, false, null);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("KYC Status", "Response: " + responseBody); // Log the response
                try {
                    Gson gson = new Gson();
                    KYCResponse kycResponse = gson.fromJson(responseBody, KYCResponse.class);
                    int dataSize = kycResponse.getData() != null ? kycResponse.getData().length : 0;
                    String UUID = kycResponse.getData()[0].uuid;
                    // Check if KYC is successful
                    if (!kycResponse.isError() && (!kycResponse.getData()[0].kyc_verified  || !kycResponse.getData()[0].kyc_document_verified)){
                        // KYC is done
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Notify listener with KYC done
                                listener.onKYCStatusChecked(true,kycResponse.getData()[0].kyc_verified,kycResponse.getData()[0].kyc_document_verified, UUID);

                            }
                        });
                    } else {
                        // KYC is not done
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Notify listener with KYC not done
                                listener.onKYCStatusChecked(true, kycResponse.getData()[0].kyc_verified,kycResponse.getData()[0].kyc_document_verified ,UUID);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle exception
                    Log.e("KYC Status", "Error parsing response: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Notify listener with KYC not done
                            // listener.onKYCStatusChecked(false, kycResponse.getData()[0].kyc_verified,kycResponse.getData()[0].kyc_document_verified );
                        }
                    });
                }
            }
        });
    }

    // Interface for KYC status callback
    interface KYCCheckListener {
        void onKYCStatusChecked(boolean isKYCDone, boolean isKyc1done, boolean isKyc2done, String UUID);
    }

    // KYCResponse class for JSON parsing
    public class KYCResponse {
        private boolean error;
        private KYCData[] data;
        private String message;

        public boolean isError() {
            return error;
        }

        public KYCData[] getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
    }

    // KYCData class for JSON parsing
    public class KYCData {
        private String uuid;
        private String created_at;
        private String updated_at;
        private String first_name;
        private String last_name;

        private boolean kyc_verified;
        private String mobile_number;
        private String email;
        private String lead;

        private boolean kyc_document_verified;

        public boolean getkyc_verified(){
            return kyc_verified;
        }

        public boolean kyc_document_verified(){
            return kyc_document_verified;
        }

        public String getUuid() {
            return uuid;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public String getMobile_number() {
            return mobile_number;
        }

        public String getEmail() {
            return email;
        }

        public String getLead() {
            return lead;
        }
    }


    // Method to format date and time
    private String formatDate(String dateTimeString) {
        try {
            // Parse the input date and time string
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(dateTimeString);

            // Format the parsed date to extract only the date
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty string in case of parsing error
        }
    }

    // Method to show progress bar
    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                progressBar.bringToFront(); // Bring it to the front
            }
        });
    }

    // Method to hide progress bar
    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE); // Hide progress bar
            }
        });
    }

    // LeadResponse class for JSON parsing
    public class LeadResponse {
        private boolean error;
        private LeadData[] data;
        private String message;

        public boolean isError() {
            return error;
        }

        public LeadData[] getData() {
            return data;
        }

        public String getMessage() {
            return message;
        }
    }

    // LeadData class for JSON parsing
    public class LeadData {
        private String uuid;
        private String created_at;
        private String updated_at;
        private String lead_id;
        private String first_name;
        private String last_name;
        private String email;
        private String mobile_number;
        private String agent_code;
        private String branch_code;
        private String branch_name;
        private String loan_amount;
        private String product_type;
        private String case_tag;
        private String customer_type;
        private String source;
        private String assigned_to;

        public String getUuid() {
            return uuid;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getLead_id() {
            return lead_id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public String getEmail() {
            return email;
        }

        public String getMobile_number() {
            return mobile_number;
        }

        public String getAgent_code() {
            return agent_code;
        }

        public String getBranch_code() {
            return branch_code;
        }

        public String getBranch_name() {
            return branch_name;
        }

        public String getLoan_amount() {
            return loan_amount;
        }

        public String getProduct_type() {
            return product_type;
        }

        public String getCase_tag() {
            return case_tag;
        }

        public String getCustomer_type() {
            return customer_type;
        }

        public String getSource() {
            return source;
        }

        public String getAssigned_to() {
            return assigned_to;
        }
    }
}