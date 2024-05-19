package com.example.bankapp;

public class Document {

    private String document_name;
    private String document_id;


    public Document(String document_name, String document_id) {
        this.document_name = document_name;
        this.document_id = document_id;
    }

    public String getDocument_name() {
        return document_name;
    }

    public void setDocument_name(String document_name) {
        this.document_name = document_name;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String covertToString() {
        return "{" +
                "document_name:" + document_name +
                ", document_id:" + document_id +
                '}';
    }


}
