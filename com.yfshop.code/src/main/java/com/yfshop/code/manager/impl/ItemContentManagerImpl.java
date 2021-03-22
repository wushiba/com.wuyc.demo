package com.yfshop.code.manager.impl;

import com.yfshop.code.model.ItemContent;
import com.yfshop.code.mapper.ItemContentMapper;
import com.yfshop.code.manager.ItemContentManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品详情表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class ItemContentManagerImpl extends ServiceImpl<ItemContentMapper, ItemContent> implements ItemContentManager {

}
