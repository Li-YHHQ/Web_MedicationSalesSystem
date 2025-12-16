package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.Category;
import com.neusoft.coursemgr.domain.Product;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.CartItemMapper;
import com.neusoft.coursemgr.mapper.CategoryMapper;
import com.neusoft.coursemgr.mapper.OrderItemMapper;
import com.neusoft.coursemgr.mapper.ProductMapper;
import com.neusoft.coursemgr.mapper.ReviewMapper;
import com.neusoft.coursemgr.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final CartItemMapper cartItemMapper;
    private final ReviewMapper reviewMapper;
    private final OrderItemMapper orderItemMapper;

    public ProductServiceImpl(
            ProductMapper productMapper,
            CategoryMapper categoryMapper,
            CartItemMapper cartItemMapper,
            ReviewMapper reviewMapper,
            OrderItemMapper orderItemMapper
    ) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.cartItemMapper = cartItemMapper;
        this.reviewMapper = reviewMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public List<Product> listPublic(Long categoryId, String keyword, BigDecimal minPrice, BigDecimal maxPrice, Integer isPrescription) {
        return productMapper.selectPublicList(categoryId, keyword, minPrice, maxPrice, isPrescription);
    }

    @Override
    public Product getDetail(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null || p.getStatus() == null || p.getStatus() != 1) {
            throw new BizException(404, "药品不存在或已下架");
        }
        return p;
    }

    @Override
    public List<Product> adminList(Long adminUserId, Long categoryId, String keyword) {
        return productMapper.selectAdminList(categoryId, keyword);
    }

    @Override
    public Long adminCreate(Long adminUserId, Product product) {
        Category c = categoryMapper.selectById(product.getCategoryId());
        if (c == null) {
            throw new BizException(400, "分类不存在");
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        productMapper.insert(product);
        log.info("admin create product, adminUserId={}, productId={}, name={}", adminUserId, product.getId(), product.getName());
        return product.getId();
    }

    @Override
    public void adminUpdate(Long adminUserId, Product product) {
        Product exist = productMapper.selectById(product.getId());
        if (exist == null) {
            throw new BizException(404, "药品不存在");
        }
        Category c = categoryMapper.selectById(product.getCategoryId());
        if (c == null) {
            throw new BizException(400, "分类不存在");
        }
        productMapper.update(product);
        log.info("admin update product, adminUserId={}, productId={}", adminUserId, product.getId());
    }

    @Override
    public void adminUpdateStatus(Long adminUserId, Long id, Integer status) {
        int rows = productMapper.updateStatus(id, status);
        if (rows <= 0) {
            throw new BizException(404, "药品不存在");
        }
        log.info("admin update product status, adminUserId={}, productId={}, status={}", adminUserId, id, status);
    }

    @Override
    @Transactional
    public void adminDelete(Long adminUserId, Long id) {
        Product exist = productMapper.selectById(id);
        if (exist == null) {
            throw new BizException(404, "药品不存在");
        }

        int orderRef = orderItemMapper.countByProductId(id);
        if (orderRef > 0) {
            throw new BizException(400, "该药品已产生订单记录，不能删除");
        }

        // 清理关联数据，避免外键/引用导致500
        cartItemMapper.deleteByProductId(id);
        reviewMapper.deleteByProductId(id);

        int rows = productMapper.deleteById(id);
        if (rows <= 0) {
            throw new BizException(404, "药品不存在");
        }
        log.info("admin delete product, adminUserId={}, productId={}", adminUserId, id);
    }
}
