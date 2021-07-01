package com.yfshop.shop.service.mall;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.mall.req.QueryItemDetailReq;
import com.yfshop.shop.service.mall.req.QueryItemReq;
import com.yfshop.shop.service.mall.result.BannerResult;
import com.yfshop.shop.service.mall.result.ItemCategoryResult;
import com.yfshop.shop.service.mall.result.ItemResult;
import com.yfshop.shop.service.mall.result.ItemSkuResult;

import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-29 10:40
 */
public interface MallService {

    List<ItemCategoryResult> queryCategories();

    List<ItemResult> queryItems(QueryItemReq req);

    ItemResult findItemDetail(QueryItemDetailReq req);

    List<BannerResult> queryHomeBannerList();

    List<BannerResult> queryLoopBannerList();

    List<BannerResult> queryPersonalCenterBannerList();

    List<BannerResult> queryCategoryBannerList();

    ItemSkuResult getItemSkuBySkuId(Integer skuId) throws ApiException;

    /**
     * 修改商品sku库存
     *
     * @param skuId skuId
     * @param num   扣减库存的数量
     * @return Integer > 0 ,说明成功， 小于0抛异常
     * @throws ApiException
     */
    Integer updateItemSkuStock(Integer skuId, Integer num) throws ApiException;

    Long getBuyGoodsCount(Integer itemId) throws ApiException;

    List<String> getBuyGoodsUser();
}
