package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
    Button loadMoreBtn;
    LinearLayout cardContainer;
    List<ApplicantData> applicantList = new ArrayList<>();
    String nextPageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        ImageView backButton = findViewById(R.id.back_btn);
        cardContainer = findViewById(R.id.card_container);
        progressBar = findViewById(R.id.progressBar);
        noApplicantsText = findViewById(R.id.no_applications_text);
        loadMoreBtn = findViewById(R.id.load_more_btn);

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
        fetchApplicants(BASE_URL + "api/v1/applicants/");

        loadMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextPageUrl != null) {
                    showProgressBar(); // Show progress bar when fetching more applicants
                    fetchApplicants(nextPageUrl);
                }
            }
        });
    }

    private void fetchApplicants(String url) {
        String accessToken = sharedPreferences.getString("accessToken", "");
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
                    nextPageUrl = applicantResponse.getNext();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (applicantResponse.getCount() == 0 && applicantList.isEmpty()) {
                                noApplicantsText.setVisibility(View.VISIBLE);
                                cardContainer.setVisibility(View.GONE);
                            } else {
                                noApplicantsText.setVisibility(View.GONE);
                                cardContainer.setVisibility(View.VISIBLE);
                                applicantList.addAll(applicants);
                                displayApplicants(applicantList, cardContainer);
                                loadMoreBtn.setVisibility(nextPageUrl == null ? View.GONE : View.VISIBLE);
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

        for (ApplicantData applicant : applicants) {
            View cardView = getLayoutInflater().inflate(R.layout.card_layout, cardContainer, false);

            // Find views in the card layout
            TextView applicationIdTextView = cardView.findViewById(R.id.application_id_text);
            TextView loanStatusTextView = cardView.findViewById(R.id.loan_status_text);
            TextView roNameTextView = cardView.findViewById(R.id.ro_name_text);
            TextView creationDateTimeTextView = cardView.findViewById(R.id.creation_date_time_text);

            // Set applicant data to views
            if (applicationIdTextView != null) {
                applicationIdTextView.setText("Application ID: " + applicant.getApplication_id());
            }
            if (loanStatusTextView != null) {
                loanStatusTextView.setText("Status: " + applicant.getStatus());
                loanStatusTextView.setBackgroundResource(R.drawable.status_completed_background);
            }

            if (roNameTextView != null) {
                roNameTextView.setText("Lead ID: " + applicant.getLead());
            }

            if (creationDateTimeTextView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    creationDateTimeTextView.setText("Created at: " + formatTime(applicant.getCreated_at()) + " on " + formatDate(applicant.getCreated_at()));
                }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatDate(String timestamp) {
        try {
            // Parse the timestamp as a ZonedDateTime in UTC
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME);

            // Convert to Indian Standard Time (IST)
            ZonedDateTime istDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

            // Format the date in the desired format
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return istDateTime.format(dateFormatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatTime(String timestamp) {
        try {
            // Parse the timestamp as a ZonedDateTime in UTC
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_ZONED_DATE_TIME);

            // Convert to Indian Standard Time (IST)
            ZonedDateTime istDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

            // Format the time in the desired format
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            return istDateTime.format(timeFormatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
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
