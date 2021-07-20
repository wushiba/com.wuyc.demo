package com.yfshop.shop.controller.route;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
@RequestMapping("front/route")
class RouteController {
    @DubboReference
    private FrontUserCouponService frontUserCouponService;

    @RequestMapping("/coupon")
    public void coupon(Long id, HttpServletResponse response) throws IOException {
        response.sendRedirect(frontUserCouponService.getCouponRouteUrl(id));
    }

}