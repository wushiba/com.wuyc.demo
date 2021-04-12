package com.yfshop.shop.service.website;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.WebsiteCodeDetailMapper;
import com.yfshop.code.model.WebsiteCodeDetail;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class FrontWebsiteServiceImpl implements FrontWebsiteService {
    @Resource
    WebsiteCodeDetailMapper websiteCodeDetailMapper;

    @Override
    public Integer checkActivate(String websiteCode) {
        WebsiteCodeDetail websiteCodeDetail = websiteCodeDetailMapper.selectOne(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getAlias, websiteCode)
                .eq(WebsiteCodeDetail::getIsActivate, "Y"));
        return websiteCodeDetail == null ? 0 : 1;
    }
}
