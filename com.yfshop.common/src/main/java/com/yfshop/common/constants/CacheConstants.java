package com.yfshop.common.constants;

/**
 * 缓存常量类
 *
 * @author Xulg
 * Created in 2021-03-23 16:30
 */
public interface CacheConstants {
    String CACHE_MANAGE_NAME = "cacheManager";

    String MERCHANT_ROLE_CACHE_NAME = "MerchantRoleCacheName";
    String MERCHANT_ROLE_CACHE_KEY_PREFIX = "merchant_role_";

    String MERCHANT_PERMISSIONS_CACHE_NAME = "MerchantPermissionsCacheName";
    String MERCHANT_PERMISSIONS_CACHE_KEY_PREFIX = "merchant_permissions_";

    String MERCHANT_MENUS_CACHE_NAME = "MerchantMenusCacheName";
    String MERCHANT_MENUS_CACHE_KEY_PREFIX = "merchant_menus_";

    String USER_CART_CACHE_NAME = "UserCartCacheName";
    String USER_CART_CACHE_KEY_PREFIX = "user_cart_";


    /** 抽奖活动缓存 */
    String DRAW_ACTIVITY_PREFIX = "draw_activity_act_id_";
    /** 抽奖活动下面的奖品缓存 */
    String DRAW_PRIZE_NAME_PREFIX = "draw_prize_act_id_";
    /** 抽奖活动下面的省份定制化缓存 */
    String DRAW_PROVINCE_RATE_PREFIX = "draw_province_rate_act_id_";

    /** 用户请求ip地址缓存 */
    String USER_INFO_ID = "user_info_id_";
    /** 用户请求ip地址缓存 */
    String USER_REQUEST_IP_STR = "user_ip_str_";

    /** 抽奖唯一编码 */
    String ACT_CODE_BATCH_ACT_NO = "act_code_batch_act_no_";

    /** 首页banner缓存 */
    String MALL_BANNER_CACHE_NAME = "MallBannersCacheName";
    String MALL_BANNER_CACHE_KEY_PREFIX = "mall_banners_";

    /** 商品分类信息 */
    String MALL_CATEGORY_CACHE_NAME = "MallCategoryCacheName";
    String MALL_CATEGORY_CACHE_KEY_PREFIX = "mall_categories_";

    /** 分类下商品列表 */
    String MALL_CATEGORY_ITEMS_CACHE_NAME = "MallCategoryItemsCacheName";
    String MALL_CATEGORY_ITEMS_CACHE_KEY_PREFIX = "mall_category_items_";

    /** 商品详情 */
    String MALL_ITEM_DETAIL_CACHE_NAME = "MallItemDetailCacheName";
    String MALL_ITEM_DETAIL_CACHE_KEY_PREFIX = "mall_item_detail_";
}
