package com.yfshop.admin.api.order.service;

import com.yfshop.common.exception.ApiException;
import java.util.List;

/**
 * @Title:用户订单Service接口
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:09:37
 * @Version:1.1.0
 */
public interface AdminUserOrderService {

	/**
	 * 用户付款后修改订单状态
	 * @param orderId
	 * @return
	 * @throws ApiException
	 */
	Void updateOrderPayStatus(Long orderId) throws ApiException;

}
