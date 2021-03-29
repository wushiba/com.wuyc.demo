package com.yfshop.admin.service.mall;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.mall.request.*;
import com.yfshop.admin.api.mall.request.SaveItemSkuReq.ItemCandidateSku;
import com.yfshop.admin.api.mall.result.*;
import com.yfshop.code.manager.*;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.api.ErrorCode;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * Created in 2021-03-22 17:04
 */
@Validated
@DubboService
public class AdminMallManageServiceImpl implements AdminMallManageService {

    @Resource
    private ItemCategoryMapper categoryMapper;
    @Resource
    private BannerMapper bannerMapper;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ItemContentMapper itemContentMapper;
    @Resource
    private ItemContentManager itemContentManager;
    @Resource
    private ItemImageMapper itemImageMapper;
    @Resource
    private ItemImageManager itemImageManager;
    @Resource
    private ItemSkuMapper skuMapper;
    @Resource
    private ItemSkuManager skuManager;
    @Resource
    private ItemSpecNameMapper specNameMapper;
    @Resource
    private ItemSpecNameManager specNameManager;
    @Resource
    private ItemSpecValueMapper specValueMapper;
    @Resource
    private ItemSpecValueManager specValueManager;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createBanner(@Valid @NotNull(message = "创建banner信息不能为空") CreateBannerReq req) throws ApiException {
        Banner banner = new Banner();
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());
        banner.setBannerName(req.getBannerName());
        banner.setPositions(req.getPositions());
        banner.setImageUrl(req.getImageUrl());
        banner.setJumpUrl(req.getJumpUrl());
        banner.setSort(req.getSort());
        banner.setIsEnable(req.getIsEnable());
        bannerMapper.insert(banner);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void editBanner(@Valid @NotNull(message = "编辑banner信息不能为空") UpdateBannerReq req) throws ApiException {
        Banner banner = new Banner();
        banner.setId(req.getBannerId());
        banner.setUpdateTime(LocalDateTime.now());
        banner.setBannerName(req.getBannerName());
        banner.setPositions(req.getPositions());
        banner.setImageUrl(req.getImageUrl());
        banner.setJumpUrl(req.getJumpUrl());
        banner.setSort(req.getSort());
        banner.setIsEnable(req.getIsEnable());
        int rows = bannerMapper.updateById(banner);
        Asserts.assertTrue(rows > 0, 500, "编辑失败");
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void deleteBanner(@NotNull(message = "bannerId不能为空") Integer bannerId) {
        bannerMapper.deleteById(bannerId);
        return null;
    }

    @Override
    public IPage<BannerResult> pageQueryBanner(Integer pageIndex, Integer pageSize, String positions) {
        LambdaQueryWrapper<Banner> queryWrapper = Wrappers.lambdaQuery(Banner.class)
                .eq(Banner::getPositions, positions)
                .orderByDesc(Banner::getCreateTime);
        Page<Banner> page = bannerMapper.selectPage(new Page<>(pageIndex, pageSize), queryWrapper);
        // wrapper
        Page<BannerResult> data = new Page<>(pageIndex, pageSize, page.getTotal());
        data.setRecords(BeanUtil.convertList(page.getRecords(), BannerResult.class));
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createCategory(@Valid @NotNull(message = "创建分类信息不能为空") CreateItemCategoryReq req) throws ApiException {
        ItemCategory category = new ItemCategory();
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCategoryName(req.getCategoryName());
        category.setIsEnable(req.getIsEnable());
        category.setSort(req.getSort());
        categoryMapper.insert(category);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void editCategory(@Valid @NotNull(message = "编辑分类信息不能为空") UpdateItemCategoryReq req) throws ApiException {
        ItemCategory category = new ItemCategory();
        category.setId(req.getCategoryId());
        category.setUpdateTime(LocalDateTime.now());
        category.setCategoryName(req.getCategoryName());
        category.setIsEnable(req.getIsEnable());
        category.setSort(req.getSort());
        int rows = categoryMapper.updateById(category);
        Asserts.assertTrue(rows > 0, 500, "编辑失败");
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void deleteCategory(Integer categoryId) {
        categoryMapper.deleteById(categoryId);
        return null;
    }

    @Override
    public List<ItemCategoryResult> queryCategory(Boolean isEnable) {
        LambdaQueryWrapper<ItemCategory> wrapper = (isEnable == null) ? null :
                Wrappers.<ItemCategory>lambdaQuery()
                        .eq(ItemCategory::getIsEnable, isEnable ? "Y" : "N")
                        .orderByDesc(ItemCategory::getCreateTime);
        List<ItemCategory> itemCategories = categoryMapper.selectList(wrapper);
        return BeanUtil.convertList(itemCategories, ItemCategoryResult.class);
    }

    @Override
    public IPage<ItemResult> pageQueryItems(QueryItemReq req) {
        LambdaQueryWrapper<Item> queryWrapper = Wrappers.lambdaQuery(Item.class)
                .eq(Item::getIsDelete, "N")
                .eq(req.getItemId() != null, Item::getId, req.getItemId())
                .eq(req.getCategoryId() != null, Item::getCategoryId, req.getCategoryId())
                .eq(StringUtils.isNotBlank(req.getIsEnable()), Item::getIsEnable, req.getIsEnable())
                .like(StringUtils.isNotBlank(req.getItemTitle()), Item::getItemTitle, req.getItemTitle())
                .orderByDesc(Item::getCreateTime);
        Page<Item> itemPage = itemMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        Page<ItemResult> page = new Page<>(itemPage.getCurrent(), itemPage.getSize(), itemPage.getTotal());
        page.setRecords(BeanUtil.convertList(itemPage.getRecords(), ItemResult.class));
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateItemIsEnable(@NotNull(message = "商品id不能为空") Integer itemId, boolean isEnable) throws ApiException {
        Item item = new Item();
        item.setId(itemId);
        item.setIsEnable(isEnable ? "Y" : "N");
        int rows = itemMapper.updateById(item);
        Asserts.assertTrue(rows > 0, 500, "修改失败");
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateSkuIsEnable(@NotNull(message = "规格id不能为空") Integer skuId, boolean isEnable) throws ApiException {
        ItemSku sku = new ItemSku();
        sku.setId(skuId);
        sku.setIsEnable(isEnable ? "Y" : "N");
        int rows = skuMapper.updateById(sku);
        Asserts.assertTrue(rows > 0, 500, "修改失败");
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void deleteItem(@NotNull(message = "规格id不能为空") Integer itemId) throws ApiException {
        Item item = itemMapper.selectById(itemId);
        if (item == null || "Y".equalsIgnoreCase(item.getIsDelete())) {
            return null;
        }

        // delete item
        Item entity = new Item();
        entity.setId(itemId);
        entity.setIsDelete("Y");
        int rows = itemMapper.updateById(entity);
        Asserts.assertTrue(rows > 0, 500, "删除失败");

        // disable all sku
        LambdaQueryWrapper<ItemSku> queryWrapper = Wrappers.lambdaQuery(ItemSku.class)
                .eq(ItemSku::getItemId, itemId);
        List<ItemSku> skuList = skuMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(skuList)) {
            List<ItemSku> beans = skuList.stream().map(itemSku -> {
                ItemSku bean = new ItemSku();
                bean.setId(itemSku.getId());
                bean.setIsEnable("N");
                return bean;
            }).collect(Collectors.toList());
            skuManager.updateBatchById(beans);
        }

        // delete item content
        itemContentMapper.delete(Wrappers.lambdaQuery(ItemContent.class).eq(ItemContent::getItemId, itemId));
        // delete item images
        itemImageMapper.delete(Wrappers.lambdaQuery(ItemImage.class).eq(ItemImage::getItemId, itemId));
        return null;
    }

    @Override
    public ItemResult findItemDetailAndSkuList(Integer itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return null;
        }
        ItemResult itemResult = new ItemResult();
        BeanUtil.copyProperties(item, itemResult);

        // 查询分类信息
        ItemCategory itemCategory = categoryMapper.selectById(item.getCategoryId());
        if (itemCategory != null) {
            itemResult.setItemCategory(BeanUtil.convert(itemCategory, ItemCategoryResult.class));
        }
        // 查询商品的详情
        ItemContent itemContent = itemContentManager.getOne(Wrappers.lambdaQuery(ItemContent.class).eq(ItemContent::getItemId, item));
        if (itemContent != null) {
            itemResult.setItemContent(BeanUtil.convert(itemContent, ItemContentResult.class));
        }
        // 查询商品的图片
        List<ItemImage> itemImages = itemImageMapper.selectList(Wrappers.lambdaQuery(ItemImage.class).eq(ItemImage::getItemId, itemId));
        if (CollectionUtil.isNotEmpty(itemImages)) {
            itemResult.setItemImages(BeanUtil.convertList(itemImages, ItemImageResult.class));
        }
        // 查询商品的sku信息
        List<ItemSku> itemSkuList = skuMapper.selectList(Wrappers.lambdaQuery(ItemSku.class).eq(ItemSku::getItemId, itemId));
        if (CollectionUtil.isNotEmpty(itemSkuList)) {
            itemResult.setItemSkuList(BeanUtil.convertList(itemSkuList, ItemSkuResult.class));
        }
        // 查询商品的规格
        List<ItemSpecName> specNames = specNameMapper.selectList(Wrappers.lambdaQuery(ItemSpecName.class).eq(ItemSpecName::getItemId, itemId));
        if (CollectionUtil.isNotEmpty(specNames)) {
            // 查询商品的规格值
            Map<Integer, List<ItemSpecValue>> specValueIndexMap = specValueMapper.selectList(Wrappers.lambdaQuery(ItemSpecValue.class).eq(ItemSpecValue::getItemId, itemId))
                    .stream().collect(Collectors.groupingBy(ItemSpecValue::getSpecId));
            List<ItemSpecNameResult> specNameResults = specNames.stream().map(itemSpecName -> {
                ItemSpecNameResult itemSpecNameResult = BeanUtil.convert(itemSpecName, ItemSpecNameResult.class);
                List<ItemSpecValueResult> values = BeanUtil.convertList(specValueIndexMap.get(itemSpecName.getId()), ItemSpecValueResult.class);
                itemSpecNameResult.setSpecValues(values);
                return itemSpecNameResult;
            }).collect(Collectors.toList());
            itemResult.setSpecNames(specNameResults);
        }
        return itemResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createItem(@Valid @NotNull ItemCreateReq req) throws ApiException {
        ItemCategory itemCategory = categoryMapper.selectById(req.getCategoryId());
        Asserts.assertNonNull(itemCategory, 500, "分类信息不存在");

        // 创建商品
        Item item = new Item();
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setCategoryId(req.getCategoryId());
        item.setReceiveWay(req.getReceiveWay());
        item.setItemTitle(req.getItemTitle());
        item.setItemSubTitle(req.getItemSubTitle());
        item.setIsEnable(req.getIsEnable());
        item.setItemPrice(BigDecimal.ZERO);
        item.setItemMarketPrice(BigDecimal.ZERO);
        item.setFreight(BigDecimal.ZERO);
        item.setItemStock(0);
        item.setItemCover(null);
        item.setIsDelete("N");
        item.setSpecNum(0);
        item.setSort(0);
        itemMapper.insert(item);

        // 创建商品详情
        ItemContent itemContent = new ItemContent();
        itemContent.setCreateTime(LocalDateTime.now());
        itemContent.setUpdateTime(LocalDateTime.now());
        itemContent.setItemId(item.getId());
        itemContent.setContent(req.getItemContent());
        itemContentMapper.insert(itemContent);

        // 创建商品图片
        List<ItemImage> itemImages = new ArrayList<>();
        for (int i = 0; i < req.getItemImages().size(); i++) {
            String imageUrl = req.getItemImages().get(i);
            ItemImage itemImage = new ItemImage();
            itemImage.setCreateTime(LocalDateTime.now());
            itemImage.setUpdateTime(LocalDateTime.now());
            itemImage.setItemId(item.getId());
            itemImage.setImageUrl(imageUrl);
            itemImage.setSort(i + 1);
            itemImages.add(itemImage);
        }
        itemImageManager.saveBatch(itemImages);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void editItem(@Valid @NotNull ItemUpdateReq req) throws ApiException {
        ItemCategory itemCategory = categoryMapper.selectById(req.getCategoryId());
        Asserts.assertNonNull(itemCategory, 500, "分类信息不存在");

        // 创建商品
        Item item = new Item();
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setCategoryId(req.getCategoryId());
        item.setReceiveWay(req.getReceiveWay());
        item.setItemTitle(req.getItemTitle());
        item.setItemSubTitle(req.getItemSubTitle());
        item.setIsEnable(req.getIsEnable());
        int rows = itemMapper.updateById(item);
        Asserts.assertTrue(rows > 0, 500, "编辑失败");

        // delete item content
        itemContentMapper.delete(Wrappers.lambdaQuery(ItemContent.class).eq(ItemContent::getItemId, req.getItemId()));
        // delete item images
        itemImageMapper.delete(Wrappers.lambdaQuery(ItemImage.class).eq(ItemImage::getItemId, req.getItemId()));

        // 创建商品详情
        ItemContent itemContent = new ItemContent();
        itemContent.setCreateTime(LocalDateTime.now());
        itemContent.setUpdateTime(LocalDateTime.now());
        itemContent.setItemId(item.getId());
        itemContent.setContent(req.getItemContent());
        itemContentMapper.insert(itemContent);

        // 创建商品图片
        List<ItemImage> itemImages = new ArrayList<>();
        for (int i = 0; i < req.getItemImages().size(); i++) {
            String imageUrl = req.getItemImages().get(i);
            ItemImage itemImage = new ItemImage();
            itemImage.setCreateTime(LocalDateTime.now());
            itemImage.setUpdateTime(LocalDateTime.now());
            itemImage.setItemId(item.getId());
            itemImage.setImageUrl(imageUrl);
            itemImage.setSort(i + 1);
            itemImages.add(itemImage);
        }
        itemImageManager.saveBatch(itemImages);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<ItemSkuResult> previewItemSku(@Valid @NotNull GenerateItemSkuReq req) throws ApiException {
        // 校验规格数据
        if (CollectionUtils.isNotEmpty(req.getSpecNameAndValues())) {
            this.checkSpec(req.getSpecNameAndValues());
        } else {
            // 没有规格则创建默认规格
            req.setSpecNameAndValues(Collections.singletonList(ItemSpecNameAndValue.createDefaultSpec()));
        }
        List<ItemSpecNameAndValue> specNameAndValues = req.getSpecNameAndValues();
        Integer itemId = req.getItemId();

        Map<String, List<String>> specNameAndSpecValuesIndexMap = new HashMap<>(specNameAndValues.size());
        for (ItemSpecNameAndValue specNameAndValue : specNameAndValues) {
            String specName = specNameAndValue.getSpecName();
            // ["specName-specValue", "specName-specValue"]
            List<String> nameAndValueIndexList = specNameAndValue.getSpecValues().stream()
                    .map((specValue) -> specName + "-" + specValue)
                    .collect(Collectors.toList());
            specNameAndSpecValuesIndexMap.put(specName, nameAndValueIndexList);
        }
        List<String[]> specValueList = specNameAndSpecValuesIndexMap.values().stream()
                .map((specValues) -> specValues.toArray(new String[0]))
                .collect(Collectors.toList());

        // 求笛卡尔积列表
        List<List<String>> cartesianProductList = this.cartesianProduct(specValueList);

        // 根据笛卡尔积创建sku数据
        List<ItemSku> itemSkuList = new ArrayList<>();
        for (int i = 0; i < cartesianProductList.size(); i++) {
            List<String> cartesianProduct = cartesianProductList.get(i);
            JSONObject specNameValueJson = new JSONObject(cartesianProduct.size());
            for (String specIdValue : cartesianProduct) {
                String[] nameAndValueIndex = StringUtils.split(specIdValue, "-");
                // 规格名称
                String specName = nameAndValueIndex[0];
                // 规格值
                String specValue = nameAndValueIndex[1];
                specNameValueJson.put(specName, specValue);
            }
            // 生成SKU数据
            ItemSku itemSku = new ItemSku();
            itemSku.setId(itemId * 1000 + (i + 1));
            itemSku.setSpecNameValueJson(JSON.toJSONString(specNameValueJson));
            itemSkuList.add(itemSku);
        }

        return BeanUtil.convertList(itemSkuList, ItemSkuResult.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void recreateItemSku(@Valid @NotNull RecreateItemSkuReq req) throws ApiException {
        Integer itemId = req.getItemId();
        Item item = itemMapper.selectById(itemId);
        Asserts.assertNonNull(item, 500, "商品不存在");

        // 校验规格数据
        if (CollectionUtils.isNotEmpty(req.getSpecNameAndValues())) {
            this.checkSpec(req.getSpecNameAndValues());
        } else {
            // 没有规格则创建默认规格
            req.setSpecNameAndValues(Collections.singletonList(ItemSpecNameAndValue.createDefaultSpec()));
        }
        List<ItemSpecNameAndValue> specNameAndValues = req.getSpecNameAndValues();
        // sort by sort field
        specNameAndValues.sort(Comparator.comparing(ItemSpecNameAndValue::getSort));

        // 清理商品的SKU信息
        this.cleanItemSkuInfo(itemId);

        // 设置商品的规格数量
        Item newBean = new Item();
        newBean.setId(itemId);
        newBean.setSpecNum(req.getSpecNameAndValues().size());
        itemMapper.updateById(newBean);

        // 创建规格
        List<ItemSpecName> itemSpecNameList = specNameAndValues.stream()
                .map((specNameAndValue) -> {
                    ItemSpecName specName = new ItemSpecName();
                    specName.setCreateTime(LocalDateTime.now());
                    specName.setUpdateTime(LocalDateTime.now());
                    specName.setItemId(item.getId());
                    specName.setSpecName(specNameAndValue.getSpecName());
                    specName.setSort(specNameAndValue.getSort() == null ? 0 : specNameAndValue.getSort());
                    return specName;
                })
                .collect(Collectors.toList());
        specNameManager.saveBatch(itemSpecNameList);

        // 创建规格值
        Map<String, ItemSpecName> specNameIndexMap = itemSpecNameList.stream()
                .collect(Collectors.toMap((specName) -> specName.getItemId() + specName.getSpecName(), specName -> specName));
        List<ItemSpecValue> itemSpecValues = new ArrayList<>();
        for (ItemSpecNameAndValue specNameAndValue : specNameAndValues) {
            ItemSpecName specName = specNameIndexMap.get(item.getId() + specNameAndValue.getSpecName());
            for (String specValueStr : specNameAndValue.getSpecValues().stream()
                    .distinct().collect(Collectors.toList())) {
                ItemSpecValue specValue = new ItemSpecValue();
                specValue.setCreateTime(LocalDateTime.now());
                specValue.setUpdateTime(LocalDateTime.now());
                specValue.setItemId(itemId);
                specValue.setSpecId(specName.getId());
                specValue.setSpecValue(specValueStr);
                specValue.setSort(specName.getSort());
                itemSpecValues.add(specValue);
            }
        }
        specValueManager.saveBatch(itemSpecValues);

        //{specId:specName}
        Map<Integer, String> specNameIdAndNameIndexMap = itemSpecNameList.stream()
                .collect(Collectors.toMap(ItemSpecName::getId, ItemSpecName::getSpecName));
        Map<String, List<String>> specNameAndSpecValuesIndexMap = new HashMap<>(specNameAndValues.size());
        for (ItemSpecNameAndValue specNameAndValue : specNameAndValues) {
            String specName = specNameAndValue.getSpecName();
            // ["specName-specValue", "specName-specValue"]
            List<String> nameAndValueIndexList = specNameAndValue.getSpecValues().stream()
                    .map((specValue) -> specName + "-" + specValue)
                    .collect(Collectors.toList());
            specNameAndSpecValuesIndexMap.put(specName, nameAndValueIndexList);
        }
        List<String[]> specValueList = specNameAndSpecValuesIndexMap.values().stream()
                .map((specValues) -> specValues.toArray(new String[0]))
                .collect(Collectors.toList());
        // 求笛卡尔积列表
        List<List<String>> cartesianProductList = this.cartesianProduct(specValueList);

        // 根据笛卡尔积创建sku数据
        List<ItemSku> itemSkuList = new ArrayList<>();
        for (int i = 0; i < cartesianProductList.size(); i++) {
            List<String> cartesianProduct = cartesianProductList.get(i);
            // 规格值id(11,13)
            List<Integer> specValueIds = new ArrayList<>(cartesianProduct.size());
            // [{\"颜色\":\"黑色\",\"尺寸\":\"S\"},{\"颜色\":\"红色\",\"尺寸\":\"XL\"}]
            //List<JSONObject> specNameValueJsonList = new ArrayList<>(cartesianProduct.size());
            // {\"颜色\":\"黑色\"}
            JSONObject specNameValueJson = new JSONObject(cartesianProduct.size());
            for (String specIdValue : cartesianProduct) {
                String[] nameAndValueIndex = StringUtils.split(specIdValue, "-");
                // 规格名称
                String specName = nameAndValueIndex[0];
                // 规格值
                String specValue = nameAndValueIndex[1];
                specNameValueJson.put(specName, specValue);
                // 规格值id
                for (ItemSpecValue itemSpecValue : itemSpecValues) {
                    // itemId + specName(specId) + specValue可以唯一确定一个规格值
                    String key = itemSpecValue.getItemId() + specNameIdAndNameIndexMap
                            .get(itemSpecValue.getSpecId()) + itemSpecValue.getSpecValue();
                    if (key.equals(itemId + specName + specValue)) {
                        specValueIds.add(itemSpecValue.getId());
                        break;
                    }
                }
            }

            // 生成SKU数据
            ItemSku itemSku = new ItemSku();
            itemSku.setId(item.getId() * 1000 + (i + 1));
            itemSku.setCreateTime(LocalDateTime.now());
            itemSku.setUpdateTime(LocalDateTime.now());
            itemSku.setItemId(itemId);
            itemSku.setCategoryId(item.getCategoryId());
            itemSku.setSkuTitle(item.getItemTitle());
            itemSku.setSkuSubTitle(item.getItemSubTitle());
            itemSku.setSkuSalePrice(item.getItemPrice());
            itemSku.setSkuMarketPrice(item.getItemMarketPrice());
            itemSku.setSkuStock(0);
            itemSku.setSkuCover(item.getItemCover());
            itemSku.setSpecValueIdPath(StringUtils.join(specValueIds, ","));
            itemSku.setSpecNameValueJson(JSON.toJSONString(specNameValueJson));
            itemSku.setIsEnable("N");
            itemSku.setSort(i + 1);
            itemSkuList.add(itemSku);
        }
        skuManager.saveBatch(itemSkuList);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void saveItemSku(@Valid @NotNull SaveItemSkuReq req) throws ApiException {
        GenerateItemSkuReq skuRequest = req.getSpecInfo();
        Asserts.assertNonNull(skuRequest, 500, "保存商品sku没有规格数据");

        Integer itemId = skuRequest.getItemId();
        Item item = itemMapper.selectById(itemId);
        Asserts.assertNonNull(item, 500, "商品不存在");

        // 校验规格数据
        if (CollectionUtils.isNotEmpty(skuRequest.getSpecNameAndValues())) {
            this.checkSpec(skuRequest.getSpecNameAndValues());
        } else {
            // 没有规格则创建默认规格
            skuRequest.setSpecNameAndValues(Collections.singletonList(ItemSpecNameAndValue.createDefaultSpec()));
        }

        // 校验候选sku信息
        List<ItemCandidateSku> candidateSkuList = req.getCandidateSkus();
        Asserts.assertCollectionNotEmpty(candidateSkuList, 500, "保存商品sku没有候选sku");
        for (ItemCandidateSku candidateSku : candidateSkuList) {
            Integer candidateSkuItemId = candidateSku.getItemId();
            Asserts.assertNonNull(candidateSkuItemId, 500, "候选sku没有商品id");
            Asserts.assertTrue(candidateSkuItemId.equals(itemId), 500, "候选sku的商品id和目标商品的id不一致");
            String specNameValueJson = candidateSku.getSpecNameValueJson();
            Asserts.assertStringNotBlank(specNameValueJson, 500, "候选sku的规格传不能为空");
            String isEnable = candidateSku.getIsEnable();
            if (StringUtils.isBlank(isEnable)) {
                // 没有值则下架
                candidateSku.setIsEnable("N");
            }
            if (StringUtils.isNotBlank(isEnable)) {
                if (!Arrays.asList("Y", "N").contains(isEnable)) {
                    candidateSku.setIsEnable("N");
                }
            }
            Integer stock = candidateSku.getStock();
            Asserts.assertTrue(stock != null && stock > 0, 500, "库存不能为空或负");
            BigDecimal price = candidateSku.getPrice();
            Asserts.assertTrue(price != null && price.compareTo(BigDecimal.ZERO) > 0, 500, "售价不能为空或负");
            BigDecimal marketPrice = candidateSku.getMarketPrice();
            Asserts.assertTrue(marketPrice != null && marketPrice.compareTo(BigDecimal.ZERO) > 0, 500, "市场价不能为空或负");
        }

        // 对候选sku建立索引
        // key:itemId+specNameValueJson
        // value:BenefitItemCandidateSku
        Map<String, ItemCandidateSku> candidateSkuItemIdSpecNameValueJsonIndexMap = candidateSkuList.stream()
                .collect(Collectors.toMap(candidateSku -> candidateSku.getItemId() + candidateSku.getSpecNameValueJson(),
                        candidateSku -> candidateSku));

        // 清理商品的SKU信息
        this.cleanItemSkuInfo(itemId);

        // 设置商品的规格数量
        Item newBo = new Item();
        newBo.setId(itemId);
        newBo.setSpecNum(skuRequest.getSpecNameAndValues().size());
        itemMapper.updateById(newBo);

        // 创建规格
        List<ItemSpecNameAndValue> specNameAndValues = skuRequest.getSpecNameAndValues();
        if (CollectionUtils.isNotEmpty(specNameAndValues)) {
            specNameAndValues.sort(Comparator.comparing(ItemSpecNameAndValue::getSort));
        }
        List<ItemSpecName> itemSpecNameList = specNameAndValues.stream()
                .map((specNameAndValue) -> {
                    ItemSpecName specName = new ItemSpecName();
                    specName.setCreateTime(LocalDateTime.now());
                    specName.setUpdateTime(LocalDateTime.now());
                    specName.setItemId(item.getId());
                    specName.setSpecName(specNameAndValue.getSpecName());
                    specName.setSort(specNameAndValue.getSort() == null ? 0 : specNameAndValue.getSort());
                    return specName;
                })
                .collect(Collectors.toList());
        specNameManager.saveBatch(itemSpecNameList);

        // 创建规格值
        Map<String, ItemSpecName> specNameIndexMap = itemSpecNameList.stream()
                .collect(Collectors.toMap((specName) -> specName.getItemId() + specName.getSpecName(), specName -> specName));
        List<ItemSpecValue> itemSpecValues = new ArrayList<>();
        for (ItemSpecNameAndValue specNameAndValue : specNameAndValues) {
            ItemSpecName specName = specNameIndexMap.get(item.getId() + specNameAndValue.getSpecName());
            for (String specValueStr : specNameAndValue.getSpecValues().stream()
                    .distinct().collect(Collectors.toList())) {
                ItemSpecValue specValue = new ItemSpecValue();
                specValue.setCreateTime(LocalDateTime.now());
                specValue.setUpdateTime(LocalDateTime.now());
                specValue.setItemId(item.getId());
                specValue.setSpecId(specName.getId());
                specValue.setSpecValue(specValueStr);
                specValue.setSort(specName.getSort());
                itemSpecValues.add(specValue);
            }
        }
        specValueManager.saveBatch(itemSpecValues);

        //{specId:specName}
        Map<Integer, String> specNameIdAndNameIndexMap = itemSpecNameList.stream()
                .collect(Collectors.toMap(ItemSpecName::getId, ItemSpecName::getSpecName));

        // 建立{规格名称:[规格名称-规格值]}映射,
        Map<String, List<String>> specNameAndSpecValuesIndexMap = new HashMap<>(specNameAndValues.size());
        for (ItemSpecNameAndValue specNameAndValue : specNameAndValues) {
            String specName = specNameAndValue.getSpecName();
            // ["specName-specValue", "specName-specValue"]
            List<String> nameAndValueIndexList = specNameAndValue.getSpecValues().stream()
                    .map((specValue) -> specName + "-" + specValue)
                    .collect(Collectors.toList());
            specNameAndSpecValuesIndexMap.put(specName, nameAndValueIndexList);
        }
        List<String[]> specValueList = specNameAndSpecValuesIndexMap.values().stream()
                .map((specValues) -> specValues.toArray(new String[0]))
                .collect(Collectors.toList());

        // 求笛卡尔积列表
        List<List<String>> cartesianProductList = this.cartesianProduct(specValueList);

        // 根据笛卡尔积创建sku数据
        List<ItemSku> itemSkuList = new ArrayList<>(cartesianProductList.size());
        for (int i = 0; i < cartesianProductList.size(); i++) {
            List<String> cartesianProduct = cartesianProductList.get(i);
            // 规格值id(11,13)
            List<Integer> specValueIds = new ArrayList<>(cartesianProduct.size());
            // [{\"颜色\":\"黑色\",\"尺寸\":\"S\"},{\"颜色\":\"红色\",\"尺寸\":\"XL\"}]
            //List<JSONObject> specNameValueJsonList = new ArrayList<>(cartesianProduct.size());
            // {\"颜色\":\"黑色\"}
            JSONObject specNameValueJson = new JSONObject(cartesianProduct.size());
            for (String specIdValue : cartesianProduct) {
                String[] nameAndValueIndex = StringUtils.split(specIdValue, "-");
                // 规格名称
                String specName = nameAndValueIndex[0];
                // 规格值
                String specValue = nameAndValueIndex[1];
                specNameValueJson.put(specName, specValue);
                // 规格值id
                for (ItemSpecValue itemSpecValue : itemSpecValues) {
                    // itemId + specName(specId) + specValue可以唯一确定一个规格值
                    String key = itemSpecValue.getItemId() + specNameIdAndNameIndexMap
                            .get(itemSpecValue.getSpecId()) + itemSpecValue.getSpecValue();
                    if (key.equals(itemId + specName + specValue)) {
                        specValueIds.add(itemSpecValue.getId());
                        break;
                    }
                }
            }
            //specNameValueJsonList.add(specNameValueJson);

            // 生成SKU数据
            ItemSku itemSku = new ItemSku();
            itemSku.setId(item.getId() * 1000 + (i + 1));
            itemSku.setCreateTime(LocalDateTime.now());
            itemSku.setUpdateTime(LocalDateTime.now());
            itemSku.setItemId(itemId);
            itemSku.setCategoryId(item.getCategoryId());
            itemSku.setSkuTitle(item.getItemTitle());
            itemSku.setSkuSubTitle(item.getItemSubTitle());
            itemSku.setSkuCover(item.getItemCover());
            itemSku.setSpecValueIdPath(StringUtils.join(specValueIds, ","));
            itemSku.setSpecNameValueJson(JSON.toJSONString(specNameValueJson));
            itemSku.setIsEnable("N");
            itemSku.setSort(i + 1);
            itemSkuList.add(itemSku);
        }

        // 验证是否这些候选值是否都正确
        List<String> targetSkuKeys = itemSkuList.stream()
                .map((sku) -> sku.getItemId() + sku.getSpecNameValueJson())
                .collect(Collectors.toList());
        for (String candidateSkuUniqueKey : candidateSkuItemIdSpecNameValueJsonIndexMap.keySet()) {
            if (!targetSkuKeys.contains(candidateSkuUniqueKey)) {
                throw new ApiException(new ErrorCode(500, "候选sku错误，不存在这种sku" + candidateSkuUniqueKey));
            }
        }

        // 过滤sku
        List<ItemSku> targetSkuList = itemSkuList.stream()
                .filter((targetSku) -> candidateSkuItemIdSpecNameValueJsonIndexMap
                        .containsKey(targetSku.getItemId() + targetSku.getSpecNameValueJson()))
                .collect(Collectors.toList());

        // 设置sku的价格和库存
        for (ItemSku targetSku : targetSkuList) {
            ItemCandidateSku candidateSku = candidateSkuItemIdSpecNameValueJsonIndexMap
                    .get(targetSku.getItemId() + targetSku.getSpecNameValueJson());
            targetSku.setSkuSalePrice(candidateSku.getPrice());
            targetSku.setSkuMarketPrice(candidateSku.getMarketPrice());
            targetSku.setIsEnable(candidateSku.getIsEnable());
            targetSku.setSkuStock(candidateSku.getStock());
        }
        skuManager.saveBatch(targetSkuList);
        return null;
    }

    private void checkSpec(List<ItemSpecNameAndValue> specNameAndValues) {
        List<String> specNames = specNameAndValues.stream().map(ItemSpecNameAndValue::getSpecName)
                .collect(Collectors.toList());
        // 规格属性不能为空
        if (specNames.stream().anyMatch(StringUtils::isBlank)) {
            throw new ApiException(new ErrorCode(500, "规格属性不能为空"));
        }
        // 规格属性是否重复
        if (specNames.stream().distinct().count() < specNameAndValues.size()) {
            throw new ApiException(new ErrorCode(500, "存在重复的规格属性"));
        }
        for (int i = 0; i < specNameAndValues.size(); i++) {
            ItemSpecNameAndValue specNameAndValue = specNameAndValues.get(i);
            String specName = specNameAndValue.getSpecName();
            List<String> specValues = specNameAndValue.getSpecValues();
            // 排序字段
            if (specNameAndValue.getSort() == null) {
                specNameAndValue.setSort(i + 1);
            }
            // 规格属性的值是否为空
            if (specValues.stream().anyMatch(StringUtils::isBlank)) {
                throw new ApiException(new ErrorCode(500, "规格值不能为空"));
            }
            // 规格属性的值是否重复
            if (specValues.stream().distinct().count() < specValues.size()) {
                throw new ApiException(new ErrorCode(500, specName + "存在重复的值"));
            }
        }
    }

    private <T> List<List<T>> cartesianProduct(List<T[]> sets) {
        if (sets == null || sets.size() == 0) {
            return new ArrayList<>(0);
        }
        int total = 1;
        //声明进位指针cIndex
        int cIndex = sets.size() - 1;
        //声明counterMap(角标 - counter)
        int[] counterMap = new int[sets.size()];
        for (int i = 0; i < sets.size(); i++) {
            counterMap[i] = 0;
            total *= (sets.get(i) == null || sets.get(i).length == 0 ? 1 : sets.get(i).length);
        }
        List<List<T>> rt = new ArrayList<>(total);
        //开始求笛卡尔积
        while (cIndex >= 0) {
            List<T> element = new ArrayList<>(sets.size());
            for (int j = 0; j < sets.size(); j++) {
                T[] set = sets.get(j);
                //忽略空集
                if (set != null && set.length > 0) {
                    element.add(set[counterMap[j]]);
                }
                //从末位触发指针进位
                if (j == sets.size() - 1) {
                    if (set == null || ++counterMap[j] > set.length - 1) {
                        //重置指针
                        counterMap[j] = 0;
                        //进位
                        int cidx = j;
                        while (--cidx >= 0) {
                            //判断如果刚好前一位也要进位继续重置指针进位
                            if (sets.get(cidx) == null || ++counterMap[cidx] > sets.get(cidx).length - 1) {
                                counterMap[cidx] = 0;
                                continue;
                            }
                            break;
                        }
                        if (cidx < cIndex) {
                            //移动进位指针
                            cIndex = cidx;
                        }
                    }
                }
            }
            if (element.size() > 0) {
                rt.add(element);
            }
        }
        return rt;
    }

    /**
     * 删除商品的规格，规格值，SKU数据
     *
     * @param itemId the item id
     */
    private void cleanItemSkuInfo(Integer itemId) {
        // 删除商品的规格
        specNameMapper.delete(Wrappers.lambdaQuery(ItemSpecName.class).eq(ItemSpecName::getItemId, itemId));
        // 删除商品的规格值
        specValueMapper.delete(Wrappers.lambdaQuery(ItemSpecValue.class).eq(ItemSpecValue::getItemId, itemId));
        // 删除商品的sku信息
        skuMapper.delete(Wrappers.lambdaQuery(ItemSku.class).eq(ItemSku::getItemId, itemId));
    }
}
