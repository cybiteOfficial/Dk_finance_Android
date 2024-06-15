package com.example.bankapp;

import static com.example.bankapp.RetrofitClient.getClient;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PhotographUploadActivity extends AppCompatActivity {

    private static final String TAG = PhotographUploadActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

    private ImageView uploadImage;
    private ImageView homeButton;
    private SharedPreferences sharedPreferences;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph_upload);

        uploadImage = findViewById(R.id.uploadImage);
        homeButton = findViewById(R.id.homeButton);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String accessToken = sharedPreferences.getString("accessToken", "");

        // Get the application ID from the previous activity intent
        Intent intent = getIntent();
        String application_id = intent.getStringExtra("application_id");

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
                // Disable the button to prevent multiple submissions
                submitButton.setEnabled(false);
                uploadDocumentsUsingRetrofit(accessToken, submitButton, application_id);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionsAndOpenFileChooser();
            }
        });
    }

    private void checkPermissionsAndOpenFileChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openFileChooser();
        }
    }

    private void openFileChooser() {
        // Create a chooser intent to select either from gallery or capture from camera
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);

        // Create an intent to pick image from gallery
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Create an intent to capture image from camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Add the camera intent as an extra to the chooser intent
        chooserIntent.putExtra(Intent.EXTRA_INTENT, cameraIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Select Picture");

        // Put both intents into an intent array
        Intent[] intentArray = {galleryIntent};

        // Set the intent array as an extra for the chooser intent
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        // Start the chooser activity
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                handleGalleryImage(data.getData());
            } else if (data != null && data.getExtras() != null && data.getExtras().get("data") != null) {
                handleCameraImage((Bitmap) data.getExtras().get("data"));
            }
        }
    }

    private void handleGalleryImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            uploadImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error while retrieving image from gallery: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCameraImage(Bitmap bitmap) {
        uploadImage.setImageBitmap(bitmap);
    }

    private void uploadDocumentsUsingRetrofit(String accessToken, final Button submitButton, String application_id) {
        Retrofit retrofit = getClient(BaseUrl.BASE_URL, accessToken);
        ApiService apiService = retrofit.create(ApiService.class);

        // Get the image from the image view
        Bitmap bitmap = ((BitmapDrawable) uploadImage.getDrawable()).getBitmap();
        File file = new File(getCacheDir(), "img_" + application_id + ".jpg");

        try {
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos); // Adjust compression quality as needed
            byte[] bitmapData = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error creating file: " + e.getMessage());
            e.printStackTrace();
        }

        // Create a request body for the image file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // Create a request body for the document type
        RequestBody documentType = RequestBody.create(MediaType.parse("text/plain"), "photos");

        // Call the uploadDocuments_photo method in the ApiService interface
        Call<Void> call = apiService.uploadDocuments_photo(documentType, body, RequestBody.create(MediaType.parse("text/plain"), application_id));

        // Enqueue the call
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PhotographUploadActivity.this, "Photograph uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PhotographUploadActivity.this, "Failed to upload photograph", Toast.LENGTH_SHORT).show();
                }
                submitButton.setEnabled(true);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failed to upload photograph: " + t.getMessage());
                Toast.makeText(PhotographUploadActivity.this, "Failed to upload photograph", Toast.LENGTH_SHORT).show();
                submitButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
