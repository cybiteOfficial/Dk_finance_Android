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

import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class KycActivity2 extends AppCompatActivity {
    private static final int FILE_PICKER_REQUEST_CODE = 2;
    private static final int REQUEST_PAN_DOCUMENT = 2;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    private static final int REQUEST_ADHAR_DOCUMENT = 1;

    EditText adhar_number, pan_number;
    EditText adharDocsEditText, panDocsEditText;
    Button submitButton;
    ImageView homeButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_2);

        final String mobNo = getIntent().getStringExtra("phoneNumber");
        final String kyc_id = getIntent().getStringExtra("kyc_id");
        // Check and request READ_EXTERNAL_STORAGE permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        adhar_number = findViewById(R.id.adhar_number);
        pan_number = findViewById(R.id.pan_number);
        adharDocsEditText = findViewById(R.id.adhardocs);
        panDocsEditText = findViewById(R.id.pandocs); // Initialize panDocsEditText
        EditText panNumberEditText = findViewById(R.id.pandocs);

        panNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().toUpperCase(); // Convert to uppercase
                if (!s.toString().equals(text)) {
                    panNumberEditText.setText(text);
                    panNumberEditText.setSelection(text.length());
                }
            }
        });

        adharDocsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(REQUEST_ADHAR_DOCUMENT);
            }
        });
        panDocsEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(REQUEST_PAN_DOCUMENT); // Change the request code to REQUEST_PAN_DOCUMENT
            }
        });

        submitButton = findViewById(R.id.submit_button);
        homeButton = findViewById(R.id.homeButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateAdharNumber() && validatePanNumber()) {
                    makeHttpRequest1(accessToken, mobNo, kyc_id);

                } else {
                    if (!validateAdharNumber()) {
                        Toast.makeText(KycActivity2.this, "Enter a valid 14-digit Aadhar number", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KycActivity2.this, "Enter a valid 9-character PAN number", Toast.LENGTH_SHORT).show();
                    }
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

        // Check READ_EXTERNAL_STORAGE permission again
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with your code
            // You can perform any actions that require this permission here
        }
    }
    private boolean validateAdharNumber() {
        String adharNumberText = adhar_number.getText().toString().trim();

        // Check if Aadhar number is exactly 12 digits
        if (adharNumberText.length() != 12) {
            return false;
        }

        return true;
    }
    private boolean validatePanNumber() {
        String panNumberText = pan_number.getText().toString().trim();

        // Check if PAN number is exactly 10 characters
        if (panNumberText.length() != 10) {
            return false;
        }

        return true;
    }

    public void onUploadDocumentClick(int requestCode) {
        // Open file picker to select only PDF and image files
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Specify MIME types for PDF and image files
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
                    adharDocsEditText.setText(filePath); // Set Aadhar document path
                } else if (requestCode == REQUEST_PAN_DOCUMENT) {
                    panDocsEditText.setText(filePath); // Set PAN document path
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
                cursor.close(); // Close the cursor to avoid memory leaks
            }
        }
        return filePath;
    }

    // onRequestPermissionsResult method to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your code
            } else {
                // Permission denied
                // You can show a message to the user indicating that the permission is required for the app to function properly
                // Toast.makeText(this, "Permission denied. File access is required for the app to function properly.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(KycActivity2.this,"Upload Failed 1", Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(KycActivity2.this,"Upload Failed 2", Toast.LENGTH_SHORT).show();
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