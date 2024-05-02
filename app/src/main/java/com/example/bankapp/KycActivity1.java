package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
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
    String kycId;

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

        fetchKycId(accessToken, leadID, new KycIdCallback() {
            @Override
            public void onKycIdFetched(String kycId) {
                KycActivity1.this.kycId = kycId; // Store kycId globally
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("KycActivity1", "Error fetching kyc id: " + throwable.getMessage());
                // Handle error
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    makeHttpRequest(accessToken, leadID, kycId); // Use stored kycId
                }
            }
        });

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

    private void fetchKycId(String accessToken, String leadID, KycIdCallback callback) {
        String url = BASE_URL + "api/v1/kyc?lead_id=" + leadID;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String getKycIdResponse = response.body().string();
                    Log.d("getKycIdResponse", getKycIdResponse);
                    try {
                        JSONObject jsonResponse = new JSONObject(getKycIdResponse);
                        Log.d("jsonResponse", jsonResponse.toString());
                        boolean isError = jsonResponse.getBoolean("error");
                        if (!isError) {
                            JSONArray KYCdata = jsonResponse.getJSONArray("data");
                            Log.d("array", KYCdata.toString());
                            String kycId = KYCdata.getJSONObject(0).getString("uuid");
                            Log.d("kycID", kycId);
                            callback.onKycIdFetched(kycId);
                        } else {
                            callback.onError(new Exception("Error occurred"));
                        }
                    } catch (JSONException e) {
                        callback.onError(e);
                    }
                } else {
                    callback.onError(new IOException("Unexpected response code " + response));
                }
            }
        });
    }

    private boolean validateFields() {
        String emailText = emailId.getText().toString().trim();

        if (!TextUtils.isEmpty(emailText)) {
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

    private void makeHttpRequest(String accessToken, String leadID, String kycId) {
        Log.d("in make http", kycId);
        String url = BASE_URL + "api/v1/kyc?kyc_id=" + kycId;

        String phoneNumberText = phoneNumber.getText().toString().trim();
        phoneNumberText = phoneNumberText.substring(4);

        RequestBody formBody = new FormBody.Builder()
                .add("first_name", firstName.getText().toString().trim())
                .add("last_name", lastName.getText().toString().trim())
                .add("mobile_number", "+91" + phoneNumberText)
                .add("email", emailId.getText().toString().trim())
//                .add("lead_id", leadID)
                .add("kyc_verified", "true")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .put(formBody)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String serverResponse = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (serverResponse.contains("false")) {
                                Toast.makeText(KycActivity1.this, "KYC Step 1 Successful", Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(KycActivity1.this, KycActivity2.class);
                                mainIntent.putExtra("phoneNumber", phoneNumber.getText().toString());
                                mainIntent.putExtra("leadId", leadID);
                                mainIntent.putExtra("kyc_id", kycId);
                                startActivity(mainIntent);
                            } else {
                                Toast.makeText(KycActivity1.this, "An error occurred", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    interface KycIdCallback {
        void onKycIdFetched(String kycId);
        void onError(Throwable throwable);
    }
}



