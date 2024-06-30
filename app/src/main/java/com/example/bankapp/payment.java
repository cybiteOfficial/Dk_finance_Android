package com.example.bankapp;
import static com.example.bankapp.environment.BaseUrl.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.phonepe.intent.sdk.api.B2BPGRequest;
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.SharedPreferences;

public class payment extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String leadID = getIntent().getStringExtra("leadId");
        final String leadUUID = getIntent().getStringExtra("leadUUID");

        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button paymentButton = findViewById(R.id.paymentButton);
        PhonePe.init(this);
        HashMap<String,Object> data = new HashMap<>();
        data.put("merchantTransactionId", String.valueOf(System.currentTimeMillis()));
        data.put("merchantId", "PGTESTPAYUAT");
        data.put("merchantUserID",String.valueOf(System.currentTimeMillis()));
        data.put("amount",600);
        data.put("mobileNumber","9669454554");
        data.put("callBackUrl","https://webhook.site/f00823e2-a622-4f9a-8f58-3969b4564f96");
        JSONObject mPaymentInstrument = new JSONObject();
        try {
            mPaymentInstrument.put("type","PAY_PAGE");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        data.put("paymentInstrument",mPaymentInstrument);

        paymentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String accessToken = sharedPreferences.getString("accessToken", "");

                makeHttpRequest(accessToken, leadID, leadUUID);
//                String base64Body = encodeDataToString(new JSONObject(data));
//                try {
//                    B2BPGRequest b2BPGRequest = createB2BPGRequest(base64Body, "/pg/v1/pay");
//                    startActivityForResult(PhonePe.getImplicitIntent(payment.this, b2BPGRequest, ""), 1);
//                } catch (PhonePeInitException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private void makeHttpRequest(String accessToken, String leadID, String leadUUID) {
        String url = BASE_URL + "api/v1/create_app_id?lead_id=" + leadID;

        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestBody formBody = new FormBody.Builder()
                        .build();

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
                    assert response.body() != null;
                    final String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Log.d("Response", responseBody);
                    boolean isError = jsonObject.getBoolean("error");
                    if (!isError) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String applicationId = data.getString("application_id");

                        Log.d("Application ID", applicationId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Display the response in a Toast message
                                Toast.makeText(payment.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                                // Pass the application ID to the next activity
                                Intent mainIntent = new Intent(payment.this, NewRegistrationActivity2.class);
                                mainIntent.putExtra("applicationId", applicationId);
                                startActivity(mainIntent);
                                finish();
                            }
                        });
                    } else {
                        final String message = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(payment.this, message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Payment successful, redirecting to main page
                Intent intent = new Intent(payment.this, NewRegistrationActivity2.class);

                startActivity(intent);
                finish();
            } else {
                // Payment failed or cancelled, showing a message to the user
                Toast.makeText(this, "Payment was not successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeDataToString(JSONObject data) {
        String jsonString = data.toString();
        return android.util.Base64.encodeToString(jsonString.getBytes(), android.util.Base64.DEFAULT);
    }

    private String sha256(String input) {
        try {
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private B2BPGRequest createB2BPGRequest(String base64Body, String apiEndPoint) {
        String checksum = calculateChecksum(base64Body);
        B2BPGRequestBuilder builder = new B2BPGRequestBuilder();
        return builder
                .setData(base64Body)
                .setChecksum(checksum)
                .setUrl(apiEndPoint)
                .build();
    }

    private String calculateChecksum(String base64Body) {
        String concatenatedString = base64Body + "/pg/v1/pay" + "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399";
        return sha256(concatenatedString) + "###1";
    }
}