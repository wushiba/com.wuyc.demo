package com.yfshop.code.manager.impl;

import com.yfshop.code.model.ItemImage;
import com.yfshop.code.mapper.ItemImageMapper;
import com.yfshop.code.manager.ItemImageManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品图片表 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Service
public class ItemImageManagerImpl extends ServiceImpl<ItemImageMapper, ItemImage> implements ItemImageManager {

}
