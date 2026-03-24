package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StockBatch {
    private Integer id;
    private Integer drugId;
    private String batchNo;
    private LocalDate produceDate;
    private LocalDate expireDate;
    private LocalDate stockInDate;
    private Integer quantity;
    private BigDecimal costPrice;
    private Integer supplierId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
