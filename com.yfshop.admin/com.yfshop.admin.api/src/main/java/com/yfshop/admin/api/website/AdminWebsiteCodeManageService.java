package com.yfshop.admin.api.website;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.website.req.WebsiteCodeQueryDetailsReq;
import com.yfshop.admin.api.website.req.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.admin.api.website.result.WebsiteTypeResult;
import com.yfshop.common.exception.ApiException;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author youshenghui
 * Created in 2021-03-25 18:20
 */
public interface AdminWebsiteCodeManageService {

    /**
     * 获取商户码列表
     *
     * @return
     */
    IPage<WebsiteCodeResult> queryWebsiteCodeList(WebsiteCodeQueryReq websiteCodeQueryReq);


    /**
     * 获取商户码详情
     * @param websiteCodeQueryDetailsReq
     * @return
     */
    IPage<WebsiteCodeDetailResult> queryWebsiteCodeDetailsList(WebsiteCodeQueryDetailsReq websiteCodeQueryDetailsReq);

}
