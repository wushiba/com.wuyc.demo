package com.yfshop.shop.controller.address;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.shop.service.address.UserAddressService;
import com.yfshop.shop.service.address.request.CreateUserAddressReq;
import com.yfshop.shop.service.address.request.UpdateUserAddressReq;
import com.yfshop.shop.service.address.result.UserAddressResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @ApiOperation(value = "查询用户收货地址列表", httpMethod = "GET")
    @RequestMapping(value = "/queryUserAddresses", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<UserAddressResult>> queryUserAddresses() {
        return CommonResult.success(userAddressService.queryUserAddresses(getCurrentUserId()));
    }

    @ApiOperation(value = "删除用户的地址", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "ids", value = "地址ID，多个使用\",\"拼接", required = true),
    })
    @RequestMapping(value = "/deleteUserAddress", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> deleteUserAddress(@NotBlank(message = "地址ID不能为空") String ids) {
        List<Integer> userAddressIds = Arrays.stream(StringUtils.split(ids, ","))
                .map(Integer::valueOf).distinct().collect(Collectors.toList());
        return CommonResult.success(userAddressService.deleteUserAddress(getCurrentUserId(), userAddressIds));
    }

    @ApiOperation(value = "设置用户的默认地址", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "userAddressId", value = "地址ID", required = true),
    })
    @RequestMapping(value = "/configDefaultUserAddress", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> configDefaultUserAddress(@NotNull(message = "地址ID不能为空") Integer userAddressId) {
        return CommonResult.success(userAddressService.configDefaultUserAddress(getCurrentUserId(), userAddressId));
    }

}
