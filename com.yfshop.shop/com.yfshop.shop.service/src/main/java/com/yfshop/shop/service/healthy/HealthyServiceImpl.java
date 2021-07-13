package com.yfshop.shop.service.healthy;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
import com.yfshop.code.mapper.HealthyActContentMapper;
import com.yfshop.code.mapper.HealthyActMapper;
import com.yfshop.code.mapper.HealthyItemContentMapper;
import com.yfshop.code.mapper.HealthyItemImageMapper;
import com.yfshop.code.mapper.HealthyItemMapper;
import com.yfshop.code.mapper.HealthyOrderMapper;
import com.yfshop.code.mapper.HealthySubOrderMapper;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.HealthyAct;
import com.yfshop.code.model.HealthyActContent;
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
import com.yfshop.common.healthy.enums.HealthySubOrderStatusEnum;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.address.UserAddressService;
import com.yfshop.shop.service.address.result.UserAddressResult;
import com.yfshop.shop.service.healthy.req.PreviewShowShipPlansReq;
import com.yfshop.shop.service.healthy.req.QueryHealthyOrdersReq;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import com.yfshop.shop.service.healthy.result.HealthyActResult;
import com.yfshop.shop.service.healthy.result.HealthyItemResult;
import com.yfshop.shop.service.healthy.result.HealthyOrderResult;
import com.yfshop.shop.service.healthy.result.HealthySubOrderResult;
import com.yfshop.wx.api.request.WxPayOrderNotifyReq;
import com.yfshop.wx.api.service.MpPayNotifyService;
import com.yfshop.wx.api.service.MpPayService;
import com.yfshop.wx.api.service.MpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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
    private HealthyActContentMapper healthyActContentMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private UserMapper userMapper;
    @DubboReference(check = false)
    private MpPayNotifyService mpPayNotifyService;
    @DubboReference(check = false)
    private MpPayService mpPayService;
    @DubboReference(check = false)
    private UserAddressService userAddressService;
    @Value("${wxPay.notifyUrl}")
    private String wxPayNotifyUrl;
    @Value("${shop.url}")
    private String shopUrl;
    @DubboReference(check = false)
    private MpService mpService;
    @Resource
    private RedisService redisService;

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
        healthyOrder.setItemSubTitle(healthyItem.getItemSubTitle());
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
        healthyRemainderGoods(itemId);

        // wechat pay info
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            try {
                return mpPayService.createPayOrder(orderRequest);
            } catch (WxPayException e) {
                logger.error("拉起微信支付失败", e);
                throw new ApiException("拉起微信支付失败");
            }
        } else {
            // 测试环境模拟支付成功回调
            CompletableFuture.runAsync(() -> {
                WxPayOrderNotifyReq notifyReq = new WxPayOrderNotifyReq();
                notifyReq.setOutTradeNo(healthyOrder.getOrderNo());
                notifyReq.setTransactionId("test-" + RandomUtil.randomNumbers(20));
                mpPayNotifyService.payOrderNotify(PayPrefixEnum.HEALTHY_ORDER.getBizType(), notifyReq);
            });
            return new WxPayMpOrderResult();
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
    public HealthyActResult queryHealthyActivityDetail(Integer id) {
        HealthyAct healthyAct = healthyActMapper.selectById(id);
        HealthyActResult healthyActResult = BeanUtil.convert(healthyAct, HealthyActResult.class);
        HealthyActContent healthyActContent = healthyActContentMapper.selectOne(Wrappers.lambdaQuery(HealthyActContent.class)
                .eq(HealthyActContent::getActId, id));
        if (healthyActContent != null) {
            healthyActResult.setContent(healthyActContent.getContent());
        }
        return healthyActResult;
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
        Page<HealthyOrder> page = healthyOrderMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), wrapper);
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

    @Override
    public List<Date> previewShowShipPlans(@Valid @NotNull PreviewShowShipPlansReq req) throws ApiException {
        Integer itemId = req.getItemId();
        String postRule = req.getPostRule();
        HealthyItem healthyItem = healthyItemMapper.selectById(itemId);
        Asserts.assertNonNull(healthyItem, 500, "商品不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(healthyItem.getIsEnable()), 500, "商品已下架");
        Asserts.assertTrue("N".equalsIgnoreCase(healthyItem.getIsDelete()), 500, "商品已删除");
        Asserts.assertTrue(healthyItem.getPostRule().contains(postRule), 500, "未知的配送规格");

        String[] postRuleInfo = StringUtils.split(postRule, "-");

        // 每次配送数量
        int count = Integer.parseInt(postRuleInfo[1]);
        // 配送次数
        int subOrderCount = healthyItem.getSpec() / count;

        // 今日16点时刻
        LocalDateTime today16Clock = LocalDateTime.of(LocalDate.now().getYear(), LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(), 16, 0, 0, 0);

        // 第一次配送时间
        Date firstPostTime;
        if (LocalDateTime.now().isAfter(today16Clock)) {
            // 第3天开始
            firstPostTime = DateUtil.parse(DateTime.of(DateUtils.addDays(new Date(), 2)).toDateStr());
        } else {
            // 第2天开始
            firstPostTime = DateUtil.parse(DateTime.of(DateUtils.addDays(new Date(), 1)).toDateStr());
        }

        // 配送时间列表
        List<Date> postDateTimes = new ArrayList<>();
        postDateTimes.add(firstPostTime);
        Date temp = firstPostTime;
        for (int time = 1; time < subOrderCount; time++) {
            if ("W".equals(postRuleInfo[0])) {
                temp = DateUtils.addWeeks(temp, 1);
                postDateTimes.add(temp);
            } else if ("M".equals(postRuleInfo[0])) {
                temp = DateUtils.addMonths(temp, 1);
                postDateTimes.add(temp);
            }
        }
        // 预计送达时间
        return postDateTimes.stream()
                .map(postDateTime -> DateUtils.addDays(postDateTime, 3))
                .collect(Collectors.toList());
    }

    @Override
    public Void confirmHealthySubOrder(Long id) {
        HealthySubOrder healthySubOrder = new HealthySubOrder();
        healthySubOrder.setId(id);
        healthySubOrder.setOrderStatus(HealthySubOrderStatusEnum.COMPLETE_DELIVERY.getCode());
        int count = healthySubOrderMapper.updateById(healthySubOrder);
        if (count > 0) {
            healthySubOrder = healthySubOrderMapper.selectById(id);
            int allCount = healthySubOrderMapper.selectCount(Wrappers.lambdaQuery(HealthySubOrder.class)
                    .eq(HealthySubOrder::getPOrderId, healthySubOrder.getPOrderId()));
            int completeCount = healthySubOrderMapper.selectCount(Wrappers.lambdaQuery(HealthySubOrder.class)
                    .eq(HealthySubOrder::getPOrderId, healthySubOrder.getPOrderId())
                    .eq(HealthySubOrder::getOrderStatus, HealthySubOrderStatusEnum.COMPLETE_DELIVERY.getCode()));
            if (allCount == completeCount) {
                HealthyOrder healthyOrder = new HealthyOrder();
                healthyOrder.setId(healthySubOrder.getPOrderId());
                healthyOrder.setOrderStatus(HealthyOrderStatusEnum.COMPLETED.getCode());
                healthyOrderMapper.updateById(healthyOrder);
                try {
                    List<WxMpTemplateData> data = new ArrayList<>();
                    data.add(new WxMpTemplateData("first", "您的订单已完成"));
                    data.add(new WxMpTemplateData("keyword1", healthySubOrder.getPOrderNo()));
                    data.add(new WxMpTemplateData("keyword2", healthySubOrder.getItemTitle()));
                    data.add(new WxMpTemplateData("keyword3", DateUtil.formatLocalDateTime(LocalDateTime.now())));
                    data.add(new WxMpTemplateData("remark", "订单已完成，点击【详情】开启新的配送"));
                    WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                            .templateId("E3xoz2b936H0M8YZasqN_mxe3s_OWqKRHzA8OXvEYA8")
                            .toUser(healthySubOrder.getOpenId())
                            .data(data)
                            .url(shopUrl + "#/actPage").build();
                    mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
                } catch (Exception e) {
                    logger.error("发送微信推送通知用户已开始配送失败", e);
                }
            }
        }
        return null;
    }

    @Override
    public Integer remainderGoods(Integer id) {
        String dataStr = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        String key = "HealthyRemainderGoods:" + dataStr + ":" + id;
        Object object = redisService.get(key);
        if (object != null) {
            Integer count = Integer.valueOf(object.toString());
            count = 2000 - count;
            return count > 10 ? count : 10;
        } else {
            return 2000;
        }
    }

    @Override
    public Long buyGoods(Integer itemId) {
        String key = "HealthyBugGoods:" + itemId;
        Object object = redisService.get(key);
        if (object != null) {
           return Long.valueOf(object.toString());

        } else {
            return 0L;
        }
    }

    private String generateOrderNo(Integer userId) {
        return "H" + String.format("%06d", userId) + DateTime.now().toString("yyyyMMddHHmmssSSS") + RandomUtil.randomInt(10000, 100000);
    }

    private void healthyRemainderGoods(Integer id) {
        String dataStr = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        String key = "HealthyRemainderGoods:" + dataStr + ":" + id;
        redisService.incr(key, 1, 1, TimeUnit.DAYS);
        redisService.incr("HealthyBugGoods:" + id,1);
    }


}
