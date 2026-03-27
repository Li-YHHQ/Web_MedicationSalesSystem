package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateDrugRequest;
import com.neusoft.coursemgr.domain.Drug;
import com.neusoft.coursemgr.domain.UpdateDrugRequest;

import java.util.List;
import java.util.Map;

public interface DrugService {

    PageResult<Drug> list(String keyword, String category, Integer status, int page, int size);

    Drug getById(Integer id);

    void create(CreateDrugRequest req);

    void update(Integer id, UpdateDrugRequest req);

    void delete(Integer id);

    List<Drug> listAll(String keyword, String category, Integer status);

    /**
     * 格式一（药品档案表）导入：按列名匹配，支持新增/更新药品及初始库存批次。
     *
     * @param nameToIdx 表头列名 → 列索引映射
     * @param rows      数据行列表（列索引 → 单元格字符串值）
     */
    void importDrugs(Map<String, Integer> nameToIdx, List<Map<Integer, String>> rows);

    /**
     * 格式二（效期批次表）导入：按药品编码查找药品，回填或新建批次。
     *
     * @param nameToIdx 表头列名 → 列索引映射
     * @param rows      数据行列表（列索引 → 单元格字符串值）
     */
    void importBatches(Map<String, Integer> nameToIdx, List<Map<Integer, String>> rows);
}
