package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateDrugRequest {
    private String drugName;
    private String commonName;
    private String category;
    private String unit;
    private String spec;
    private String manufacturer;
    private String approvalNo;
    private String barcode;

    @DecimalMin(value = "0", message = "成本价不能为负")
    private BigDecimal costPrice;

    @DecimalMin(value = "0", message = "零售价不能为负")
    private BigDecimal retailPrice;

    @Min(value = 0, message = "库存下限不能为负")
    private Integer stockMin;

    private Integer status;
}
