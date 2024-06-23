package com.example.bankapp;

import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
    Spinner productType, customerType,caseTag,transaction_type;
    EditText loanAmount, appliedTenure, appliedRoi;
    EditText app_rate_processing_fee, edit_change_amount_processing_fee, edit_tax_amount_processing_fee, edit_total_amount_processing_fee;
    EditText app_rate_valuation_charges, edit_change_valuation_charges, edit_tax_valuation_charges, edit_total_valuation_charges;
    EditText app_rate_legal_incidental_charges, edit_change_legal_incidental_charges, edit_tax_legal_incidental_charges, edit_total_legal_incidental_charges;
    EditText app_rate_stamp_duty_charges, edit_change_stamp_duty_charges, edit_tax_stamp_duty_charges, edit_total_stamp_duty_charges;
    EditText app_rate_rcu_charges, edit_change_rcu_charges, edit_tax_rcu_charges, edit_total_rcu_charges;
    EditText app_rate_stamping_charges, edit_change_stamping_charges, edit_tax_stamping_charges, edit_total_stamping_charges;
    Button submitButton;
    private static final String TAG = "LoanActivity";

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting LoanActivity");
        setContentView(R.layout.activity_loan_details);
        DecimalFormat formatter = new DecimalFormat("#,##,##0");
        loanAmount = findViewById(R.id.loanAmount);
        appliedRoi = findViewById(R.id.appliedRoi);
        appliedTenure = findViewById(R.id.appliedTenure);
        productType = findViewById(R.id.productType);
        caseTag = findViewById(R.id.caseTag);
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

        // Get the application ID from the intent
        String application_id = getIntent().getStringExtra("application_id");
        Log.d(TAG, "onCreate: application_id = " + application_id);

        TextView applicationIdTextView = findViewById(R.id.applicationID);
        applicationIdTextView.setText(application_id);

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
        setupSpinner(R.id.transaction_type,R.array.transaction_type);

        String selectedProductType = productType.getSelectedItem().toString().toLowerCase();
        String selectedCaseType = caseTag.getSelectedItem().toString().toLowerCase();
        String selectedCustomerType = customerType.getSelectedItem().toString().toLowerCase();


        checkIfDataExists(application_id, accessToken);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Submit button clicked");
                if (validateFields()) {
                    Log.d(TAG, "onClick: Fields validated successfully");
                    // Disable the button to prevent multiple submissions
                    submitButton.setEnabled(false);
                    makeHttpRequest(accessToken, submitButton, application_id);
                } else {
                    Log.d(TAG, "onClick: Field validation failed");
                }
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


    private void makeHttpRequest(String accessToken, final Button submitButton, String application_id) {
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
                String selectedProductType = productType.getSelectedItem().toString().toLowerCase();
                String selectedTransactionType = transaction_type.getSelectedItem().toString().toLowerCase();

                // Build the form body
                FormBody.Builder formBodyBuilder = new FormBody.Builder()
                        .add("applicant_id", application_id)
                        .add("applied_ROI", appliedRoi.getText().toString().trim())
                        .add("applied_tenure", appliedTenure.getText().toString())
                        .add("transaction_type", "purchase")
                        .add("case_tag","normal")
                        .add("description", "")
                        .add("comment", "")
                        .add("applied_loan_amount", loanAmountText)
                        .add("product_type", selectedProductType)
                        .add("processing_fees", processingFees.toString())
                        .add("valuation_charges", valuationCharges.toString())
                        .add("legal_and_incidental_fee", legalIncidentalCharges.toString())
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

                    // log response
                    Log.i("LoanDetailsActivity", serverResponse);

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(serverResponse);
                    boolean isError = jsonResponse.getBoolean("error");
                    if (!isError) {
                        // Get the lead data
                        JSONObject data = jsonResponse.getJSONObject("data");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Loan Details Added successfully");
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

    // Get and Checked Data
    private void checkIfDataExists(String application_id, String accessToken) {
        String url = BASE_URL + "api/v1/loan_details?application_id=" + application_id;
        Log.d(TAG, "checkIfDataExists: URL = " + url);

        new Thread(() -> {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String serverResponse = response.body().string();
                Log.d(TAG, "run: Server response = " + serverResponse);

                JSONObject jsonResponse = new JSONObject(serverResponse);

                boolean isError = jsonResponse.getBoolean("error");
                if (!isError) {
                    JSONArray dataArray = jsonResponse.getJSONArray("data");
                    if (dataArray.length() > 0) {
                        JSONObject data = dataArray.getJSONObject(0); // Get the first object in the array
                        runOnUiThread(() -> displayData(data));
                    } else {
                        runOnUiThread(() -> showToast("No data found"));
                    }
                } else {
                    runOnUiThread(() -> {
                        try {
                            showToast("Error: " + jsonResponse.getString("message"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (IOException | JSONException e) {
                Log.e(TAG, "run: Exception occurred", e);
            }
        }).start();
    }

    private void displayData(JSONObject data) {
        try {
            productType.setSelection(getSpinnerPosition(productType, data.getString("product_type")));
            transaction_type.setSelection(getSpinnerPosition(transaction_type, data.getString("transaction_type")));
            caseTag.setSelection(getSpinnerPosition(caseTag, data.getString("case_tag")));
            loanAmount.setText(data.getString("applied_loan_amount"));
            appliedTenure.setText(String.valueOf(data.getInt("applied_tenure")));
            appliedRoi.setText(data.getString("applied_ROI"));

            // Processing Fees
            JSONObject processingFees = data.getJSONObject("processing_fees");
            edit_tax_amount_processing_fee.setText(processingFees.getString("tax_amount"));
            edit_total_amount_processing_fee.setText(processingFees.getString("total_amount"));
            edit_change_amount_processing_fee.setText(processingFees.getString("change_amount"));
            app_rate_processing_fee.setText(processingFees.getString("applicable_rate"));

            // Valuation Charges
            JSONObject valuationCharges = data.getJSONObject("valuation_charges");
            edit_tax_valuation_charges.setText(valuationCharges.getString("tax_amount"));
            edit_total_valuation_charges.setText(valuationCharges.getString("total_amount"));
            edit_change_valuation_charges.setText(valuationCharges.getString("change_amount"));
            app_rate_valuation_charges.setText(valuationCharges.getString("applicable_rate"));

            // Legal and Incidental Fee
            JSONObject legalIncidentalFee = data.getJSONObject("legal_and_incidental_fee");
            edit_tax_legal_incidental_charges.setText(legalIncidentalFee.getString("tax_amount"));
            edit_total_legal_incidental_charges.setText(legalIncidentalFee.getString("total_amount"));
            edit_change_legal_incidental_charges.setText(legalIncidentalFee.getString("change_amount"));
            app_rate_legal_incidental_charges.setText(legalIncidentalFee.getString("applicable_rate"));

            // Stamp Duty Applicable Rate
            JSONObject stampDutyApplicableRate = data.getJSONObject("stamp_duty_applicable_rate");
            edit_tax_stamp_duty_charges.setText(stampDutyApplicableRate.getString("tax_amount"));
            edit_total_stamp_duty_charges.setText(stampDutyApplicableRate.getString("total_amount"));
            edit_change_stamp_duty_charges.setText(stampDutyApplicableRate.getString("change_amount"));
            app_rate_stamp_duty_charges.setText(stampDutyApplicableRate.getString("applicable_rate"));

            // RCU Charges Applicable Rate
            JSONObject rcuChargesApplicableRate = data.getJSONObject("rcu_charges_applicable_rate");
            edit_tax_rcu_charges.setText(rcuChargesApplicableRate.getString("tax_amount"));
            edit_total_rcu_charges.setText(rcuChargesApplicableRate.getString("total_amount"));
            edit_change_rcu_charges.setText(rcuChargesApplicableRate.getString("change_amount"));
            app_rate_rcu_charges.setText(rcuChargesApplicableRate.getString("applicable_rate"));

            // Stamping Expenses Applicable Rate
            JSONObject stampingExpensesApplicableRate = data.getJSONObject("stamping_expenses_applicable_rate");
            edit_tax_stamping_charges.setText(stampingExpensesApplicableRate.getString("tax_amount"));
            edit_total_stamping_charges.setText(stampingExpensesApplicableRate.getString("total_amount"));
            edit_change_stamping_charges.setText(stampingExpensesApplicableRate.getString("change_amount"));
            app_rate_stamping_charges.setText(stampingExpensesApplicableRate.getString("applicable_rate"));

            // Make fields non-editable
            productType.setEnabled(false);
            transaction_type.setEnabled(false);
            caseTag.setEnabled(false);
            loanAmount.setEnabled(false);
            appliedTenure.setEnabled(false);
            appliedRoi.setEnabled(false);
            edit_tax_amount_processing_fee.setEnabled(false);
            edit_total_amount_processing_fee.setEnabled(false);
            edit_change_amount_processing_fee.setEnabled(false);
            app_rate_processing_fee.setEnabled(false);
            edit_tax_valuation_charges.setEnabled(false);
            edit_total_valuation_charges.setEnabled(false);
            edit_change_valuation_charges.setEnabled(false);
            app_rate_valuation_charges.setEnabled(false);
            edit_tax_legal_incidental_charges.setEnabled(false);
            edit_total_legal_incidental_charges.setEnabled(false);
            edit_change_legal_incidental_charges.setEnabled(false);
            app_rate_legal_incidental_charges.setEnabled(false);
            edit_tax_stamp_duty_charges.setEnabled(false);
            edit_total_stamp_duty_charges.setEnabled(false);
            edit_change_stamp_duty_charges.setEnabled(false);
            app_rate_stamp_duty_charges.setEnabled(false);
            edit_tax_stamping_charges.setEnabled(false);
            edit_total_stamping_charges.setEnabled(false);
            edit_change_stamping_charges.setEnabled(false);
            app_rate_stamping_charges.setEnabled(false);
            edit_tax_rcu_charges.setEnabled(false);
            edit_total_rcu_charges.setEnabled(false);
            edit_change_rcu_charges.setEnabled(false);
            app_rate_rcu_charges.setEnabled(false);

            // Hide the submit button
            submitButton.setVisibility(View.GONE);

            showToast("Data found and displayed");
        } catch (JSONException e) {
            Log.e(TAG, "displayData: JSON Exception", e);
            showToast("Error displaying data");
        }
    }


    // Helper method to get position in a Spinner based on item value
    private int getSpinnerPosition(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        return adapter.getPosition(value);
    }



}