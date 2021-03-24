package com.yfshop.admin.service.merchant;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.admin.api.service.merchant.MerchantLoginService;
import com.yfshop.admin.api.service.merchant.result.MerchantResult;
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
                .eq(Merchant::getPassword, SecureUtil.md5(pwd)));
        Asserts.assertNonNull(merchant, 500, "账号或密码输入错误！");
        MerchantResult merchantResult = new MerchantResult();
        BeanUtil.copyProperties(merchant, merchantResult);
        return merchantResult;
    }

    @Override
    public MerchantResult loginByCaptcha(String mobile, String captcha) throws ApiException {
        captchaService.checkCaptcha(mobile, captcha, CaptchaSourceEnum.LOGIN_CAPTCHA);
        Merchant merchant = merchantMapper.selectOne(Wrappers.<Merchant>lambdaQuery()
                .eq(Merchant::getMobile, mobile));
        Asserts.assertNonNull(merchant, 500, "账号输入错误！");
        MerchantResult merchantResult = new MerchantResult();
        BeanUtil.copyProperties(merchant, merchantResult);
        return merchantResult;
    }
}
