package com.example.bankapp;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class KycActivity2 extends AppCompatActivity {
    EditText adharNumberEditText;
    EditText panNumberEditText;
    Button submitButton;

    // Request codes for document types
    private static final int REQUEST_ADHAR_DOCUMENT = 1;
    private static final int REQUEST_PAN_DOCUMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_2);

        adharNumberEditText = findViewById(R.id.adharDocs);
        panNumberEditText = findViewById(R.id.panDocs);
        submitButton = findViewById(R.id.submit_button);

        // Set click listeners for EditText fields
        adharNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(REQUEST_ADHAR_DOCUMENT);
            }
        });

        panNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(REQUEST_PAN_DOCUMENT);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KycActivity2.this, OtpActivity.class);
                startActivity(intent);
            }
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




}