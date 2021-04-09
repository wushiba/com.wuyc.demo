package com.yfshop.admin.service.order;

import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.dao.OrderDao;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.model.Order;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * 后台订单serviceImpl
 * @author wuyc
 * created 2021/4/9 13:43
 **/
@DubboService
public class AdminUserServiceImpl implements AdminUserOrderService {

    @Resource
    private OrderDao orderDao;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private WebsiteBillService websiteBillService;

    /**
     * 用户付款后修改订单状态
     * @param orderId   主订单id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateOrderPayStatus(Long orderId) throws ApiException {
        Asserts.assertNonNull(orderId, 500, "主订单id不可以为空");
        Order order = orderMapper.selectById(orderId);
        Asserts.assertNonNull(order, 500, "订单不存在");
        Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单不可以重复修改状态");

        // 修改订单状态，用乐观锁
        int result = orderDao.updateOrderPayStatus(orderId);
        if (result <= 0) {
            Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单不可以重复修改状态");
        }
        websiteBillService.insertWebsiteBill(orderId);
        return null;
    }
}
