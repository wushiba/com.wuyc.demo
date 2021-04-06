package com.yfshop.shop.controller.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.yfshop.common.api.CommonResult;
import com.yfshop.common.base.BaseController;
import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.cart.UserCartService;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import com.yfshop.shop.service.merchant.service.FrontMerchantService;
import com.yfshop.shop.service.order.result.YfUserOrderDetailResult;
import com.yfshop.shop.service.order.result.YfUserOrderListResult;
import com.yfshop.shop.service.order.service.FrontUserOrderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Validated
@RequestMapping("front/user")
public class FrontOrderController implements BaseController {

    @DubboReference(check = false)
    private UserCartService userCartService;

    @DubboReference(check = false)
    private FrontMerchantService frontMerchantService;

    @DubboReference(check = false)
    private FrontUserOrderService frontUserOrderService;

    /**
     * 查询附近的商户
     * @param districtId    区id
     * @param longitude     经度
     * @param latitude      纬度
     * @return  List<MerchantResult>
     */
    @RequestMapping(value = "/findNearMerchantList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<MerchantResult>> findNearMerchantList(Integer districtId, Double longitude, Double latitude) {
        return CommonResult.success(frontMerchantService.findNearMerchantList(districtId, longitude, latitude));
    }

    /**
     * 订单结算页商品信息列表
     * @param skuId     skuId
     * @param num       商品数量
     * @param cartIds   购物车ids
     * @return
     */
    @RequestMapping(value = "/order/calc/findItemList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public CommonResult<List<UserCartResult>> findItemInfo(Integer skuId, Integer num, String cartIds) {
        return CommonResult.success(userCartService.findItemList(skuId, num, cartIds));
    }

    /**
     * 根据skuId提交订单
     * @param skuId             skuId
     * @param num               商品数量
     * @param userCouponId      用户优惠券id
     * @param addressId         地址id
     * @return
     */
    @RequestMapping(value = "/order/submitOrderBySkuId", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> submitOrderBySkuId(Integer skuId, Integer num, Long userCouponId, Integer addressId) {
        return CommonResult.success(frontUserOrderService.submitOrderBySkuId(101, skuId, num, userCouponId, addressId));
    }

    /**
     * 根据购物车提交订单
     * @param cartIds           购物车ids
     * @param userCouponId      用户优惠券id
     * @param addressId         用户地址id
     * @return
     */
    @RequestMapping(value = "/order/submitOrderByCart", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> submitOrderByCart(String cartIds, Long userCouponId, Integer addressId) {
        return CommonResult.success(frontUserOrderService.submitOrderByCart(101, cartIds, userCouponId, addressId));
    }

    /**
     * 优惠券购买提交订单
     * @param userCouponIds	用户优惠券ids
     * @param userMobile	用户手机号
     * @param websiteCode	商户网点码
     * @return
     * @throws ApiException
     */
    @RequestMapping(value = "/order/submitOrderByUserCouponIds", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> submitOrderByUserCouponIds(String userCouponIds, String userMobile, String websiteCode) {
        return CommonResult.success(frontUserOrderService.submitOrderByUserCouponIds(101, userCouponIds, userMobile, websiteCode));
    }

    /**
     * 查询用户订单列表
     * @param   orderStatus   订单状态, 对应订单枚举 UserOrderStatusEnum
     * @return  List<YfUserOrderListResult>
     */
    @RequestMapping(value = "/order/findList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<List<YfUserOrderListResult>> findUserOrderList(String orderStatus) {
        return CommonResult.success(frontUserOrderService.findUserOrderList(101, orderStatus));
    }

    /**
     * 用户订单详情
     * @param   orderId           订单id
     * @param   orderDetailId     订单详情id
     * @return  YfUserOrderDetailResult
     */
    @RequestMapping(value = "/order/getOrderDetail", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<YfUserOrderDetailResult> getUserOrderDetail(Integer orderId, Integer orderDetailId) {
        return CommonResult.success(frontUserOrderService.getUserOrderDetail(101, orderId, orderDetailId));
    }

    /**
     * 用户撤销订单
     * @param   orderId   订单id
     * @return  Void
     */
    @RequestMapping(value = "/order/cancelOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> cancelOrder(Integer orderId) {
        return CommonResult.success(frontUserOrderService.cancelOrder(101, orderId));
    }

    /**
     * 用户确认订单
     * @param   orderDetailId   订单详情id
     * @return  Void
     */
    @RequestMapping(value = "/order/confirmOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @SaCheckLogin
    public CommonResult<Void> confirmOrder(Integer orderDetailId) {
        return CommonResult.success(frontUserOrderService.confirmOrder(101, orderDetailId));
    }

}
