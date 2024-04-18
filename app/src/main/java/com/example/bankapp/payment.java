package com.example.bankapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.phonepe.intent.sdk.api.B2BPGRequest;
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.PhonePeInitException;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
                String base64Body = encodeDataToString(new JSONObject(data));
                try {
                    B2BPGRequest b2BPGRequest = createB2BPGRequest(base64Body, "/pg/v1/pay");
                    startActivityForResult(PhonePe.getImplicitIntent(payment.this, b2BPGRequest, ""), 1);
                } catch (PhonePeInitException e) {
                    // Handle PhonePe initialization exception
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Handle the result of the PhonePe transaction
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
            return null; // Handle the error as needed
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