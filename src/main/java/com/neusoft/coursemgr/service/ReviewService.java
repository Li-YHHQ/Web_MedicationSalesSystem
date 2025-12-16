package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.Review;

import java.util.List;

public interface ReviewService {

    Long create(Long userId, Long orderId, Long productId, Integer rating, String content);

    List<Review> listByProduct(Long productId);
}
