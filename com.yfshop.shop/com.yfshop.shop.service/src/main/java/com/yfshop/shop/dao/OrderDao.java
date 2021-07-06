package com.yfshop.shop.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @author wuyc
 * created 2021/4/7 14:36
 **/
public interface OrderDao {

    int updateOrderPayStatus(@Param("orderId") Long orderId);

    int updateOrderPayEntryCount(@Param("orderId") Long orderId);

    void orderCancelPay(@Param("orderId") Long orderId);

    int updateOrderPayBillStatus(@Param("orderId") Long orderId, @Param("billNo") String billNo);
}
