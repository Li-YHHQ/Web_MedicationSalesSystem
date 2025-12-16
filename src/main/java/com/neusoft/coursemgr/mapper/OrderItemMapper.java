package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    int insertBatch(@Param("orderId") Long orderId, @Param("items") List<OrderItem> items);

    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);

    List<OrderItem> selectByOrderIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    int countOrderItem(@Param("orderId") Long orderId, @Param("productId") Long productId);

    int countByProductId(@Param("productId") Long productId);
}
