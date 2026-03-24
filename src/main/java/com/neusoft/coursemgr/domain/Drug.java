package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Drug {
    private Integer id;
    private String drugCode;
    private String drugName;
    private String commonName;
    private String category;
    private String unit;
    private String spec;
    private String manufacturer;
    private String approvalNo;
    private String barcode;
    private BigDecimal costPrice;
    private BigDecimal retailPrice;
    private Integer stockMin;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
