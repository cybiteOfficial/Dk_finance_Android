package com.example.bankapp;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("api/v1/upload_document")
    Call<Void> uploadDocuments(
            @Part("document_type") RequestBody documentType,
            @Part("documents") RequestBody documents,
            @Part List<MultipartBody.Part> file,
            @Part("kyc_id") RequestBody kycId
    );
}