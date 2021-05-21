package com.yfshop.admin.api.mall;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.mall.request.CreateBannerReq;
import com.yfshop.admin.api.mall.request.CreateItemCategoryReq;
import com.yfshop.admin.api.mall.request.GenerateItemSkuReq;
import com.yfshop.admin.api.mall.request.ItemCreateReq;
import com.yfshop.admin.api.mall.request.ItemUpdateReq;
import com.yfshop.admin.api.mall.request.QueryItemReq;
import com.yfshop.admin.api.mall.request.RecreateItemSkuReq;
import com.yfshop.admin.api.mall.request.SaveItemSkuReq;
import com.yfshop.admin.api.mall.request.UpdateBannerReq;
import com.yfshop.admin.api.mall.request.UpdateItemCategoryReq;
import com.yfshop.admin.api.mall.result.BannerResult;
import com.yfshop.admin.api.mall.result.ItemCategoryResult;
import com.yfshop.admin.api.mall.result.ItemResult;
import com.yfshop.admin.api.mall.result.ItemSkuResult;
import com.yfshop.common.exception.ApiException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商城管理服务
 *
 * @author Xulg
 * Created in 2019-06-23 9:10
 */
public interface AdminMallManageService {

    /**
     * 创建banner
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void createBanner(@Valid @NotNull(message = "创建banner信息不能为空") CreateBannerReq req) throws ApiException;

    /**
     * 编辑banner
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void editBanner(@Valid @NotNull(message = "编辑banner信息不能为空") UpdateBannerReq req) throws ApiException;

    /**
     * 删除banner
     *
     * @param bannerId the banner id
     * @return void
     */
    Void deleteBanner(@NotNull(message = "bannerId不能为空") Integer bannerId);

    /**
     * 分页查询首页banner
     *
     * @param pageIndex 页码
     * @param pageSize  每页显示个数
     * @param positions home|banner
     * @return the page data
     */
    IPage<BannerResult> pageQueryBanner(Integer pageIndex, Integer pageSize, String positions);

    /**
     * 创建分类
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void createCategory(@Valid @NotNull(message = "创建分类信息不能为空") CreateItemCategoryReq req) throws ApiException;

    /**
     * 编辑分类
     *
     * @param req the req
     * @return void
     * @throws ApiException e
     */
    Void editCategory(@Valid @NotNull(message = "编辑分类信息不能为空") UpdateItemCategoryReq req) throws ApiException;

    /**
     * 删除类目
     *
     * @param categoryId 类目id
     * @return void
     */
    Void deleteCategory(Integer categoryId) throws ApiException;

    /**
     * 查询分类
     *
     * @param isEnable 是否可用
     * @return the first category list
     */
    List<ItemCategoryResult> queryCategory(Boolean isEnable);

    /**
     * 修改分类排序序号
     *
     * @param categoryId the category id
     * @param sort       the sort
     * @return void
     */
    Void modifyCategorySort(@NotNull(message = "分类ID不能为空") Integer categoryId, @NotNull(message = "序号不能为空") Integer sort);

    /**
     * 分页条件查询商品
     *
     * @param req the req
     * @return the page data
     */
    IPage<ItemResult> pageQueryItems(QueryItemReq req);

    /**
     * 商品上架/下架
     *
     * @param itemId   商品id
     * @param isEnable 是否上架
     * @return void
     * @throws ApiException e
     */
    Void updateItemIsEnable(@NotNull(message = "商品id不能为空") Integer itemId, boolean isEnable) throws ApiException;

    /**
     * sku上架/下架
     *
     * @param skuId    sku id
     * @param isEnable 是否上架
     * @return void
     * @throws ApiException e
     */
    Void updateSkuIsEnable(@NotNull(message = "规格id不能为空") Integer skuId, boolean isEnable) throws ApiException;

    /**
     * 删除商品
     *
     * @param itemId 商品id
     * @return void
     * @throws ApiException e
     */
    Void deleteItem(@NotNull(message = "规格id不能为空") Integer itemId) throws ApiException;

    /**
     * 查询商品的详情及其sku列表
     *
     * @param itemId the item id
     * @return the item details
     */
    ItemResult findItemDetailAndSkuList(Integer itemId);

    /**
     * 创建商品的基本信息
     * 商品信息，商品图片，商品详情
     *
     * @param req 商品数据
     * @return void
     * @throws ApiException e
     */
    Void createItem(@Valid @NotNull ItemCreateReq req) throws ApiException;

    /**
     * 编辑商品的基本信息
     *
     * @param req 商品数据
     * @return void
     * @throws ApiException e
     */
    Void editItem(@Valid @NotNull ItemUpdateReq req) throws ApiException;

    /**
     * 根据规格生成sku信息用于预览
     *
     * @param req 规格数据
     * @return the sku list
     * @throws ApiException e
     */
    List<ItemSkuResult> previewItemSku(@Valid @NotNull GenerateItemSkuReq req) throws ApiException;

    /**
     * 重建商品的sku信息
     *
     * @param req 规格数据
     * @return void
     * @throws ApiException e
     */
    Void recreateItemSku(@Valid @NotNull RecreateItemSkuReq req) throws ApiException;

    /**
     * 保存商品的sku信息
     *
     * @param req 规格数据和候选sku列表
     * @return void
     * @throws ApiException e
     */
    Void saveItemSku(@Valid @NotNull SaveItemSkuReq req) throws ApiException;
}