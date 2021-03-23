package com.yfshop.admin.service.mall;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.mall.request.CreateBannerReq;
import com.yfshop.admin.api.mall.request.CreateItemCategoryReq;
import com.yfshop.admin.api.mall.request.GenerateItemSkuReq;
import com.yfshop.admin.api.mall.request.ItemCreateReq;
import com.yfshop.admin.api.mall.request.ItemUpdateReq;
import com.yfshop.admin.api.mall.request.QueryItemReq;
import com.yfshop.admin.api.mall.request.SaveItemSkuReq;
import com.yfshop.admin.api.mall.request.UpdateBannerReq;
import com.yfshop.admin.api.mall.request.UpdateItemCategoryReq;
import com.yfshop.admin.api.mall.result.BannerResult;
import com.yfshop.admin.api.mall.result.ItemCategoryResult;
import com.yfshop.code.mapper.BannerMapper;
import com.yfshop.code.mapper.ItemCategoryMapper;
import com.yfshop.code.mapper.ItemContentMapper;
import com.yfshop.code.mapper.ItemImageMapper;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.mapper.ItemSkuMapper;
import com.yfshop.code.model.Banner;
import com.yfshop.code.model.Item;
import com.yfshop.code.model.ItemCategory;
import com.yfshop.code.model.ItemContent;
import com.yfshop.code.model.ItemImage;
import com.yfshop.code.model.ItemSku;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private ItemImageMapper itemImageMapper;
    @Resource
    private ItemSkuMapper skuMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createBanner(@NotNull(message = "创建banner信息不能为空") CreateBannerReq req) throws ApiException {
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
    public Void editBanner(@NotNull(message = "编辑banner信息不能为空") UpdateBannerReq req) throws ApiException {
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
    public IPage<BannerResult> pageQueryBanner(Integer currentPage, Integer pageSize, String positions) {
        LambdaQueryWrapper<Banner> queryWrapper = Wrappers.<Banner>lambdaQuery().eq(Banner::getPositions, positions);
        Page<Banner> page = bannerMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        Page<BannerResult> data = new Page<>(currentPage, pageSize, page.getTotal());
        BeanUtil.copyProperties(page, data);
        return data;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createCategory(@NotNull(message = "创建分类信息不能为空") CreateItemCategoryReq req) throws ApiException {
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
    public Void editCategory(@NotNull(message = "编辑分类信息不能为空") UpdateItemCategoryReq req) throws ApiException {
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
        LambdaQueryWrapper<ItemCategory> wrapper = isEnable == null ? null :
                Wrappers.<ItemCategory>lambdaQuery().eq(ItemCategory::getIsEnable, isEnable ? "Y" : "N");
        List<ItemCategory> itemCategories = categoryMapper.selectList(wrapper);
        return itemCategories.stream().map(itemCategory -> {
            ItemCategoryResult r = new ItemCategoryResult();
            BeanUtil.copyProperties(itemCategory, r);
            return r;
        }).collect(Collectors.toList());
    }

    @Override
    public IPage<Object> pageQueryItems(QueryItemReq req) {
        return null;
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
        return null;
    }

    @Override
    public Object findItemDetailAndSkuList(Integer itemId) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createItem(@NotNull ItemCreateReq req) throws ApiException {
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
        for (ItemImage itemImage : itemImages) {
            itemImageMapper.insert(itemImage);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void editItem(@NotNull ItemUpdateReq req) throws ApiException {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<Object> generateItemSku(@NotNull GenerateItemSkuReq req) throws ApiException {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void recreateItemSku(@NotNull GenerateItemSkuReq req) throws ApiException {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void saveItemSku(@NotNull SaveItemSkuReq req) throws ApiException {
        return null;
    }
}
