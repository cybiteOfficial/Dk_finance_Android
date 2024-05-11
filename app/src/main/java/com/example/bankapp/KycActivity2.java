package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;


import android.provider.OpenableColumns;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.Call;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KycActivity2 extends AppCompatActivity {
    private static final int FILE_PICKER_REQUEST_CODE = 2;
    private static final int REQUEST_PAN_DOCUMENT = 2;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    private static final int REQUEST_ADHAR_DOCUMENT = 1;

    EditText adhar_number,panNumber, voterIdNumber, drivingId;
    EditText adharDocsEditText, panDocsEditText;
    Button submitButton;
    ImageView homeButton;
    SharedPreferences sharedPreferences;

    // Declare spinnerItems as a class-level variable
    private ArrayList<CharSequence> spinnerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_2);
        Spinner spinner = findViewById(R.id.add_spinner);

        final String mobNo = getIntent().getStringExtra("phoneNumber");
        final String kyc_id = getIntent().getStringExtra("kyc_id");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        adhar_number = findViewById(R.id.adhar_number);
        adharDocsEditText = findViewById(R.id.adhardocs);

        adharDocsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(REQUEST_ADHAR_DOCUMENT);
            }
        });

        spinnerItems = new ArrayList<>(Arrays.asList(getResources().getTextArray(R.array.addDocs)));

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = (String) parentView.getItemAtPosition(position);
                LinearLayout container = findViewById(R.id.container);
                switch (selectedItem) {
                    case "PAN Card":
                        spinnerItems.remove("PAN Card");
                        View panCardLayout = getLayoutInflater().inflate(R.layout.fragment_pan_card, null);
                        container.addView(panCardLayout);
                        spinner.setSelection(0);
                        break;
                    case "Voter ID Card":
                        spinnerItems.remove("Voter ID Card");
                        View voterIdCardLayout = getLayoutInflater().inflate(R.layout.fragment_voter_id, null);
                        container.addView(voterIdCardLayout);
                        spinner.setSelection(0);
                        break;
                    case "Driving License":
                        spinnerItems.remove("Driving License");
                        View drivingLicenseLayout = getLayoutInflater().inflate(R.layout.fragment_driving_license, null);
                        container.addView(drivingLicenseLayout);
                        spinner.setSelection(0);
                        break;
                    case "Passport":
                        spinnerItems.remove("Passport");
                        View passportLayout = getLayoutInflater().inflate(R.layout.fragment_passport, null);
                        container.addView(passportLayout);
                        spinner.setSelection(0);
                        break;
                    case "Form 60":
                        spinnerItems.remove("Form 60");
                        View form60Layout = getLayoutInflater().inflate(R.layout.fragment_form_60, null);
                        container.addView(form60Layout);
                        spinner.setSelection(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        submitButton = findViewById(R.id.submit_button);
        homeButton = findViewById(R.id.homeButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateAdharNumber()) {
                    Toast.makeText(KycActivity2.this, "Enter a valid 14-digit Aadhaar number", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(KycActivity2.this, "Enter valid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(KycActivity2.this, DashboardActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with your code
        }
    }

    private boolean validateAdharNumber() {
        String adharNumberText = adhar_number.getText().toString().trim();
        if (adharNumberText.length() != 12) {
            return false;
        }
        return true;
    }

    public void onUploadDocumentClick(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("/");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"application/pdf", "image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Please install a file manager app to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedFileUri = data.getData();
            String filePath = getFilePathFromUri(selectedFileUri);
            if (filePath != null) {
                if (requestCode == REQUEST_ADHAR_DOCUMENT) {
                    adharDocsEditText.setText(filePath);
                } else if (requestCode == REQUEST_PAN_DOCUMENT) {
                    panDocsEditText.setText(filePath);
                }
            } else {
                Toast.makeText(this, "File path not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    filePath = cursor.getString(displayNameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }

    private void makeHttpRequest1(String accessToken, String phoneNumber, String kyc_id) {
        String url1 = BASE_URL + "api/v1/upload_document";

        String uuid = sharedPreferences.getString("uuid", "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody formBody = new FormBody.Builder()
                        .add("adhar_document_name", "Adhar Card")
                        .add("adhar_document_id", adhar_number.getText().toString().trim())
                        .add("adhar_document_type", "KYC")
                        .add("kyc_id", uuid)
                        .build();

                Request request = new Request.Builder()
                        .url(url1)
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
                            if (serverResponse.contains("false")) {
                                makeHttpRequest2(accessToken, phoneNumber, kyc_id);
                                Toast.makeText(KycActivity2.this, "Documents Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(KycActivity2.this, "Upload Failed 1", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void makeHttpRequest2(String accessToken, String phoneNumber, String kyc_id) {

        String url2 = BASE_URL + "api/v1/kyc?kyc_id=" + kyc_id;

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody formBody = new FormBody.Builder()

                        .add("kyc_document_verified", "true")
                        .build();

                Request request = new Request.Builder()
                        .url(url2)
                        .put(formBody)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                Response response = null;
                try {
                    response = call.execute();
                    String serverResponse2 = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("serv2resp", serverResponse2);
                            if (serverResponse2.contains("Successfully updated.")) {
                                Intent mainIntent = new Intent(KycActivity2.this, OtpActivity.class);
                                mainIntent.putExtra("phoneNumber", phoneNumber);
                                startActivity(mainIntent);
                                finish();
                            } else {
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // PAN Card
    public void onPANCardSaveButtonClick(View view) {
        EditText panNumberEditText = findViewById(R.id.panNumber);
        String panNumber = panNumberEditText.getText().toString().trim();

        if (!validatePAN(panNumber)) {
            // PAN card number is invalid, show error message
            panNumberEditText.setError("Enter a valid PAN card number");
            return;
        }

        // PAN card number is valid, proceed with saving the data
        findViewById(R.id.saveButton).setVisibility(View.GONE);
    }


    public void onPANCardDeleteButtonClick(View view) {
        LinearLayout container = findViewById(R.id.container);
        container.removeView(findViewById(R.id.pan));
        spinnerItems.add("PAN Card");
    }

    // Voter ID
    public void onVoterIDSaveButtonClick(View view) {
        EditText voterIdEditText = findViewById(R.id.voterIdNumber);
        String voterIdNumber = voterIdEditText.getText().toString().trim();

        if(!validateVoterID(voterIdNumber)){
            voterIdEditText.setError("Enter a valid Voter ID number");
        }
        findViewById(R.id.voterIdSaveButton).setVisibility(View.GONE);
    }

    public void onVoterIDDeleteButtonClick(View view) {
        LinearLayout container = findViewById(R.id.container);
        container.removeView(findViewById(R.id.voterId));
        spinnerItems.add("Voter ID Card");
    }

    // Driving License
    public void onDrivingLicenseSaveButtonClick(View view) {
        EditText drivingLicenseEditText = findViewById(R.id.drivingId);
        String drivingLicenseNumber = drivingLicenseEditText.getText().toString().trim();

        if(!validateDrivingLicense(drivingLicenseNumber)){
            drivingLicenseEditText.setError("Enter a valid Driving License number");
        }
        findViewById(R.id.drivingLicenseSaveButton).setVisibility(View.GONE);
    }

    public void onDrivingLicenseDeleteButtonClick(View view) {
        LinearLayout container = findViewById(R.id.container);
        container.removeView(findViewById(R.id.drivingLicense));
        spinnerItems.add("Driving License");
    }


    public void onPassportSaveButtonClick(View view) {
        EditText passportEdittext = findViewById(R.id.drivingId);
        String passportNumber = passportEdittext.getText().toString().trim();

        if(!validatePassport(passportNumber)){
            passportEdittext.setError("Enter a valid Passport number");
        }
        findViewById(R.id.passportSaveButton).setVisibility(View.GONE);
    }

    // Passport delete button click handler
    public void onPassportDeleteButtonClick(View view) {
        LinearLayout container = findViewById(R.id.container);
        container.removeView(findViewById(R.id.passport));
        spinnerItems.add("Passport");
    }

    // Form 60 save button click handler
    public void onForm60SaveButtonClick(View view) {

        findViewById(R.id.form60SaveButton).setVisibility(View.GONE);
    }

    // Form 60 delete button click handler
    public void onForm60DeleteButtonClick(View view) {
        LinearLayout container = findViewById(R.id.container);
        container.removeView(findViewById(R.id.form60));
        spinnerItems.add("Form 60");
    }



    // PAN Card validation
    private boolean validatePAN(String panNumberText) {
        String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        return Pattern.matches(panPattern, panNumberText);
    }

    // Voter ID validation
    private boolean validateVoterID(String voterIDText) {
        String voterIDPattern = "[A-Z]{3}[0-9]{7}";
        return Pattern.matches(voterIDPattern, voterIDText);
    }

    // Driving License validation
    private boolean validateDrivingLicense(String drivingLicenseText) {
        Pattern pattern = Pattern.compile("^[A-Z]{2}[0-9]{2}( )|([A-Z]{2}-[0-9]{2})((19|20)[0-9][0-9])[0-9]{7}$");
        Matcher matcher = pattern.matcher(drivingLicenseText);
        return matcher.matches();
    }

    private boolean validatePassport(String passportText){
        Pattern pattern = Pattern.compile("^[A-PR-WY][1-9]\\d" + "\\s?\\d{4}[1-9]$");
        Matcher matcher = pattern.matcher(passportText);
        return matcher.matches();
    }
}
