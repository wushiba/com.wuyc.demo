package com.yfshop.shop.controller.draw;

import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.enums.CouponResourceEnum;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.activity.service.FrontDrawService;
import com.yfshop.shop.service.coupon.request.QueryUserCouponReq;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("front/draw")
public class FrontDrawController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(FrontDrawController.class);

    @DubboReference(check = false)
    private FrontDrawService frontDrawService;

    @DubboReference(check = false)
    private FrontUserCouponService frontUserCouponService;

    @RequestMapping(value = "activity/getDetail", method = {RequestMethod.POST})
    @ResponseBody
//    @IpAccessLimit(limit = 10, second = 1)
    public CommonResult<YfDrawActivityResult> getActivityDetail(Integer id) {
        return CommonResult.success(frontDrawService.getDrawActivityDetailById(id));
    }

    @RequestMapping(value = "user/clickDraw", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<YfUserCouponResult> userClickDraw(String actCode) {
        return CommonResult.success(frontDrawService.userClickDraw(getCurrentUserId(), getRequestIpStr(), actCode));
    }

    @RequestMapping(value = "record/findList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<YfUserCouponResult>> findDrawRecordList() {
        QueryUserCouponReq req = new QueryUserCouponReq();
        req.setUserId(getCurrentUserId());
        req.setCouponResource(CouponResourceEnum.DRAW.getCode());
        return CommonResult.success(frontUserCouponService.findUserCouponList(req));
    }

    @RequestMapping(value = "all/record/findList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<YfUserCouponResult>> findAllUserDrawRecordList() {
        QueryUserCouponReq req = new QueryUserCouponReq();
        req.setCouponResource(CouponResourceEnum.DRAW.getCode());
        return CommonResult.success(frontUserCouponService.findAllUserDrawRecordList(req));
    }

    @RequestMapping(value = "user/white/add", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Long> addDrawUserWhite(Integer userId) {
        return CommonResult.success(frontDrawService.addDrawUserWhite(userId));
    }

    @RequestMapping(value = "user/white/delete", method = {RequestMethod.POST})
    @ResponseBody
    public CommonResult<Long> deleteDrawUserWhite(Integer userId) {
        return CommonResult.success(frontDrawService.addDrawUserWhite(userId));
    }

}
