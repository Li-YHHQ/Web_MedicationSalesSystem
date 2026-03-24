package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateSupplierRequest;
import com.neusoft.coursemgr.domain.Supplier;
import com.neusoft.coursemgr.domain.UpdateSupplierRequest;

import java.util.List;

public interface SupplierService {

    PageResult<Supplier> list(String keyword, Integer status, int page, int size);

    Supplier getById(Integer id);

    List<Supplier> listAll();

    void create(CreateSupplierRequest req);

    void update(Integer id, UpdateSupplierRequest req);

    void delete(Integer id);
}
