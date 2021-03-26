package com.yfshop.admin.controller;

import com.yfshop.common.accesslimit.IpAccessLimit;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author Xulg
 * Created in 2021-03-26 16:40
 */
@Controller
public class TestIpAccessLimitController {

    @IpAccessLimit(second = 1, limit = 1)
    @GetMapping("limit1")
    @ResponseBody
    public Object testAccessLimit1() {
        return new HashMap<>();
    }

    @IpAccessLimit(second = 1, limit = 10)
    @GetMapping("limit2")
    @ResponseBody
    public Object testAccessLimit2() {
        return new HashMap<>();
    }

    @IpAccessLimit(second = 60, limit = 10)
    @GetMapping("limit3")
    @ResponseBody
    public Object testAccessLimit3() {
        return new HashMap<>();
    }

}
