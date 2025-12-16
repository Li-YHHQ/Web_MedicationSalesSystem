package com.neusoft.coursemgr.domain;

public class UploadFileResponse {

    private String url;
    private String originalFilename;
    private Long size;
    private String contentType;
    private Long uploaderAdminId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getUploaderAdminId() {
        return uploaderAdminId;
    }

    public void setUploaderAdminId(Long uploaderAdminId) {
        this.uploaderAdminId = uploaderAdminId;
    }
}
