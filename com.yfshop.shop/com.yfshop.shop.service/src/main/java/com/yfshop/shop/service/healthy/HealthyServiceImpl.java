package com.yfshop.shop.service.healthy;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.code.mapper.HealthyActMapper;
import com.yfshop.code.mapper.HealthyItemContentMapper;
import com.yfshop.code.mapper.HealthyItemImageMapper;
import com.yfshop.code.mapper.HealthyItemMapper;
import com.yfshop.code.mapper.HealthyOrderMapper;
import com.yfshop.code.mapper.HealthySubOrderMapper;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.HealthyAct;
import com.yfshop.code.model.HealthyItem;
import com.yfshop.code.model.HealthyItemContent;
import com.yfshop.code.model.HealthyItemImage;
import com.yfshop.code.model.HealthyOrder;
import com.yfshop.code.model.HealthySubOrder;
import com.yfshop.code.model.User;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.healthy.enums.HealthyOrderStatusEnum;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.address.UserAddressService;
import com.yfshop.shop.service.address.result.UserAddressResult;
import com.yfshop.shop.service.healthy.req.QueryHealthyOrdersReq;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import com.yfshop.shop.service.healthy.result.HealthyActResult;
import com.yfshop.shop.service.healthy.result.HealthyItemResult;
import com.yfshop.shop.service.healthy.result.HealthyOrderResult;
import com.yfshop.shop.service.healthy.result.HealthySubOrderResult;
import com.yfshop.wx.api.service.MpPayService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-26 15:53
 */
@DubboService
@Validated
public class HealthyServiceImpl implements HealthyService {
    private static final Logger logger = LoggerFactory.getLogger(HealthyServiceImpl.class);

    private static final List<String> ALL_ORDER_STATUS = Arrays.asList(HealthyOrderStatusEnum.SERVICING.getCode(),
            HealthyOrderStatusEnum.COMPLETED.getCode(), HealthyOrderStatusEnum.CLOSED.getCode());

