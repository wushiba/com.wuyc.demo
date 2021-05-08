package com.yfshop.admin.service.website;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.order.service.AdminUserOrderService;
import com.yfshop.admin.api.website.WebsiteBillService;
import com.yfshop.admin.api.website.result.WebsiteBillDayResult;
import com.yfshop.admin.api.website.result.WebsiteBillResult;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.common.util.DateUtil;
import com.yfshop.wx.api.service.MpService;
import io.swagger.models.auth.In;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 网店记账服务
 *
 * @author youshenghui
 * Created in 2021-03-23 9:10
 */
@Validated
@DubboService
public class WebsiteBillServiceImpl implements WebsiteBillService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private WebsiteBillMapper websiteBillMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;
    @DubboReference
    private MpService mpService;
    @Resource
    private AdminUserOrderService adminUserOrderService;

    @Value("${merchant.url}")
    private String merchantUrl;

    @Value("${shop.url}")
    private String shopUrl;

    /**
     * 获取网店记账列表
     * @param merchantId
     * @param dateTime
     * @param status
     * @return
     */
    @Override
    public WebsiteBillDayResult getBillListByMerchantId(Integer merchantId, Date startTime,Date endTime, String status) throws ApiException {
        if (endTime!=null){
            endTime = DateUtil.plusDays(endTime, 1);
        }
        Integer count=websiteBillMapper.selectCount(Wrappers.<WebsiteBill>lambdaQuery()
                        .eq(WebsiteBill::getMerchantId, merchantId)
                        .eq(WebsiteBill::getIsConfirm, status));
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .ge(startTime != null, WebsiteBill::getCreateTime, startTime)
                .lt(endTime != null, WebsiteBill::getCreateTime, endTime)
                .eq(WebsiteBill::getIsConfirm, status)
                .orderByDesc(WebsiteBill::getCreateTime));
        WebsiteBillDayResult websiteBillDayResult = new WebsiteBillDayResult();
        List<WebsiteBillResult> websiteBillResults = new ArrayList<>();
        Integer totalQuantity = websiteBills.size();
        websiteBills.forEach(item -> {
            WebsiteBillResult websiteBillResult = new WebsiteBillResult();
            BeanUtil.copyProperties(item, websiteBillResult);
            websiteBillResults.add(websiteBillResult);
        });
        websiteBillDayResult.setWebSiteBillList(websiteBillResults);
        websiteBillDayResult.setTotalAmount(count);
        websiteBillDayResult.setTotalQuantity(totalQuantity);
        return websiteBillDayResult;
    }

    @Override
    public WebsiteBillDayResult getBillByWebsiteCode(String websiteCode, Date startTime,Date endTime) {
        Integer count=websiteBillMapper.selectCount(Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getWebsiteCode, websiteCode));
//                .eq(WebsiteBill::getIsConfirm, 'Y'));
        List<WebsiteBill> websiteBills = websiteBillMapper.selectList(Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getWebsiteCode, websiteCode)
                .ge(startTime != null, WebsiteBill::getCreateTime, startTime)
                .lt(endTime != null, WebsiteBill::getCreateTime, endTime)
