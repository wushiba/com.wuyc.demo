package com.yfshop.admin.controller.coupon;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.CreateCouponReq;
import com.yfshop.admin.api.coupon.request.QueryCouponReq;
import com.yfshop.admin.api.coupon.request.QueryUserCouponReq;
import com.yfshop.admin.api.coupon.result.YfCouponResult;
import com.yfshop.admin.api.coupon.result.YfUserCouponResult;
import com.yfshop.admin.api.coupon.service.AdminCouponService;
import com.yfshop.admin.api.coupon.service.AdminUserCouponService;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.mall.request.*;
import com.yfshop.admin.api.mall.result.ItemResult;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("admin/coupon")
public class AdminCouponController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminCouponController.class);

    @DubboReference(check = false)
    private AdminCouponService adminCouponService;

    @DubboReference(check = false)
    private AdminUserCouponService adminUserCouponService;

    @DubboReference(check = false)
    private AdminMallManageService adminMallManageService;

    @RequestMapping(value = "/findList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<YfCouponResult>> findList(QueryCouponReq req) {
        return CommonResult.success(adminCouponService.findYfCouponListByPage(req));
    }

    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> updateCouponStatus(@NotNull(message = "优惠券id不能为空") Integer id,
                                           @NotNull(message = "上下架状态不能为空") String isEnable) {
        adminCouponService.updateCouponStatus(id, isEnable);
        return CommonResult.success(null);
    }

    @RequestMapping(value = "/deleteCoupon", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> deleteCoupon(@NotNull(message = "优惠券id不能为空") Integer id) {
        adminCouponService.deleteYfCoupon(id);
        return CommonResult.success(null);
    }

    @RequestMapping(value = "/item/findList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<ItemResult>> findItemList(QueryItemReq req) {
        return CommonResult.success(adminMallManageService.pageQueryItems(req));
    }

    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> saveOrUpdate(@NotNull(message = "优惠券信息不能为空") CreateCouponReq req) {
        if (req.getId() == null || req.getId() <= 0) {
            adminCouponService.insertYfCoupon(req);
        } else {
            adminCouponService.updateYfCoupon(req);
        }
        return CommonResult.success(null);
    }

    @RequestMapping(value = "/userCoupon/findList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<YfUserCouponResult>> findUserCouponList(QueryUserCouponReq req) {
        return CommonResult.success(adminUserCouponService.findYfUserCouponListByPage(req));
    }

}
