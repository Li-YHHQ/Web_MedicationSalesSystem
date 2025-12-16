package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.Category;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.CategoryMapper;
import com.neusoft.coursemgr.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<Category> listEnabled() {
        return categoryMapper.selectEnabled();
    }

    @Override
    public List<Category> adminListAll(Long adminUserId) {
        return categoryMapper.selectAll();
    }

    @Override
    public Long adminCreate(Long adminUserId, String name, Integer sortOrder) {
        Category exist = categoryMapper.selectByName(name);
        if (exist != null) {
            throw new BizException(400, "分类名称已存在");
        }
        Category c = new Category();
        c.setName(name);
        c.setSortOrder(sortOrder);
        c.setStatus(1);
        categoryMapper.insert(c);
        log.info("admin create category, adminUserId={}, categoryId={}, name={}", adminUserId, c.getId(), name);
        return c.getId();
    }

    @Override
    public void adminUpdate(Long adminUserId, Long id, String name, Integer sortOrder, Integer status) {
        Category exist = categoryMapper.selectById(id);
        if (exist == null) {
            throw new BizException(404, "分类不存在");
        }
        Category nameExist = categoryMapper.selectByName(name);
        if (nameExist != null && !nameExist.getId().equals(id)) {
            throw new BizException(400, "分类名称已存在");
        }
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        c.setSortOrder(sortOrder);
        c.setStatus(status);
        categoryMapper.update(c);
        log.info("admin update category, adminUserId={}, categoryId={}", adminUserId, id);
    }

    @Override
    public void adminDelete(Long adminUserId, Long id) {
        int rows = categoryMapper.deleteById(id);
        if (rows <= 0) {
            throw new BizException(404, "分类不存在");
        }
        log.info("admin delete category, adminUserId={}, categoryId={}", adminUserId, id);
    }
}
