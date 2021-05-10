package com.yfshop.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WebsiteCodeDao {

    List<WebsiteCodeResult> queryWebsiteCodeList(IPage iPage,@Param("req") WebsiteCodeQueryReq req);

    int queryWebsiteCodeCount(WebsiteCodeQueryReq req);

    int sumWebsiteCodeByBeforeId(@Param("id") Integer id,@Param("merchantId") Integer merchantId);

    List<WebsiteCodeResult> queryWebsiteCodeByWl(IPage page,@Param("req") WebsiteCodeQueryReq req);

    long queryWebsiteCodeCountByWl(WebsiteCodeQueryReq req);
}
