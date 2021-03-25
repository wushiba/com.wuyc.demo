package com.yfshop.admin.controller.draw;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.QueryCouponReq;
import com.yfshop.admin.api.coupon.result.YfCouponResult;
import com.yfshop.admin.api.coupon.service.AdminCouponService;
import com.yfshop.admin.api.draw.request.CreateDrawActivityReq;
import com.yfshop.admin.api.draw.request.QueryDrawActivityReq;
import com.yfshop.admin.api.draw.result.YfDrawActivityResult;
import com.yfshop.admin.api.draw.service.AdminDrawActivityService;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("admin/draw")
public class AdminDrawController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminDrawController.class);

    @DubboReference(check = false)
    private AdminCouponService adminCouponService;

    @DubboReference(check = false)
    private AdminDrawActivityService adminDrawActivityService;

    @DubboReference(check = false)
    private AdminMallManageService adminMallManageService;

    @RequestMapping(value = "/findList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Page<YfDrawActivityResult>> findList(QueryDrawActivityReq req) {
        return CommonResult.success(adminDrawActivityService.findYfDrawActivityListByPage(req));
    }

    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateCouponStatus(@NotNull(message = "活动id不能为空") Integer id,
                                           @NotNull(message = "上下架状态不能为空") String isEnable) {
        adminDrawActivityService.updateYfDrawActivityStatus(id, isEnable);
        return CommonResult.success(null);
    }

    @RequestMapping(value = "/deleteDrawActivity", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> deleteDrawActivity(@NotNull(message = "优惠券id不能为空") Integer id) {
        adminDrawActivityService.deleteYfDrawActivityById(id);
        return CommonResult.success(null);
    }

    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> saveOrUpdate(@NotNull(message = "抽奖活动信息不能为空") CreateDrawActivityReq req) {
        if (req.getId() == null || req.getId() <= 0) {
            adminDrawActivityService.insertYfDrawActivity(req);
        } else {
            adminDrawActivityService.updateYfDrawActivity(req);
        }
        return CommonResult.success(null);
    }

    @RequestMapping(value = "/coupon/findList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Page<YfCouponResult>> findCouponList(QueryCouponReq req) {
        return CommonResult.success(adminCouponService.findYfCouponListByPage(req));
    }

}