package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StockOutVO {
    private Integer id;
    private Integer drugId;
    private String drugName;
    private String drugSpec;
    private Integer batchId;
    private String batchNo;
    private LocalDate expireDate;
    private Integer outType;
    private String outTypeName;
    private Integer quantity;
    private BigDecimal retailPrice;
    private BigDecimal costPrice;
    private BigDecimal totalAmount;
    private Integer operatorId;
    private String operatorUsername;
    private String remark;
    private LocalDateTime createTime;
}
