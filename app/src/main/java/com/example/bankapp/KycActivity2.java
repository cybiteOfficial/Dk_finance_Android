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

        phoneNumberEditText = findViewById(R.id.phoneNumber);
        Button submitButton = findViewById(R.id.submit_button);

        TextView btnSave = findViewById(R.id.save_button);
        btnSave.setPaintFlags(btnSave.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // OnClickListener for phoneNumberEditText
        phoneNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUploadDocumentClick(v);
            }
        });

        // OnClickListener for submitButton
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirecting to payment activity
                Intent intent = new Intent(KycActivity2.this, payment.class);
                startActivity(intent);
            }
        });
    }

    public void onUploadDocumentClick(View view) {
        // Opening a file picker or document upload dialog here
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allowing any file type to be selected
        try {
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            // Showing a message to the user indicating that they need to install a file manager app
            Toast.makeText(this, "Please install a file manager app to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            // Getting the filename from the URI and setting it as the text of the EditText
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
