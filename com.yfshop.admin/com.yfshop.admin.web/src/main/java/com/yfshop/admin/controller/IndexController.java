package com.yfshop.admin.controller;

import cn.dev33.satoken.dao.SaTokenDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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


}