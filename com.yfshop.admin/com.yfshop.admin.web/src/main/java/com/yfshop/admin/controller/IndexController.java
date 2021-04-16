package com.yfshop.admin.controller;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpUtil;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.common.api.CommonResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
class IndexController {


    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping("/index.html/**")
    public ModelAndView adminIndex() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping(value = "/admin/login/loginOut", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<MerchantResult> logout() {
        StpUtil.logout();
        return CommonResult.success(null);
    }

}