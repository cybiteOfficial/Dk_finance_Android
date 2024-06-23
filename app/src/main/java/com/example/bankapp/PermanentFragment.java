package com.example.bankapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bankapp.environment.BaseUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PermanentFragment extends Fragment {


    private EditText addressLine1EditText;
    private EditText addressLine2EditText;
    private EditText addressLine3EditText;
    private EditText stateEditText;
    private EditText districtEditText;
    private EditText cityEditText;
    private EditText talukaEditText;
    private EditText pinCodeEditText;
    private EditText landmarkEditText;
    private EditText  stabilityAtResidenceEditText;
    private EditText  distanceFromBranchEditText;
    private Spinner residenceStateSpinner;
    private Spinner residenceTypeSpinner;


    private CurrentAddressData currentAddressData;

    private boolean isSameAsCurrent = false;

    LinearLayout sameAsCurrentLayout;
    Button submitBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_permanent, container, false);

        // get customer details from parent activity
        Intent i = getActivity().getIntent();
        String application_id = i.getStringExtra("application_id");
        CustomerData customerData = (CustomerData) i.getSerializableExtra("customerData");

        String customerDataString = customerData.toString();

        Log.d("PermanentFragment", "onCreateView: Application ID = " + application_id);
        Log.d("PermanentFragment", "onCreateView: Customer Data = " + customerData.toString());

        // extract image path from customer data
        String imagePath = customerData.getImagePath();
        Log.d("PermanentFragment", "onCreateView: Image Path = " + imagePath);


        // get access token from shared preferences
        String accessToken = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("accessToken", "");

        // Initialize EditText fields
        addressLine1EditText = rootView.findViewById(R.id.address_line_1);
        addressLine2EditText = rootView.findViewById(R.id.address_line_2);
        addressLine3EditText = rootView.findViewById(R.id.address_line_3);
        stateEditText = rootView.findViewById(R.id.state);
        districtEditText = rootView.findViewById(R.id.district);
        cityEditText = rootView.findViewById(R.id.city);
        talukaEditText = rootView.findViewById(R.id.taluka);
        pinCodeEditText = rootView.findViewById(R.id.pinCode);
        landmarkEditText = rootView.findViewById(R.id.landmark);
        stabilityAtResidenceEditText = rootView.findViewById(R.id.stabilityAtResidence);
        distanceFromBranchEditText = rootView.findViewById(R.id.distanceFromBranch);

        // Setup Spinners
        residenceStateSpinner = setupSpinner(rootView, R.id.residenceState, R.array.residence_state);
        residenceTypeSpinner = setupSpinner(rootView, R.id.residenceType, R.array.residence_type);

        // Initialize LinearLayout
        sameAsCurrentLayout = rootView.findViewById(R.id.sameAsCurrentLayout);

        // Initialize Button
        submitBtn = rootView.findViewById(R.id.submit_button);

        sameAsCurrentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSameAsCurrent) {
                    clearAndEnableFields();
                    isSameAsCurrent = false;
                } else {
                    if (currentAddressData != null) {
                        updateUIWithCurrentData(currentAddressData);
                        isSameAsCurrent = true;
                    }
                }
            }
        });

        // Set click listener for the button
        submitBtn.setOnClickListener(v -> {

            Boolean is_permanent = isSameAsCurrent;
            if(!is_permanent) {
                JSONObject currentAddressJson;
                try {
                    currentAddressJson = createAddressJson(currentAddressData);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                JSONObject permanentAddressJson;
                try {
                    permanentAddressJson = createAddressJsonFromInput();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                makeHttpRequest(application_id, submitBtn, accessToken, is_permanent, customerData, currentAddressJson, permanentAddressJson);
            }

            else {
                JSONObject currentAddressJson;
                try {
                    currentAddressJson = createAddressJson(currentAddressData);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                makeHttpRequest(application_id, submitBtn, accessToken, is_permanent, customerData, currentAddressJson, null);
            }

        });

        return rootView;
    }

    private void makeHttpRequest(String applicationId, Button submitButton, String accessToken, boolean isPermanent, CustomerData customerData, JSONObject currentAddressJson, @Nullable JSONObject permanentAddressJson) {
        String url = BaseUrl.BASE_URL + "api/v1/customers";
        Log.d("PermanentFragment", "makeHttpRequest: URL = " + url);
        Log.d("PermanentFragment", "makeHttpRequest: Access Token = " + accessToken);
        Log.d("PermanentFragment", "makeHttpRequest: isPermanent = " + String.valueOf(isPermanent));
        Log.d("PermanentFragment", "makeHttpRequest: Customer Data = " + customerData.toString());
        Log.d("PermanentFragment", "makeHttpRequest: Current Address = " + currentAddressJson.toString());

        // Get file path from customer data
        String imagePath = customerData.getImagePath();
        File imageFile = null;

        // Create the image file if imagePath is not null
        if (imagePath != null) {
            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                Log.e("PermanentFragment", "makeHttpRequest: Image file does not exist at: " + imageFile.getAbsolutePath());
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Error: Image file not found");
                        submitButton.setEnabled(true);
                    }
                });
                return; // Exit method if image file does not exist
            }
            Log.d("PermanentFragment", "makeHttpRequest: Image File Path = " + imageFile.getAbsolutePath());
        }

        // Check if permanent address is present
        if (permanentAddressJson != null) {
            Log.d("PermanentFragment", "makeHttpRequest: Permanent Address = " + permanentAddressJson.toString());
        }

        File finalImageFile = imageFile;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create the multipart form body builder
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("customer_data", customerData.toString())
                            .addFormDataPart("is_permanent", String.valueOf(isPermanent))
                            .addFormDataPart("current_address", currentAddressJson.toString());

                    // Add permanent address if it's not the same as current address
                    if (!isPermanent && permanentAddressJson != null) {
                        multipartBuilder.addFormDataPart("permanent_address", permanentAddressJson.toString());
                    }

                    // Add image file if it exists
                    if (finalImageFile != null && finalImageFile.exists()) {
                        multipartBuilder.addFormDataPart("profile_photo", finalImageFile.getName(),
                                RequestBody.create(MediaType.parse("image/jpeg"), finalImageFile));
                    }

                    // Build the multipart form body
                    RequestBody requestBody = multipartBuilder.build();

                    // Create the request
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();

                    // Create the OkHttpClient and execute the request
                    OkHttpClient client = new OkHttpClient();
                    Call call = client.newCall(request);

                    Response response = call.execute();

                    // Check response code
                    if (!response.isSuccessful()) {
                        Log.e("PermanentFragment", "run: HTTP error code: " + response.code());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Error: Failed to upload data. HTTP error code: " + response.code());
                                submitButton.setEnabled(true);
                            }
                        });
                        return; // Exit method on HTTP error
                    }

                    // Read server response
                    String serverResponse = response.body().string();
                    Log.d("PermanentFragment", "run: Server response = " + serverResponse);

                    // Parse JSON response
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(serverResponse);

                        boolean isError = jsonResponse.getBoolean("error");
                        if (!isError) {
                            Log.d("PermanentFragment", "run: Data uploaded successfully");
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data Uploaded Successfully");

                                    // Start AddCustomerActivity and finish the current activity
                                    Intent intent = new Intent(requireContext(), AddCustomerActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("application_id", applicationId);
                                    requireContext().startActivity(intent);
                                    requireActivity().finish();
                                }
                            });
                        }
                        else {
                            String errorMessage = jsonResponse.optString("message", "Unknown error");
                            Log.e("PermanentFragment", "run: Error from server: " + errorMessage);
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Error uploading data: " + errorMessage);
                                    submitButton.setEnabled(true);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("PermanentFragment", "run: Error parsing server response", e);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Error parsing server response");
                                submitButton.setEnabled(true);
                            }
                        });
                    }
                } catch (IOException e) {
                    Log.e("PermanentFragment", "run: Exception occurred during HTTP request", e);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Error occurred while uploading data: " + e.getMessage());
                            submitButton.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }


    private JSONObject createAddressJson(CurrentAddressData data) throws Exception {
        JSONObject addressJson = new JSONObject();
        addressJson.put("address_line_1", data.getAddressLine1());
        addressJson.put("address_line_2", data.getAddressLine2());
        addressJson.put("address_line_3", data.getAddressLine3());
        addressJson.put("state", data.getState());
        addressJson.put("city", data.getCity());
        addressJson.put("district", data.getDistrict());
        addressJson.put("tehsil_or_taluka", data.getTaluka());
        addressJson.put("pincode", data.getPinCode());
        addressJson.put("landmark", data.getLandmark());
        addressJson.put("residence_state", data.getResidenceState());
        addressJson.put("residence_type", data.getResidenceType());
        addressJson.put("stability_at_residence", data.getStabilityAtResidence());
        addressJson.put("distance_from_branch", data.getDistanceFromBranch());
        return addressJson;
    }

    private JSONObject createAddressJsonFromInput() throws Exception {
        JSONObject addressJson = new JSONObject();
        addressJson.put("address_line_1", addressLine1EditText.getText().toString());
        addressJson.put("address_line_2", addressLine2EditText.getText().toString());
        addressJson.put("address_line_3", addressLine3EditText.getText().toString());
        addressJson.put("state", stateEditText.getText().toString());
        addressJson.put("city", cityEditText.getText().toString());
        addressJson.put("district", districtEditText.getText().toString());
        addressJson.put("tehsil_or_taluka", talukaEditText.getText().toString());
        addressJson.put("pincode", pinCodeEditText.getText().toString());
        addressJson.put("landmark", landmarkEditText.getText().toString());
        addressJson.put("residence_state", residenceStateSpinner.getSelectedItem().toString());
        addressJson.put("residence_type", residenceTypeSpinner.getSelectedItem().toString());
        addressJson.put("stability_at_residence", stabilityAtResidenceEditText.getText().toString());
        addressJson.put("distance_from_branch", distanceFromBranchEditText.getText().toString());
        return addressJson;
    }


    private Spinner setupSpinner(View view, int spinnerId, int arrayId) {
        Spinner spinner = view.findViewById(spinnerId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.sample_spinner_item, getResources().getStringArray(arrayId)) {
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
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        spinner.setAdapter(adapter);
        return spinner;
    }

    // Method to store the current address data
    public void updateData(CurrentAddressData currentAddressData) {
        this.currentAddressData = currentAddressData;
    }

    // Method to update UI with CurrentAddressData
    private void updateUIWithCurrentData(CurrentAddressData currentAddressData) {
        if (currentAddressData != null) {
            addressLine1EditText.setText(currentAddressData.getAddressLine1());
            addressLine2EditText.setText(currentAddressData.getAddressLine2());
            addressLine3EditText.setText(currentAddressData.getAddressLine3());
            stateEditText.setText(currentAddressData.getState());
            districtEditText.setText(currentAddressData.getDistrict());
            cityEditText.setText(currentAddressData.getCity());
            talukaEditText.setText(currentAddressData.getTaluka());
            pinCodeEditText.setText(currentAddressData.getPinCode());
            landmarkEditText.setText(currentAddressData.getLandmark());
            stabilityAtResidenceEditText.setText(currentAddressData.getStabilityAtResidence());
            distanceFromBranchEditText.setText(currentAddressData.getDistanceFromBranch());
            residenceStateSpinner.setSelection(((ArrayAdapter<String>) residenceStateSpinner.getAdapter()).getPosition(currentAddressData.getResidenceState()));
            residenceTypeSpinner.setSelection(((ArrayAdapter<String>) residenceTypeSpinner.getAdapter()).getPosition(currentAddressData.getResidenceType()));

            // Make EditText fields non-editable
            makeFieldsNonEditable();
        } else {
            Log.e("PermanentFragment", "CurrentAddressData object is null.");
        }
    }

    private void makeFieldsNonEditable() {

        addressLine1EditText.setEnabled(false);
        addressLine1EditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        addressLine1EditText.setTextColor(getResources().getColor(R.color.black));

        addressLine2EditText.setEnabled(false);
        addressLine2EditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        addressLine2EditText.setTextColor(getResources().getColor(R.color.black));

        addressLine3EditText.setEnabled(false);
        addressLine3EditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        addressLine3EditText.setTextColor(getResources().getColor(R.color.black));

        stateEditText.setEnabled(false);
        stateEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        stateEditText.setTextColor(getResources().getColor(R.color.black));

        districtEditText.setEnabled(false);
        districtEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        districtEditText.setTextColor(getResources().getColor(R.color.black));

        cityEditText.setEnabled(false);
        cityEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        cityEditText.setTextColor(getResources().getColor(R.color.black));

        talukaEditText.setEnabled(false);
        talukaEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        talukaEditText.setTextColor(getResources().getColor(R.color.black));

        pinCodeEditText.setEnabled(false);
        pinCodeEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        pinCodeEditText.setTextColor(getResources().getColor(R.color.black));

        landmarkEditText.setEnabled(false);
        landmarkEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        landmarkEditText.setTextColor(getResources().getColor(R.color.black));

        stabilityAtResidenceEditText.setEnabled(false);
        stabilityAtResidenceEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        stabilityAtResidenceEditText.setTextColor(getResources().getColor(R.color.black));

        distanceFromBranchEditText.setEnabled(false);
        distanceFromBranchEditText.setBackgroundResource(R.drawable.edit_text_border_fixed);
        distanceFromBranchEditText.setTextColor(getResources().getColor(R.color.black));

        residenceStateSpinner.setEnabled(false);
//        residenceStateSpinner.setBackgroundResource(R.drawable.spinner_border_fixed);

        residenceTypeSpinner.setEnabled(false);
//        residenceTypeSpinner.setBackgroundResource(R.drawable.spinner_border_fixed);

    }

    private void clearAndEnableFields() {

        addressLine1EditText.setText("");
        addressLine1EditText.setEnabled(true);
        addressLine1EditText.setBackgroundResource(R.drawable.edit_text_border);
        addressLine1EditText.setTextColor(getResources().getColor(R.color.black));

        addressLine2EditText.setText("");
        addressLine2EditText.setEnabled(true);
        addressLine2EditText.setBackgroundResource(R.drawable.edit_text_border);
        addressLine2EditText.setTextColor(getResources().getColor(R.color.black));

        addressLine3EditText.setText("");
        addressLine3EditText.setEnabled(true);
        addressLine3EditText.setBackgroundResource(R.drawable.edit_text_border);
        addressLine3EditText.setTextColor(getResources().getColor(R.color.black));

        stateEditText.setText("");
        stateEditText.setEnabled(true);
        stateEditText.setBackgroundResource(R.drawable.edit_text_border);
        stateEditText.setTextColor(getResources().getColor(R.color.black));

        districtEditText.setText("");
        districtEditText.setEnabled(true);
        districtEditText.setBackgroundResource(R.drawable.edit_text_border);
        districtEditText.setTextColor(getResources().getColor(R.color.black));

        cityEditText.setText("");
        cityEditText.setEnabled(true);
        cityEditText.setBackgroundResource(R.drawable.edit_text_border);
        cityEditText.setTextColor(getResources().getColor(R.color.black));

        talukaEditText.setText("");
        talukaEditText.setEnabled(true);
        talukaEditText.setBackgroundResource(R.drawable.edit_text_border);
        talukaEditText.setTextColor(getResources().getColor(R.color.black));

        pinCodeEditText.setText("");
        pinCodeEditText.setEnabled(true);
        pinCodeEditText.setBackgroundResource(R.drawable.edit_text_border);
        pinCodeEditText.setTextColor(getResources().getColor(R.color.black));

        landmarkEditText.setText("");
        landmarkEditText.setEnabled(true);
        landmarkEditText.setBackgroundResource(R.drawable.edit_text_border);
        landmarkEditText.setTextColor(getResources().getColor(R.color.black));

        stabilityAtResidenceEditText.setText("");
        stabilityAtResidenceEditText.setEnabled(true);
        stabilityAtResidenceEditText.setBackgroundResource(R.drawable.edit_text_border);
        stabilityAtResidenceEditText.setTextColor(getResources().getColor(R.color.black));

        distanceFromBranchEditText.setText("");
        distanceFromBranchEditText.setEnabled(true);
        distanceFromBranchEditText.setBackgroundResource(R.drawable.edit_text_border);
        distanceFromBranchEditText.setTextColor(getResources().getColor(R.color.black));

        residenceStateSpinner.setEnabled(true);
//        residenceStateSpinner.setBackgroundResource(R.drawable.spinner_border);
        residenceTypeSpinner.setEnabled(true);
//        residenceTypeSpinner.setBackgroundResource(R.drawable.spinner_border);

    }
}