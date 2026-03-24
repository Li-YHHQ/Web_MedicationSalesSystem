package com.neusoft.coursemgr.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FinanceDaily {
    private Integer id;
    private LocalDate statDate;
    private BigDecimal salesAmount;
    private BigDecimal costAmount;
    private BigDecimal profitAmount;
    private Integer salesCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
