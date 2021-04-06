package com.yfshop.shop.service.mall;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.BannerPositionsEnum;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.mall.req.QueryItemDetailReq;
import com.yfshop.shop.service.mall.req.QueryItemReq;
import com.yfshop.shop.service.mall.result.ItemCategoryResult;
import com.yfshop.shop.service.mall.result.ItemContentResult;
import com.yfshop.shop.service.mall.result.ItemImageResult;
import com.yfshop.shop.service.mall.result.ItemResult;
import com.yfshop.shop.service.mall.result.ItemSkuResult;
import com.yfshop.shop.service.mall.result.ItemSpecNameResult;
import com.yfshop.shop.service.mall.result.ItemSpecValueResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.Cacheable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * Created in 2021-03-29 10:39
 */
@DubboService
public class MallServiceImpl implements MallService {

    @Resource
    private ItemCategoryMapper itemCategoryMapper;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ItemContentMapper itemContentMapper;
    @Resource
    private ItemImageMapper itemImageMapper;
    @Resource
    private ItemSkuMapper skuMapper;
    @Resource
    private ItemSpecNameMapper specNameMapper;
    @Resource
    private ItemSpecValueMapper specValueMapper;
    @Resource
    private BannerMapper bannerMapper;
    @Resource
    private UserCartMapper userCartMapper;

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_CATEGORY_CACHE_NAME,
            key = "'" + CacheConstants.MALL_CATEGORY_CACHE_KEY_PREFIX + "' + #root.methodName")
    @Override
    public List<ItemCategoryResult> queryCategories() {
        List<ItemCategory> categories = itemCategoryMapper.selectList(Wrappers.lambdaQuery(ItemCategory.class)
                .eq(ItemCategory::getIsEnable, "Y").orderByAsc(ItemCategory::getSort));
        return BeanUtil.convertList(categories, ItemCategoryResult.class);
    }

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_CATEGORY_ITEMS_CACHE_NAME,
            key = "'" + CacheConstants.MALL_CATEGORY_ITEMS_CACHE_KEY_PREFIX + "' + #root.args[0].categoryId")
    @Override
    public List<ItemResult> queryItems(QueryItemReq req) {
        if (req == null || req.getCategoryId() == null) {
            return new ArrayList<>(0);
        }
        List<Item> items = itemMapper.selectList(Wrappers.lambdaQuery(Item.class)
                .eq(req.getCategoryId() != null, Item::getCategoryId, req.getCategoryId())
                .eq(Item::getIsEnable, "Y").eq(Item::getIsDelete, "N"));
        return BeanUtil.convertList(items, ItemResult.class);
    }

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_ITEM_DETAIL_CACHE_NAME,
            key = "'" + CacheConstants.MALL_ITEM_DETAIL_CACHE_KEY_PREFIX + "' + #root.args[0].itemId")
    @Override
    public ItemResult findItemDetail(QueryItemDetailReq req) {
        if (req == null || req.getItemId() == null) {
            return null;
        }
        Item item = itemMapper.selectById(req.getItemId());
        if (item == null) {
            return null;
        }
        ItemResult itemResult = BeanUtil.convert(item, ItemResult.class);
        // category
        ItemCategory category = itemCategoryMapper.selectById(item.getCategoryId());
        if (category != null) {
            itemResult.setItemCategory(BeanUtil.convert(category, ItemCategoryResult.class));
        }
        // item content
        ItemContent itemContent = itemContentMapper.selectOne(Wrappers.lambdaQuery(ItemContent.class)
                .eq(ItemContent::getItemId, req.getItemId()));
        if (itemContent != null) {
            itemResult.setItemContent(BeanUtil.convert(itemContent, ItemContentResult.class));
        }
        // item images
        List<ItemImage> itemImages = itemImageMapper.selectList(Wrappers.lambdaQuery(ItemImage.class)
                .eq(ItemImage::getItemId, req.getItemId()));
        if (itemImages != null) {
            itemResult.setItemImages(BeanUtil.convertList(itemImages, ItemImageResult.class));
        }
        // sku list
        if (req.isQuerySku()) {
            List<ItemSku> skuList = skuMapper.selectList(Wrappers.lambdaQuery(ItemSku.class).eq(ItemSku::getItemId, req.getItemId())
                    .eq(ItemSku::getIsEnable, "Y").orderByAsc(ItemSku::getSort));
            if (skuList != null) {
                itemResult.setItemSkuList(BeanUtil.convertList(skuList, ItemSkuResult.class));
            }
        }
        // spec name value
        List<ItemSpecName> itemSpecNames = specNameMapper.selectList(Wrappers.lambdaQuery(ItemSpecName.class)
                .eq(ItemSpecName::getItemId, req.getItemId()).orderByAsc(ItemSpecName::getSort));
        if (CollectionUtil.isNotEmpty(itemSpecNames)) {
            List<ItemSpecNameResult> itemSpecNameResults = BeanUtil.convertList(itemSpecNames, ItemSpecNameResult.class);
            // spec values
            List<Integer> specNameIds = itemSpecNames.stream().map(ItemSpecName::getId).collect(Collectors.toList());
            Map<Integer, List<ItemSpecValueResult>> specValueIndexMap = specValueMapper.selectList(Wrappers.lambdaQuery(ItemSpecValue.class)
                    .in(ItemSpecValue::getSpecId, specNameIds)).stream().map(specValue -> BeanUtil.convert(specValue, ItemSpecValueResult.class))
                    .collect(Collectors.groupingBy(ItemSpecValueResult::getSpecId));
            itemSpecNameResults.forEach(r -> r.setSpecValues(specValueIndexMap.getOrDefault(r.getId(), new ArrayList<>(0))));
            itemResult.setSpecNames(itemSpecNameResults);
        }
        return itemResult;
    }


    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_BANNER_CACHE_NAME,
            key = "'" + CacheConstants.MALL_BANNER_CACHE_KEY_PREFIX + "' + #root.methodName")
    @Override
    public List<String> queryHomeBanners() {
        List<Banner> banners = bannerMapper.selectList(Wrappers.lambdaQuery(Banner.class)
                .eq(Banner::getPositions, BannerPositionsEnum.HOME.getCode())
                .eq(Banner::getIsEnable, "Y").orderByAsc(Banner::getSort));
        return banners.stream().map(Banner::getImageUrl).collect(Collectors.toList());
    }

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_BANNER_CACHE_NAME,
            key = "'" + CacheConstants.MALL_BANNER_CACHE_KEY_PREFIX + "' + #root.methodName")
    @Override
    public List<String> queryLoopBanners() {
        List<Banner> banners = bannerMapper.selectList(Wrappers.lambdaQuery(Banner.class)
                .eq(Banner::getPositions, BannerPositionsEnum.BANNER.getCode())
                .eq(Banner::getIsEnable, "Y").orderByAsc(Banner::getSort));
        return banners.stream().map(Banner::getImageUrl).collect(Collectors.toList());
    }


}
