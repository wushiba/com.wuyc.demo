package com.yfshop.admin.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface WebsiteCodeDao {

    List<WebsiteCodeResult> queryWebsiteCodeList(IPage iPage,@Param("req") WebsiteCodeQueryReq req);

    int queryWebsiteCodeCount(WebsiteCodeQueryReq req);

    int sumWebsiteCodeByBeforeId(Integer id,Date statDate);
}