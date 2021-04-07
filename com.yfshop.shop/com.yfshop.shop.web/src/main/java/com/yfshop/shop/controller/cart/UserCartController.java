package com.yfshop.shop.controller.cart;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.controller.vo.UserCartPageData;
import com.yfshop.shop.service.cart.UserCartService;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.cart.result.UserCartSummary;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户购物车接口
 *
 * @author Xulg
 * Created in 2021-03-24 10:35
 */
@Controller
@RequestMapping("user/cart")
@Validated
public class UserCartController implements BaseController {

    @DubboReference(check = false)
    private UserCartService userCartService;
    @DubboReference(check = false)
    private FrontUserCouponService userCouponService;

    @ApiOperation(value = "查询购物车页面数据", httpMethod = "GET")
    @RequestMapping(value = "/queryUserCartPageData", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<UserCartPageData> queryUserCartPageData() {
        CompletableFuture<List<UserCartResult>> userCartsFuture = CompletableFuture.supplyAsync(
                () -> userCartService.queryUserCarts(getCurrentUserId()));
        CompletableFuture<List<YfUserCouponResult>> userCouponsFuture = CompletableFuture.supplyAsync(
                () -> userCouponService.findUserCouponList(getCurrentUserId(), "Y", null));
        try {
            UserCartPageData userCartPageData = new UserCartPageData();
            userCartPageData.setCarts(userCartsFuture.get(10, TimeUnit.SECONDS));
            userCartPageData.setCoupons(userCouponsFuture.get(10, TimeUnit.SECONDS));
            return CommonResult.success(userCartPageData);
        } catch (Exception e) {
            throw new ApiException("查询超时，请稍后再试！");
        }
    }

    @ApiOperation(value = "添加商品到购物车", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "skuId", value = "商品skuId", required = true),
            @ApiImplicitParam(paramType = "query", name = "num", value = "数量", required = true)
    })
    @RequestMapping(value = "/addUserCart", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> addUserCart(@NotNull(message = "商品SKU不能为空") Integer skuId,
                                          @NotNull(message = "数量不能为空")
                                          @Positive(message = "数量不能为负") Integer num) {
        return CommonResult.success(userCartService.addUserCart(getCurrentUserId(), skuId, num));
    }

    @ApiOperation(value = "编辑购物车的数量", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "skuId", value = "商品skuId", required = true),
            @ApiImplicitParam(paramType = "query", name = "num", value = "数量", required = true)
    })
    @RequestMapping(value = "/updateUserCart", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> updateUserCart(@NotNull(message = "商品SKU不能为空") Integer skuId,
                                             @NotNull(message = "数量不能为空")
                                             @Min(value = 0L, message = "数量不能为负") Integer num) {
        return CommonResult.success(userCartService.updateUserCart(getCurrentUserId(), skuId, num));
    }

    @ApiOperation(value = "批量删除购物车的商品", httpMethod = "GET")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "query", name = "ids", value = "购物车ID，多个使用\",\"拼接", required = true),
    })
    @RequestMapping(value = "/deleteUserCarts", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> deleteUserCarts(@NotNull(message = "购物车ID不能为空") String ids) {
        List<Integer> skuIds = Arrays.stream(StringUtils.split(ids, ","))
                .map(Integer::valueOf).distinct().collect(Collectors.toList());
        return CommonResult.success(userCartService.deleteUserCarts(getCurrentUserId(), skuIds));
    }

    @ApiOperation(value = "清空用户购物车", httpMethod = "GET")
    @RequestMapping(value = "/clearUserCart", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> clearUserCart() {
        return CommonResult.success(userCartService.clearUserCart(getCurrentUserId()));
    }

//    @ApiOperation(value = "单个SKU商品购买支付页结算数据", httpMethod = "GET")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(paramType = "query", name = "skuId", value = "商品skuId", required = true),
//            @ApiImplicitParam(paramType = "query", name = "num", value = "数量", required = true)
//    })
//    @RequestMapping(value = "/calcUserBuySkuDetails", method = {RequestMethod.GET, RequestMethod.POST})
//    @ResponseBody
//    @SaCheckLogin
//    public CommonResult<UserCartSummary> calcUserBuySkuDetails(@NotNull(message = "skuId不能为空") Integer skuId,
//                                                               @NotNull(message = "购买数量不能为空")
//                                                               @Positive(message = "数量不能为负") Integer num) {
//        return CommonResult.success(userCartService.calcUserBuySkuDetails(getCurrentUserId(), skuId, num));
//    }

//    @ApiOperation(value = "购物车支付页面详情", httpMethod = "GET")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(paramType = "query", name = "ids", value = "购物车ID，多个使用\",\"拼接", required = true),
//    })
//    @RequestMapping(value = "/calcUserSelectedCarts")
//    @ResponseBody
//    @SaCheckLogin
//    public CommonResult<UserCartSummary> calcUserSelectedCarts(@NotBlank(message = "购物车列表不能为空") String ids) {
//        List<Integer> cartIds = Arrays.stream(StringUtils.split(ids, ","))
//                .map(Integer::valueOf).collect(Collectors.toList());
//        return CommonResult.success(userCartService.calcUserSelectedCarts(getCurrentUserId(), cartIds));
//    }

}
