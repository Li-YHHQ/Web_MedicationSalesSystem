package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateDrugRequest;
import com.neusoft.coursemgr.domain.Drug;
import com.neusoft.coursemgr.domain.UpdateDrugRequest;

import java.util.List;

public interface DrugService {

    PageResult<Drug> list(String keyword, String category, Integer status, int page, int size);

    Drug getById(Integer id);

    void create(CreateDrugRequest req);

    void update(Integer id, UpdateDrugRequest req);

    void delete(Integer id);

    List<Drug> listAll(String keyword, String category, Integer status);

    void importDrugs(List<Drug> drugs);
}
