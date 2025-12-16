package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    List<Product> listPublic(Long categoryId, String keyword, BigDecimal minPrice, BigDecimal maxPrice, Integer isPrescription);

    Product getDetail(Long id);

    List<Product> adminList(Long adminUserId, Long categoryId, String keyword);

    Long adminCreate(Long adminUserId, Product product);

    void adminUpdate(Long adminUserId, Product product);

    void adminUpdateStatus(Long adminUserId, Long id, Integer status);

    void adminDelete(Long adminUserId, Long id);
}
