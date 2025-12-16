package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.Category;

import java.util.List;

public interface CategoryService {

    List<Category> listEnabled();

    List<Category> adminListAll(Long adminUserId);

    Long adminCreate(Long adminUserId, String name, Integer sortOrder);

    void adminUpdate(Long adminUserId, Long id, String name, Integer sortOrder, Integer status);

    void adminDelete(Long adminUserId, Long id);
}
