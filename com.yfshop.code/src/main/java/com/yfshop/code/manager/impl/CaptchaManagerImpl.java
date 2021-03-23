package com.yfshop.code.manager.impl;

import com.yfshop.code.model.Captcha;
import com.yfshop.code.mapper.CaptchaMapper;
import com.yfshop.code.manager.CaptchaManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 验证码 服务实现类
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Service
public class CaptchaManagerImpl extends ServiceImpl<CaptchaMapper, Captcha> implements CaptchaManager {

}
