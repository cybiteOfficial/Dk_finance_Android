package com.example.bankapp;

import static com.example.bankapp.RetrofitClient.getClient;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Callback;
import retrofit2.Retrofit;

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

                // print the contents of DocumentNames in logs
                for (Map.Entry<Integer, String> entry : documentNames.entrySet()) {
                    Log.d("DocumentNames", "Key: " + entry.getKey() + ", Value: " + entry.getValue());
                }

                if (!validateAdharNumber()) {
                    Toast.makeText(KycActivity2.this, "Enter a valid 12-digit Aadhaar number", Toast.LENGTH_SHORT).show();
                } else {
                    uploadDocumentsUsingRetrofit(accessToken, mobNo, kyc_id);
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

    // HashMap to store selected document IDs and their types
    private Map<Integer, String> selectedDocuments = new HashMap<>();
    private Map<Integer, String> notSelectedDocuments = new HashMap<>();

    {
        notSelectedDocuments.put(R.layout.add_more_pan, "Pan");
        notSelectedDocuments.put(R.layout.add_more_passport, "Passport");
        notSelectedDocuments.put(R.layout.add_more_driving, "Driving");
    }


    private void showDocumentSelectionPopup() {
        // Initialize the dialog
        documentSelectionDialog = new Dialog(this);
        documentSelectionDialog.setContentView(R.layout.document_selection_popup);
        // Set background color to white
        documentSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        final LinearLayout notSelectedContainer = documentSelectionDialog.findViewById(R.id.not_selected_container);
        final LinearLayout selectedContainer = documentSelectionDialog.findViewById(R.id.selected_container);

        // Show the popup
        documentSelectionDialog.show();

        renderDocuments(selectedContainer, notSelectedContainer, selectedDocuments, notSelectedDocuments);

        Button saveChanges = documentSelectionDialog.findViewById(R.id.save_changes);

        // Set click listener for Save Changes button
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SaveChanges", "Save Changes button clicked");
                // Clear all views from the container
                clearDocumentViews();
                listenChanges(selectedDocuments, notSelectedDocuments);

                // Update the UI with the latest selected documents
                renderDocuments(selectedContainer, notSelectedContainer, selectedDocuments, notSelectedDocuments);

                // Close the dialog
                documentSelectionDialog.dismiss();
            }
        });
    }

    private void clearDocumentViews() {
        LinearLayout container = findViewById(R.id.container);
        container.removeAllViews();
    }

    private void listenChanges(Map<Integer, String> selectedDocs, Map<Integer, String> notSelectedDocs){
        Log.d("ListenChanges", "Called with selectedDocs: " + selectedDocs + ", notSelectedDocs: " + notSelectedDocs);
        Map<Integer, String> selectedDocumentsToDisplay = new HashMap<>(selectedDocs);

        // Iterate over selected documents and inflate the corresponding layouts
        for (Map.Entry<Integer, String> entry : selectedDocumentsToDisplay.entrySet()) {
            int documentId = entry.getKey();
            String documentType = entry.getValue();

            // Check if the document layout already exists, if not, inflate it
            if (!layoutExists(documentId)) {
                addDocumentLayout(documentId, documentType); // Pass both documentId and documentType
            } else {
                Log.d("LayoutInflation", "Layout already exists for document ID: " + documentId);
            }
        }
    }




    // Method to check if a layout already exists
    private boolean layoutExists(int layoutId) {
        LinearLayout container = findViewById(R.id.container);
        View existingLayout = container.findViewById(layoutId);
        boolean exists = existingLayout != null;
        Log.d("LayoutExistsCheck", "Layout with ID " + layoutId + " exists: " + exists); // Log to check if layout exists
        return exists;
    }


    // Render documents in a given container
    private void renderDocuments(LinearLayout selectedContainer, LinearLayout notSelectedContainer, Map<Integer, String> selectedDocs, Map<Integer, String> notSelectedDocs) {
        // Render selected documents
        selectedContainer.removeAllViews();
        for (Map.Entry<Integer, String> entry : selectedDocs.entrySet()) {
            int documentId = entry.getKey();
            String documentType = entry.getValue();
            View documentView = LayoutInflater.from(this).inflate(documentId, selectedContainer, false);
            selectedContainer.addView(documentView);
            setRemoveButtonClickListener(selectedContainer,notSelectedContainer, documentView, documentType, documentId);
        }

        // Render not selected documents
        notSelectedContainer.removeAllViews();
        for (Map.Entry<Integer, String> entry : notSelectedDocs.entrySet()) {
            int documentId = entry.getKey();
            String documentType = entry.getValue();
            View documentView = LayoutInflater.from(this).inflate(documentId, notSelectedContainer, false);
            notSelectedContainer.addView(documentView);
            setAddButtonClickListener( selectedContainer, notSelectedContainer, documentView, documentType, documentId);

        }
    }


    private void setAddButtonClickListener(final LinearLayout selectedContainer, final LinearLayout notSelectedContainer, final View documentView, final String documentType, final int documentId) {
        Log.d("ButtonClickListener", "setAddButtonClickListener called for documentType: " + documentType + ", documentId: " + documentId);

        // Find the add button
        ImageView addButton = documentView.findViewById(getResources().getIdentifier("plusIcon" + documentType, "id", getPackageName()));
        if (addButton == null) {
            // Button not found, return
            Log.e("ButtonClickListener", "Add button not found for documentType: " + documentType);
            return;
        }
        // Set click listener for add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notSelectedDocuments.remove(documentId);
                // Generate selected document ID
                int selectedDocumentId = generateSelectedDocumentId(documentType);
                // Add document to selected
                selectedDocuments.put(selectedDocumentId, documentType);
                // render all
                renderDocuments(selectedContainer, notSelectedContainer, selectedDocuments, notSelectedDocuments);
            }
        });
    }

    // Helper method to generate selected document ID
    private int generateSelectedDocumentId(String documentType) {
        // Assuming documentType follows the format "Pan", "Passport", "Driving"
        String selectedDocumentIdString = "selected_" + documentType.toLowerCase().replace(" ", "_");
        return getResources().getIdentifier(selectedDocumentIdString, "layout", getPackageName());
    }


    private void setRemoveButtonClickListener(final LinearLayout selectedContainer, final LinearLayout notSelectedContainer, final View selectedView, final String documentType, final int documentId) {
        Log.d("ButtonClickListener", "setRemoveButtonClickListener called for documentType: " + documentType + ", documentId: " + documentId);

        // Find the remove button
        ImageView removeButton = selectedView.findViewById(getResources().getIdentifier("deleteIcon" + documentType, "id", getPackageName()));
        if (removeButton == null) {
            // Button not found, return
            Log.e("ButtonClickListener", "Remove button not found for documentType: " + documentType);
            return;
        }
        // Set click listener for remove button
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove document from selected
                selectedDocuments.remove(documentId);

                if (documentType.equals("Pan")) {
                    documentNames.remove(REQUEST_PAN_DOCS);
                } else if (documentType.equals("Driving")) {
                    documentNames.remove(REQUEST_DRIVING_DOCS);
                } else if (documentType.equals("Passport")) {
                    documentNames.remove(REQUEST_PASSPORT_DOCS);
                } else if (documentType.equals("Adhar")) {
                    documentNames.remove(REQUEST_ADHAR_DOCUMENT);
                } else if (documentType.equals("Voter")) {
                    documentNames.remove(REQUEST_VOTER_ID_DOCS);
                } else if (documentType.equals("Form60")) {
                    documentNames.remove(REQUEST_FORM_60_DOCS);
                }

                // Generate not selected document ID
                int notSelectedDocumentId = generateNotSelectedDocumentId(documentType);
                // Add document to not selected
                notSelectedDocuments.put(notSelectedDocumentId, documentType);
                // Render all
                renderDocuments(selectedContainer, notSelectedContainer, selectedDocuments, notSelectedDocuments);
            }
        });
    }

    // Helper method to generate not selected document ID
    private int generateNotSelectedDocumentId(String documentType) {
        // Assuming documentType follows the format "Pan", "Passport", "Driving"
        String notSelectedDocumentIdString = "add_more_" + documentType.toLowerCase().replace(" ", "_");
        return getResources().getIdentifier(notSelectedDocumentIdString, "layout", getPackageName());
    }



