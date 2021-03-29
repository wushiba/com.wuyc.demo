package com.yfshop.admin.dao;

import com.yfshop.admin.dto.query.*;
import com.yfshop.admin.dto.result.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商户复杂查询sql
 *
 * @author Xulg
 * Created in 2021-03-25 14:12
 */
public interface MerchantDao {

    List<MerchantInfo> pageQueryMerchantInfo(@Param("param") QueryMerchantDetail query,
                                             @Param("startIndex") int startIndex,
                                             @Param("pageSize") int pageSize);

    int countMerchantInfo(@Param("param") QueryMerchantDetail query);
}
