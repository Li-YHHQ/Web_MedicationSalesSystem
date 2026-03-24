package com.neusoft.coursemgr.domain;

import lombok.Data;

@Data
public class UpdateSupplierRequest {
    private String name;
    private String contact;
    private String phone;
    private String address;
    private String remark;
    private Integer status;
}
