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
                startActivity(intent);
            }
        });
    }

    private void fetchCustomerData(String applicationId) {
        String url = BASE_URL + "api/v1/customers?application_id=" + applicationId + "&is_all=True";

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
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d(TAG, "Response received: " + responseData);
                    runOnUiThread(() -> handleResponse(responseData));
                } else {
                    Log.e(TAG, "Failed to fetch customer data: " + response.message());
                    runOnUiThread(() -> Toast.makeText(AddCustomerActivity.this, "Failed to fetch customer data: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void handleResponse(String responseData) {
        try {
            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
            if (!jsonObject.get("error").getAsBoolean()) {
                JsonArray data = jsonObject.getAsJsonArray("data");
                ArrayList<String> coApplicantNames = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    JsonObject customer = data.get(i).getAsJsonObject();
                    String firstName = customer.get("firstName").getAsString();
                    coApplicantNames.add(firstName);
                }
                displayCoApplicantNames(coApplicantNames);
            } else {
                String message = jsonObject.get("message").getAsString();
                Log.e(TAG, "Error in response: " + message);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse response", e);
            Toast.makeText(this, "Failed to parse response", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCoApplicantNames(ArrayList<String> coApplicantNames) {
        int i = 0;

        for (String name : coApplicantNames) {
            TextView titleName = new TextView(this);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            titleParams.setMargins(0, 10, 0, 8);
            titleName.setLayoutParams(titleParams);
            titleName.setTextColor(getResources().getColor(R.color.primary));
            if (i == 0) {
                titleName.setText("Applicant Name");
            } else {
                titleName.setText("Co-Applicant " + i + " Name");
            }
            titleName.setTextSize(18);

            TextView applicantName = new TextView(this);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            nameParams.setMargins(0, 16, 0, 20);
            nameParams.height = 160;
            applicantName.setLayoutParams(nameParams);
            applicantName.setTextColor(Color.parseColor("#000000"));
            applicantName.setText(name);
            applicantName.setBackgroundResource(R.drawable.edit_text_border);
            applicantName.setPadding(18, 12, 18, 12);
            applicantName.setTextSize(20);
            applicantName.setGravity(Gravity.CENTER_VERTICAL);

            coApplicantsLayout.addView(titleName);
            coApplicantsLayout.addView(applicantName);

            i++;
        }

        Toast.makeText(this, "Co-applicant names displayed successfully", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Co-applicant names displayed successfully");
    }
}
