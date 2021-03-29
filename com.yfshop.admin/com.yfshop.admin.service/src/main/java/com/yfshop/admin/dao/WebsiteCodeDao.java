package com.yfshop.admin.dao;

import com.yfshop.admin.api.website.req.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;

import java.util.List;

public interface WebsiteCodeDao {


    List<WebsiteCodeResult> queryWebsiteCodeList(WebsiteCodeQueryReq websiteCodeQueryReq);
}
