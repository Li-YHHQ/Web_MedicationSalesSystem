package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {

    Product selectById(@Param("id") Long id);

    int insert(Product product);

    int update(Product product);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    int deleteById(@Param("id") Long id);

    List<Product> selectPublicList(@Param("categoryId") Long categoryId,
                                   @Param("keyword") String keyword,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("isPrescription") Integer isPrescription);

    List<Product> selectAdminList(@Param("categoryId") Long categoryId,
                                  @Param("keyword") String keyword);
}
