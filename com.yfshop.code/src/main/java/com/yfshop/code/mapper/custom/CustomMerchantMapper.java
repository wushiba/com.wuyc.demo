package com.yfshop.code.mapper.custom;

import com.yfshop.code.query.QueryMerchantDetail;
import com.yfshop.code.result.MerchantInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商户复杂查询sql
 *
 * @author Xulg
 * Created in 2021-03-25 14:12
 */
public interface CustomMerchantMapper {

    List<MerchantInfo> pageQueryMerchantInfo(@Param("param") QueryMerchantDetail query,
                                             @Param("startIndex") int startIndex,
                                             @Param("pageSize") int pageSize);

    int countMerchantInfo(@Param("param") QueryMerchantDetail query);
}
