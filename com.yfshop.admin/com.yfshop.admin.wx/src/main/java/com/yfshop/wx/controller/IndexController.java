package com.yfshop.admin.controller;

import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
class IndexController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    public CommonResult index() {
       // logger.info("来自{}的请求",getRequestIpStr());
        return CommonResult.success("ok");
    }


}