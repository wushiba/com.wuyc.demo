package com.yfshop.shop.controller;

import com.yfshop.common.log.IgnoreLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 错误页面处理
 *
 * @author Xulg
 * Created in 2019-05-30 11:04
 */
@IgnoreLog
@Controller
@ApiIgnore
public class ErrorController {

    /**
     * 404页面
     */
    @RequestMapping(value = "/404.html")
    public String page404() {
        return "404";
    }

    /**
     * 500页面
     */
    @RequestMapping(value = "/500.html")
    public String page500() {
        return "500";
    }
}
