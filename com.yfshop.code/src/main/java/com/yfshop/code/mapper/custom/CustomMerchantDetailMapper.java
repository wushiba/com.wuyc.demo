package com.yfshop.code.mapper.custom;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商户详情表(扩展表) Mapper 接口
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
public interface CustomMerchantDetailMapper {
    List<Map<String, Object>> countGroupByWebsiteType();
}
