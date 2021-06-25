package com.yfshop.shop.service.mall;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.BannerPositionsEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.dao.ItemDao;
import com.yfshop.shop.dao.UserDao;
import com.yfshop.shop.service.mall.req.QueryItemDetailReq;
import com.yfshop.shop.service.mall.req.QueryItemReq;
import com.yfshop.shop.service.mall.result.BannerResult;
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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private ItemDao itemDao;
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
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserDao userDao;
    @Resource
    private RedisService redisService;

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
        //        if (req == null || req.getCategoryId() == null) {
        //            return new ArrayList<>(0);
        //        }
        List<Item> items = itemMapper.selectList(Wrappers.lambdaQuery(Item.class)
                .eq(req.getCategoryId() != null, Item::getCategoryId, req.getCategoryId())
                .eq(Item::getIsEnable, "Y").eq(Item::getIsDelete, "N").orderByAsc(Item::getSort,Item::getId));
        List<ItemResult> list = BeanUtil.convertList(items, ItemResult.class);
        list.parallelStream().forEach(itemResult -> {
            ItemSku itemSku = skuMapper.selectOne(Wrappers.lambdaQuery(ItemSku.class)
                    .eq(ItemSku::getItemId, itemResult.getId()).eq(ItemSku::getIsEnable, "Y").orderByAsc(ItemSku::getSkuSalePrice));
            itemResult.setMinSalePrice(itemSku.getSkuSalePrice());
            itemResult.setItemMarketPrice(itemSku.getSkuMarketPrice());
            itemResult.setItemPrice(itemSku.getSkuSalePrice());
        });
        return list;
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
        BigDecimal minSalePrice = skuMapper.selectList(Wrappers.lambdaQuery(ItemSku.class)
                .eq(ItemSku::getItemId, itemResult.getId()).eq(ItemSku::getIsEnable, "Y"))
                .stream().map(ItemSku::getSkuSalePrice).min(BigDecimal::compareTo).orElse(null);
        itemResult.setMinSalePrice(minSalePrice);
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
                .eq(ItemImage::getItemId, req.getItemId()).orderByAsc(ItemImage::getSort));
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
    public List<BannerResult> queryHomeBannerList() {
        List<Banner> banners = bannerMapper.selectList(Wrappers.lambdaQuery(Banner.class)
                .eq(Banner::getPositions, BannerPositionsEnum.HOME.getCode())
                .eq(Banner::getIsEnable, "Y").orderByAsc(Banner::getSort));
        return BeanUtil.convertList(banners, BannerResult.class);
        //        return banners.stream().map(Banner::getImageUrl).collect(Collectors.toList());
    }


    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_BANNER_CACHE_NAME,
            key = "'" + CacheConstants.MALL_BANNER_CACHE_KEY_PREFIX + "' + #root.methodName")
    @Override
    public List<BannerResult> queryLoopBannerList() {
        List<Banner> banners = bannerMapper.selectList(Wrappers.lambdaQuery(Banner.class)
                .eq(Banner::getPositions, BannerPositionsEnum.BANNER.getCode())
                .eq(Banner::getIsEnable, "Y").orderByAsc(Banner::getSort));
        return BeanUtil.convertList(banners, BannerResult.class);
        //        return banners.stream().map(Banner::getImageUrl).collect(Collectors.toList());
    }


    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MALL_BANNER_CACHE_NAME,
            key = "'" + CacheConstants.MALL_BANNER_CACHE_KEY_PREFIX + "' + #root.methodName")
    @Override
    public List<BannerResult> queryPersonalCenterBannerList() {
        List<Banner> banners = bannerMapper.selectList(Wrappers.lambdaQuery(Banner.class)
                .eq(Banner::getPositions, BannerPositionsEnum.PERSONAL_CENTER.getCode())
                .eq(Banner::getIsEnable, "Y").orderByAsc(Banner::getSort));
        return BeanUtil.convertList(banners, BannerResult.class);
    }


    @Override
    public ItemSkuResult getItemSkuBySkuId(Integer skuId) throws ApiException {
        Asserts.assertNonNull(skuId, 500, "商品skuId不可以为空");
        Object itemSkuObject = redisService.get(CacheConstants.MALL_ITEM_SKU_CACHE_KEY_PREFIX + skuId);
        if (itemSkuObject != null) {
            return JSON.parseObject(itemSkuObject.toString(), ItemSkuResult.class);
        }

        ItemSku itemSku = skuMapper.selectOne(Wrappers.lambdaQuery(ItemSku.class).eq(ItemSku::getId, skuId));
        Asserts.assertNonNull(itemSku, 500, "商品sku不存在");

        ItemSkuResult itemSkuResult = BeanUtil.convert(itemSku, ItemSkuResult.class);
        redisService.set(CacheConstants.MALL_ITEM_SKU_CACHE_KEY_PREFIX + skuId, JSON.toJSONString(itemSkuResult), 60 * 60);
        return itemSkuResult;
    }

    /**
     * 修改商品sku库存
     *
     * @param skuId skuId
     * @param num   扣减库存的数量
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateItemSkuStock(Integer skuId, Integer num) throws ApiException {
        Asserts.assertNonNull(skuId, 500, "商品skuId不可以为空");
        Asserts.assertFalse(num == null || num <= 0, 500, "请传入正确的数量");

        int result = itemDao.updateItemSkuStock(skuId, num);
        Asserts.assertFalse(result <= 0, 500, "库存不足，请稍后重试");
        return result;
    }

    @Override
    public Long getBuyGoodsCount(Integer itemId) throws ApiException {
        Object value = redisService.get("BuyGoods:" + itemId);
        return value == null ? 0 : Long.valueOf(value.toString());
    }

    @Override
    public List<String> getBuyGoodsUser() {
        Integer count = 0;
        Object value = redisService.get("UserCount");
        if (value == null) {
            count = userMapper.selectCount(Wrappers.lambdaQuery());
            redisService.set("UserCount", count);
            redisService.expire("UserCount", 60 * 60 * 6);
        } else {
            count = Integer.valueOf(value.toString());
        }
        int index = RandomUtil.randomInt(count);
        List<String> users = userDao.findByIndex(index, 100);

        List<String> items = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            String name = users.get(i);
            if (StringUtils.isNotBlank(users.get(i))) {
                name = name.length() > 4 ? name.substring(0, 4).concat("...") : name;
                String date = "";
                if (i < 10) {
                    date = "刚刚";
                } else if (i >= 10 && i < 20) {
                    date = "1分钟前";
                } else if (i >= 20 && i < 40) {
                    date = "3分钟前";
                } else if (i >= 40 && i < 60) {
                    date = "5分钟前";
                } else if (i >= 60 && i < 80) {
                    date = "8分钟分钟前";
                } else {
                    date = "10分钟前";
                }
                items.add(String.format("%s%s正在抢购这个商品", name, date));
            }
        }

        return items;
    }

}
