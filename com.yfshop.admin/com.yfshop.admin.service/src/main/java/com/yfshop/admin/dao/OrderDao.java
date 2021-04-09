package com.yfshop.admin.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @author wuyc
 * created 2021/4/7 14:36
 **/
public interface OrderDao {

    int updateOrderPayStatus(@Param("orderId") Long orderId);

}
