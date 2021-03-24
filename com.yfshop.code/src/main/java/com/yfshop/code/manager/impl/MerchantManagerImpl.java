package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Merchant;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.manager.MerchantManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商户表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Service
public class MerchantManagerImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantManager {

}
