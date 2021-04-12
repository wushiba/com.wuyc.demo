package com.yfshop.admin.service.merchant;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.admin.api.merchant.MerchantLoginService;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.model.Merchant;
import com.yfshop.common.enums.CaptchaSourceEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 用户登录服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
@Validated
@DubboService
public class MerchantLoginServiceImpl implements MerchantLoginService {

    @Resource
    MerchantMapper merchantMapper;

    @DubboReference(check = false)
    private CaptchaService captchaService;

    @Override
    public MerchantResult loginByPwd(String mobile, String pwd) throws ApiException {
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMobile, mobile)
                .eq(Merchant::getPassword, SecureUtil.md5(pwd))
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N"));
        Asserts.assertNonNull(merchant, 500, "账号或密码输入错误！");
        return BeanUtil.convert(merchant, MerchantResult.class);
    }

    @Override
    public MerchantResult loginByCaptcha(String mobile, String captcha) throws ApiException {
        captchaService.checkCaptcha(mobile, captcha, CaptchaSourceEnum.LOGIN_CAPTCHA);
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMobile, mobile)
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N"));
        Asserts.assertNonNull(merchant, 500, "账号输入错误！");
        return BeanUtil.convert(merchant, MerchantResult.class);
    }

    @Override
    public MerchantResult loginByWx(String openId) throws ApiException {
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getOpenId, openId)
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N"));
        Asserts.assertNonNull(merchant, 500, "没有商户信息");
        return BeanUtil.convert(merchant, MerchantResult.class);
    }
}
