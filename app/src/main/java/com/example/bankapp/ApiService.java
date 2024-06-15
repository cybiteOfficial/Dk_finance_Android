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
    @Multipart
    @POST("api/v1/upload_document")
    Call<Void> uploadDocuments_collateral(
            @Part("document_type") RequestBody documentType
    );

    @Multipart
    @POST("api/v1/upload_document")
    Call<Void> uploadDocuments_other(
            @Part("document_type") RequestBody documentType,
            @Part("documents") RequestBody documents,
            @Part List<MultipartBody.Part> file,
            @Part("application_id") RequestBody applicationId
    );

    @Multipart
    @POST("api/v1/upload_document")
    Call<Void> uploadDocuments_photo(
            @Part("document_type") RequestBody documentType,
            @Part MultipartBody.Part file,
            @Part("application_id") RequestBody applicationId
    );

    @Multipart
    @POST("api/v1/collateral_details")
    Call<Void> uploadCollateralDetails(
            @Part("collateralType") RequestBody collateralType,
            @Part("collateralName") RequestBody collateralName,
            @Part("primarySecondary") RequestBody primarySecondary,
            @Part("valuationRequired") RequestBody valuationRequired,
            @Part("relationshipWithLoan") RequestBody relationshipWithLoan,
            @Part("propertyOwner") RequestBody propertyOwner,
            @Part("propertyCategory") RequestBody propertyCategory,
            @Part("propertyType") RequestBody propertyType,
            @Part("occupationStatus") RequestBody occupationStatus,
            @Part("propertyStatus") RequestBody propertyStatus,
            @Part("propertyTitle") RequestBody propertyTitle,
            @Part("houseFlatShopNo") RequestBody houseFlatShopNo,
            @Part("khasraPlotNo") RequestBody khasraPlotNo,
            @Part("locality") RequestBody locality,
            @Part("village") RequestBody village,
            @Part("state") RequestBody state,
            @Part("district") RequestBody district,
            @Part("city") RequestBody city,
            @Part("taluka") RequestBody taluka,
            @Part("pincode") RequestBody pincode,
            @Part("landmark") RequestBody landmark,
            @Part("estimatedPropertyValue") RequestBody estimatedPropertyValue,
            @Part("documentName") RequestBody documentName,
            @Part("isExisting") RequestBody isExisting,
            @Part("applicant_id") RequestBody applicationId,
            @Part MultipartBody.Part documentUpload
    );

}