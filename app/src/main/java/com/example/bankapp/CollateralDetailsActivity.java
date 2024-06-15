package com.example.bankapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bankapp.environment.BaseUrl;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollateralDetailsActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private static final String TAG = "CollateralActivity";

    private EditText collateralType, collateralName, primarySecondary, valuationRequired, relationshipWithLoan;
    private EditText propertyOwner, propertyCategory, propertyType, occupationStatus, propertyStatus;
    private EditText propertyTitle, houseFlatShopNo, khasraPlotNo, locality, village, state;
    private EditText district, city, taluka, pincode, landmark, estimatedPropertyValue, documentName, isExisting, docs;
    private EditText docFileEditText;
    private Button submitButton;
    private Uri selectedDocumentUri;
    private String applicationId;
    private String accessToken;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collateral_details);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        accessToken = sharedPreferences.getString("accessToken", "");

        Intent intent = getIntent();
        applicationId = intent.getStringExtra("application_id");

        // Initialize EditText fields
        collateralType = findViewById(R.id.collateralType);
        collateralName = findViewById(R.id.collateralName);
        primarySecondary = findViewById(R.id.primarySecondaryCollateral);
        valuationRequired = findViewById(R.id.valuationRequired);
        relationshipWithLoan = findViewById(R.id.relationWithLoan);
        propertyOwner = findViewById(R.id.propertyOwner);
        propertyCategory = findViewById(R.id.propertyCategory);
        propertyType = findViewById(R.id.typeOfProperty);
        occupationStatus = findViewById(R.id.occupationStatus);
        propertyStatus = findViewById(R.id.propertyStatus);
        propertyTitle = findViewById(R.id.propertyTitle);
        houseFlatShopNo = findViewById(R.id.houseNumber);
        khasraPlotNo = findViewById(R.id.khasraNumber);
        locality = findViewById(R.id.locality);
        village = findViewById(R.id.village);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        city = findViewById(R.id.city);
        taluka = findViewById(R.id.taluka);
        pincode = findViewById(R.id.pinCode);
        landmark = findViewById(R.id.landmark);
        estimatedPropertyValue = findViewById(R.id.estimatedPropertyValue);
        documentName = findViewById(R.id.documentName);
        isExisting = findViewById(R.id.isExistingCollateral);
        docs = findViewById(R.id.docs);

        submitButton = findViewById(R.id.submit_button);

        docs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCollateralDetails();
            }
        });
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
                    if (fileUri != null) {
                        String fileName = getFileNameFromUri(fileUri);
                        docs.setText(fileName);
                        selectedDocumentUri = fileUri;
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

    private void submitCollateralDetails() {
        try {
            Log.d(TAG, "submitCollateralDetails: Submitting collateral details");

            // Validate the inputs
            if (selectedDocumentUri == null) {
                Toast.makeText(this, "Please upload a document", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create the request body parts for the API call
            RequestBody collateralTypePart = createPartFromString(collateralType.getText().toString().trim());
            RequestBody collateralNamePart = createPartFromString(collateralName.getText().toString().trim());
            RequestBody primarySecondaryPart = createPartFromString(primarySecondary.getText().toString().trim());
            RequestBody valuationRequiredPart = createPartFromString(valuationRequired.getText().toString().trim());
            RequestBody relationshipWithLoanPart = createPartFromString(relationshipWithLoan.getText().toString().trim());
            RequestBody propertyOwnerPart = createPartFromString(propertyOwner.getText().toString().trim());
            RequestBody propertyCategoryPart = createPartFromString(propertyCategory.getText().toString().trim());
            RequestBody propertyTypePart = createPartFromString(propertyType.getText().toString().trim());
            RequestBody occupationStatusPart = createPartFromString(occupationStatus.getText().toString().trim());
            RequestBody propertyStatusPart = createPartFromString(propertyStatus.getText().toString().trim());
            RequestBody propertyTitlePart = createPartFromString(propertyTitle.getText().toString().trim());
            RequestBody houseFlatShopNoPart = createPartFromString(houseFlatShopNo.getText().toString().trim());
            RequestBody khasraPlotNoPart = createPartFromString(khasraPlotNo.getText().toString().trim());
            RequestBody localityPart = createPartFromString(locality.getText().toString().trim());
            RequestBody villagePart = createPartFromString(village.getText().toString().trim());
            RequestBody statePart = createPartFromString(state.getText().toString().trim());
            RequestBody districtPart = createPartFromString(district.getText().toString().trim());
            RequestBody cityPart = createPartFromString(city.getText().toString().trim());
            RequestBody talukaPart = createPartFromString(taluka.getText().toString().trim());
            RequestBody pincodePart = createPartFromString(pincode.getText().toString().trim());
            RequestBody landmarkPart = createPartFromString(landmark.getText().toString().trim());
            RequestBody estimatedPropertyValuePart = createPartFromString(estimatedPropertyValue.getText().toString().trim());
            RequestBody documentNamePart = createPartFromString(documentName.getText().toString().trim());
            RequestBody isExistingPart = createPartFromString(isExisting.getText().toString().trim());
            RequestBody applicationIdPart = createPartFromString(applicationId);

            // Create documentPart
            MultipartBody.Part documentPart = null;
            if (selectedDocumentUri != null) {
                try {
                    String fileName = getFileNameFromUri(selectedDocumentUri);
                    String mimeType = getContentResolver().getType(selectedDocumentUri);
                    if (mimeType == null) {
                        mimeType = "*/*"; // Default to all MIME types
                    }

                    InputStream inputStream = getContentResolver().openInputStream(selectedDocumentUri);
                    byte[] fileBytes = new byte[inputStream.available()];
                    inputStream.read(fileBytes);
                    inputStream.close();

                    RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), fileBytes);
                    documentPart = MultipartBody.Part.createFormData("uploadDocument", fileName, requestFile);

                } catch (IOException e) {
                    Log.e(TAG, "submitCollateralDetails: Error reading file", e);
                    Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "Please select a document to upload", Toast.LENGTH_SHORT).show();
                return;
            }

            // Make Retrofit API call
            ApiService apiService = RetrofitClient.getClient(BaseUrl.BASE_URL, accessToken).create(ApiService.class);

            Call<Void> call = apiService.uploadCollateralDetails(
                    collateralTypePart, collateralNamePart, primarySecondaryPart, valuationRequiredPart,
                    relationshipWithLoanPart, propertyOwnerPart, propertyCategoryPart, propertyTypePart,
                    occupationStatusPart, propertyStatusPart, propertyTitlePart, houseFlatShopNoPart,
                    khasraPlotNoPart, localityPart, villagePart, statePart, districtPart, cityPart,
                    talukaPart, pincodePart, landmarkPart, estimatedPropertyValuePart, documentNamePart,
                    isExistingPart, applicationIdPart, documentPart
            );

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "submitCollateralDetails: Successful");
                        Toast.makeText(CollateralDetailsActivity.this, "Collateral details submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String errorMessage = response.errorBody().string();
                            Log.e(TAG, "submitCollateralDetails: Error - " + errorMessage);
                            Toast.makeText(CollateralDetailsActivity.this, "Failed to submit collateral details: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e(TAG, "submitCollateralDetails: Error reading error response", e);
                            Toast.makeText(CollateralDetailsActivity.this, "Failed to submit collateral details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "submitCollateralDetails: Failure", t);
                    Toast.makeText(CollateralDetailsActivity.this, "Error occurred while submitting details", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "submitCollateralDetails: Exception", e);
            Toast.makeText(this, "Error submitting collateral details", Toast.LENGTH_SHORT).show();
        }
    }

    private RequestBody createPartFromString(String value) {
        return RequestBody.create(MultipartBody.FORM, value.isEmpty() ? "" : value);
    }

}