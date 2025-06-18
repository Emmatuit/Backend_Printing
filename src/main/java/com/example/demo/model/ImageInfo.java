package com.example.demo.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ImageInfo {
    private String url;
    private String fileId;

    public ImageInfo() {
    }

    public ImageInfo(String url, String fileId) {
        this.url = url;
        this.fileId = fileId;
    }

    public String getUrl() {
        return url;
    }

    public String getFileId() {
        return fileId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
