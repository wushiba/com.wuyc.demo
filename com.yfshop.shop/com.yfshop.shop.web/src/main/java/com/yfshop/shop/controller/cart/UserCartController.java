package com.yfshop.shop.controller.cart;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.controller.vo.UserCartPageData;
import com.yfshop.shop.service.cart.UserCartService;
import com.yfshop.shop.service.cart.result.UserCartResult;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 用户购物车接口
 *
 * @author Xulg
 * Created in 2021-03-24 10:35
 */
@Controller
@Validated
public class UserCartController implements BaseController {

    @DubboReference(check = false)
    private UserCartService userCartService;

    @ApiOperation(value = "查询购物车页面数据", httpMethod = "GET")
    @RequestMapping(value = "/queryUserCartPageData", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<UserCartPageData> queryUserCartPageData() {
        CompletableFuture<List<UserCartResult>> userCartsFuture = CompletableFuture.supplyAsync(
                () -> userCartService.queryUserCarts(getCurrentUserId()));
        // TODO: 2021/3/24 查询用户的优惠券列表
        try {
            UserCartPageData userCartPageData = new UserCartPageData();
            userCartPageData.setCarts(userCartsFuture.get(10, TimeUnit.SECONDS));
            userCartPageData.setCoupons(null);
            return CommonResult.success(userCartPageData);
        } catch (Exception e) {
            throw new ApiException("查询超时，请稍后再试！");
        }
    }

}
