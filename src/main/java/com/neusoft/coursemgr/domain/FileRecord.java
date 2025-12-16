package com.neusoft.coursemgr.domain;

import java.time.LocalDateTime;

public class FileRecord {

    private Long id;
    private String contentType;
    private String originalFilename;
    private Long size;
    private byte[] data;
    private Long uploaderAdminId;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getUploaderAdminId() {
        return uploaderAdminId;
    }

    public void setUploaderAdminId(Long uploaderAdminId) {
        this.uploaderAdminId = uploaderAdminId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
