package com.example.bankapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;

public class PhotographUploadActivity extends AppCompatActivity {

    private static final String TAG = PhotographUploadActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;

    private ImageView uploadImage;
    private ImageView homeButton;
    private SharedPreferences sharedPreferences;

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

        // Fetch and display the existing photo
        fetchAndDisplayPhoto(accessToken, application_id);
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

        // Create OkHttp client and request body
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                .addFormDataPart("documentType", "photos")
                .addFormDataPart("application_id", application_id)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url(BaseUrl.BASE_URL + "/upload/photo") // Replace with your endpoint
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to upload photograph: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PhotographUploadActivity.this, "Failed to upload photograph", Toast.LENGTH_SHORT).show();
                        submitButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhotographUploadActivity.this, "Photograph uploaded successfully", Toast.LENGTH_SHORT).show();
                            try {
                                Log.d(TAG, "Upload Response: " + response.body().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            submitButton.setEnabled(true);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhotographUploadActivity.this, "Failed to upload photograph", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Upload Failed: " + response.code() + " " + response.message());
                            submitButton.setEnabled(true);
                        }
                    });
                }
            }
        });
    }


    private void fetchAndDisplayPhoto(String accessToken, String application_id) {
        OkHttpClient client = new OkHttpClient();

        // Replace with your API endpoint and parameter handling
        String photoUrl = BaseUrl.BASE_URL + "/photos/" + application_id;

        Request request = new Request.Builder()
                .url(photoUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PhotographUploadActivity.this, "Failed to fetch photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PhotographUploadActivity.this, "Failed to fetch photo, code: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                // Read the response body and convert it to a Bitmap
                byte[] imageData = response.body().bytes();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                // Update the ImageView on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadImage.setImageBitmap(bitmap);
                    }
                });
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