package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    private static final int REQUEST_ADHAR_DOCUMENT = 1;
    private static final int REQUEST_PAN_DOCUMENT = 2;
    private static final int REQUEST_PAN_DOCS = 3;
    private static final int REQUEST_DRIVING_DOCS = 4;
    private static final int REQUEST_VOTER_ID_DOCS = 5;
    private static final int REQUEST_FORM_60_DOCS = 6;
    private static final int REQUEST_PASSPORT_DOCS = 7;

    EditText adhar_number, panNumber, voterIdNumber, drivingId, passportNo;
    EditText adharDocsEditText, panDocs, formDocs, passportDocs, voterDocs, drivingDocs;
    Button submitButton;
    ImageView homeButton;
    SharedPreferences sharedPreferences;

    private ArrayList<CharSequence> spinnerItems;

    @SuppressLint("WrongViewCast")
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

        ////////////////////////////////////////////

        ///////////////////////////////////////////


        // Initialize spinnerItems with the list of document types
        spinnerItems = new ArrayList<>(Arrays.asList(getResources().getTextArray(R.array.addDocs)));

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_dropdown_item, spinnerItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.black)); // Change the color here
                return view;
            }
        };


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
                        addDocumentLayout(R.layout.fragment_pan_card);
                        panNumber = findViewById(R.id.panNumber);
                        panDocs = findViewById(R.id.panDocs);
                        spinner.setSelection(0);
                        break;
                    case "Voter ID Card":
                        spinnerItems.remove("Voter ID Card");
                        addDocumentLayout(R.layout.fragment_voter_id);
                        voterIdNumber = findViewById(R.id.voterIdNumber);
                        voterDocs = findViewById(R.id.voterDocs);
                        spinner.setSelection(0);
                        break;
                    case "Driving License":
                        spinnerItems.remove("Driving License");
                        addDocumentLayout(R.layout.fragment_driving_license);
                        drivingId = findViewById(R.id.drivingId);
                        drivingDocs = findViewById(R.id.drivingDocs);
                        spinner.setSelection(0);
                        break;
                    case "Passport":
                        spinnerItems.remove("Passport");
                        addDocumentLayout(R.layout.fragment_passport);
                        passportNo = findViewById(R.id.passportNo);
                        passportDocs = findViewById(R.id.passportDocs);
                        spinner.setSelection(0);
                        break;
                    case "Form 60":
                        spinnerItems.remove("Form 60");
                        addDocumentLayout(R.layout.fragment_form_60);
                        formDocs = findViewById(R.id.formDocs);
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
                    Toast.makeText(KycActivity2.this, "Enter a valid 12-digit Aadhaar number", Toast.LENGTH_SHORT).show();
                } else {
                    makeHttpRequest1(accessToken, mobNo, kyc_id);
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
        }
    }

    private void addDocumentLayout(int layoutId) {
        View documentLayout = getLayoutInflater().inflate(layoutId, null);
        LinearLayout container = findViewById(R.id.container);
        container.addView(documentLayout);

        // After inflating the layout, find the EditTexts and set up the TextWatchers
        if (layoutId == R.layout.fragment_pan_card) {
            panNumber = documentLayout.findViewById(R.id.panNumber);
            panDocs = documentLayout.findViewById(R.id.panDocs);
            setUpTextWatcher(panNumber);
        } else if (layoutId == R.layout.fragment_driving_license) {
            drivingId = documentLayout.findViewById(R.id.drivingId);
            drivingDocs = documentLayout.findViewById(R.id.drivingDocs);
            setUpTextWatcher(drivingId);
        } else if (layoutId == R.layout.fragment_passport) {
            passportNo = documentLayout.findViewById(R.id.passportNo);
            passportDocs = documentLayout.findViewById(R.id.passportDocs);
            setUpTextWatcher(passportNo);
        }

    }


    // Method to set up TextWatcher for EditText
    private void setUpTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No need to implement anything here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No need to implement anything here
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Convert all lowercase letters to uppercase
                String text = s.toString().toUpperCase();
                if (!s.toString().equals(text)) {
                    editText.setText(text);
                    editText.setSelection(text.length()); // Move cursor to the end
                }
            }
        });
    }

    private boolean validateAdharNumber() {
        String adharNumberText = adhar_number.getText().toString().trim();
        return adharNumberText.length() == 12;
    }

    public void onUploadDocumentClick(int requestCode) {
        // Open file picker to select any type of file
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

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
            String fileName = getFileName(selectedFileUri);
            if (fileName != null) {
                switch (requestCode) {
                    case REQUEST_ADHAR_DOCUMENT:
                        adharDocsEditText.setText(fileName);
                        break;
                    case REQUEST_PAN_DOCS:
                        panDocs.setText(fileName);
                        break;
                    case REQUEST_DRIVING_DOCS:
                        drivingDocs.setText(fileName);
                        break;
                    case REQUEST_VOTER_ID_DOCS:
                        voterDocs.setText(fileName);
                        break;
                    case REQUEST_FORM_60_DOCS:
                        formDocs.setText(fileName);
                        break;
                    case REQUEST_PASSPORT_DOCS:
                        passportDocs.setText(fileName);
                        break;
                }
            } else {
                Toast.makeText(this, "File name not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do nothing
            } else {
                // Permission denied
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

                Response response;
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

                Response response;
                try {
                    response = call.execute();
                    String serverResponse2 = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (serverResponse2.contains("Successfully updated.")) {
                                Intent mainIntent = new Intent(KycActivity2.this, OtpActivity.class);
                                mainIntent.putExtra("phoneNumber", phoneNumber);
                                startActivity(mainIntent);
                                finish();
                            } else {
                                // Handle error
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
            panNumberEditText.setError("Enter a valid PAN card number");
            return;
        }

        findViewById(R.id.saveButton).setVisibility(View.GONE);
    }

    public void onPANDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_PAN_DOCS);
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
            return;
        }
        findViewById(R.id.voterIdSaveButton).setVisibility(View.GONE);
    }

    public void onVoterIDDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_VOTER_ID_DOCS);
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
            return;
        }
        findViewById(R.id.drivingLicenseSaveButton).setVisibility(View.GONE);
    }

    public void onDrivingDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_DRIVING_DOCS);
    }

    public void onDrivingLicenseDeleteButtonClick(View view) {
        LinearLayout container = findViewById(R.id.container);
        container.removeView(findViewById(R.id.drivingLicense));
        spinnerItems.add("Driving License");
    }

    // Passport
    public void onPassportSaveButtonClick(View view) {
        EditText passportEdittext = findViewById(R.id.passportNo);
        String passportNumber = passportEdittext.getText().toString().trim();

        if(!validatePassport(passportNumber)){
            passportEdittext.setError("Enter a valid Passport number");
            return;
        }
        findViewById(R.id.passportSaveButton).setVisibility(View.GONE);
    }

    public void onPassportDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_PASSPORT_DOCS);
    }

    public void onPassportDeleteButtonClick(View view) {
        LinearLayout container = findViewById(R.id.container);
        container.removeView(findViewById(R.id.passport));
        spinnerItems.add("Passport");
    }

    // Form 60
    public void onForm60SaveButtonClick(View view) {
        findViewById(R.id.form60SaveButton).setVisibility(View.GONE);
    }

    public void onForm60DocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_FORM_60_DOCS);
    }

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

    // Passport validation
    private boolean validatePassport(String passportText) {
        Pattern pattern = Pattern.compile("^[A-PR-WY][1-9]\\d" + "\\s?\\d{4}[1-9]$");
        Matcher matcher = pattern.matcher(passportText);
        return matcher.matches();
    }
}
