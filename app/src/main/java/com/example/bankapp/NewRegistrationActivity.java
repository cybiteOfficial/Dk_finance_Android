package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class NewRegistrationActivity extends AppCompatActivity {

    EditText firstName, lastName, phoneNumber, branchName, loanAmount;
    Spinner caseTag, productType, customerType;
    ImageView homeButton;
    TextView date, agentCode, branchCode;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration);

        String userID = getIntent().getStringExtra("userId");

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        agentCode = findViewById(R.id.agentCode);
        loanAmount = findViewById(R.id.loanAmount);
        date = findViewById(R.id.date);
        homeButton = findViewById(R.id.homeButton);


//        agentCode.setText(userID);

        phoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Get the cursor position
                int cursorPosition = phoneNumber.getSelectionStart();

                // If the cursor is positioned before the country code, move it after the country code
                if (cursorPosition < 4) {
                    // Move the cursor to the end of the country code
                    phoneNumber.setSelection(4);
                    return true; // Consume the key event
                }

                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    if (cursorPosition <= 4) {
                        // Move the cursor to the end of the country code
                        phoneNumber.setSelection(4);
                        return true; // Consume the key event
                    }
                }

                return false; // Let the system handle the key event
            }
        });



        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to dashboard activity
                Intent intent = new Intent(v.getContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        productType = findViewById(R.id.productType);
        customerType = findViewById(R.id.customerType);

        Spinner caseTagSpinner = findViewById(R.id.caseTag);
        ArrayAdapter<String> caseTagAdapter = new ArrayAdapter<>(this,R.layout.sample_spinner_item, getResources().getStringArray(R.array.case_tag));
        caseTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caseTagSpinner.setAdapter(caseTagAdapter);

        Spinner productSpinner = findViewById(R.id.productType);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this,R.layout.sample_spinner_item, getResources().getStringArray(R.array.product_types_array));
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);

        Spinner customerTypeSpinner = findViewById(R.id.customerType);
        ArrayAdapter<String> customerTypeAdapter = new ArrayAdapter<>(this,R.layout.sample_spinner_item, getResources().getStringArray(R.array.customer_type));
        customerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerTypeSpinner.setAdapter(customerTypeAdapter);

        autofetchDate();

        Button submitButton = findViewById(R.id.submit_button);
        Button btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Disable the button to prevent multiple submissions
                    submitButton.setEnabled(false);
                    makeHttpRequest(accessToken, submitButton);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autofetchDate() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        // Set the current date in the TextView
        date.setText(currentDate);
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(firstName.getText().toString().trim())) {
            firstName.setError("Please enter first name");
            return false;
        }

        if (TextUtils.isEmpty(lastName.getText().toString().trim())) {
            lastName.setError("Please enter last name");
            return false;
        }

        if (phoneNumber.getText().toString().trim().length() < 14) {
            phoneNumber.setError("Please enter a valid phone number");
            return false;
        }

        if(TextUtils.isEmpty(loanAmount.getText().toString().trim())){
            loanAmount.setError("Please enter loan amount");
            return false;
        }

        return true;
    }

    private void makeHttpRequest(String accessToken, final Button submitButton) {
        String url = BASE_URL + "api/v1/leads";

        new Thread(new Runnable() {
            @Override
            public void run() {
                String phoneNumberText = phoneNumber.getText().toString().trim();
                phoneNumberText = phoneNumberText.substring(4);

                FormBody.Builder formBodyBuilder = new FormBody.Builder()
                        .add("first_name", firstName.getText().toString().trim())
                        .add("mobile_number", phoneNumberText)
                        .add("last_name", lastName.getText().toString().trim())
                        .add("email", "admin@gmail.com")
                        .add("agent_code", agentCode.getText().toString().trim())
                        .add("loan_amount", loanAmount.getText().toString().trim())
                        .add("product_type", "normal")
                        .add("case_tag", "normal")
                        .add("customer_type", "home_loan")
                        .add("comment", "testing");

                // Check if loan amount is provided
                String loanAmountText = loanAmount.getText().toString().trim();
                if (!TextUtils.isEmpty(loanAmountText)) {
                    formBodyBuilder.add("loan_amount", loanAmountText);
                }

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
                        Intent mainIntent = new Intent(NewRegistrationActivity.this, KycActivity1.class);
                        mainIntent.putExtra("firstName", firstName.getText().toString().trim());
                        mainIntent.putExtra("lastName", lastName.getText().toString().trim());
                        mainIntent.putExtra("phoneNumber", phoneNumber.getText().toString().trim());
                        mainIntent.putExtra("leadId", leadId); // Pass lead ID as extra
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Lead created successfully"); // Fixed toast message
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