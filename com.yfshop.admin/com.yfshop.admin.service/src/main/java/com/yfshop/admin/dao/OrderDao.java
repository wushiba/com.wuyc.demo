package com.yfshop.admin.dao;

import com.yfshop.code.model.Order;
import org.apache.ibatis.annotations.Param;

/**
 * @author wuyc
 * created 2021/4/7 14:36
 **/
public interface OrderDao {

    int updateOrderPayStatus(@Param("orderId") Long orderId, @Param("billNo") String billNo);

    Order selectByIdForUpdate(@Param("orderId") Long orderId);
}
