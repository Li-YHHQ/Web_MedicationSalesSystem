package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSupplierRequest {

    @NotBlank(message = "供应商名称不能为空")
    private String name;

    private String contact;
    private String phone;
    private String address;
    private String remark;
}
