package com.yfshop.common.constants;

import org.springframework.data.redis.connection.RedisGeoCommands;

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

    String USER_ID = "user_address_id_";
    /** 用户请求ip地址缓存 */
    String USER_INFO_ID = "user_info_id_";
    /** 用户请求ip地址缓存 */
    String USER_REQUEST_IP_STR = "user_ip_str_";
    /** 用户收货地址缓存 */
    String USER_ADDRESS_ID = "user_address_id_";

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
    /** 商品skuId缓存详情 */
    String MALL_ITEM_SKU_CACHE_KEY_PREFIX = "mall_item_sku_";

    /** 用户收货地址 */
    String MALL_USER_ADDRESS_CACHE_NAME = "MallUserAddressCacheName";
    String MALL_USER_ADDRESS_CACHE_KEY_PREFIX = "mall_user_address_";

    /** 商户地理位置缓存 */
    String MERCHANT_GRO_DATA = "merchant_geo_data";
    /** 商户id信息缓存 */
    String MERCHANT_INFO_DATA = "merchant_info_id_";
    /** 商户列表信息缓存 */
    String MERCHANT_LIST_INFO_DATA = "merchant_list_info_data";
    /** 商户网点码缓存 */
    String MERCHANT_WEBSITE_CODE = "merchant_website_code_";

    /** 用户商户距离 */
    Integer USER_MERCHANT_DISTANCE = 10;
    RedisGeoCommands.DistanceUnit USER_MERCHANT_DISTANCE_UNIT = RedisGeoCommands.DistanceUnit.KILOMETERS;

    /** 优惠券-优惠券单个缓存 */
    String COUPON_INFO_DATA = "coupon_data_id_";
    /** 抽奖跑马灯缓存 */
    String ALL_USER_COUPON_RECORD_LIST = "all_user_coupon_record_list";

}
