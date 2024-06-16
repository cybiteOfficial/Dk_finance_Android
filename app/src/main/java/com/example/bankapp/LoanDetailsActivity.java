package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DecimalFormat;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class LoanDetailsActivity extends AppCompatActivity {
    ImageView homeButton;
    SharedPreferences sharedPreferences;
    Spinner productType, customerType;
    EditText loanAmount, appliedTenure, appliedRoi,transaction_type;
    EditText app_rate_processing_fee, edit_change_amount_processing_fee, edit_tax_amount_processing_fee, edit_total_amount_processing_fee;
    EditText app_rate_valuation_charges, edit_change_valuation_charges, edit_tax_valuation_charges, edit_total_valuation_charges;
    EditText app_rate_legal_incidental_charges, edit_change_legal_incidental_charges, edit_tax_legal_incidental_charges, edit_total_legal_incidental_charges;
    EditText app_rate_stamp_duty_charges, edit_change_stamp_duty_charges, edit_tax_stamp_duty_charges, edit_total_stamp_duty_charges;
    EditText app_rate_rcu_charges, edit_change_rcu_charges, edit_tax_rcu_charges, edit_total_rcu_charges;
    EditText app_rate_stamping_charges, edit_change_stamping_charges, edit_tax_stamping_charges, edit_total_stamping_charges;
    Button submitButton;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_details);
        DecimalFormat formatter = new DecimalFormat("#,##,##0");
        loanAmount = findViewById(R.id.loanAmount);
        appliedRoi = findViewById(R.id.appliedRoi);
        appliedTenure = findViewById(R.id.appliedTenure);
        productType = findViewById(R.id.productType);
        customerType = findViewById(R.id.customerType);
        submitButton = findViewById(R.id.submit_button);
        homeButton = findViewById(R.id.homeButton);
        transaction_type = findViewById(R.id.transaction_type);
        // Inflate the Charges processing Layout
        app_rate_processing_fee = findViewById(R.id.section_processing_fees).findViewById(R.id.edit_applicable_rate);
        edit_change_amount_processing_fee = findViewById(R.id.section_processing_fees).findViewById(R.id.edit_change_amount);
        edit_tax_amount_processing_fee = findViewById(R.id.section_processing_fees).findViewById(R.id.edit_tax_amount);
        edit_total_amount_processing_fee = findViewById(R.id.section_processing_fees).findViewById(R.id.edit_total_amount);

        // Inflate the valuation charges layout
        app_rate_valuation_charges = findViewById(R.id.section_valuation_charges).findViewById(R.id.edit_applicable_rate);
        edit_change_valuation_charges = findViewById(R.id.section_valuation_charges).findViewById(R.id.edit_change_amount);
        edit_tax_valuation_charges = findViewById(R.id.section_valuation_charges).findViewById(R.id.edit_tax_amount);
        edit_total_valuation_charges = findViewById(R.id.section_valuation_charges).findViewById(R.id.edit_total_amount);

        // Inflate the legal incidental charges
        app_rate_legal_incidental_charges = findViewById(R.id.section_legal_incidental_charges).findViewById(R.id.edit_applicable_rate);
        edit_change_legal_incidental_charges = findViewById(R.id.section_legal_incidental_charges).findViewById(R.id.edit_change_amount);
        edit_tax_legal_incidental_charges = findViewById(R.id.section_legal_incidental_charges).findViewById(R.id.edit_tax_amount);
        edit_total_legal_incidental_charges = findViewById(R.id.section_legal_incidental_charges).findViewById(R.id.edit_total_amount);

        // Inflate the legal stamp duty charges
        app_rate_stamp_duty_charges = findViewById(R.id.section_stamp_duty_charges).findViewById(R.id.edit_applicable_rate);
        edit_change_stamp_duty_charges = findViewById(R.id.section_stamp_duty_charges).findViewById(R.id.edit_change_amount);
        edit_tax_stamp_duty_charges = findViewById(R.id.section_stamp_duty_charges).findViewById(R.id.edit_tax_amount);
        edit_total_stamp_duty_charges = findViewById(R.id.section_stamp_duty_charges).findViewById(R.id.edit_total_amount);

        // Inflate the rcu charges
        app_rate_rcu_charges = findViewById(R.id.section_rcu_charges).findViewById(R.id.edit_applicable_rate);
        edit_change_rcu_charges = findViewById(R.id.section_rcu_charges).findViewById(R.id.edit_change_amount);
        edit_tax_rcu_charges = findViewById(R.id.section_rcu_charges).findViewById(R.id.edit_tax_amount);
        edit_total_rcu_charges = findViewById(R.id.section_rcu_charges).findViewById(R.id.edit_total_amount);

        // Inflate the stamping charges
        app_rate_stamping_charges = findViewById(R.id.section_stamping_charges).findViewById(R.id.edit_applicable_rate);
        edit_change_stamping_charges = findViewById(R.id.section_stamping_charges).findViewById(R.id.edit_change_amount);
        edit_tax_stamping_charges = findViewById(R.id.section_stamping_charges).findViewById(R.id.edit_tax_amount);
        edit_total_stamping_charges = findViewById(R.id.section_stamping_charges).findViewById(R.id.edit_total_amount);

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
                    if (str.length() > 15) {
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

        // Setup Spinners
        setupSpinner(R.id.caseTag, R.array.case_tag);
        setupSpinner(R.id.productType, R.array.product_types_array);
        setupSpinner(R.id.customerType, R.array.customer_type);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButton.setEnabled(false);
                makeHttpRequest(accessToken, submitButton);
            }
        });
    }

    private void setupSpinner(int spinnerId, int arrayId) {
        Spinner spinner = findViewById(spinnerId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.sample_spinner_item, getResources().getStringArray(arrayId)) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private boolean validateFields() {
        if (TextUtils.isEmpty(loanAmount.getText().toString().trim())) {
            loanAmount.setError("Please enter Loan Amount");
            return false;
        }
        if (TextUtils.isEmpty(appliedRoi.getText().toString().trim())) {
            appliedRoi.setError("Please enter Loan Amount");
            return false;
        }
        if (TextUtils.isEmpty(appliedTenure.getText().toString().trim())) {
            appliedTenure.setError("Please enter Applied Tenure");
            return false;
        }
        if (TextUtils.isEmpty(app_rate_processing_fee.getText().toString().trim())) {
            app_rate_processing_fee.setError("Please enter Application rate");
            return false;
        }
        if (TextUtils.isEmpty(edit_change_amount_processing_fee.getText().toString().trim())) {
            edit_change_amount_processing_fee.setError("Please enter Change Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_tax_amount_processing_fee.getText().toString().trim())) {
            edit_tax_amount_processing_fee.setError("Please enter Tax Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_total_amount_processing_fee.getText().toString().trim())) {
            edit_total_amount_processing_fee.setError("Please enter Total Amount");
            return false;
        }

        if (TextUtils.isEmpty(loanAmount.getText().toString().trim())) {
            loanAmount.setError("Please enter loan amount");
            return false;
        }

        if (TextUtils.isEmpty(app_rate_valuation_charges.getText().toString().trim())) {
            app_rate_valuation_charges.setError("Please enter Application rate");
            return false;
        }
        if (TextUtils.isEmpty(edit_change_valuation_charges.getText().toString().trim())) {
            edit_change_valuation_charges.setError("Please enter Change Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_tax_valuation_charges.getText().toString().trim())) {
            edit_tax_valuation_charges.setError("Please enter Tax Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_total_valuation_charges.getText().toString().trim())) {
            edit_total_valuation_charges.setError("Please enter Total Amount");
            return false;
        }

        if (TextUtils.isEmpty(app_rate_legal_incidental_charges.getText().toString().trim())) {
            app_rate_legal_incidental_charges.setError("Please enter Application rate");
            return false;
        }
        if (TextUtils.isEmpty(edit_change_legal_incidental_charges.getText().toString().trim())) {
            edit_change_legal_incidental_charges.setError("Please enter Change Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_tax_legal_incidental_charges.getText().toString().trim())) {
            edit_tax_legal_incidental_charges.setError("Please enter Tax Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_total_legal_incidental_charges.getText().toString().trim())) {
            edit_total_legal_incidental_charges.setError("Please enter Total Amount");
            return false;
        }

        if (TextUtils.isEmpty(app_rate_stamp_duty_charges.getText().toString().trim())) {
            app_rate_stamp_duty_charges.setError("Please enter Application rate");
            return false;
        }
        if (TextUtils.isEmpty(edit_change_stamp_duty_charges.getText().toString().trim())) {
            edit_change_stamp_duty_charges.setError("Please enter Change Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_tax_stamp_duty_charges.getText().toString().trim())) {
            edit_tax_stamp_duty_charges.setError("Please enter Tax Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_total_stamp_duty_charges.getText().toString().trim())) {
            edit_total_stamp_duty_charges.setError("Please enter Total Amount");
            return false;
        }

        if (TextUtils.isEmpty(app_rate_rcu_charges.getText().toString().trim())) {
            app_rate_rcu_charges.setError("Please enter Application rate");
            return false;
        }
        if (TextUtils.isEmpty(edit_change_rcu_charges.getText().toString().trim())) {
            edit_change_rcu_charges.setError("Please enter Change Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_tax_rcu_charges.getText().toString().trim())) {
            edit_tax_rcu_charges.setError("Please enter Tax Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_total_rcu_charges.getText().toString().trim())) {
            edit_total_rcu_charges.setError("Please enter Total Amount");
            return false;
        }

        if (TextUtils.isEmpty(app_rate_stamping_charges.getText().toString().trim())) {
            app_rate_stamping_charges.setError("Please enter Application rate");
            return false;
        }
        if (TextUtils.isEmpty(edit_change_stamping_charges.getText().toString().trim())) {
            edit_change_stamping_charges.setError("Please enter Change Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_tax_stamping_charges.getText().toString().trim())) {
            edit_tax_stamping_charges.setError("Please enter Tax Amount");
            return false;
        }
        if (TextUtils.isEmpty(edit_total_stamping_charges.getText().toString().trim())) {
            edit_total_stamping_charges.setError("Please enter Total Amount");
            return false;
        }

        return true;
    }


    private void makeHttpRequest(String accessToken, final Button submitButton) {
        String url = BASE_URL + "api/v1/loan_details";

        // Collect all the charges data into JSON objects
        JSONObject processingFees = new JSONObject();
        JSONObject valuationCharges = new JSONObject();
        JSONObject legalIncidentalCharges = new JSONObject();
        JSONObject stampDutyCharges = new JSONObject();
        JSONObject rcuCharges = new JSONObject();
        JSONObject stampingCharges = new JSONObject();

        try {
            processingFees.put("applicable_rate", app_rate_processing_fee.getText().toString().trim());
            processingFees.put("change_amount", edit_change_amount_processing_fee.getText().toString());
            processingFees.put("tax_amount", edit_tax_amount_processing_fee.getText().toString());
            processingFees.put("total_amount", edit_total_amount_processing_fee.getText().toString());

            valuationCharges.put("applicable_rate", app_rate_valuation_charges.getText().toString().trim());
            valuationCharges.put("change_amount", edit_change_valuation_charges.getText().toString());
            valuationCharges.put("tax_amount", edit_tax_valuation_charges.getText().toString());
            valuationCharges.put("total_amount", edit_total_valuation_charges.getText().toString());

            legalIncidentalCharges.put("applicable_rate", app_rate_legal_incidental_charges.getText().toString().trim());
            legalIncidentalCharges.put("change_amount", edit_change_legal_incidental_charges.getText().toString());
            legalIncidentalCharges.put("tax_amount", edit_tax_legal_incidental_charges.getText().toString());
            legalIncidentalCharges.put("total_amount", edit_total_legal_incidental_charges.getText().toString());

            stampDutyCharges.put("applicable_rate", app_rate_stamp_duty_charges.getText().toString().trim());
            stampDutyCharges.put("change_amount", edit_change_stamp_duty_charges.getText().toString());
            stampDutyCharges.put("tax_amount", edit_tax_stamp_duty_charges.getText().toString());
            stampDutyCharges.put("total_amount", edit_total_stamp_duty_charges.getText().toString());

            rcuCharges.put("applicable_rate", app_rate_rcu_charges.getText().toString().trim());
            rcuCharges.put("change_amount", edit_change_rcu_charges.getText().toString());
            rcuCharges.put("tax_amount", edit_tax_rcu_charges.getText().toString());
            rcuCharges.put("total_amount", edit_total_rcu_charges.getText().toString());

            stampingCharges.put("applicable_rate", app_rate_stamping_charges.getText().toString().trim());
            stampingCharges.put("change_amount", edit_change_stamping_charges.getText().toString());
            stampingCharges.put("tax_amount", edit_tax_stamping_charges.getText().toString());
            stampingCharges.put("total_amount", edit_total_stamping_charges.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Run the network operation in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the loan amount as a string without commas
                String loanAmountText = loanAmount.getText().toString().replaceAll(",", "");

                // Get the selected product type from the spinner
                String selectedProductType = productType.getSelectedItem().toString();

                // Get the selected customer type from the spinner
                String selectedCustomerType = customerType.getSelectedItem().toString();

                // Build the form body
                FormBody.Builder formBodyBuilder = new FormBody.Builder()
                        .add("applied_roi", appliedRoi.getText().toString().trim())
                        .add("applied_tenure", appliedTenure.getText().toString())
                        .add("transacton_type", transaction_type.getText().toString())
                        .add("description", "")
                        .add("comment", "")
                        .add("applied_loan_amount", loanAmountText)
                        .add("productType", selectedProductType)
                        .add("customerType", selectedCustomerType)
                        .add("processing_fees", processingFees.toString())
                        .add("valuation_charges", valuationCharges.toString())
                        .add("legal_and_incidental_fees", legalIncidentalCharges.toString())
                        .add("stamp_duty_applicable_rate", stampDutyCharges.toString())
                        .add("rcu_charges_applicable_rate", rcuCharges.toString())
                        .add("stamping_expenses_applicable_rate", stampingCharges.toString());

                RequestBody formBody = formBodyBuilder.build();

                // Create the request
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                // Create the OkHttpClient and make the call
                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                try {
                    Response response = call.execute();
                    assert response.body() != null;
                    String serverResponse = response.body().string();

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(serverResponse);
                    boolean isError = jsonResponse.getBoolean("error");
                    if (!isError) {
                        // Get the lead data
                        JSONObject leadData = jsonResponse.getJSONObject("data");
                        String leadId = leadData.getString("lead_id");

                        // Pass lead ID to KycActivity1
                        Intent mainIntent = new Intent(LoanDetailsActivity.this, Document.class);
                        mainIntent.putExtra("lead_id", leadId);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Loan Details Added successfully");
                                startActivity(mainIntent);
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                submitButton.setEnabled(true);
                                showToast("Failed to create Loan Details");
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submitButton.setEnabled(true);
                            showToast("An error occurred");
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