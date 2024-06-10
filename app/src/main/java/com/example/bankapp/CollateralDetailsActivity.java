package com.example.bankapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class CollateralDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_DOCUMENT = 1;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;

    private EditText docs, isExistingCollateral, collateralType, collateralName, primarySecondaryCollateral, valuationRequired, relationWithLoan,
            propertyOwner, propertyCategory, typeOfProperty, occupationStatus, propertyStatus, propertyTitle, houseNumber, khasraNumber,
            locality, village, state, city, district, taluka, pinCode, landmark, estimatedPropertyValue, documentName, documentId;

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
                Toast.makeText(this, "Permission denied to read external storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}