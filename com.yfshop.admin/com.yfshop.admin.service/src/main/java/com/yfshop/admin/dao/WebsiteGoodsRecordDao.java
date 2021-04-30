package com.yfshop.admin.dao;

import java.util.Date;

public interface WebsiteGoodsRecordDao {
    int sumGoodsRecord(Integer merchantId, Date startTime,Date endTime);
}
