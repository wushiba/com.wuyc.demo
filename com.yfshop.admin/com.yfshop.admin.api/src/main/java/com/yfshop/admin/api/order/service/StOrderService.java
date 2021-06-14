package com.yfshop.admin.api.order.service;

public interface StOrderService {

    void pushStOrder(Long orderId,Long childOrderId);
}
