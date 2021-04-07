package com.yfshop.admin.controller;

import com.yfshop.common.api.CommonResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
class IndexController {


    @RequestMapping("/")
    public CommonResult index() {

        return CommonResult.success("ok");
    }


}