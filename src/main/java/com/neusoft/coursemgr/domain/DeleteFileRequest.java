package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.NotBlank;

public class DeleteFileRequest {

    @NotBlank(message = "url不能为空")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
