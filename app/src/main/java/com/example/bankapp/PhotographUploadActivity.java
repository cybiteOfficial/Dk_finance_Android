package com.example.bankapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class PhotographUploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView uploadIcon;
    private TextView labelUploadImage;
    private ImageView uploadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph_upload);
        uploadIcon = findViewById(R.id.upload_icon);
        labelUploadImage = findViewById(R.id.label_upload_image);
        uploadedImage = findViewById(R.id.uploaded_image);
    }

    public void openFileChooser(View view) {
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        chooserIntent.putExtra(Intent.EXTRA_INTENT, cameraIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Select Picture");

        Intent[] intentArray = {galleryIntent};
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    uploadedImage.setImageBitmap(bitmap);
                    showSelectedImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                uploadedImage.setImageBitmap(bitmap);
                showSelectedImage();
            }
        }
    }

    private void showSelectedImage() {
        uploadIcon.setVisibility(View.GONE);
        labelUploadImage.setVisibility(View.GONE);
        uploadedImage.setVisibility(View.VISIBLE);
    }
}