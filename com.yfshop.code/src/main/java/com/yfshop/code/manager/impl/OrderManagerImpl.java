package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Order;
import com.yfshop.code.mapper.OrderMapper;
import com.yfshop.code.manager.OrderManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class OrderManagerImpl extends ServiceImpl<OrderMapper, Order> implements OrderManager {

}
