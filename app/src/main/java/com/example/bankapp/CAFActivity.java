package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.text.DecimalFormat;
import android.text.TextWatcher;
import android.content.SharedPreferences;
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

public class CAFActivity extends AppCompatActivity {
    EditText tentativeAmount,pdID,pdAddress,location,description;
    Button submitbutton;
    ImageView homeButton;
    SharedPreferences sharedPreferences;

    // fetch application_id from previous activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_caf);
        submitbutton = findViewById(R.id.submitbutton);
        tentativeAmount = findViewById(R.id.tentativeAmount);
        pdID = findViewById(R.id.pdID);
        pdAddress = findViewById(R.id.pdAddress);
        location = findViewById(R.id.location);
        description = findViewById(R.id.description);
        homeButton = findViewById(R.id.homeButton);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");
        DecimalFormat formatter = new DecimalFormat("#,##,##0");

        Intent intent = getIntent();
        String application_id = intent.getStringExtra("application_id");

        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Disable the button to prevent multiple submissions
                    submitbutton.setEnabled(false);
                    makeHttpRequest(accessToken, submitbutton, application_id);
                }
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to dashboard activity
                Intent intent = new Intent(v.getContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });
        tentativeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                tentativeAmount.removeTextChangedListener(this);
                try {
                    String str = s.toString().replaceAll(",", "");
                    if(str.length() > 15) {
                        // If the length exceeds 15, trim it
                        str = str.substring(0, 15);
                    }
                    long number = Long.parseLong(str);
                    // Format the number
                    String formattedNumber = formatter.format(number);
                    // Set the formatted number back to the EditText
                    tentativeAmount.setText(formattedNumber);
                    tentativeAmount.setSelection(formattedNumber.length());
                } catch (NumberFormatException e) {
                    // Not a valid number
                }
                tentativeAmount.addTextChangedListener(this);
            }
        });
    }
    private boolean validateFields() {
        if (TextUtils.isEmpty(tentativeAmount.getText().toString().trim())) {
            tentativeAmount.setError("Please enter Tentative Amount");
            return false;
        }

        if (TextUtils.isEmpty(pdID.getText().toString().trim())) {
            pdID.setError("Please enter PD (with applicant name with ID)");
            return false;
        }


        if(TextUtils.isEmpty(pdAddress.getText().toString().trim())){
            pdAddress.setError("Please enter Place of PD Address");
            return false;
        }
        if(TextUtils.isEmpty(location.getText().toString().trim())){
            location.setError("Please enter location");
            return false;
        }
        if(TextUtils.isEmpty(description.getText().toString().trim())){
            description.setError("Please enter description");
            return false;
        }

        return true;
    }
    private void makeHttpRequest(String accessToken, final Button submitButton, String application_id) {
        String url = BASE_URL + "api/v1/caf_detail";

        new Thread(new Runnable() {
            @Override
            public void run() {

                // Get the loan amount as a string without commas
                String tentativeAmountText = tentativeAmount.getText().toString().replaceAll(",", "");


                FormBody.Builder formBodyBuilder = new FormBody.Builder()
                        .add("tentative_amt", tentativeAmountText)
                        .add("pdWith", pdID.getText().toString())
                        .add("placeOfPdAddress", pdAddress.getText().toString().trim())
                        .add("location", location.getText().toString().trim())
                        .add("description", description.getText().toString().trim())
                        .add("extra_data", "null")
                        .add("applicant_id", application_id);

                RequestBody formBody = formBodyBuilder.build();

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
                    String serverResponse = response.body().string();
                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(serverResponse);
                    boolean isError = jsonResponse.getBoolean("error");
                    if (!isError) {
                        // Get the lead data
                        JSONObject leadData = jsonResponse.getJSONObject("data");
                        String leadId = leadData.getString("lead_id");

                        // Pass lead ID to KycActivity1
                        Intent mainIntent = new Intent(CAFActivity.this, DashboardActivity.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Data Uploaded Successfully"); // Fixed toast message
                            }
                        });
                        startActivity(mainIntent);
                        finish();
                    } else {
                        // Enable the button again
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                submitButton.setEnabled(true);
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    // Enable the button again in case of an error
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submitButton.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}