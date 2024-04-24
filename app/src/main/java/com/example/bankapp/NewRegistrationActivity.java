package com.example.bankapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
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
public class NewRegistrationActivity extends AppCompatActivity {

    EditText firstName, lastName, phoneNumber, agentCode, branchCode, branchName, loanAmount;
    Spinner caseTag, productType, customerType;
    ImageView homeButton;
    TextView date, time;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registration);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneNumber = findViewById(R.id.phoneNumber);
        agentCode = findViewById(R.id.agentCode);
        branchCode = findViewById(R.id.branchCode);
        branchName = findViewById(R.id.branchName);
        loanAmount = findViewById(R.id.loanAmount);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        homeButton = findViewById(R.id.homeButton);

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
        ArrayAdapter<String> caseTagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.case_tag));
        caseTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        caseTagSpinner.setAdapter(caseTagAdapter);

        Spinner productSpinner = findViewById(R.id.productType);
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.product_types_array));
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSpinner.setAdapter(productAdapter);

        Spinner customerTypeSpinner = findViewById(R.id.customerType);
        ArrayAdapter<String> customerTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.customer_type));
        customerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerTypeSpinner.setAdapter(customerTypeAdapter);

        autofetchDate();
        autofetchTime();

        Button submitButton = findViewById(R.id.submit_button);
        Button btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (validateFields()) {
//                    makeHttpRequest();
//                }
                makeHttpRequest(accessToken);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autofetchDate() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        // Set the current date in the EditText
        date.setText(currentDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void autofetchTime() {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String currentTime = timeFormat.format(calendar.getTime());

        // Set the current time in the EditText
        time.setText(currentTime);
    }

//    private boolean validateFields() {
//        if (TextUtils.isEmpty(customerName.getText().toString().trim())) {
//            showToast("Please enter customer name");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())) {
//            showToast("Please enter phone number");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(agentCode.getText().toString().trim())) {
//            showToast("Please enter agent code");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(branchCode.getText().toString().trim())) {
//            showToast("Please enter branch code");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(branchName.getText().toString().trim())) {
//            showToast("Please enter branch name");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(loanAmount.getText().toString().trim())) {
//            showToast("Please enter loan amount");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(date.getText().toString().trim())) {
//            showToast("Please enter date");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(time.getText().toString().trim())) {
//            showToast("Please enter time");
//            return false;
//        }
//
//        return true;
//    }

    private void makeHttpRequest(String accessToken) {

        String url = BASE_URL + "api/v1/leads";

        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestBody formBody = new FormBody.Builder()
                        .add("first_name", firstName.getText().toString().trim())
                        .add("mobile_number", phoneNumber.getText().toString().trim())
                        .add("last_name", lastName.getText().toString().trim())
                        .add("email", "admin@gmail.com")
                        .add("agent_code", agentCode.getText().toString().trim())
                        .add("branch_code", branchCode.getText().toString().trim())
                        .add("branch_name", branchName.getText().toString().trim())
                        .add("loan_amount", loanAmount.getText().toString().trim())

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
                    String serverResponse = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (serverResponse.contains("Lead created successfully")) {
                                Toast.makeText(NewRegistrationActivity.this, "Lead creation successful", Toast.LENGTH_SHORT).show();
                                Intent mainIntent = new Intent(NewRegistrationActivity.this, KycActivity1.class);
                                startActivity(mainIntent);
                                finish();
                            } else {
                                Toast.makeText(NewRegistrationActivity.this, "Enter all the necessary details", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }





}