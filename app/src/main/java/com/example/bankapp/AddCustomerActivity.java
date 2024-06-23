package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.environment.BaseUrl;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddCustomerActivity extends AppCompatActivity {

    private static final String TAG = "AddCustomerActivity";
    private LinearLayout coApplicantsLayout;
    private TextView appIdTextView;
    private ImageView homeBtn;
    private OkHttpClient client;
    private String accessToken;
    private boolean applicantExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        appIdTextView = findViewById(R.id.applicationID);
        TextView btnSave = findViewById(R.id.save_button);
        homeBtn = findViewById(R.id.homeButton);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        coApplicantsLayout = findViewById(R.id.coApplicantsLayout);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", "");

        if (accessToken.isEmpty()) {
            Toast.makeText(this, "Access token is missing", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Access token is missing");
            return;
        }

        String applicationId = getIntent().getStringExtra("application_id");
        if (applicationId == null || applicationId.isEmpty()) {
            Toast.makeText(this, "Application ID is missing", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Application ID is missing");
            return;
        }

        appIdTextView.setText(applicationId);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCustomerActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        client = new OkHttpClient();

        fetchCustomerData(applicationId);

        findViewById(R.id.addCustomerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCustomerActivity.this, CreatCustomer.class);
                intent.putExtra("application_id", applicationId);
                intent.putExtra("applicantExists", applicantExists);
                startActivity(intent);
            }
        });
    }

    private void fetchCustomerData(String applicationId) {
        String url = BASE_URL + "api/v1/customers?application_id=" + applicationId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        Log.d(TAG, "Fetching customer data from URL: " + url);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch customer data", e);
                runOnUiThread(() -> Toast.makeText(AddCustomerActivity.this, "Failed to fetch customer data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response received: " + responseData);
                    runOnUiThread(() -> handleResponse(responseData));
                } else {
                    Log.e(TAG, "Failed to fetch customer data: " + response.message());
                    runOnUiThread(() -> {
                        Log.e(TAG, "Response: " + responseData);
                        Toast.makeText(AddCustomerActivity.this, "Failed to fetch customer data: " + response.message(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }


    private void handleResponse(String responseData) {
        try {
            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
            Log.d(TAG, "Parsed JSON: " + jsonObject.toString());

            // Check if there's an error field and its value
            if (jsonObject.has("error")) {
                boolean error = jsonObject.get("error").getAsBoolean();
                if (error) {
                    String message = jsonObject.has("message") ? jsonObject.get("message").getAsString() : "Unknown error";
                    Log.e(TAG, "Error in response: " + message);
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Process the data if there's no error
            JsonArray data = jsonObject.getAsJsonArray("results");
            if (data == null) {
                throw new Exception("Missing 'results' array in the response.");
            }
            ArrayList<String> coApplicantNames = new ArrayList<>();
            String applicantName = null;
            for (int i = 0; i < data.size(); i++) {
                JsonObject customer = data.get(i).getAsJsonObject();
                String firstName = customer.has("firstName") ? customer.get("firstName").getAsString() : "Unknown";
                String role = customer.has("role") ? customer.get("role").getAsString() : "unknown";
                if ("applicant".equals(role)) {
                    applicantName = firstName;
                    applicantExists = true;
                } else {
                    coApplicantNames.add(firstName);
                }
            }
            displayApplicantAndCoApplicants(applicantName, coApplicantNames);

        } catch (Exception e) {
            Log.e(TAG, "Failed to parse response", e);
            Toast.makeText(this, "Failed to parse response", Toast.LENGTH_SHORT).show();
        }
    }


    private void displayApplicantAndCoApplicants(String applicantName, ArrayList<String> coApplicantNames) {
        if (applicantName != null) {
            TextView titleName = new TextView(this);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(0, 10, 0, 8);
            titleName.setLayoutParams(titleParams);
            titleName.setTextColor(getResources().getColor(R.color.primary));
            titleName.setText("Applicant");
            titleName.setTextSize(18);

            TextView applicantNameView = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            nameParams.setMargins(0, 16, 0, 20);
            nameParams.height = 160;
            applicantNameView.setLayoutParams(nameParams);
            applicantNameView.setTextColor(Color.parseColor("#000000"));
            applicantNameView.setText(applicantName);
            applicantNameView.setBackgroundResource(R.drawable.edit_text_border);
            applicantNameView.setPadding(18, 12, 18, 12);
            applicantNameView.setTextSize(20);
            applicantNameView.setGravity(Gravity.CENTER_VERTICAL);

            coApplicantsLayout.addView(titleName);
            coApplicantsLayout.addView(applicantNameView);
        }

        int i = 1; // Start co-applicant index from 1
        for (String name : coApplicantNames) {
            TextView titleName = new TextView(this);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(0, 10, 0, 8);
            titleName.setLayoutParams(titleParams);
            titleName.setTextColor(getResources().getColor(R.color.primary));
            titleName.setText("Co-Applicant " + i);
            titleName.setTextSize(18);

            TextView applicant_Name = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            nameParams.setMargins(0, 16, 0, 20);
            nameParams.height = 160;
            applicant_Name.setLayoutParams(nameParams);
            applicant_Name.setTextColor(Color.parseColor("#000000"));
            applicant_Name.setText(name);
            applicant_Name.setBackgroundResource(R.drawable.edit_text_border);
            applicant_Name.setPadding(18, 12, 18, 12);
            applicant_Name.setTextSize(20);
            applicant_Name.setGravity(Gravity.CENTER_VERTICAL);

            coApplicantsLayout.addView(titleName);
            coApplicantsLayout.addView(applicant_Name);

            i++;
        }

        Toast.makeText(this, "Customer details displayed successfully", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Customer details displayed successfully");
    }
}
