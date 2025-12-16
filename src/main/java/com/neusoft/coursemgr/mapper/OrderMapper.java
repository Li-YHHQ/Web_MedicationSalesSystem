package com.neusoft.coursemgr.mapper;

import com.neusoft.coursemgr.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    Order selectById(@Param("id") Long id);

    Order selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    int insert(Order order);

    int updateStatus(@Param("id") Long id,
                     @Param("userId") Long userId,
                     @Param("fromStatus") String fromStatus,
                     @Param("toStatus") String toStatus);

    int adminUpdateStatus(@Param("id") Long id,
                          @Param("fromStatus") String fromStatus,
                          @Param("toStatus") String toStatus);

    List<Order> selectByUserId(@Param("userId") Long userId, @Param("status") String status);

    List<Order> selectAdminList(@Param("status") String status, @Param("keyword") String keyword);
}