    @Resource
    private HealthyActMapper healthyActMapper;
    @Resource
    private HealthyOrderMapper healthyOrderMapper;
    @Resource
    private HealthySubOrderMapper healthySubOrderMapper;
    @Resource
    private HealthyItemMapper healthyItemMapper;
    @Resource
    private HealthyItemContentMapper healthyItemContentMapper;
    @Resource
    private HealthyItemImageMapper healthyItemImageMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private UserMapper userMapper;
    @DubboReference(check = false)
    private MpPayService mpPayService;
    @DubboReference(check = false)
    private UserAddressService userAddressService;
    @Value("${wxPay.notifyUrl}")
    private String wxPayNotifyUrl;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WxPayMpOrderResult submitOrder(@NotNull(message = "用户ID不能为空") Integer userId,
                                          @Valid @NotNull SubmitHealthyOrderReq req) throws ApiException {
        Integer itemId = req.getItemId(), addressId = req.getAddressId(), buyCount = req.getBuyCount();
        String postRule = req.getPostRule();

        User user = userMapper.selectById(userId);
        Asserts.assertNonNull(user, 500, "用户不存在");

        HealthyItem healthyItem = healthyItemMapper.selectById(itemId);
        Asserts.assertNonNull(healthyItem, 500, "商品不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(healthyItem.getIsEnable()), 500, "商品已下架");
        Asserts.assertTrue("N".equalsIgnoreCase(healthyItem.getIsDelete()), 500, "商品已删除");
        Asserts.assertTrue(healthyItem.getPostRule().contains(postRule), 500, "未知的配送规格");

        UserAddressResult userAddress = userAddressService.queryUserAddresses(userId)
                .stream().filter(address -> address.getId().equals(addressId)).findFirst()
                .orElseThrow(() -> new ApiException(500, "收货地址不存在"));

        BigDecimal orderPrice = new BigDecimal(buyCount).multiply(healthyItem.getItemMarketPrice());
        BigDecimal payPrice = new BigDecimal(buyCount).multiply(healthyItem.getItemPrice());
        BigDecimal freight = BigDecimal.ZERO;
        String orderNo = generateOrderNo(userId);
        int subOrderCount = healthyItem.getSpec() / Integer.parseInt(StringUtils.split(healthyItem.getPostRule(), "-")[1]);

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
        healthyOrder.setPostRule(postRule);
        healthyOrder.setUserId(user.getId());
        healthyOrder.setOpenId(user.getOpenId());
        healthyOrder.setChildOrderCount(subOrderCount);
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
        healthyOrder.setItemDesc(healthyItem.getItemDesc());
        healthyOrderMapper.insert(healthyOrder);

        // wechat pay info
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody("送健康订单支付");
        orderRequest.setOutTradeNo(healthyOrder.getOrderNo());
        orderRequest.setNotifyUrl(wxPayNotifyUrl + PayPrefixEnum.HEALTHY_ORDER.getBizType());
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(payPrice.toPlainString()));
        } else {
            // 测试环境1毛钱
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

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME, cacheNames = CacheConstants.HEALTHY_CACHE_NAME,
            key = "'" + CacheConstants.HEALTHY_ITEMS_KEY_PREFIX + "' + #root.methodName")
    @Override
    public List<HealthyItemResult> queryHealthyItems() {
        List<HealthyItem> items = healthyItemMapper.selectList(Wrappers.lambdaQuery(HealthyItem.class)
                .eq(HealthyItem::getIsDelete, "N")
                .eq(HealthyItem::getIsEnable, "Y")
                .orderByAsc(HealthyItem::getSort));
        List<HealthyItemResult> list = items.stream().map(item -> BeanUtil.convert(item, HealthyItemResult.class))
                .collect(Collectors.toList());
        list.forEach(item -> item.setPostRules(Arrays.asList(StringUtils.split(item.getPostRule(), ","))));
        return list;
    }

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME, cacheNames = CacheConstants.HEALTHY_CACHE_NAME,
            key = "'" + CacheConstants.HEALTHY_ITEM_DETAIL_KEY_PREFIX + "' + #root.args[0]")
    @Override
    public HealthyItemResult findHealthyItemDetail(Integer itemId) {
        if (itemId == null) {
            return null;
        }
        HealthyItem healthyItem = healthyItemMapper.selectById(itemId);
        if (healthyItem == null) {
            return null;
        }
        if ("Y".equalsIgnoreCase(healthyItem.getIsDelete())) {
            return null;
        }
        HealthyItemContent content = healthyItemContentMapper.selectList(Wrappers.lambdaQuery(HealthyItemContent.class)
                .eq(HealthyItemContent::getItemId, itemId)).stream().findFirst().orElse(null);
        List<HealthyItemImage> images = healthyItemImageMapper.selectList(Wrappers.lambdaQuery(HealthyItemImage.class)
                .eq(HealthyItemImage::getItemId, itemId).orderByAsc(HealthyItemImage::getSort));
        HealthyItemResult healthyItemResult = BeanUtil.convert(healthyItem, HealthyItemResult.class);
        healthyItemResult.setPostRules(Arrays.asList(StringUtils.split(healthyItemResult.getPostRule(), ",")));
        healthyItemResult.setContent(content != null ? content.getContent() : null);
        healthyItemResult.setImages(images.stream().map(HealthyItemImage::getImageUrl).collect(Collectors.toList()));
        return healthyItemResult;
    }

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME, cacheNames = CacheConstants.HEALTHY_CACHE_NAME,
            key = "'" + CacheConstants.HEALTHY_ACTIVITIES_KEY_PREFIX + "' + #root.methodName")
    @Override
    public List<HealthyActResult> queryHealthyActivities() {
        List<HealthyAct> acts = healthyActMapper.selectList(Wrappers.lambdaQuery(HealthyAct.class)
                .eq(HealthyAct::getIsEnable, "Y").orderByAsc(HealthyAct::getSort));
        return acts.stream().map(act -> BeanUtil.convert(act, HealthyActResult.class))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<HealthyOrderResult> pageQueryUserHealthyOrders(@Valid @NotNull QueryHealthyOrdersReq req) {
        LambdaQueryWrapper<HealthyOrder> wrapper;
        if ("ALL".equalsIgnoreCase(req.getOrderStatus())) {
            wrapper = Wrappers.lambdaQuery(HealthyOrder.class)
                    .eq(HealthyOrder::getUserId, req.getUserId())
                    .in(HealthyOrder::getOrderStatus, ALL_ORDER_STATUS);
        } else {
            wrapper = Wrappers.lambdaQuery(HealthyOrder.class)
                    .eq(HealthyOrder::getUserId, req.getUserId())
                    .eq(HealthyOrder::getOrderStatus, req.getOrderStatus());
        }
        wrapper.orderByDesc(HealthyOrder::getCreateTime);
        Page<HealthyOrder> page = healthyOrderMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageIndex()), wrapper);
        return BeanUtil.iPageConvert(page, HealthyOrderResult.class);
    }

    @Override
    public List<HealthySubOrderResult> pageQueryHealthyOrderDetail(Integer userId, Long orderId) {
        if (userId == null || orderId == null || orderId < 1) {
            return new ArrayList<>(0);
        }
        List<HealthySubOrder> subOrders = healthySubOrderMapper.selectList(Wrappers.lambdaQuery(HealthySubOrder.class)
                .eq(HealthySubOrder::getPOrderId, orderId)
                .eq(HealthySubOrder::getUserId, userId)
                .orderByAsc(HealthySubOrder::getExpectShipTime));
        return subOrders.stream()
                .map((subOrder) -> {
                    HealthySubOrderResult subOrderResult = BeanUtil.convert(subOrder, HealthySubOrderResult.class);
                    if (StringUtils.isNotBlank(subOrder.getDeliveryMan())) {
                        subOrderResult.setDeliveryMan(JSON.parseObject(subOrder.getDeliveryMan()));
                    }
                    return subOrderResult;
                })
                .collect(Collectors.toList());
    }

    private String generateOrderNo(Integer userId) {
        return "H" + String.format("%06d", userId) + DateTime.now().toString("yyyyMMddHHmmssSSS") + RandomUtil.randomInt(10000, 100000);
    }
}
