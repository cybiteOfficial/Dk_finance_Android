package com.example.bankapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class KycActivity2 extends AppCompatActivity {
    EditText adharNumberEditText;
    EditText panNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_2);

        adharNumberEditText = findViewById(R.id.adharDocs);
        panNumberEditText = findViewById(R.id.panDocs);

        Button submitButton = findViewById(R.id.submit_button);

        TextView btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        adharNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(adharNumberEditText);
            }
        });

        panNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(panNumberEditText);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (validateInputs()) {
                    Intent intent = new Intent(KycActivity2.this, OtpActivity.class);
                    startActivity(intent);
                }
//            }
        });
    }

    public void onUploadDocumentClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Allow only PDF files
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/sdcard/tmp")));

        // Create a chooser with both intents
        Intent chooser = Intent.createChooser(intent, "Select Document");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });

        try {
            startActivityForResult(chooser, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Please install a file manager app to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedFileUri = data.getData();
            EditText editText = getCurrentFocus() instanceof EditText ? (EditText) getCurrentFocus() : null;
            if (editText != null && selectedFileUri != null) {
                String filename = getFileName(selectedFileUri);
                editText.setText(filename);
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

//    private boolean validateInputs() {
//        boolean isValid = true;
//
//        String adharNumber = adharNumberEditText.getText().toString().trim();
//        String panNumber = panNumberEditText.getText().toString().trim();
//
//        if (TextUtils.isEmpty(adharNumber)) {
//            adharNumberEditText.setError("Aadhar number is required");
//            isValid = false;
//        }
//
//        if (TextUtils.isEmpty(panNumber)) {
//            panNumberEditText.setError("PAN number is required");
//            isValid = false;
//        }
//
//        return isValid;
//    }
}
