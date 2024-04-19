package com.example.bankapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class KycActivity2 extends AppCompatActivity {
    EditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kyc_2);

        // Initialize phoneNumberEditText after setContentView
        phoneNumberEditText = findViewById(R.id.phoneNumber);
        Button submitButton = findViewById(R.id.submit_button);

        TextView btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Set OnClickListener for phoneNumberEditText
        phoneNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(v);
            }
        });

        // Set OnClickListener for submitButton
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to payment activity
                Intent intent = new Intent(KycActivity2.this, payment.class);
                startActivity(intent);
            }
        });
    }

    // Define the onUploadDocumentClick method
    public void onUploadDocumentClick(View view) {
        // Open a file picker or document upload dialog here
        // For example:
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow any file type to be selected
        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            // Handle the case where no app can handle the intent
            // Show a message to the user indicating that they need to install a file manager app
            Toast.makeText(this, "Please install a file manager app to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // The user has selected a document
            // Now, you can handle the selected document
            // For example, you can retrieve the URI of the selected document
            Uri selectedFileUri = data.getData();

            // Get the filename from the URI and set it as the text of the EditText
            if (selectedFileUri != null) {
                String filename = getFileName(selectedFileUri);
                phoneNumberEditText.setText(filename);
            }
        }
    }

    // Method to extract filename from URI
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
