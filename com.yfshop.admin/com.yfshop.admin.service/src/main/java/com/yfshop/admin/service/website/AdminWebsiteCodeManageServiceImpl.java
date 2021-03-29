package com.yfshop.admin.service.website;

import com.yfshop.admin.api.website.AdminWebsiteCodeManageService;
import com.yfshop.admin.api.website.req.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.dao.WebsiteCodeDao;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class AdminWebsiteCodeManageServiceImpl implements AdminWebsiteCodeManageService {

    @Resource
    private WebsiteCodeDao websiteCodeDao;

    @Override
    public List<WebsiteCodeResult> queryWebsiteCodeList(WebsiteCodeQueryReq websiteCodeQueryReq) {

        return websiteCodeDao.queryWebsiteCodeList(websiteCodeQueryReq);
    }
}
