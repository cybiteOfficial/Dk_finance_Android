package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KycActivity1 extends AppCompatActivity {

    TextView firstName, lastName, phoneNumber;
    TextView emailId;
    Button submitButton;
    ImageView homeButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_1);

        // Retrieve the values from Intent
        String fName = getIntent().getStringExtra("firstName");
        String lName = getIntent().getStringExtra("lastName");
        String phone = getIntent().getStringExtra("phoneNumber");
        String leadID = getIntent().getStringExtra("leadId");

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);

        firstName.setText(fName);
        lastName.setText(lName);
        phoneNumber.setText(phone);

        // Adding underline to the "Save" button text
        TextView btnSave = findViewById(R.id.btn_save);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        emailId = findViewById(R.id.emailId);
        submitButton = findViewById(R.id.btn_submit);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate fields
                if (validateFields()) {
                    // Save data
                    saveData();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    makeHttpRequest(accessToken, leadID);
                }
            }
        });

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to DashboardActivity
                Intent mainIntent = new Intent(KycActivity1.this, DashboardActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    private boolean validateFields() {
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String phoneNumberText = phoneNumber.getText().toString().trim();
        String emailText = emailId.getText().toString().trim();

        if (TextUtils.isEmpty(firstNameText)) {
            firstName.setError("First name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastNameText)) {
            lastName.setError("Last name is required");
            return false;
        }

        if (TextUtils.isEmpty(phoneNumberText)) {
            phoneNumber.setError("Phone number is required");
            return false;
        }

        if (TextUtils.isEmpty(emailText)) {
            emailId.setError("Email is required");

            if (!isValidEmail(emailText)) {
                emailId.setError("Enter a valid email address");
                return false;
            }
        }

        return true;
    }

    private void saveData() {
        // You can implement the logic to save data here
        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void makeHttpRequest(String accessToken, final String leadID) {

        String url = BASE_URL + "api/v1/kyc";

        new Thread(new Runnable() {
            @Override
            public void run() {

                String phoneNumberText = phoneNumber.getText().toString().trim();
                phoneNumberText = phoneNumberText.substring(4);

                RequestBody formBody = new FormBody.Builder()
                        .add("first_name", firstName.getText().toString().trim())
                        .add("last_name", lastName.getText().toString().trim())
                        .add("mobile_number", "+91" + phoneNumberText)
                        .add("email", emailId.getText().toString().trim())
                        .add("lead_id", leadID)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                Response response = null;
                try {
                    response = call.execute();
                    final String serverResponse = response.body().string();
                    String finalPhoneNumberText = phoneNumberText;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display the response in a Toast message
                            if (serverResponse.contains("false")) {
                                Toast.makeText(KycActivity1.this, "KYC Step 1 Successful", Toast.LENGTH_SHORT).show();
                                // Extracting UUID from the server response
                                String uuid = extractUUID(serverResponse);
                                // Passing UUID to KycActivity2
                                Intent mainIntent = new Intent(KycActivity1.this, KycActivity2.class);
                                mainIntent.putExtra("phoneNumber", finalPhoneNumberText);
                                mainIntent.putExtra("leadId", leadID);
                                mainIntent.putExtra("uuid", uuid);
                                startActivity(mainIntent);
                            } else {
                                Toast.makeText(KycActivity1.this, serverResponse, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Method to extract UUID from server response
    private String extractUUID(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject data = jsonResponse.getJSONObject("data");
            return data.getString("uuid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
