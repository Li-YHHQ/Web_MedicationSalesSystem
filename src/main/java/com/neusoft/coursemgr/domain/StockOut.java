package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockOut {
    private Integer id;
    private Integer drugId;
    private Integer batchId;
    private Integer outType;
    private Integer quantity;
    private BigDecimal retailPrice;
    private BigDecimal costPrice;
    private BigDecimal totalAmount;
    private Integer operatorId;
    private String remark;
    private LocalDateTime createTime;
}
