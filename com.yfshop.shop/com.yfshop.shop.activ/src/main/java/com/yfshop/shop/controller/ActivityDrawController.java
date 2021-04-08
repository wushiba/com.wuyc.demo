package com.yfshop.shop.controller;

import com.yfshop.common.accesslimit.IpAccessLimit;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.request.QueryUserCouponReq;
import com.yfshop.shop.result.YfDrawActivityResult;
import com.yfshop.shop.result.YfUserCouponResult;
import com.yfshop.shop.service.ActivityCouponService;
import com.yfshop.shop.service.ActivityDrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("admin/draw")
public class ActivityDrawController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityDrawController.class);

    @Autowired
    private ActivityDrawService activityDrawService;

    @Autowired
    private ActivityCouponService activityCouponService;

    @RequestMapping(value = "activity/getDetail", method = {RequestMethod.POST})
    @ResponseBody
//    @IpAccessLimit(limit = 10, second = 1)
    public CommonResult<YfDrawActivityResult> getActivityDetail(Integer id) {
        return CommonResult.success(activityDrawService.getDrawActivityDetailById(id));
    }

    @RequestMapping(value = "record/findList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
//    @IpAccessLimit(limit = 10, second = 1)
    public CommonResult<List<YfUserCouponResult>> findDrawRecordList() {
        QueryUserCouponReq req = new QueryUserCouponReq();
        req.setUserId(getCurrentUserId());
        return CommonResult.success(activityCouponService.findUserCouponList(req));
    }

    @RequestMapping(value = "user/clickDraw", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
//    @IpAccessLimit(limit = 10, second = 1)
    public CommonResult<YfUserCouponResult> userClickDraw(HttpServletRequest request, String actCode) {
//        return CommonResult.success(activityDrawService.userClickDraw(getCurrentUserId(), getRequestIpStr(request), actCode));
//        String activeProfile = SpringUtil.getActiveProfile();
        return CommonResult.success(activityDrawService.userClickDraw(getCurrentUserId(), "115.239.212.133", actCode));
    }
}
