package com.example.bankapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.environment.BaseUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentUploadActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;

    private LinearLayout documentContainer;
    private LinearLayout addMoreDocumentsButton;
    private Button submitButton;
    private HashMap<String, View> documentSections;
    private HashMap<String, Uri> documentFileUris;
    private EditText currentEditText;
    private String accessToken;
    SharedPreferences sharedPreferences;

    private static final String TAG = "DocumentUploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        try {
            Log.d(TAG, "onCreate: Initializing");

            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", "");

            // fetch application_id from previous activity (intent)
            Intent intent = getIntent();
            String application_id = intent.getStringExtra("application_id");
            Log.d(TAG, "onCreate: Application ID: " + application_id);

            documentContainer = findViewById(R.id.documentContainer);
            addMoreDocumentsButton = findViewById(R.id.addMoreDocumentsButton);
            submitButton = findViewById(R.id.submit_button);
            documentSections = new HashMap<>();
            documentFileUris = new HashMap<>();

            addMoreDocumentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "addMoreDocumentsButton clicked");
                    addDocumentSection();
                }
            });

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "submitButton clicked");
                    submitDocuments(application_id);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Exception", e);
            Toast.makeText(this, "Initialization error", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDocumentSection() {
        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            View documentView = inflater.inflate(R.layout.document_input_fields, documentContainer, false);
            String sectionId = "documentSection" + (documentSections.size() + 1);
            // Set a unique tag to identify this section
            documentView.setTag(sectionId);
            documentSections.put(sectionId, documentView);

            Log.d(TAG, "addDocumentSection: Added section " + sectionId);

            // Set click listener for the Upload Document field
            EditText docFileEditText = documentView.findViewById(R.id.docFileEditText);
            docFileEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentEditText = docFileEditText;
                    currentEditText.setTag(sectionId); // Set tag for identification
                    openFilePicker();
                }
            });

            // Set click listener for the Delete Document button
            Button deleteButton = documentView.findViewById(R.id.deleteDocumentBtn);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteDocumentSection(v); // Call onDeleteDocumentSection with the view clicked
                }
            });

            documentContainer.addView(documentView);
        } catch (Exception e) {
            Log.e(TAG, "addDocumentSection: Exception", e);
            Toast.makeText(this, "Error adding document section", Toast.LENGTH_SHORT).show();
        }
    }

    private void onDeleteDocumentSection(View view) {
        try {
            // Find the parent view which is the document section
            View documentView = (View) view.getParent();

            // Retrieve the sectionId from the tag set in XML or programmatically
            String sectionId = (String) documentView.getTag();
            documentContainer.removeView(documentView);
            documentSections.remove(sectionId);
            documentFileUris.remove(sectionId);
            Log.d(TAG, "onDeleteDocumentSection: Removed section " + sectionId);
        } catch (Exception e) {
            Log.e(TAG, "onDeleteDocumentSection: Exception", e);
            Toast.makeText(this, "Error deleting document section", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFilePicker() {
        try {
            Log.d(TAG, "openFilePicker: Opening file picker");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String[] mimeTypes = {"image/jpeg", "image/png", "application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        } catch (Exception e) {
            Log.e(TAG, "openFilePicker: Exception", e);
            Toast.makeText(this, "Error opening file picker", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            Log.d(TAG, "onActivityResult: requestCode " + requestCode + ", resultCode " + resultCode);

            if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
                if (data != null) {
                    Uri fileUri = data.getData();
                    Log.d(TAG, "onActivityResult: fileUri " + fileUri);
                    if (fileUri != null && currentEditText != null) {
                        String fileName = getFileNameFromUri(fileUri); // Obtain file name from Uri
                        currentEditText.setText(fileName); // Set file name in EditText
                        String sectionId = (String) currentEditText.getTag();
                        documentFileUris.put(sectionId, fileUri);
                        Log.d(TAG, "onActivityResult: Added fileUri for section " + sectionId);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onActivityResult: Exception", e);
            Toast.makeText(this, "Error processing selected file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileNameFromUri(Uri uri) {
        Cursor cursor = null;
        try {
            Log.d(TAG, "getFileNameFromUri: Getting file name for URI " + uri);
            String fileName = null;
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex);
                }
            }
            Log.d(TAG, "getFileNameFromUri: File name is " + fileName);
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "getFileNameFromUri: Exception", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void submitDocuments(String application_id) {
        try {
            Log.d(TAG, "submitDocuments: Submitting documents");

            JSONArray documentsArray = new JSONArray();
            ArrayList<String> fileNames = new ArrayList<>();
            List<MultipartBody.Part> documentParts = new ArrayList<>();
            List<String> validationErrors = new ArrayList<>();

            for (String key : documentSections.keySet()) {
                View documentView = documentSections.get(key);
                EditText docNameEditText = documentView.findViewById(R.id.docNameEditText);
                EditText docIdEditText = documentView.findViewById(R.id.docIdEditText);
                EditText docFileEditText = documentView.findViewById(R.id.docFileEditText);

                String documentName = docNameEditText.getText().toString();
                String documentId = docIdEditText.getText().toString();
                String fileName = docFileEditText.getText().toString();
                Uri fileUri = documentFileUris.get(key);

                // Check if the fileUri or the ID is missing and add to validation errors
                if (fileUri == null) {
                    validationErrors.add("Please upload the document file for " + documentName);
                }
                if (documentId.isEmpty()) {
                    validationErrors.add("Please enter the ID number for " + documentName);
                }

                // Proceed only if there are no validation errors
                if (fileUri != null && !documentId.isEmpty()) {
                    try {
                        // Get the MIME type from the URI
                        String mimeType = getContentResolver().getType(fileUri);
                        if (mimeType == null) {
                            mimeType = "*/*"; // Default to all MIME types
                        }

                        // Create a request body with the determined MIME type
                        InputStream inputStream = getContentResolver().openInputStream(fileUri);
                        byte[] fileBytes = new byte[inputStream.available()];
                        inputStream.read(fileBytes);
                        inputStream.close();

                        RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);
                        MultipartBody.Part documentPart = MultipartBody.Part.createFormData("file", fileName, requestFile);
                        documentParts.add(documentPart);

                        // Add document details to JSON array
                        JSONObject documentObject = new JSONObject();
                        documentObject.put("document_name", documentName);
                        documentObject.put("document_id", documentId);

                        documentsArray.put(documentObject);

                        fileNames.add(fileName);
                        Log.d(TAG, "submitDocuments: Added document with name " + documentName + " and ID " + documentId);
                    } catch (Exception e) {
                        Log.e(TAG, "submitDocuments: Exception", e);
                        validationErrors.add("Error processing document details for " + documentName);
                    }
                }
            }

            if (!validationErrors.isEmpty()) {
                for (String error : validationErrors) {
                    Log.e(TAG, "submitDocuments: Validation Error - " + error);
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            String jsonDocuments = documentsArray.toString();

            // Log the file names of the documents to be uploaded
            for (String fileName : fileNames) {
                Log.d(TAG, "submitDocuments: File name: " + fileName);
            }

            // Log the JSON string of the documents to be uploaded
            Log.d(TAG, "submitDocuments: Documents: " + jsonDocuments);

            RequestBody applicationId = RequestBody.create(MediaType.parse("text/plain"), application_id);
            RequestBody documentType = RequestBody.create(MediaType.parse("text/plain"), "other");
            RequestBody documents = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonDocuments);

            ApiService apiService = RetrofitClient.getClient(BaseUrl.BASE_URL, accessToken).create(ApiService.class);
            Call<Void> call = apiService.uploadDocuments_other(documentType, documents, documentParts, applicationId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: Documents uploaded successfully!");
                        Toast.makeText(DocumentUploadActivity.this, "Documents uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "onResponse: Upload failed! Response code: " + response.code());
                        Toast.makeText(DocumentUploadActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "onFailure: Upload error", t);
                    Toast.makeText(DocumentUploadActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "submitDocuments: Exception", e);
            Toast.makeText(this, "Error submitting documents", Toast.LENGTH_SHORT).show();
        }
    }
}
