package com.neusoft.coursemgr.service;

import com.neusoft.coursemgr.domain.CartView;

public interface CartService {

    void addItem(Long userId, Long productId, Integer quantity);

    CartView getCart(Long userId);

    void updateItemQuantity(Long userId, Long itemId, Integer quantity);

    void removeItem(Long userId, Long itemId);

    void clear(Long userId);
}
