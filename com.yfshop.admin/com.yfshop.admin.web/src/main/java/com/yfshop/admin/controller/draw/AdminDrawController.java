package com.yfshop.admin.controller.draw;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.coupon.request.QueryCouponReq;
import com.yfshop.admin.api.coupon.result.YfCouponResult;
import com.yfshop.admin.api.coupon.service.AdminCouponService;
import com.yfshop.admin.api.draw.request.CreateDrawActivityReq;
import com.yfshop.admin.api.draw.request.QueryDrawActivityReq;
import com.yfshop.admin.api.draw.request.QueryDrawRecordReq;
import com.yfshop.admin.api.draw.request.SaveProvinceRateReq;
import com.yfshop.admin.api.draw.result.DrawActivityDetailsResult;
import com.yfshop.admin.api.draw.result.DrawActivityResult;
import com.yfshop.admin.api.draw.result.DrawProvinceResult;
import com.yfshop.admin.api.draw.result.DrawRecordResult;
import com.yfshop.admin.api.draw.service.AdminDrawActivityService;
import com.yfshop.admin.api.draw.service.AdminDrawProvinceService;
import com.yfshop.admin.api.draw.service.AdminDrawRecordService;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import io.swagger.models.auth.In;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import java.util.List;

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

    @DubboReference
    private AdminDrawProvinceService adminDrawProvinceService;

    @DubboReference
    private AdminDrawRecordService adminDrawRecordService;

    @RequestMapping(value = "/findList", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Page<DrawActivityResult>> findList(QueryDrawActivityReq req) {
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

    @RequestMapping(value = "/getDrawActivityDetails", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<DrawActivityDetailsResult> getDrawActivityDetails(Integer id) {

        return CommonResult.success(adminDrawActivityService.getDrawActivityDetails(id));
    }


    @RequestMapping(value = "/saveOrUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> saveOrUpdate(@NotNull(message = "抽奖活动信息不能为空") @RequestBody CreateDrawActivityReq req) {
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


    @RequestMapping(value = "/rate/province/save", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> saveProvinceRate(@RequestBody List<SaveProvinceRateReq> req) {

        return CommonResult.success(adminDrawProvinceService.saveProvinceRate(req));
    }

    @RequestMapping(value = "/rate/province/list", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<List<DrawProvinceResult>> getProvinceRate(Integer id) {

        return CommonResult.success(adminDrawProvinceService.getProvinceRate(id));
    }


    @RequestMapping(value = "/rate/province/delete", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<Void> deleteProvinceRate(Integer id) {

        return CommonResult.success(adminDrawProvinceService.deleteProvinceRate(id));
    }

    @RequestMapping(value = "/record/list", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<IPage<DrawRecordResult>> getDrawRecordList(QueryDrawRecordReq recordReq) {

        return CommonResult.success(adminDrawRecordService.getDrawRecordList(recordReq));
    }

}
