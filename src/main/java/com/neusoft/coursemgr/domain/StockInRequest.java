package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockInRequest {

    @NotNull(message = "药品ID不能为空")
    private Integer drugId;

    private String batchNo;

    private LocalDate produceDate;

    @NotNull(message = "有效期不能为空")
    private LocalDate expireDate;

    private LocalDate stockInDate;

    @NotNull(message = "入库数量不能为空")
    @Min(value = 1, message = "入库数量最小为1")
    private Integer quantity;

    @NotNull(message = "进价不能为空")
    @DecimalMin(value = "0", message = "进价不能为负")
    private BigDecimal costPrice;

    private Integer supplierId;
    private String remark;
}
