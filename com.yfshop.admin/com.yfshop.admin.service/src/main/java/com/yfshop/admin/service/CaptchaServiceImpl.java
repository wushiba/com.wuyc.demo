package com.yfshop.admin.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.enums.CaptchaSourceEnum;
import com.yfshop.admin.api.service.CaptchaService;
import com.yfshop.code.mapper.CaptchaMapper;
import com.yfshop.code.model.Captcha;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 验证码服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
@Validated
@DubboService
public class CaptchaServiceImpl implements CaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);

    @Resource
    CaptchaMapper captchaMapper;

    //@Autowired
    private RedisService redisService;


    @Override
    public Void sendCaptcha(String mobile, CaptchaSourceEnum captchaSourceEnum) throws ApiException {
        Date now = DateUtil.getDate(new Date());
        Date nextDay = DateUtil.plusDays(now, 1);
        int count = captchaMapper.selectCount(Wrappers.<Captcha>lambdaQuery()
                .eq(Captcha::getSource, captchaSourceEnum.getSource())
                .eq(Captcha::getMobile, mobile)
                .ge(Captcha::getCreateTime, now)
                .lt(Captcha::getCreateTime, nextDay));
        Asserts.assertTrue(captchaSourceEnum.getToDayLimit() > count, 500, "获取验证码已上限！");
        String captcha = RandomUtil.randomNumbers(4);
        String key = "CAPTCHA" + ":" + captchaSourceEnum.getSource() + ":" + mobile;
        redisService.set(key, captcha, captchaSourceEnum.getExpireDuration() * 60);
        Captcha c = new Captcha();
        c.setCaptcha(captcha);
        c.setMobile(mobile);
        c.setSource(captchaSourceEnum.getSource());
        c.setSmsTemplate(captchaSourceEnum.getSmsTemplate());
        c.setExpireTime(LocalDateTime.now().plusMinutes(captchaSourceEnum.getExpireDuration()));
        captchaMapper.insert(c);
        logger.debug(String.format(captchaSourceEnum.getSmsTemplate(), captcha));
        return null;
    }

    @Override
    public Void checkCaptcha(String mobile, String captcha, CaptchaSourceEnum captchaSourceEnum) throws ApiException {
        String key = "CAPTCHA" + ":" + captchaSourceEnum.getSource() + ":" + mobile;
        Asserts.assertTrue(redisService.hasKey(key), 500, "验证码校验失败！");
        return null;
    }
}
