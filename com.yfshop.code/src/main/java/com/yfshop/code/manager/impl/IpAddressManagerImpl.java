package com.yfshop.code.manager.impl;

import com.yfshop.code.model.IpAddress;
import com.yfshop.code.mapper.IpAddressMapper;
import com.yfshop.code.manager.IpAddressManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ip地址表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-24
 */
@Service
public class IpAddressManagerImpl extends ServiceImpl<IpAddressMapper, IpAddress> implements IpAddressManager {

}
