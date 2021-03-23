package com.yfshop.code.manager.impl;

import com.yfshop.code.model.OrderDetail;
import com.yfshop.code.mapper.OrderDetailMapper;
import com.yfshop.code.manager.OrderDetailManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Service
public class OrderDetailManagerImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailManager {

}
