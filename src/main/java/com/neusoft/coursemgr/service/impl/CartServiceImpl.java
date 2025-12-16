package com.neusoft.coursemgr.service.impl;

import com.neusoft.coursemgr.domain.CartItemView;
import com.neusoft.coursemgr.domain.CartView;
import com.neusoft.coursemgr.domain.Product;
import com.neusoft.coursemgr.exception.BizException;
import com.neusoft.coursemgr.mapper.CartItemMapper;
import com.neusoft.coursemgr.mapper.CartMapper;
import com.neusoft.coursemgr.mapper.ProductMapper;
import com.neusoft.coursemgr.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartMapper cartMapper, CartItemMapper cartItemMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
        this.productMapper = productMapper;
    }

    private Long ensureCartId(Long userId) {
        Long cartId = cartMapper.selectCartIdByUserId(userId);
        if (cartId != null) {
            return cartId;
        }
        cartMapper.insertCart(userId);
        cartId = cartMapper.selectCartIdByUserId(userId);
        if (cartId == null) {
            throw new BizException(500, "创建购物车失败");
        }
        return cartId;
    }

    @Override
    public void addItem(Long userId, Long productId, Integer quantity) {
        Product p = productMapper.selectById(productId);
        if (p == null || p.getStatus() == null || p.getStatus() != 1) {
            throw new BizException(404, "商品不存在或已下架");
        }
        if (p.getStock() == null || p.getStock() < quantity) {
            throw new BizException(400, "库存不足");
        }

        Long cartId = ensureCartId(userId);
        Integer existQty = cartItemMapper.selectQuantity(cartId, productId);
        if (existQty == null) {
            cartItemMapper.insert(cartId, productId, quantity);
        } else {
            int newQty = existQty + quantity;
            if (p.getStock() < newQty) {
                throw new BizException(400, "库存不足");
            }
            cartItemMapper.updateQuantity(cartId, productId, newQty);
        }
        log.info("add to cart, userId={}, productId={}, qty={}", userId, productId, quantity);
    }

    @Override
    public CartView getCart(Long userId) {
        Long cartId = ensureCartId(userId);
        List<CartItemView> items = cartItemMapper.selectViewList(cartId);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemView it : items) {
            if (it.getAmount() != null) {
                total = total.add(it.getAmount());
            }
        }
        return new CartView(items, total);
    }

    @Override
    public void updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        Long cartId = ensureCartId(userId);
        int rows = cartItemMapper.updateQuantityByItemId(cartId, itemId, quantity);
        if (rows <= 0) {
            throw new BizException(404, "购物车条目不存在");
        }
        log.info("update cart item, userId={}, itemId={}, qty={}", userId, itemId, quantity);
    }

    @Override
    public void removeItem(Long userId, Long itemId) {
        Long cartId = ensureCartId(userId);
        int rows = cartItemMapper.deleteByItemId(cartId, itemId);
        if (rows <= 0) {
            throw new BizException(404, "购物车条目不存在");
        }
        log.info("remove cart item, userId={}, itemId={}", userId, itemId);
    }

    @Override
    public void clear(Long userId) {
        Long cartId = cartMapper.selectCartIdByUserId(userId);
        if (cartId == null) {
            return;
        }
        cartItemMapper.deleteAll(cartId);
        log.info("clear cart, userId={}, cartId={}", userId, cartId);
    }
}
