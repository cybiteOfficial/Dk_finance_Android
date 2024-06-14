package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApprovedLoansActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_loans);

        Spinner loanStatusSpinner = findViewById(R.id.page_spinner);
        progressBar = findViewById(R.id.progressBar);

        List<String> loanStatuses = new ArrayList<>();
        loanStatuses.add("Pending loans");
        loanStatuses.add("Rejected Loans");
        loanStatuses.add("Approved Loans");

        List<Integer> icons = new ArrayList<>();
        icons.add(R.drawable.icon_pending_loans);
        icons.add(R.drawable.icon_rejected_loans);
        icons.add(R.drawable.icon_approved_loans);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, loanStatuses, icons);
        loanStatusSpinner.setAdapter(adapter);

        loanStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        startActivity(new Intent(ApprovedLoansActivity.this, RejectedLoansActivity.class));
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(ApprovedLoansActivity.this, FailedPaymentsActivity.class));
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
                Intent intent = new Intent(ApprovedLoansActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize sharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Fetch applicants and display cards
        fetchApplicants(cardContainer);
    }

    private void fetchApplicants(LinearLayout cardContainer) {
        showProgressBar(); // Show progress bar when fetching applicants
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "api/v1/applicants/";

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
                        showToast("Failed to fetch applicants");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    hideProgressBar(); // Hide progress bar on unsuccessful response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Failed to fetch applicants");
                        }
                    });
                    return;
                }

                String responseBody = response.body().string();

                // Process JSON response
                Gson gson = new Gson();
                ApplicantResponseApproved applicantResponse = gson.fromJson(responseBody, ApplicantResponseApproved.class);
                final List<ApplicantDataApproved> applicants = Arrays.asList(applicantResponse.getResults());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar(); // Hide progress bar after fetching applicants
                        if(applicantResponse.getCount() == 0){
                            TextView noApplicantsText = findViewById(R.id.no_applications_text);
                            noApplicantsText.setVisibility(View.VISIBLE);
                        }
                        else {
                            displayApplicants(applicants, cardContainer);
                        }
                    }
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayApplicants(List<ApplicantDataApproved> applicants, LinearLayout cardContainer) {
        cardContainer.removeAllViews();
        for (int i = 0; i < applicants.size(); i++) {  // Changed to i < applicants.size() for better readability
            ApplicantDataApproved applicant = applicants.get(i);

            if (!Objects.equals(applicant.getStatus(), "md")) {
                View cardView = getLayoutInflater().inflate(R.layout.card_layout, cardContainer, false);

                // Find views in the card layout
                TextView applicationIdTextView = cardView.findViewById(R.id.application_id_text);
                TextView loanStatusTextView = cardView.findViewById(R.id.loan_status_text);

                // Set applicant data to views
                if (applicationIdTextView != null) {
                    applicationIdTextView.setText("Application ID: " + applicant.getApplication_id());
                }
                if (loanStatusTextView != null) {
                    loanStatusTextView.setText("Status: " + applicant.getStatus());
                    loanStatusTextView.setBackgroundResource(R.drawable.status_completed_background);
                }

                // Add click listener to the card view
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ApprovedLoansActivity.this, DashboardInsideActivity.class);
                        intent.putExtra("application_id", applicant.getApplication_id());
                        startActivity(intent);
                    }
                });

                cardContainer.addView(cardView);
            }
        }
    }


    // Show toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Method to show progress bar
    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE); // Show progress bar
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
}

class ApplicantResponseApproved {
    private int count;
    private String next;
    private String previous;
    private ApplicantDataApproved[] results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public ApplicantDataApproved[] getResults() {
        return results;
    }
}

class ApplicantDataApproved {
    private String uuid;
    private String created_at;
    private String updated_at;
    private String application_id;
    private String status;
    private String lead;
    private String paymentedetails;

    public String getUuid() {
        return uuid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getApplication_id() {
        return application_id;
    }

    public String getStatus() {
        return status;
    }

    public String getLead() {
        return lead;
    }

    public String getPaymentedetails() {
        return paymentedetails;
    }
}
