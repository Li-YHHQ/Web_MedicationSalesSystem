package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateSupplierRequest;
import com.neusoft.coursemgr.domain.Supplier;
import com.neusoft.coursemgr.domain.UpdateSupplierRequest;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.SupplierMapper;
import com.neusoft.coursemgr.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierServiceImpl.class);

    private final SupplierMapper supplierMapper;

    public SupplierServiceImpl(SupplierMapper supplierMapper) {
        this.supplierMapper = supplierMapper;
    }

    @Override
    public PageResult<Supplier> list(String keyword, Integer status, int page, int size) {
        int offset = (page - 1) * size;
        List<Supplier> list = supplierMapper.selectList(keyword, status, offset, size);
        long total = supplierMapper.countList(keyword, status);
        return new PageResult<>(total, list);
    }

    @Override
    public Supplier getById(Integer id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BizException(404, "供应商不存在");
        }
        return supplier;
    }

    @Override
    public List<Supplier> listAll() {
        return supplierMapper.selectList(null, 1, null, null);
    }

    @Override
    public void create(CreateSupplierRequest req) {
        Supplier supplier = new Supplier();
        supplier.setName(req.getName());
        supplier.setContact(req.getContact());
        supplier.setPhone(req.getPhone());
        supplier.setAddress(req.getAddress());
        supplier.setRemark(req.getRemark());
        supplier.setStatus(1);

        supplierMapper.insert(supplier);
        log.info("supplier created, id={}, name={}", supplier.getId(), supplier.getName());
    }

    @Override
    public void update(Integer id, UpdateSupplierRequest req) {
        Supplier existing = supplierMapper.selectById(id);
        if (existing == null) {
            throw new BizException(404, "供应商不存在");
        }

        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier.setName(req.getName());
        supplier.setContact(req.getContact());
        supplier.setPhone(req.getPhone());
        supplier.setAddress(req.getAddress());
        supplier.setRemark(req.getRemark());
        supplier.setStatus(req.getStatus());

        supplierMapper.updateById(supplier);
        log.info("supplier updated, id={}", id);
    }

    @Override
    public void delete(Integer id) {
        Supplier existing = supplierMapper.selectById(id);
        if (existing == null) {
            throw new BizException(404, "供应商不存在");
        }
        supplierMapper.deleteById(id);
        log.info("supplier deleted (soft), id={}", id);
    }
}
