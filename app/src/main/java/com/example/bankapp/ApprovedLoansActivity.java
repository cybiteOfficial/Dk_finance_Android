package com.example.bankapp;

// line no 203 -> cardView onclick listener

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApprovedLoansActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    LinearLayout cardContainer;
    Button loadMoreBtn;
    int currentPage = 1; // Initial page number
    boolean isLoading = false; // Flag to prevent multiple simultaneous requests
    boolean isLastPage = false; // Flag to track if all pages have been loaded

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_loans);

        Spinner loanStatusSpinner = findViewById(R.id.page_spinner);
        progressBar = findViewById(R.id.progressBar);
        cardContainer = findViewById(R.id.card_container);
        loadMoreBtn = findViewById(R.id.load_more_btn);

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
        fetchApplicants();
    }

    private void fetchApplicants() {
        showProgressBar(); // Show progress bar when fetching applicants
        String accessToken = sharedPreferences.getString("accessToken", "");
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "api/v1/applicants/?page=" + currentPage;

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .get()
                .build();

        isLoading = true; // Set loading flag to true before making the request

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
                isLoading = false; // Reset loading flag on failure
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
                    isLoading = false; // Reset loading flag on failure
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
                        if (applicantResponse.getCount() == 0) {
                            TextView noApplicantsText = findViewById(R.id.no_applications_text);
                            noApplicantsText.setVisibility(View.VISIBLE);
                        } else {
                            // log applicants data
                            Log.d("Applicants", applicants.toString());
                            // log hello
                            Log.d("Hello", "Hello");
                            displayApplicants(applicants);
                        }
                        isLoading = false; // Reset loading flag after fetching data
                        if (applicantResponse.getNext() == null) {
                            isLastPage = true; // Mark as last page if there is no next page
                            loadMoreBtn.setVisibility(View.GONE); // Hide load more button if it's the last page
                        } else {
                            currentPage++; // Increment page number for next request
                        }
                    }
                });
            }
        });
    }

    private void displayApplicants(List<ApplicantDataApproved> applicants) {
        for (int i = 0; i < applicants.size(); i++) {
            ApplicantDataApproved applicant = applicants.get(i);

            if (!Objects.equals(applicant.getStatus(), "sanctioned")) {
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


                // Add click listener to the card view
//                cardView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(ApprovedLoansActivity.this, DashboardInsideActivity.class);
//                        intent.putExtra("application_id", applicant.getApplication_id());
//                        startActivity(intent);
//                    }
//                });

                cardContainer.addView(cardView);
            }
        }
    }

    public void loadMoreData(View view) {
        if (!isLoading && !isLastPage) {
            fetchApplicants();
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
class CreatedBy {
    private String ro_name;
    private String employee_id;

    public String getRo_name() {
        return ro_name;
    }

    public String getEmployee_id() {
        return employee_id;
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
    private CreatedBy created_by;  // Add this line

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

    public CreatedBy getCreated_by() {
        return created_by;
    }

    public String getRo_name() {  // Add this method
        return created_by != null ? created_by.getRo_name() : null;
    }
}

