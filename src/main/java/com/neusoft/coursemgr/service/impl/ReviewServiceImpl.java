package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.Order;
import com.neusoft.coursemgr.domain.Review;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.OrderItemMapper;
import com.neusoft.coursemgr.mapper.OrderMapper;
import com.neusoft.coursemgr.mapper.ReviewMapper;
import com.neusoft.coursemgr.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public Long create(Long userId, Long orderId, Long productId, Integer rating, String content) {
        Order o = orderMapper.selectByIdAndUserId(orderId, userId);
        if (o == null) {
            throw new BizException(404, "订单不存在");
        }
        if (!"COMPLETED".equals(o.getStatus())) {
            throw new BizException(400, "订单未完成，不能评价");
        }
        int cnt = orderItemMapper.countOrderItem(orderId, productId);
        if (cnt <= 0) {
            throw new BizException(400, "该订单不包含此商品");
        }
        Review exist = reviewMapper.selectByUserOrderProduct(userId, orderId, productId);
        if (exist != null) {
            throw new BizException(400, "该商品已评价");
        }

        Review r = new Review();
        r.setUserId(userId);
        r.setOrderId(orderId);
        r.setProductId(productId);
        r.setRating(rating);
        r.setContent(content);
        reviewMapper.insert(r);
        log.info("create review, userId={}, orderId={}, productId={}, rating={}", userId, orderId, productId, rating);
        return r.getId();
    }

    @Override
    public List<Review> listByProduct(Long productId) {
        return reviewMapper.selectByProductId(productId);
    }
}
