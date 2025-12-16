package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.CreateOrderRequest;
import com.neusoft.coursemgr.domain.Order;
import com.neusoft.coursemgr.domain.OrderDetail;

import java.util.List;

public interface OrderService {

    Long createFromCart(Long userId, CreateOrderRequest req);

    void pay(Long userId, Long orderId);

    void receive(Long userId, Long orderId);

    void cancel(Long userId, Long orderId);

    List<Order> listMyOrders(Long userId, String status);

    OrderDetail getMyOrderDetail(Long userId, Long orderId);

    List<Order> adminListOrders(Long adminUserId, String status, String keyword);

    void adminShip(Long adminUserId, Long orderId);
}
