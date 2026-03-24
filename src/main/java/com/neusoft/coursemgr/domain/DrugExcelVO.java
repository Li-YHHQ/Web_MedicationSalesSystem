package com.neusoft.coursemgr.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DrugExcelVO {

    @ExcelProperty(value = "药品编码", index = 0)
    private String drugCode;

    @ExcelProperty(value = "药品名称", index = 1)
    private String drugName;

    @ExcelProperty(value = "通用名称", index = 2)
    private String commonName;

    @ExcelProperty(value = "类别", index = 3)
    private String category;

    @ExcelProperty(value = "单位", index = 4)
    private String unit;

    @ExcelProperty(value = "规格", index = 5)
    private String spec;

    @ExcelProperty(value = "生产厂家", index = 6)
    private String manufacturer;

    @ExcelProperty(value = "批准文号", index = 7)
    private String approvalNo;

    @ExcelProperty(value = "条码", index = 8)
    private String barcode;

    @ExcelProperty(value = "成本价", index = 9)
    private BigDecimal costPrice;

    @ExcelProperty(value = "零售价", index = 10)
    private BigDecimal retailPrice;

    @ExcelProperty(value = "库存下限", index = 11)
    private Integer stockMin;
}
