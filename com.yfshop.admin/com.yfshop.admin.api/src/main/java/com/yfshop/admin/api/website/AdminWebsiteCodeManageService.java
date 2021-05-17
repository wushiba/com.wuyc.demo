package com.yfshop.admin.api.website;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryDetailsReq;
import com.yfshop.admin.api.website.request.WebsiteCodeExpressReq;
import com.yfshop.admin.api.website.request.WebsiteCodeQueryReq;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailExport;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.admin.api.website.result.WebsiteCodeResult;
import com.yfshop.common.exception.ApiException;

import java.util.List;

/**
 * @author youshenghui
 * Created in 2021-03-25 18:20
 */
public interface AdminWebsiteCodeManageService {

    /**
     * 获取网点码列表
     *
     * @return
     */
    IPage<WebsiteCodeResult> queryWebsiteCodeList(WebsiteCodeQueryReq websiteCodeQueryReq) throws ApiException;


    IPage<WebsiteCodeResult> queryWebsiteCodeByWl(WebsiteCodeQueryReq websiteCodeQueryReq) throws ApiException;

    /**
     * 获取网点码详情
     *
     * @param websiteCodeQueryDetailsReq
     * @return
     */
    IPage<WebsiteCodeDetailResult> queryWebsiteCodeDetailsList(WebsiteCodeQueryDetailsReq websiteCodeQueryDetailsReq) throws ApiException;

    /**
     * 导出网点码详情数据
     *
     * @param websiteCodeQueryDetailsReq
     * @return
     */
    List<WebsiteCodeDetailExport> exportWebsiteCodeDetails(WebsiteCodeQueryDetailsReq websiteCodeQueryDetailsReq) throws ApiException;

    /**
     * 更新网点码快递信息
     *
     * @param websiteCodeQueryExpressReq
     * @return
     */
    Void updateWebsiteCodeExpress(WebsiteCodeExpressReq websiteCodeQueryExpressReq) throws ApiException;

    /**
     * 获取网点码下载文件
     *
     * @param id
     * @return
     */
    String getWebsiteCodeUrl(Integer id) throws ApiException;

    Void retryWebsiteCode(Integer websiteCodeId);
}
