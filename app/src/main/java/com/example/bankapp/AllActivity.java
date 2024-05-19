package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    TextView noApplicantsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        ImageView backButton = findViewById(R.id.back_btn);
        LinearLayout cardContainer = findViewById(R.id.card_container);
        progressBar = findViewById(R.id.progressBar);
        noApplicantsText = findViewById(R.id.no_applications_text);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllActivity.this, DashboardActivity.class);
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
                hideProgressBar(); // Hide progress bar on failure
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
                hideProgressBar(); // Hide progress bar after response
                String responseBody = response.body().string();
                if (response.isSuccessful() && response.body() != null) {

                    // Process JSON response
                    Gson gson = new Gson();
                    ApplicantResponse applicantResponse = gson.fromJson(responseBody, ApplicantResponse.class);
                    final List<ApplicantData> applicants = Arrays.asList(applicantResponse.getResults());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (applicantResponse.getCount() == 0) {
                                noApplicantsText.setVisibility(View.VISIBLE);
                                cardContainer.setVisibility(View.GONE);
                            } else {
                                noApplicantsText.setVisibility(View.GONE);
                                cardContainer.setVisibility(View.VISIBLE);
                                displayApplicants(applicants, cardContainer);
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(responseBody);
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayApplicants(List<ApplicantData> applicants, LinearLayout cardContainer) {
        cardContainer.removeAllViews();

        for (int i = 0; i <= applicants.size() - 1; i++) {
            ApplicantData applicant = applicants.get(i);
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

            cardContainer.addView(cardView);
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

class ApplicantResponse {
    private int count;
    private String next;
    private String previous;
    private ApplicantData[] results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public ApplicantData[] getResults() {
        return results;
    }
}

class ApplicantData {
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
