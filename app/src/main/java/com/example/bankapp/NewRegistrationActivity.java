package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.DecimalFormat;
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
    TextView date, agentCode, branchCode, timeTextView;


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
        timeTextView = findViewById(R.id.time);
        homeButton = findViewById(R.id.homeButton);


        // Define a DecimalFormat to format the number
        DecimalFormat formatter = new DecimalFormat("#,##,##0");

// Set text change listener to format the number as user types
        loanAmount.addTextChangedListener(new TextWatcher() {
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
                loanAmount.removeTextChangedListener(this);
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
                    loanAmount.setText(formattedNumber);
                    loanAmount.setSelection(formattedNumber.length());
                } catch (NumberFormatException e) {
                    // Not a valid number
                }
                loanAmount.addTextChangedListener(this);
            }
        });
//        agentCode.setText(userID);

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move the cursor to the end of the text in the EditText
                phoneNumber.setSelection(phoneNumber.getText().length());
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
        ArrayAdapter<String> caseTagAdapter = new ArrayAdapter<String>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.case_tag)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }
        };
        caseTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caseTagSpinner.setAdapter(caseTagAdapter);


        Spinner productSpinner = findViewById(R.id.productType);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<String>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.product_types_array)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }
        };
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);

        Spinner customerTypeSpinner = findViewById(R.id.customerType);
        ArrayAdapter<String> customerTypeAdapter = new ArrayAdapter<String>(this, R.layout.sample_spinner_item, getResources().getStringArray(R.array.customer_type)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        customerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerTypeSpinner.setAdapter(customerTypeAdapter);

        autofetchDate();
        autoTime();

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
    private void autoTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(calendar.getTime());
        timeTextView.setText(currentTime);
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

        if (phoneNumber.getText().toString().trim().length() < 10) {
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

                // Get the loan amount as a string without commas
                String loanAmountText = loanAmount.getText().toString().replaceAll(",", "");

                // Parse the loan amount string to an integer
//                int loanAmountInt = 0;
//                try {
//                    loanAmountInt = Integer.parseInt(loanAmountText);
//                } catch (NumberFormatException e) {
//                    // Handle the case where the loan amount is not a valid integer
//                    e.printStackTrace(); // Or show an error message
//                }

                FormBody.Builder formBodyBuilder = new FormBody.Builder()
                        .add("first_name", firstName.getText().toString().trim())
                        .add("mobile_number", phoneNumber.getText().toString())
                        .add("last_name", lastName.getText().toString().trim())
                        .add("email", "")
//                        .add("agent_code", agentCode.getText().toString().trim())
                        .add("loan_amount", loanAmountText)
                        .add("agent_code", "DKFE001")
                        .add("product_type", "normal")
                        .add("case_tag", "normal")
                        .add("customer_type", "home_loan")
                        .add("comment", "testing");

                // Check if loan amount is provided
//                String loanAmountText = loanAmount.getText().toString().trim();
//                if (!TextUtils.isEmpty(loanAmountText)) {
//                    formBodyBuilder.add("loan_amount", loanAmountText);
//                }

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
                        mainIntent.putExtra("phoneNumber", "+91 " + phoneNumber.getText().toString().trim());
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