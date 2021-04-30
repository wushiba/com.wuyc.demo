package com.yfshop.admin.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface WebsiteGoodsRecordDao {

    int sumCurrentGoodsRecord(@Param("merchantId") Integer merchantId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    int sumAllGoodsRecord(@Param("merchantId") Integer merchantId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    int sumGoodsRecordByMerchantId(@Param("merchantId") Integer merchantId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
