package com.yfshop.admin.service.merchant;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.service.merchant.MerchantInfoService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
import com.yfshop.admin.api.website.result.WebsiteCodeDetailResult;
import com.yfshop.code.mapper.MerchantDetailMapper;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.WebsiteCodeDetailMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.MerchantDetail;
import com.yfshop.code.model.WebsiteCode;
import com.yfshop.code.model.WebsiteCodeDetail;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户用户服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
@Validated
@DubboService
public class MerchantInfoServiceImpl implements MerchantInfoService {

    @Resource
    MerchantMapper merchantMapper;

    @Resource
    MerchantDetailMapper merchantDetailMapper;

    @Resource
    WebsiteCodeDetailMapper websiteCodeDetailMapper;


    @Override
    public MerchantResult getWebsiteInfo(Integer merchantId) throws ApiException {
        Merchant merchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(merchant, 500, "网点信息不存在！");
        MerchantDetail merchantDetail = merchantDetailMapper.selectOne(Wrappers.<MerchantDetail>lambdaQuery()
                .eq(MerchantDetail::getMerchantId, merchantId));
        MerchantResult merchantResult = BeanUtil.convert(merchant, MerchantResult.class);
        if (merchantDetail != null) {
            BeanUtil.copyProperties(merchantDetail, merchantResult);
        }
        return merchantResult;
    }

    @Override
    public List<WebsiteCodeDetailResult> getWebsiteCode(Integer merchantId) throws ApiException {
        List<WebsiteCodeDetail> websiteCodeDetails = websiteCodeDetailMapper.selectList(Wrappers.<WebsiteCodeDetail>lambdaQuery()
                .eq(WebsiteCodeDetail::getMerchantId, merchantId)
                .orderByDesc(WebsiteCodeDetail::getUpdateTime));
        return BeanUtil.convertList(websiteCodeDetails, WebsiteCodeDetailResult.class);
    }

}
