package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.NotBlank;

public class UpdateLocationRequest {
    @NotBlank
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
