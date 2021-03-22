package com.yfshop.code.manager.impl;

import com.yfshop.code.model.OrderAddress;
import com.yfshop.code.mapper.OrderAddressMapper;
import com.yfshop.code.manager.OrderAddressManager;
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
public class OrderAddressManagerImpl extends ServiceImpl<OrderAddressMapper, OrderAddress> implements OrderAddressManager {

}
