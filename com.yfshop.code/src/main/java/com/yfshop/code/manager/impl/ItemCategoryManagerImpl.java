package com.yfshop.code.manager.impl;

import com.yfshop.code.model.ItemCategory;
import com.yfshop.code.mapper.ItemCategoryMapper;
import com.yfshop.code.manager.ItemCategoryManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class ItemCategoryManagerImpl extends ServiceImpl<ItemCategoryMapper, ItemCategory> implements ItemCategoryManager {

}
