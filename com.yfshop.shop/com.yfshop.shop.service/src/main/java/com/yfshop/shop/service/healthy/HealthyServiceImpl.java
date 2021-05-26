package com.yfshop.shop.service.healthy;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.code.mapper.HealthyItemMapper;
import com.yfshop.code.mapper.HealthyOrderMapper;
import com.yfshop.code.mapper.HealthySubOrderMapper;
import com.yfshop.code.mapper.UserAddressMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.HealthyItem;
import com.yfshop.code.model.HealthyOrder;
import com.yfshop.code.model.HealthySubOrder;
import com.yfshop.code.model.User;
import com.yfshop.code.model.UserAddress;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.shop.service.healthy.enums.HealthyOrderStatusEnum;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import com.yfshop.wx.api.service.MpPayService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-26 15:53
 */
@DubboService
@Validated
public class HealthyServiceImpl implements HealthyService {
    private static final Logger logger = LoggerFactory.getLogger(HealthyServiceImpl.class);

    @Resource
    private HealthyOrderMapper orderMapper;
    @Resource
    private HealthySubOrderMapper subOrderMapper;
    @Resource
    private HealthyItemMapper itemMapper;
    @Resource
    private UserAddressMapper addressMapper;
    @Resource
    private UserMapper userMapper;
    @DubboReference
    private MpPayService mpPayService;
    @Value("${wxPay.notifyUrl}")
    private String wxPayNotifyUrl;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WxPayMpOrderResult submitOrder(@NotNull(message = "用户ID不能为空") Integer userId,
                                          @Valid @NotNull SubmitHealthyOrderReq req) throws ApiException {
        Integer itemId = req.getItemId(), addressId = req.getAddressId(), buyCount = req.getBuyCount();

        User user = userMapper.selectById(userId);
        Asserts.assertNonNull(user, 500, "用户不存在");
        HealthyItem healthyItem = itemMapper.selectById(itemId);
        Asserts.assertNonNull(healthyItem, 500, "商品不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(healthyItem.getIsEnable()), 500, "商品已下架");
        Asserts.assertTrue("N".equalsIgnoreCase(healthyItem.getIsDelete()), 500, "商品已删除");
        UserAddress userAddress = addressMapper.selectById(addressId);
        Asserts.assertNonNull(userAddress, 500, "收货地址不存在");
        Asserts.assertTrue(userAddress.getUserId().equals(userId), 500, "错误的收货地址");

        BigDecimal orderPrice = new BigDecimal(buyCount).multiply(healthyItem.getItemPrice());
        BigDecimal payPrice = orderPrice.add(BigDecimal.ZERO);
        BigDecimal freight = BigDecimal.ZERO;
        String orderNo = this.generateOrderNo(userId);

        // create pay order
        HealthyOrder healthyOrder = new HealthyOrder();
        healthyOrder.setCreateTime(LocalDateTime.now());
        healthyOrder.setUpdateTime(LocalDateTime.now());
        healthyOrder.setOrderNo(orderNo);
        healthyOrder.setItemId(healthyItem.getId());
        healthyOrder.setItemTitle(healthyItem.getItemTitle());
        healthyOrder.setItemPrice(healthyItem.getItemPrice());
        healthyOrder.setItemCover(healthyItem.getItemCover());
        healthyOrder.setItemCount(buyCount);
        healthyOrder.setItemSpec(healthyItem.getSpec());
        healthyOrder.setPostRule(null);
        healthyOrder.setUserId(user.getId());
        healthyOrder.setChildOrderCount(0);
        healthyOrder.setOrderPrice(orderPrice);
        healthyOrder.setPayPrice(payPrice);
        healthyOrder.setFreight(freight);
        healthyOrder.setOrderStatus(HealthyOrderStatusEnum.PAYING.getCode());
        healthyOrder.setProvince(userAddress.getProvince());
        healthyOrder.setCity(userAddress.getCity());
        healthyOrder.setDistrict(userAddress.getDistrict());
        healthyOrder.setProvinceId(userAddress.getProvinceId());
        healthyOrder.setCityId(userAddress.getCityId());
        healthyOrder.setDistrictId(userAddress.getDistrictId());
        healthyOrder.setAddress(userAddress.getAddress());
        healthyOrder.setMobile(userAddress.getMobile());
        healthyOrder.setContracts(userAddress.getRealname());
        healthyOrder.setBillNo(null);
        healthyOrder.setPayTime(null);
        healthyOrder.setCancelTime(null);
        orderMapper.insert(healthyOrder);

        // wechat pay info
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody("送健康订单支付");
        orderRequest.setOutTradeNo(healthyOrder.getOrderNo());
        orderRequest.setNotifyUrl(wxPayNotifyUrl + PayPrefixEnum.HEALTHY_ORDER.getBizType());
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(payPrice.toPlainString()));
        } else {
            orderRequest.setTotalFee(1);
        }
        orderRequest.setOpenid(user.getOpenId());
        orderRequest.setTradeType("JSAPI");
        orderRequest.setSpbillCreateIp(req.getClientIp());
        orderRequest.setTimeStart(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        orderRequest.setTimeExpire(DateFormatUtils.format(new Date(System.currentTimeMillis() + (1000 * 60 * 15)), "yyyyMMddHHmmss"));
        try {
            return mpPayService.createPayOrder(orderRequest);
        } catch (WxPayException e) {
            logger.error("拉起微信支付失败", e);
            throw new ApiException("拉起微信支付失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void notifyByWechatPay(@NotBlank(message = "订单ID不能为空") String orderNo,
                                  @NotBlank(message = "支付流水号不能为空") String wechatBillNo) throws ApiException {
        HealthyOrder healthyOrder = orderMapper.selectOne(Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getOrderNo, orderNo));
        Asserts.assertNonNull(healthyOrder, 500, "订单不存在");
        HealthyOrder bean = new HealthyOrder();
        bean.setOrderNo(orderNo);
        bean.setBillNo(wechatBillNo);
        bean.setOrderStatus(HealthyOrderStatusEnum.SERVICING.getCode());
        bean.setPayTime(LocalDateTime.now());
        int rows = orderMapper.update(bean, Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getOrderNo, orderNo)
                .eq(HealthyOrder::getOrderStatus, HealthyOrderStatusEnum.PAYING.getCode()));
        if (rows > 0) {
            // create sub order
            String[] postRule = StringUtils.split(healthyOrder.getPostRule(), "-");
            String period = postRule[0], count = postRule[1];
            
            HealthySubOrder subOrder = new HealthySubOrder();
        }
        return null;
    }

    private String generateOrderNo(Integer userId) {
        return userId + DateTime.now().toString("yyyyMMddHHmmssSSS") + RandomUtil.randomInt(10000, 100000);
    }
}
