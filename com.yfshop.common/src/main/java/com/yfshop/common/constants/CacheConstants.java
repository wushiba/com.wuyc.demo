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


    String DRAW_ACTIVITY_PREFIX = "draw_activity_act_id_";
    String DRAW_PRIZE_NAME_PREFIX = "draw_prize_act_id_";
    String DRAW_PROVINCE_RATE_PREFIX = "draw_province_rate_act_id_";

}
