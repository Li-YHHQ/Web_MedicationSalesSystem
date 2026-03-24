package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockOutRequest {

    @NotNull(message = "药品ID不能为空")
    private Integer drugId;

    @NotNull(message = "批次ID不能为空")
    private Integer batchId;

    @NotNull(message = "出库类型不能为空")
    private Integer outType;

    @NotNull(message = "出库数量不能为空")
    @Min(value = 1, message = "出库数量最小为1")
    private Integer quantity;

    private BigDecimal retailPrice;

    private String remark;
}
