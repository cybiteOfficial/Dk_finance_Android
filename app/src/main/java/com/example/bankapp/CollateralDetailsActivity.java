package com.example.bankapp;

import static com.example.bankapp.RetrofitClient.getClient;
import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.environment.BaseUrl;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class CollateralDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_DOCUMENT = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    ImageView homeButton;
    private EditText docs, isExistingCollateral, collateralType, collateralName, primarySecondaryCollateral, valuationRequired, relationWithLoan,
            propertyOwner, propertyCategory, typeOfProperty, occupationStatus, propertyStatus, propertyTitle, houseNumber, khasraNumber,
            locality, village, state, city, district, taluka, pinCode, landmark, estimatedPropertyValue, documentName, documentId;
    SharedPreferences sharedPreferences;
    private Uri adharDocsUri;
    private Map<Integer, String> documentNames = new HashMap<>();
    private Map<Integer, Uri> documentURIs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collateral_details);

        // Initialize EditText fields
        docs = findViewById(R.id.docs);
        collateralType = findViewById(R.id.collateralType);
        collateralName = findViewById(R.id.collateralName);
        isExistingCollateral = findViewById(R.id.isExistingCollateral);
        primarySecondaryCollateral = findViewById(R.id.primarySecondaryCollateral);
        valuationRequired = findViewById(R.id.valuationRequired);
        propertyOwner = findViewById(R.id.propertyOwner);
        locality = findViewById(R.id.locality);
        village = findViewById(R.id.village);
        state = findViewById(R.id.state);
        city = findViewById(R.id.city);
        district = findViewById(R.id.district);
        taluka = findViewById(R.id.taluka);
        propertyCategory = findViewById(R.id.propertyCategory);
        landmark = findViewById(R.id.landmark);
        pinCode = findViewById(R.id.pinCode);
        typeOfProperty = findViewById(R.id.typeOfProperty);
        estimatedPropertyValue = findViewById(R.id.estimatedPropertyValue);
        occupationStatus = findViewById(R.id.occupationStatus);
        propertyStatus = findViewById(R.id.propertyStatus);
        documentName = findViewById(R.id.documentName);
        documentId = findViewById(R.id.documentId);
        propertyTitle = findViewById(R.id.propertyTitle);
        houseNumber = findViewById(R.id.houseNumber);
        relationWithLoan = findViewById(R.id.relationWithLoan);
        khasraNumber = findViewById(R.id.khasraNumber);
        homeButton = findViewById(R.id.homeButton);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("accessToken", "");

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to dashboard activity
                Intent intent = new Intent(v.getContext(), DashboardActivity.class);
                startActivity(intent);
            }
        });

        final Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Disable the button to prevent multiple submissions
                    submitButton.setEnabled(false);
                    makeHttpRequest(accessToken, submitButton);
                    uploadDocumentsUsingRetrofit(accessToken, submitButton);
                }
            }
        });

    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(collateralName.getText().toString().trim())) {
            collateralName.setError("Please enter Collateral Name");
            return false;
        }

        if (TextUtils.isEmpty(isExistingCollateral.getText().toString().trim())) {
            isExistingCollateral.setError("Please enter Is Existing Collateral");
            return false;
        }

        if (TextUtils.isEmpty(collateralType.getText().toString().trim())) {
            collateralType.setError("Please enter Collateral Type");
            return false;
        }

        return true;
    }

    public void onUploadDocumentClick(View view) {
        // Open file picker to select any type of file
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("/");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, REQUEST_DOCUMENT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Please install a file manager app to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Context context = CollateralDetailsActivity.this;
        return RealPathUtil.getRealPath(context, uri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedFileUri = data.getData();
            String fileName = getFileName(selectedFileUri);
            if (fileName != null) {
                if (requestCode == REQUEST_DOCUMENT) {
                    docs.setText(fileName);
                    documentNames.put(REQUEST_DOCUMENT, fileName); // Store document name
                    adharDocsUri = selectedFileUri;
                    documentURIs.put(REQUEST_DOCUMENT, selectedFileUri);
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
            if (cursor
                    != null) {
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
                Toast.makeText(this, "Permission denied to read external storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeHttpRequest(String accessToken, final Button submitButton) {
        String url = BASE_URL + "api/v1/leads";

        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody.Builder formBodyBuilder = new FormBody.Builder()
                        .add("first_name", isExistingCollateral.getText().toString().trim())
                        .add("last_name", collateralType.getText().toString().trim())
                        .add("collateral_name", collateralName.getText().toString().trim())
                        .add("primary_secondary_collateral", primarySecondaryCollateral.getText().toString().trim())
                        .add("valuation_required", valuationRequired.getText().toString().trim())
                        .add("relation_with_loan", relationWithLoan.getText().toString().trim())
                        .add("property_owner", propertyOwner.getText().toString().trim())
                        .add("property_category", propertyCategory.getText().toString().trim())
                        .add("type_of_property", typeOfProperty.getText().toString().trim())
                        .add("occupation_status", occupationStatus.getText().toString().trim())
                        .add("property_status", propertyStatus.getText().toString().trim())
                        .add("property_title", propertyTitle.getText().toString().trim())
                        .add("house_number", houseNumber.getText().toString().trim())
                        .add("khasra_number", khasraNumber.getText().toString().trim())
                        .add("locality", locality.getText().toString().trim())
                        .add("village", village.getText().toString().trim())
                        .add("state", state.getText().toString().trim())
                        .add("city", city.getText().toString().trim())
                        .add("district", district.getText().toString().trim())
                        .add("taluka", taluka.getText().toString().trim())
                        .add("pin_code", pinCode.getText().toString().trim())
                        .add("landmark", landmark.getText().toString().trim())
                        .add("estimated_property_value", estimatedPropertyValue.getText().toString().trim())
                        .add("document_name", documentName.getText().toString().trim())
                        .add("document_id", documentId.getText().toString().trim());

                RequestBody formBody = formBodyBuilder.build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Call call = client.newCall(request);

                Response response = null;
                try {
                    response = call.execute();
                    String serverResponse = response.body().string();
                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(serverResponse);
                    boolean isError = jsonResponse.getBoolean("error");
                    if (!isError) {
                        // Get the lead data
                        JSONObject leadData = jsonResponse.getJSONObject("data");
                        String leadId = leadData.getString("lead_id");

                        // Pass lead ID to CAFActivity
                        Intent mainIntent = new Intent(CollateralDetailsActivity.this, CAFActivity.class);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Lead created successfully"); // Fixed toast message
                            }
                        });
                        startActivity(mainIntent);
                        finish();
                    } else {
                        // Enable the button again
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                submitButton.setEnabled(true);
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    // Enable the button again in case of an error
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            submitButton.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void uploadDocumentsUsingRetrofit(String accessToken, final Button submitButton) {
        Retrofit retrofit = getClient(BaseUrl.BASE_URL, accessToken);
        ApiService apiService = retrofit.create(ApiService.class);

        // Create MultipartBody.Part list for the documents
        List<MultipartBody.Part> documentParts = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : documentNames.entrySet()) {
            int requestCode = entry.getKey();
            String fileName = entry.getValue();
            Uri fileUri = null;
            switch (requestCode) {
                case REQUEST_DOCUMENT:
                    fileUri = adharDocsUri;
                    break;
            }

            String realPath = getRealPathFromURI(fileUri);
            File file = new File(realPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
            MultipartBody.Part documentPart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            documentParts.add(documentPart);

            // Log the file name and the request code
            Log.d("FileName", fileName);
            Log.d("RequestCode", String.valueOf(requestCode));
            Log.d("RealPath", realPath);
        }

        // Create document objects
        List<Document> documents = new ArrayList<>();
        String documentsJson = null;
        for (Map.Entry<Integer, String> entry : documentNames.entrySet()) {
            String fileName = entry.getValue();
            String doc_id = docs.getText().toString().trim();
            Document document = new Document(fileName, doc_id);
            documents.add(document);

            // Create an array to store strings of the document objects
            List<Document> documentInfo = new ArrayList<>();
            for (Document doc : documents) {
                documentInfo.add(doc);
            }
            // Convert the array to a JSON string
            documentsJson = new Gson().toJson(documentInfo);
        }

        // Log the JSON string
        Log.d("DocumentsJson", documentsJson);

        // Log documentParts
        Log.d("DocumentParts", documentParts.toString());

        apiService.uploadDocuments_collateral(
                RequestBody.create(MultipartBody.FORM, "kyc"),
                RequestBody.create(MultipartBody.FORM, documentsJson)

        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    makeHttpRequest(accessToken, submitButton);
                    Toast.makeText(CollateralDetailsActivity.this, "Documents Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("UploadError", "Response code: " + response.code() + ", Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(CollateralDetailsActivity.this, "Upload Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.e("UploadFailure", "Error: " + t.getMessage(), t);
                Toast.makeText(CollateralDetailsActivity.this, "Upload Failed onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}