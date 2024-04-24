package com.example.bankapp;

import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.SharedPreferences;
public class KycActivity2 extends AppCompatActivity {
    EditText adharNumberEditText, panNumberEditText;
    SharedPreferences sharedPreferences;


    // Request codes for document types
    private static final int REQUEST_ADHAR_DOCUMENT = 1;
    private static final int REQUEST_PAN_DOCUMENT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_2);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String access_token = sharedPreferences.getString("accessToken", "");
        adharNumberEditText = findViewById(R.id.adharDocs);
        panNumberEditText = findViewById(R.id.panDocs);

        // Set click listeners for EditText fields
        adharNumberEditText.setOnClickListener(v -> onUploadDocumentClick(REQUEST_ADHAR_DOCUMENT));

        panNumberEditText.setOnClickListener(v -> onUploadDocumentClick(REQUEST_PAN_DOCUMENT));
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> {
            // Call makeHttpRequest method with the file paths of uploaded documents
            String adharFilePath = adharNumberEditText.getText().toString().trim();
            String panFilePath = panNumberEditText.getText().toString().trim();
            Toast.makeText(KycActivity2.this, "Documents uploaded successfully", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(KycActivity2.this, OtpActivity.class);
            startActivity(mainIntent);
            finish();
           // makeHttpRequest(access_token, adharFilePath, panFilePath);
        });
    }


    public void onUploadDocumentClick(int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Allow only PDF files
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
            EditText editTextToUpdate = null;
            switch (requestCode) {
                case REQUEST_ADHAR_DOCUMENT:
                    editTextToUpdate = adharNumberEditText;
                    break;
                case REQUEST_PAN_DOCUMENT:
                    editTextToUpdate = panNumberEditText;
                    break;
            }
            if (editTextToUpdate != null) {
                String filePath = getFileName(selectedFileUri);
                if (filePath != null) {
                    editTextToUpdate.setText(filePath);
                } else {
                    editTextToUpdate.setText(selectedFileUri.toString());
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex != -1) {
                        fileName = cursor.getString(displayNameIndex);
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }

    private void makeHttpRequest(final String accessToken, final String adharFilePath, final String panFilePath) {
        String url = BASE_URL + "api/v1/kyc";

        new Thread(() -> {
            try {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("adharNumber", adharNumberEditText.getText().toString().trim())
                        .addFormDataPart("panNumber", panNumberEditText.getText().toString().trim());

                // Add Aadhar document if file path is available
                if (!TextUtils.isEmpty(adharFilePath)) {
                    File adharFile = new File(adharFilePath);
                    if (adharFile.exists()) {
                        String adharFileName = adharFile.getName();
                        builder.addFormDataPart("adharDocument", adharFileName, RequestBody.create(adharFile, MediaType.parse("application/pdf")));
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(KycActivity2.this, "Aadhar file not found", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }

                // Add PAN document if file path is available
                if (!TextUtils.isEmpty(panFilePath)) {
                    File panFile = new File(panFilePath);
                    if (panFile.exists()) {
                        String panFileName = panFile.getName();
                        builder.addFormDataPart("panDocument", panFileName, RequestBody.create(panFile, MediaType.parse("application/pdf")));
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(KycActivity2.this, "PAN file not found", Toast.LENGTH_SHORT).show();
                        });
                        return; // Exit the method
                    }
                }

                // Build the request body
                RequestBody formBody = builder.build();

                // Create the request
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                // Execute the request
                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);
                Response response = call.execute();

                // Process the response on the UI thread
                final String serverResponse = response.body().string();
                runOnUiThread(() -> {
                    // Display the response in a Toast message
                    Toast.makeText(KycActivity2.this, "Documents uploaded successfully", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(KycActivity2.this, OtpActivity.class);
                    startActivity(mainIntent);
                    finish();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
