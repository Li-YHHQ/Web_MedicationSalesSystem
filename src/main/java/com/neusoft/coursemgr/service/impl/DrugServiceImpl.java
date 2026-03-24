package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.common.PageResult;
import com.neusoft.coursemgr.domain.CreateDrugRequest;
import com.neusoft.coursemgr.domain.Drug;
import com.neusoft.coursemgr.domain.UpdateDrugRequest;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.DrugMapper;
import com.neusoft.coursemgr.service.DrugService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugServiceImpl implements DrugService {

    private static final Logger log = LoggerFactory.getLogger(DrugServiceImpl.class);

    private final DrugMapper drugMapper;

    public DrugServiceImpl(DrugMapper drugMapper) {
        this.drugMapper = drugMapper;
    }

    @Override
    public PageResult<Drug> list(String keyword, String category, Integer status, int page, int size) {
        int offset = (page - 1) * size;
        List<Drug> list = drugMapper.selectList(keyword, category, status, offset, size);
        long total = drugMapper.countList(keyword, category, status);
        return new PageResult<>(total, list);
    }

    @Override
    public Drug getById(Integer id) {
        Drug drug = drugMapper.selectById(id);
        if (drug == null) {
            throw new BizException(404, "药品不存在");
        }
        return drug;
    }

    @Override
    public void create(CreateDrugRequest req) {
        Drug existing = drugMapper.selectByDrugCode(req.getDrugCode());
        if (existing != null) {
            throw new BizException(400, "药品编码已存在");
        }

        Drug drug = new Drug();
        drug.setDrugCode(req.getDrugCode());
        drug.setDrugName(req.getDrugName());
        drug.setCommonName(req.getCommonName());
        drug.setCategory(req.getCategory());
        drug.setUnit(req.getUnit());
        drug.setSpec(req.getSpec());
        drug.setManufacturer(req.getManufacturer());
        drug.setApprovalNo(req.getApprovalNo());
        drug.setBarcode(req.getBarcode());
        drug.setCostPrice(req.getCostPrice());
        drug.setRetailPrice(req.getRetailPrice());
        drug.setStockMin(req.getStockMin() != null ? req.getStockMin() : 0);
        drug.setStatus(1);

        drugMapper.insert(drug);
        log.info("drug created, id={}, drugCode={}", drug.getId(), drug.getDrugCode());
    }

    @Override
    public void update(Integer id, UpdateDrugRequest req) {
        Drug existing = drugMapper.selectById(id);
        if (existing == null) {
            throw new BizException(404, "药品不存在");
        }

        Drug drug = new Drug();
        drug.setId(id);
        drug.setDrugName(req.getDrugName());
        drug.setCommonName(req.getCommonName());
        drug.setCategory(req.getCategory());
        drug.setUnit(req.getUnit());
        drug.setSpec(req.getSpec());
        drug.setManufacturer(req.getManufacturer());
        drug.setApprovalNo(req.getApprovalNo());
        drug.setBarcode(req.getBarcode());
        drug.setCostPrice(req.getCostPrice());
        drug.setRetailPrice(req.getRetailPrice());
        drug.setStockMin(req.getStockMin());
        drug.setStatus(req.getStatus());

        drugMapper.updateById(drug);
        log.info("drug updated, id={}", id);
    }

    @Override
    public void delete(Integer id) {
        Drug existing = drugMapper.selectById(id);
        if (existing == null) {
            throw new BizException(404, "药品不存在");
        }
        drugMapper.deleteById(id);
        log.info("drug deleted (soft), id={}", id);
    }

    @Override
    public List<Drug> listAll(String keyword, String category, Integer status) {
        return drugMapper.selectList(keyword, category, status, null, null);
    }

    @Override
    public void importDrugs(List<Drug> drugs) {
        int imported = 0;
        int skipped = 0;
        for (Drug drug : drugs) {
            if (drug.getDrugCode() == null || drug.getDrugCode().isBlank()) {
                skipped++;
                continue;
            }
            Drug existing = drugMapper.selectByDrugCode(drug.getDrugCode());
            if (existing != null) {
                skipped++;
                continue;
            }
            drug.setStatus(1);
            if (drug.getStockMin() == null) {
                drug.setStockMin(0);
            }
            drugMapper.insert(drug);
            imported++;
        }
        log.info("drug import finished, imported={}, skipped={}", imported, skipped);
    }
}
