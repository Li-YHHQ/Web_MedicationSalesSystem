package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {

    Review selectByUserOrderProduct(@Param("userId") Long userId,
                                    @Param("orderId") Long orderId,
                                    @Param("productId") Long productId);

    int insert(Review review);

    int deleteByProductId(@Param("productId") Long productId);

    List<Review> selectByProductId(@Param("productId") Long productId);
}