//                .eq(WebsiteBill::getIsConfirm, 'Y')
                .orderByDesc(WebsiteBill::getCreateTime));
        WebsiteBillDayResult websiteBillDayResult = new WebsiteBillDayResult();
        List<WebsiteBillResult> websiteBillResults = new ArrayList<>();
        Integer totalQuantity = websiteBills.size();
        websiteBills.forEach(item -> {
            WebsiteBillResult websiteBillResult = new WebsiteBillResult();
            BeanUtil.copyProperties(item, websiteBillResult);
            websiteBillResults.add(websiteBillResult);
        });
        websiteBillDayResult.setWebSiteBillList(websiteBillResults);
        websiteBillDayResult.setTotalAmount(count);
        websiteBillDayResult.setTotalQuantity(totalQuantity);
        return websiteBillDayResult;
    }

    /**
     * 确认记账
     *
     * @param merchantId
     * @param billIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void billConfirm(Integer merchantId, List<Long> billIds) throws ApiException {
        LambdaQueryWrapper<WebsiteBill> lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .in(WebsiteBill::getId, billIds)
                .eq(WebsiteBill::getIsConfirm, "N");
        WebsiteBill updateWebsiteBill = new WebsiteBill();
        updateWebsiteBill.setIsConfirm("Y");
        websiteBillMapper.update(updateWebsiteBill, lambdaQueryWrapper);

        List<WebsiteBill> billList = websiteBillMapper.selectList(lambdaQueryWrapper);
        for (WebsiteBill websiteBill : billList) {
            adminUserOrderService.confirmOrder(websiteBill.getUserId(), websiteBill.getOrderId());
        }
        return null;
    }

    /**
     * 一键确认记账
     *
     * @param merchantId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void billAllConfirm(Integer merchantId) throws ApiException {
        LambdaQueryWrapper<WebsiteBill> lambdaQueryWrapper = Wrappers.<WebsiteBill>lambdaQuery()
                .eq(WebsiteBill::getMerchantId, merchantId)
                .eq(WebsiteBill::getIsConfirm, "N");
        WebsiteBill updateWebsiteBill = new WebsiteBill();
        updateWebsiteBill.setIsConfirm("Y");
        websiteBillMapper.update(updateWebsiteBill, lambdaQueryWrapper);

        List<WebsiteBill> billList = websiteBillMapper.selectList(lambdaQueryWrapper);
        for (WebsiteBill websiteBill : billList) {
            adminUserOrderService.confirmOrder(websiteBill.getUserId(), websiteBill.getOrderId());
        }
        return null;
    }

    /**
     * 用户自提二等奖成功后，生成网点记账单
     *
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void insertWebsiteBill(Long orderId) throws ApiException {
        Asserts.assertNonNull(orderId, 500, "主订单id不可以为空");
        Order order = orderMapper.selectById(orderId);
        if (order == null || ReceiveWayEnum.PS.getCode().equalsIgnoreCase(order.getReceiveWay())) {
            return null;
        }

        List<OrderDetail> detailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getOrderId, orderId));
        if (CollectionUtil.isEmpty(detailList)) return null;

        User user = userMapper.selectById(detailList.get(0).getUserId());
        if (CollectionUtil.isNotEmpty(detailList)) {
            Merchant merchant = merchantMapper.selectById(detailList.get(0).getMerchantId());
            detailList.forEach(detail -> {
                WebsiteBill websiteBill = new WebsiteBill();
                websiteBill.setCreateTime(LocalDateTime.now());
                websiteBill.setUpdateTime(LocalDateTime.now());
                websiteBill.setMerchantId(detail.getMerchantId());
                websiteBill.setPidPath(detail.getPidPath());
                websiteBill.setUserId(detail.getUserId());
                websiteBill.setNickname(user == null ? "" : user.getNickname());
                websiteBill.setOrderId(orderId);
                websiteBill.setItemTitle(detail.getItemTitle());
                websiteBill.setPayPrice(detail.getPayPrice());
                websiteBill.setBillNo(order.getBillNo());
                websiteBill.setIsConfirm("N");
                websiteBill.setWebsiteCode(detail.getWebsiteCode());
                websiteBillMapper.insert(websiteBill);
                if (user != null) {
                    sendPayMsg(user.getOpenId(), order.getBillNo(), detail.getOrderId().toString(), detail.getId().toString());
                    if (merchant != null) {
                        sendConfirmMsg(user.getNickname(), merchant.getOpenId(), order.getBillNo());
                    }
                }

            });
        }
        return null;
    }

    /**
     * 发送网点确认消息
     *
     * @param userName
     * @param openId
     * @param billId
     */
    private void sendConfirmMsg(String userName, String openId, String billId) {
        try {
            List<WxMpTemplateData> data = new ArrayList<>();
            data.add(new WxMpTemplateData("first", String.format("您有来自%s的门店自取订单，请及时处理~", userName)));
            data.add(new WxMpTemplateData("keyword1", billId));
            data.add(new WxMpTemplateData("keyword2", "2元"));
            data.add(new WxMpTemplateData("remark", "请核对好用户信息，避免错拿商品。"));
            WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                    .templateId("kEnXD9LGvWpcWud99dUu_A85vc5w1vT9-rMzqybrQaw")
                    .toUser(openId)
                    .data(data)
                    .url(String.format("%s#/MerchantBooking",merchantUrl))
                    .build();
            mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送用户兑换支付成功消息
     *
     * @param openId
     * @param billId
     * @param orderId
     * @param orderDetailId
     */
    private void sendPayMsg(String openId, String billId, String orderId, String orderDetailId) {
        try {
            List<WxMpTemplateData> data = new ArrayList<>();
            data.add(new WxMpTemplateData("first", "您已成功兑换椰岛135ml鹿龟酒1瓶，恭喜恭喜~"));
            data.add(new WxMpTemplateData("keyword1", billId));
            data.add(new WxMpTemplateData("keyword2", "2元"));
            data.add(new WxMpTemplateData("remark", "点击查阅订单"));
            WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder()
                    .templateId("kEnXD9LGvWpcWud99dUu_A85vc5w1vT9-rMzqybrQaw")
                    .toUser(openId)
                    .data(data)
                    .url(String.format("%s#/MyOrderDetail?orderId=%s&orderDetailId=%s&receiveWay=ZT",shopUrl, orderId, orderDetailId))
                    .build();
            mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
