package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.CreateOrderRequest;
import com.neusoft.coursemgr.domain.CartItemView;
import com.neusoft.coursemgr.domain.Order;
import com.neusoft.coursemgr.domain.OrderDetail;
import com.neusoft.coursemgr.domain.OrderItem;
import com.neusoft.coursemgr.domain.Product;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.CartMapper;
import com.neusoft.coursemgr.mapper.CartItemMapper;
import com.neusoft.coursemgr.mapper.OrderItemMapper;
import com.neusoft.coursemgr.mapper.OrderMapper;
import com.neusoft.coursemgr.mapper.ProductMapper;
import com.neusoft.coursemgr.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(CartMapper cartMapper,
                            CartItemMapper cartItemMapper,
                            ProductMapper productMapper,
                            OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    private Long getCartIdOrFail(Long userId) {
        Long cartId = cartMapper.selectCartIdByUserId(userId);
        if (cartId == null) {
            throw new BizException(400, "购物车为空");
        }
        return cartId;
    }

    private String genOrderNo() {
        return "NO" + System.currentTimeMillis();
    }

    @Override
    @Transactional
    public Long createFromCart(Long userId, CreateOrderRequest req) {
        Long cartId = getCartIdOrFail(userId);
        List<CartItemView> cartItems = cartItemMapper.selectViewList(cartId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BizException(400, "购物车为空");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (CartItemView ci : cartItems) {
            Product p = productMapper.selectById(ci.getProductId());
            if (p == null || p.getStatus() == null || p.getStatus() != 1) {
                throw new BizException(400, "存在已下架商品，无法下单");
            }
            int qty = ci.getQuantity() == null ? 0 : ci.getQuantity();
            if (qty <= 0) {
                throw new BizException(400, "购物车商品数量非法");
            }
            int updated = productMapper.decreaseStock(p.getId(), qty);
            if (updated <= 0) {
                throw new BizException(400, "库存不足：" + p.getName());
            }

            BigDecimal amount = p.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(amount);

            OrderItem oi = new OrderItem();
            oi.setProductId(p.getId());
            oi.setProductName(p.getName());
            oi.setProductCoverUrl(p.getCoverUrl());
            oi.setUnitPrice(p.getPrice());
            oi.setQuantity(qty);
            oi.setAmount(amount);
            items.add(oi);
        }

        Order order = new Order();
        order.setOrderNo(genOrderNo());
        order.setUserId(userId);
        order.setStatus("PENDING_PAY");
        order.setTotalAmount(total);
        order.setReceiverName(req.getReceiverName());
        order.setReceiverPhone(req.getReceiverPhone());
        order.setReceiverAddress(req.getReceiverAddress());
        orderMapper.insert(order);

        orderItemMapper.insertBatch(order.getId(), items);

        cartItemMapper.deleteAll(cartId);

        log.info("create order from cart, userId={}, orderId={}, orderNo={}, total={}", userId, order.getId(), order.getOrderNo(), total);
        return order.getId();
    }

    @Override
    public void pay(Long userId, Long orderId) {
        int rows = orderMapper.updateStatus(orderId, userId, "PENDING_PAY", "PENDING_SHIP");
        if (rows <= 0) {
            throw new BizException(400, "订单状态不允许支付");
        }
        log.info("pay order, userId={}, orderId={}", userId, orderId);
    }

    @Override
    public void receive(Long userId, Long orderId) {
        int rows = orderMapper.updateStatus(orderId, userId, "PENDING_RECEIVE", "COMPLETED");
        if (rows <= 0) {
            throw new BizException(400, "订单状态不允许收货");
        }
        log.info("receive order, userId={}, orderId={}", userId, orderId);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long orderId) {
        List<OrderItem> items = orderItemMapper.selectByOrderIdAndUserId(orderId, userId);
        if (items == null) {
            throw new BizException(404, "订单不存在");
        }

        int rows = orderMapper.updateStatus(orderId, userId, "PENDING_PAY", "CANCELED");
        if (rows <= 0) {
            throw new BizException(400, "订单状态不允许取消");
        }

        for (OrderItem it : items) {
            if (it == null || it.getProductId() == null || it.getQuantity() == null) {
                continue;
            }
            if (it.getQuantity() <= 0) {
                continue;
            }
            productMapper.increaseStock(it.getProductId(), it.getQuantity());
        }
        log.info("cancel order, userId={}, orderId={}", userId, orderId);
    }

    @Override
    public List<Order> listMyOrders(Long userId, String status) {
        return orderMapper.selectByUserId(userId, status);
    }

    @Override
    public OrderDetail getMyOrderDetail(Long userId, Long orderId) {
        Order o = orderMapper.selectByIdAndUserId(orderId, userId);
        if (o == null) {
            throw new BizException(404, "订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderIdAndUserId(orderId, userId);
        return new OrderDetail(o, items);
    }

    @Override
    public List<Order> adminListOrders(Long adminUserId, String status, String keyword) {
        return orderMapper.selectAdminList(status, keyword);
    }

    @Override
    public void adminShip(Long adminUserId, Long orderId) {
        int rows = orderMapper.adminUpdateStatus(orderId, "PENDING_SHIP", "PENDING_RECEIVE");
        if (rows <= 0) {
            throw new BizException(400, "订单状态不允许发货");
        }
        log.info("admin ship order, adminUserId={}, orderId={}, time={}", adminUserId, orderId, LocalDateTime.now());
    }
}
