package com.neusoft.coursemgr.domain;

import lombok.Data;

@Data
public class StockSyncDetail {
    /** 药品编码 */
    private String drugCode;
    /** 药品名称（新增药品时与 drugCode 相同） */
    private String drugName;
    /** 变化类型：NEW / IN / OUT / UNCHANGED */
    private String changeType;
    /** 原库存，新增药品为 0 */
    private int oldQuantity;
    /** 新库存 */
    private int newQuantity;
    /** 差值：IN/NEW 为正（入库量），OUT 为正（出库量），UNCHANGED 为 0 */
    private int diff;
}
