package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.CartItemView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {

    Integer selectQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId);

    int insert(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    int updateQuantity(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    int updateQuantityByItemId(@Param("cartId") Long cartId, @Param("itemId") Long itemId, @Param("quantity") Integer quantity);

    int deleteByItemId(@Param("cartId") Long cartId, @Param("itemId") Long itemId);

    int deleteByProductId(@Param("productId") Long productId);

    List<CartItemView> selectViewList(@Param("cartId") Long cartId);

    int deleteAll(@Param("cartId") Long cartId);
}
