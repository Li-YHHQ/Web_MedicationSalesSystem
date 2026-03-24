package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockInVO {
    private Integer id;
    private Integer drugId;
    private String drugName;
    private Integer batchId;
    private String batchNo;
    private Integer quantity;
    private BigDecimal costPrice;
    private BigDecimal totalAmount;
    private Integer supplierId;
    private String supplierName;
    private Integer operatorId;
    private String operatorUsername;
    private String remark;
    private LocalDateTime createTime;
}
