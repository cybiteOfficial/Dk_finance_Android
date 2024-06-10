package com.example.bankapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.environment.BaseUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", "");

        documentContainer = findViewById(R.id.documentContainer);
        addMoreDocumentsButton = findViewById(R.id.addMoreDocumentsButton);
        submitButton = findViewById(R.id.submit_button);
        documentSections = new HashMap<>();
        documentFileUris = new HashMap<>();

        addMoreDocumentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDocumentSection();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDocuments();
            }
        });
    }

    private void addDocumentSection() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View documentView = inflater.inflate(R.layout.document_input_fields, documentContainer, false);
        String sectionId = "documentSection" + (documentSections.size() + 1);
        documentSections.put(sectionId, documentView);

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

        documentContainer.addView(documentView);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    private String getRealPathFromURI(Uri uri) {
        String realPath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            realPath = cursor.getString(columnIndex);
            cursor.close();
        }
        return realPath;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                if (fileUri != null && currentEditText != null) {
                    String fileName = getFileNameFromUri(fileUri); // Obtain file name from Uri
                    currentEditText.setText(fileName); // Set file name in EditText
                    String sectionId = (String) currentEditText.getTag();
                    documentFileUris.put(sectionId, fileUri);
                }
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
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

    private void submitDocuments() {
        JSONArray documentsArray = new JSONArray();
        ArrayList<String> fileNames = new ArrayList<>();
        List<MultipartBody.Part> fileParts = new ArrayList<>();

        for (String key : documentSections.keySet()) {
            View documentView = documentSections.get(key);
            EditText docNameEditText = documentView.findViewById(R.id.docNameEditText);
            EditText docIdEditText = documentView.findViewById(R.id.docIdEditText);

            String documentName = docNameEditText.getText().toString();
            String documentId = docIdEditText.getText().toString();

            try {
                JSONObject documentObject = new JSONObject();
                documentObject.put("document_name", documentName);
                documentObject.put("document_id", documentId);
                documentsArray.put(documentObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Uri fileUri = documentFileUris.get(key);

            // log  fileUri
            Log.d("DocumentUploadActivity", "File Uri: " + fileUri);

            if (fileUri != null) {
                String realPath = getRealPathFromURI(fileUri);
                if (realPath != null) {
                    File file = new File(realPath);
                    if (file.exists()) {
                        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                        fileParts.add(body);
                        fileNames.add(file.getName());
                    } else {
                        Log.e("DocumentUploadActivity", "File does not exist: " + realPath);
                    }
                } else {
                    Log.e("DocumentUploadActivity", "Real path is null for fileUri: " + fileUri.toString());
                }
            } else {
                Log.e("DocumentUploadActivity", "fileUri is null");
            }
        }

        String jsonDocuments = documentsArray.toString();

        // Log the file names of the documents to be uploaded
        for (String fileName : fileNames) {
            Log.d("DocumentUploadActivity", "File name: " + fileName);
        }

        // Log the JSON string of the documents to be uploaded
        Log.d("DocumentUploadActivity", "Documents: " + jsonDocuments);

        // Proceed with the actual upload process

        RequestBody requestBodyDocuments = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonDocuments);
        RequestBody applicationId = RequestBody.create(MediaType.parse("text/plain"), "app_63429322");
        RequestBody documentType = RequestBody.create(MediaType.parse("text/plain"), "other");

        ApiService apiService = RetrofitClient.getClient(BaseUrl.BASE_URL, accessToken).create(ApiService.class);
        Call<Void> call = apiService.uploadDocuments(documentType, applicationId, fileParts, requestBodyDocuments);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DocumentUploadActivity.this, "Documents uploaded successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DocumentUploadActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(DocumentUploadActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
