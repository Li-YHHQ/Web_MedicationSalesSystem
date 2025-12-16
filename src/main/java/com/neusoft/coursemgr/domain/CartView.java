package com.neusoft.coursemgr.domain;

import java.math.BigDecimal;
import java.util.List;

public class CartView {
    private List<CartItemView> items;
    private BigDecimal totalAmount;

    public CartView() {
    }

    public CartView(List<CartItemView> items, BigDecimal totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public List<CartItemView> getItems() {
        return items;
    }

    public void setItems(List<CartItemView> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
