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
import com.yfshop.code.mapper.BannerMapper;
import com.yfshop.code.mapper.ItemCategoryMapper;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.model.Banner;
import com.yfshop.common.exception.ApiException;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void createBanner(CreateBannerReq req) throws ApiException {
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
    public Void editBanner(UpdateBannerReq req) throws ApiException {
        Banner banner = new Banner();
        banner.setId(req.getBannerId());
        banner.setUpdateTime(LocalDateTime.now());
        banner.setBannerName(req.getBannerName());
        banner.setPositions(req.getPositions());
        banner.setImageUrl(req.getImageUrl());
        banner.setJumpUrl(req.getJumpUrl());
        banner.setSort(req.getSort());
        banner.setIsEnable(req.getIsEnable());
        bannerMapper.updateById(banner);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void deleteBanner(Integer bannerId) {
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

    @Override
    public Void createCategory(CreateItemCategoryReq req) throws ApiException {
        return null;
    }

    @Override
    public Void editCategory(UpdateItemCategoryReq req) throws ApiException {
        return null;
    }

    @Override
    public Void deleteCategory(Integer categoryId) {
        return null;
    }

    @Override
    public List<Object> queryCategory(Boolean isEnable) {
        return null;
    }

    @Override
    public IPage<Object> pageQueryItems(QueryItemReq req) {
        return null;
    }

    @Override
    public Void updateItemIsEnable(Integer itemId, boolean isEnable) throws ApiException {
        return null;
    }

    @Override
    public Void updateSkuIsEnable(Integer skuId, boolean isEnable) throws ApiException {
        return null;
    }

    @Override
    public Void deleteItem(Integer itemId) throws ApiException {
        return null;
    }

    @Override
    public Object findItemDetailAndSkuList(Integer itemId) {
        return null;
    }

    @Override
    public Void createItem(ItemCreateReq req) throws ApiException {
        return null;
    }

    @Override
    public Void editItem(ItemUpdateReq req) throws ApiException {
        return null;
    }

    @Override
    public List<Object> generateItemSku(GenerateItemSkuReq req) throws ApiException {
        return null;
    }

    @Override
    public Void recreateItemSku(GenerateItemSkuReq req) throws ApiException {
        return null;
    }

    @Override
    public Void saveItemSku(SaveItemSkuReq req) throws ApiException {
        return null;
    }
}
