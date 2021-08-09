package com.yfshop.admin.controller;

import com.yfshop.admin.api.spread.SpreadService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
class JdShortController {
    @DubboReference
    private SpreadService spreadService;


    @RequestMapping("jd/{shortCode}")
    public void jumpUrl(@PathVariable("shortCode") String shortCode, HttpServletResponse response) throws IOException {
        response.sendRedirect(spreadService.getLongUrlByShortCode(shortCode));
    }


}