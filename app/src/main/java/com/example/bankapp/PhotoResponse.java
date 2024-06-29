package com.example.bankapp;

import com.google.gson.annotations.SerializedName;

public class PhotoResponse {

    @SerializedName("uuid")
    private String uuid;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("file")
    private String file;

    // Other fields as needed

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String file() {
        return file;
    }

    public void setPhotoUrl(String photoUrl) {
        this.file = photoUrl;
    }
}