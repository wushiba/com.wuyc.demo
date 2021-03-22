package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Item;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.manager.ItemManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class ItemManagerImpl extends ServiceImpl<ItemMapper, Item> implements ItemManager {

}
