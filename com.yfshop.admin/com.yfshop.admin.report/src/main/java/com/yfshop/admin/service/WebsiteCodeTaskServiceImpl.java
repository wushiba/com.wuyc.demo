package com.yfshop.admin.service;

import com.yfshop.admin.api.website.WebsiteCodeTaskService;
import com.yfshop.admin.task.WebsiteCodeTask;
import com.yfshop.code.mapper.WebsiteCodeMapper;
import com.yfshop.code.model.WebsiteCode;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@DubboService
public class WebsiteCodeTaskServiceImpl implements WebsiteCodeTaskService {
    @Resource
    private WebsiteCodeMapper websiteCodeMapper;
    @Autowired
    private WebsiteCodeTask websiteCodeTask;

    @Override
    public void buildWebSiteCode(Integer id) {
        WebsiteCode websiteCode = websiteCodeMapper.selectById(id);
        websiteCodeTask.buildWebSiteCode(websiteCode);
    }

    @Override
    public void doWorkWebsiteCodeFile(String outTradeNo) {
        websiteCodeTask.doWorkWebsiteCodeFile(outTradeNo);
    }
}
