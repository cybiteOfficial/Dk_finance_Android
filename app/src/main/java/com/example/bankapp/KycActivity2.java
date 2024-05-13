package com.example.bankapp;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bankapp.environment.BaseUrl;

import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KycActivity2 extends AppCompatActivity {
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    private static final int REQUEST_ADHAR_DOCUMENT = 1;
    private static final int REQUEST_PAN_DOCS = 2;
    private static final int REQUEST_DRIVING_DOCS = 3;
    private static final int REQUEST_VOTER_ID_DOCS = 4;
    private static final int REQUEST_FORM_60_DOCS = 5;
    private static final int REQUEST_PASSPORT_DOCS = 6;

    EditText adhar_number, panNumber, voterIdNumber, drivingId, passportNo;
    EditText adharDocsEditText, panDocs, formDocs, passportDocs, voterDocs, drivingDocs;
    Button submitButton;
    ImageView homeButton;
    SharedPreferences sharedPreferences;

    private Dialog documentSelectionDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_2);

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


        // Initialize popup window
        documentSelectionDialog = new Dialog(this);
        documentSelectionDialog.setContentView(R.layout.document_selection_popup);

        LinearLayout addMoreDocumentsButton = findViewById(R.id.addMoreDocumentsButton);

        addMoreDocumentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDocumentSelectionPopup();
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

/////////////////
private void showDocumentSelectionPopup() {
    // Initialize the dialog
    documentSelectionDialog = new Dialog(this);
    documentSelectionDialog.setContentView(R.layout.document_selection_popup);
    // Set background color to white
    documentSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

    LinearLayout notSelectedContainer = documentSelectionDialog.findViewById(R.id.not_selected_container);
    LinearLayout selectedContainer = documentSelectionDialog.findViewById(R.id.selected_container);

    // Show the popup
    documentSelectionDialog.show();

    // Set click listeners for add buttons in popup
    setAddButtonClickListener(notSelectedContainer, selectedContainer, R.layout.selected_pan, R.layout.add_more_pan, "Pan");
    setAddButtonClickListener(notSelectedContainer, selectedContainer, R.layout.selected_passport, R.layout.add_more_passport, "Passport");
    setAddButtonClickListener(notSelectedContainer, selectedContainer, R.layout.selected_driving_license, R.layout.add_more_driving_license, "Driving");
}


    // HashSet to store selected document IDs
    private HashSet<Integer> selectedDocumentIds = new HashSet<>();
    private HashSet<Integer> notSelectedDocumentIds = new HashSet<>();

    // Add a method to check if a document is selected
    private boolean isDocumentSelected(int documentId) {
        return selectedDocumentIds.contains(documentId);
    }

    // Add a method to toggle document selection
    private void toggleDocumentSelection(int documentId) {
        if (selectedDocumentIds.contains(documentId)) {
            selectedDocumentIds.remove(documentId); // If already selected, remove it
        } else {
            selectedDocumentIds.add(documentId); // If not selected, add it
        }
    }

    private void setAddButtonClickListener(final LinearLayout notSelectedContainer, final LinearLayout selectedContainer, final int selectedLayoutId, final int notSelectedLayoutId, final String documentType) {
        // Find the add button
        ImageView addButton = documentSelectionDialog.findViewById(getResources().getIdentifier("plusIcon" + documentType, "id", getPackageName()));
        if (addButton == null) {
            // Button not found, return
            return;
        }
        // Set click listener for add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the selected document is already in the selected container
                boolean isAlreadySelected = isDocumentSelected(selectedLayoutId);
                if (!isAlreadySelected) {
                    // Inflate the selected layout
                    View selectedView = LayoutInflater.from(KycActivity2.this).inflate(selectedLayoutId, selectedContainer, false);
                    // Add to selected
                    selectedContainer.addView(selectedView);

                    // Remove from not selected
                    View notSelectedViewToRemove = notSelectedContainer.findViewById(notSelectedLayoutId);
                    if (notSelectedViewToRemove != null) {
                        notSelectedContainer.removeView(notSelectedViewToRemove);
                        // Add the document ID to the HashSet
                        selectedDocumentIds.add(selectedLayoutId);
                        // Remove document ID from non-selected HashSet
                        notSelectedDocumentIds.remove(notSelectedLayoutId);
                    }
                    // Set click listener for remove button
                    setRemoveButtonClickListener(selectedContainer, selectedView, notSelectedContainer, notSelectedLayoutId, documentType, selectedLayoutId);
                }
            }
        });
    }


    private void setRemoveButtonClickListener(final LinearLayout selectedContainer, final View selectedView, final LinearLayout notSelectedContainer, final int notSelectedLayoutId, final String documentType, final int selectedLayoutId) {
        // Find the remove button
        ImageView removeButton = selectedView.findViewById(getResources().getIdentifier("deleteIcon" + documentType, "id", getPackageName()));
        if (removeButton == null) {
            // Button not found, return
            return;
        }
        // Set click listener for remove button
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove from selected
                selectedContainer.removeView(selectedView);
                // Remove the document ID from the HashSet
                selectedDocumentIds.remove(selectedLayoutId);
                // Inflate the not selected layout
                View notSelectedView = LayoutInflater.from(KycActivity2.this).inflate(notSelectedLayoutId, notSelectedContainer, false);
                // Add to not selected
                notSelectedContainer.addView(notSelectedView);
                // Set click listener for add button
                setAddButtonClickListener(notSelectedContainer, selectedContainer, notSelectedLayoutId, selectedLayoutId, documentType);
                // Add document ID to non-selected HashSet
                notSelectedDocumentIds.add(notSelectedLayoutId);
            }
        });
    }


// /////////////////////



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
        String url1 = BaseUrl.BASE_URL + "api/v1/upload_document";
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

                try {
                    Response response = call.execute();
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
        String url2 = BaseUrl.BASE_URL + "api/v1/kyc?kyc_id=" + kyc_id;

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

                try {
                    Response response = call.execute();
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
