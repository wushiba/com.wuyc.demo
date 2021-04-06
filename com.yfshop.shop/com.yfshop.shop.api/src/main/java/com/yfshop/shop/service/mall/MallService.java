package com.yfshop.shop.service.mall;

import com.yfshop.shop.service.mall.req.QueryItemDetailReq;
import com.yfshop.shop.service.mall.req.QueryItemReq;
import com.yfshop.shop.service.mall.result.ItemCategoryResult;
import com.yfshop.shop.service.mall.result.ItemResult;

import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-29 10:40
 */
public interface MallService {

    List<ItemCategoryResult> queryCategories();

    List<ItemResult> queryItems(QueryItemReq req);

    ItemResult findItemDetail(QueryItemDetailReq req);

    List<String> queryHomeBanners();

    List<String> queryLoopBanners();


}
