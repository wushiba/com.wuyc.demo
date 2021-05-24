package com.yfshop.admin.controller.draw;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yfshop.admin.api.draw.request.QueryDrawRecordReq;
import com.yfshop.admin.api.draw.request.QueryDrawRecordSatsReq;
import com.yfshop.admin.api.draw.result.DrawRecordResult;
import com.yfshop.admin.api.draw.result.DrawRecordSatsByDayResult;
import com.yfshop.admin.api.draw.result.DrawRecordSatsByLevelResult;
import com.yfshop.admin.api.draw.result.DrawRecordSatsByProvinceResult;
import com.yfshop.admin.api.draw.service.AdminDrawRecordService;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("admin/draw/sats")
public class AdminDrawSatsController implements BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AdminDrawSatsController.class);

    @DubboReference
    private AdminDrawRecordService adminDrawRecordService;


    @RequestMapping(value = "/satsByDay", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<List<DrawRecordSatsByDayResult>> satsByDay(QueryDrawRecordSatsReq recordReq) {

        return CommonResult.success(adminDrawRecordService.satsByDay(recordReq));
    }


    @RequestMapping(value = "/satsByLeve", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<List<DrawRecordSatsByLevelResult>> satsByLeve(QueryDrawRecordSatsReq recordReq) {

        return CommonResult.success(adminDrawRecordService.satsByLeve(recordReq));
    }


    @RequestMapping(value = "/satsByProvince", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    @SaCheckRole(value = "sys")
    public CommonResult<List<DrawRecordSatsByProvinceResult>> satsByProvince(QueryDrawRecordSatsReq recordReq) {

        return CommonResult.success(adminDrawRecordService.satsByProvince(recordReq));
    }

}