// /////////////////////

    private HashMap<Integer, String> documentNames = new HashMap<>();

    // create a hashmap to store the URIs of the selected documents named as documetURIs
    private HashMap<Integer, Uri> documentURIs = new HashMap<>();

    private Uri adharDocsUri, panDocsUri, drivingDocsUri, voterDocsUri, formDocsUri, passportDocsUri;



    private void addDocumentLayout(int documentId, String documentType) {
        int layoutId = generateLayoutId(documentType);
        if (layoutId != -1) { // Check if layout ID is valid
            View documentLayout = getLayoutInflater().inflate(layoutId, null);
            documentLayout.setId(documentId); // Set the ID for the layout
            Log.d("LayoutInflation", "Layout inflated with ID: " + documentId); // Log to check if layout is inflated
            LinearLayout container = findViewById(R.id.container);
            container.addView(documentLayout);

            // After inflating the layout, find the EditTexts and set up the TextWatchers
            if (layoutId == R.layout.fragment_pan_card) {
                panNumber = documentLayout.findViewById(R.id.panNumber);
                panDocs = documentLayout.findViewById(R.id.panDocs);
                setUpTextWatcher(panNumber);
                if (documentNames.containsKey(REQUEST_PAN_DOCS)) {
                    panDocs.setText(documentNames.get(REQUEST_PAN_DOCS));
                }
            } else if (layoutId == R.layout.fragment_driving_license) {
                drivingId = documentLayout.findViewById(R.id.drivingId);
                drivingDocs = documentLayout.findViewById(R.id.drivingDocs);
                setUpTextWatcher(drivingId);
                if (documentNames.containsKey(REQUEST_DRIVING_DOCS)) {
                    drivingDocs.setText(documentNames.get(REQUEST_DRIVING_DOCS));
                }
            } else if (layoutId == R.layout.fragment_passport) {
                passportNo = documentLayout.findViewById(R.id.passportNo);
                passportDocs = documentLayout.findViewById(R.id.passportDocs);
                setUpTextWatcher(passportNo);
                if (documentNames.containsKey(REQUEST_PASSPORT_DOCS)) {
                    passportDocs.setText(documentNames.get(REQUEST_PASSPORT_DOCS));
                }
            }
            // Add similar blocks for other document types if needed
        } else {
            Log.e("LayoutInflation", "Invalid layout ID for document type: " + documentType);
        }
    }


    // Helper method to generate layout ID based on document type
    private int generateLayoutId(String documentType) {
        if (documentType.equals("Pan")) {
            return R.layout.fragment_pan_card;
        } else if (documentType.equals("Driving")) {
            return R.layout.fragment_driving_license;
        } else if (documentType.equals("Passport")) {
            return R.layout.fragment_passport;
        } else {
            // Handle other types as needed
            return -1; // Return an invalid layout ID
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
                        documentNames.put(REQUEST_ADHAR_DOCUMENT, fileName); // Store document name
                        adharDocsUri = selectedFileUri;
                        documentURIs.put(REQUEST_ADHAR_DOCUMENT, selectedFileUri);
                    break;
                    case REQUEST_PAN_DOCS:
                        panDocs.setText(fileName);
                        documentNames.put(REQUEST_PAN_DOCS, fileName); // Store document name
                        panDocsUri = selectedFileUri;
                        documentURIs.put(REQUEST_PAN_DOCS, selectedFileUri);
                        break;
                    case REQUEST_DRIVING_DOCS:
                        drivingDocs.setText(fileName);
                        documentNames.put(REQUEST_DRIVING_DOCS, fileName); // Store document name
                        drivingDocsUri = selectedFileUri;
                        documentURIs.put(REQUEST_DRIVING_DOCS, selectedFileUri);
                        break;
                    case REQUEST_VOTER_ID_DOCS:
                        voterDocs.setText(fileName);
                        documentNames.put(REQUEST_VOTER_ID_DOCS, fileName); // Store document name
                        voterDocsUri = selectedFileUri;
                        documentURIs.put(REQUEST_VOTER_ID_DOCS, selectedFileUri);
                        break;
                    case REQUEST_FORM_60_DOCS:
                        formDocs.setText(fileName);
                        documentNames.put(REQUEST_FORM_60_DOCS, fileName); // Store document name
                        formDocsUri = selectedFileUri;
                        documentURIs.put(REQUEST_FORM_60_DOCS, selectedFileUri);
                        break;
                    case REQUEST_PASSPORT_DOCS:
                        passportDocs.setText(fileName);
                        documentNames.put(REQUEST_PASSPORT_DOCS, fileName); // Store document name
                        passportDocsUri = selectedFileUri;
                        documentURIs.put(REQUEST_PASSPORT_DOCS, selectedFileUri);
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


    //////////////////////////////
    private void uploadDocumentsUsingRetrofit(String accessToken, String phoneNumber, String kyc_id) {
        Retrofit retrofit = getClient(BaseUrl.BASE_URL, accessToken);
        ApiService apiService = retrofit.create(ApiService.class);

        // Create MultipartBody.Part list for the documents
        List<MultipartBody.Part> documentParts = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : documentNames.entrySet()) {

            int requestCode = entry.getKey();
            String fileName = entry.getValue();
            Uri fileUri = null;
            switch (requestCode) {
                case REQUEST_ADHAR_DOCUMENT:
                    fileUri = adharDocsUri;
                    break;
                case REQUEST_PAN_DOCS:
                    fileUri = panDocsUri;
                    break;
                case REQUEST_DRIVING_DOCS:
                    fileUri = drivingDocsUri;
                    break;
                case REQUEST_VOTER_ID_DOCS:
                    fileUri = voterDocsUri;
                    break;
                case REQUEST_FORM_60_DOCS:
                    fileUri = formDocsUri;
                    break;
                case REQUEST_PASSPORT_DOCS:
                    fileUri = passportDocsUri;
                    break;
            }

            String realPath = getRealPathFromURI(fileUri);
            File file = new File(realPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
            MultipartBody.Part documentPart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            documentParts.add(documentPart);

            // log the file name and the request code
            Log.d("FileName", fileName);
            Log.d("RequestCode", String.valueOf(requestCode));
            Log.d("RealPath", realPath);
        }

        // create document objects
        List<Document> documents = new ArrayList<>();
        String documentsJson = null;
        for (Map.Entry<Integer, String> entry : documentNames.entrySet()) {
            String fileName = entry.getValue();
            String doc_id;
            switch (entry.getKey()) {
                case REQUEST_ADHAR_DOCUMENT:
                    doc_id = adhar_number.getText().toString().trim();
                    break;
                case REQUEST_PAN_DOCS:
                    doc_id = panNumber.getText().toString().trim();
                    break;
                case REQUEST_DRIVING_DOCS:
                    doc_id = drivingId.getText().toString().trim();
                    break;
                case REQUEST_VOTER_ID_DOCS:
                    doc_id = voterIdNumber.getText().toString().trim();
                    break;
                case REQUEST_FORM_60_DOCS:
                    doc_id = "Form 60";
                    break;
                case REQUEST_PASSPORT_DOCS:
                    doc_id = passportNo.getText().toString().trim();
                    break;
                default:
                    doc_id = "";
            }
            Document document = new Document(fileName, doc_id);
            documents.add(document);

            // create an array to store strings of the document objects
            List<Document> documentInfo = new ArrayList<>();
            for (Document doc : documents) {
                documentInfo.add(doc);
            }
            // convert the array to a JSON string
            documentsJson = new Gson().toJson(documentInfo);

        }

        // log the JSON string
        Log.d("DocumentsJson", documentsJson);

        // log documentParts
        Log.d("DocumentParts", documentParts.toString());


        apiService.uploadDocuments(
                RequestBody.create(MultipartBody.FORM, "kyc"),
                RequestBody.create(MultipartBody.FORM, documentsJson),
                documentParts,
                RequestBody.create(MultipartBody.FORM, kyc_id)
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    makeHttpRequest2(accessToken, phoneNumber, kyc_id);
                    Toast.makeText(KycActivity2.this, "Documents Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("UploadError", "Response code: " + response.code() + ", Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(KycActivity2.this, "Upload Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.e("UploadFailure", "Error: " + t.getMessage(), t);
                Toast.makeText(KycActivity2.this, "Upload Failed onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // implement getRealPathFromURI() method
    private String getRealPathFromURI(Uri uri) {
        Context context = KycActivity2.this;
        String path = RealPathUtil.getRealPath(context, uri);
        return path;
    }

    //////////////////////////////
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


    public void onPANDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_PAN_DOCS);
    }


    public void onVoterIDDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_VOTER_ID_DOCS);
    }


    public void onDrivingDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_DRIVING_DOCS);
    }


    public void onPassportDocsUploadClick(View view) {
        onUploadDocumentClick(REQUEST_PASSPORT_DOCS);
    }
}