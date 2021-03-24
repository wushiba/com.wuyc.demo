package com.yfshop.shop.controller.address;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.service.address.UserAddressService;
import com.yfshop.shop.service.address.request.CreateUserAddressReq;
import com.yfshop.shop.service.address.request.UpdateUserAddressReq;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Created in 2021-03-23 20:02
 */
@Controller
@Validated
public class UserAddressController implements BaseController {

    @DubboReference(check = false)
    private UserAddressService userAddressService;

    @ApiOperation(value = "添加或修改用户收货地址", httpMethod = "GET")
    @RequestMapping(value = "/addOrUpdateUserAddress", method = {RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> addOrUpdateUserAddress(@NotNull(message = "创建分类信息不能为空") UpdateUserAddressReq req) {
        if (req.getUserAddressId() == null) {
            return CommonResult.success(userAddressService.addUserAddress(1, (CreateUserAddressReq) req));
        } else {
            return CommonResult.success(userAddressService.updateUserAddress(1, req));
        }
    }

    public CommonResult<Object> objectCommonResult() {
        return CommonResult.success(userAddressService.queryUserAddresses(getCurrentUserId()));
    }

}
