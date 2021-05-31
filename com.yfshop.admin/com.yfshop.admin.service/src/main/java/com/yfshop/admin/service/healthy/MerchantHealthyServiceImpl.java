package com.yfshop.admin.service.healthy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.healthy.MerchantHealthyService;
import com.yfshop.admin.api.healthy.request.QueryMerchantHealthySubOrdersReq;
import com.yfshop.admin.api.healthy.result.HealthySubOrderResult;
import com.yfshop.code.mapper.HealthySubOrderMapper;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.model.HealthySubOrder;
import com.yfshop.code.model.Merchant;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.healthy.enums.HealthySubOrderStatusEnum;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.wx.api.service.MpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Xulg
 * Description: TODO 请加入类描述信息
 * Created in 2021-05-31 10:47
 */
@DubboService
@Validated
public class MerchantHealthyServiceImpl implements MerchantHealthyService {

    private static final List<String> FIT_ROLES = new ArrayList<>(Arrays.asList(GroupRoleEnum.FXS.getCode(), GroupRoleEnum.YWY.getCode(), GroupRoleEnum.CXY.getCode()));

    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private HealthySubOrderMapper healthySubOrderMapper;
    @DubboReference(check = false)
    private MpService mpService;

    @Override
    public IPage<HealthySubOrderResult> pageQueryMerchantHealthySubOrders(@Valid @NotNull QueryMerchantHealthySubOrdersReq req) throws ApiException {
        Integer merchantId = req.getMerchantId();
        Merchant merchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(merchant, 500, "商户不存在");
        Asserts.assertTrue(FIT_ROLES.contains(merchant.getRoleAlias()), 500, "只有分销商、业务员、促销员才能操作");
        LambdaQueryWrapper<HealthySubOrder> queryWrapper;
        if ("ALL".equalsIgnoreCase(req.getOrderStatus())) {
            List<String> orderStatuses = Arrays.asList(HealthySubOrderStatusEnum.WAIT_DELIVERY.getCode(),
                    HealthySubOrderStatusEnum.IN_DELIVERY.getCode(), HealthySubOrderStatusEnum.COMPLETE_DELIVERY.getCode());
            queryWrapper = Wrappers.lambdaQuery(HealthySubOrder.class).eq(HealthySubOrder::getCurrentMerchantId, merchantId)
                    .in(HealthySubOrder::getOrderStatus, orderStatuses);
        } else {
            queryWrapper = Wrappers.lambdaQuery(HealthySubOrder.class).eq(HealthySubOrder::getCurrentMerchantId, merchantId)
                    .eq(HealthySubOrder::getOrderStatus, req.getOrderStatus());
        }
        Page<HealthySubOrder> page = healthySubOrderMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        return BeanUtil.iPageConvert(page, HealthySubOrderResult.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void startDelivery(@NotNull(message = "订单ID不能为空") Integer subOrderId,
                              @NotNull(message = "配送商户ID不能为空") Integer merchantId) throws ApiException {
        HealthySubOrder subOrder = healthySubOrderMapper.selectById(subOrderId);
        Asserts.assertNonNull(subOrder, 500, "订单不存在");
        Merchant merchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(merchant, 500, "商户不存在");
        Asserts.assertTrue(subOrder.getCurrentMerchantId().equals(merchantId), 500, "您不能配送别人的订单");
        Asserts.assertTrue(subOrder.getOrderStatus().equals(HealthySubOrderStatusEnum.WAIT_DELIVERY.getCode()), 500, "非待配送订单");

        // modify order status
        HealthySubOrder entity = new HealthySubOrder();
        entity.setOrderStatus(HealthySubOrderStatusEnum.IN_CIRCULATION.getCode());
        entity.setShipTime(LocalDateTime.now());
        int rows = healthySubOrderMapper.update(entity, Wrappers.lambdaQuery(HealthySubOrder.class)
                .eq(HealthySubOrder::getId, subOrder).eq(HealthySubOrder::getOrderStatus, HealthySubOrderStatusEnum.WAIT_DELIVERY.getCode()));
        if (rows > 0) {
            // 通知用户已开始配送
            List<WxMpTemplateData> data = new ArrayList<>();
            data.add(new WxMpTemplateData("first", "您已成功兑换椰岛135ml鹿龟酒1瓶，恭喜恭喜~"));
            data.add(new WxMpTemplateData("keyword1", ""));
            data.add(new WxMpTemplateData("keyword2", "2元"));
            data.add(new WxMpTemplateData("remark", "点击查阅订单"));
            WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder().templateId("kEnXD9LGvWpcWud99dUu_A85vc5w1vT9-rMzqybrQaw")
                    .toUser(subOrder.getOpenId()).data(data).url("tgsdfghjksdfhgsdhaks").build();
            mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void completeDelivery(@NotNull(message = "订单ID不能为空") Integer subOrderId,
                                 @NotNull(message = "配送商户ID不能为空") Integer merchantId) throws ApiException {
        HealthySubOrder subOrder = healthySubOrderMapper.selectById(subOrderId);
        Asserts.assertNonNull(subOrder, 500, "订单不存在");
        Merchant merchant = merchantMapper.selectById(merchantId);
        Asserts.assertNonNull(merchant, 500, "商户不存在");
        Asserts.assertTrue(subOrder.getCurrentMerchantId().equals(merchantId), 500, "您不能确认别人的订单");
        Asserts.assertTrue(subOrder.getOrderStatus().equals(HealthySubOrderStatusEnum.IN_DELIVERY.getCode()), 500, "非配送中的订单");

        // modify order status
        HealthySubOrder entity = new HealthySubOrder();
        entity.setOrderStatus(HealthySubOrderStatusEnum.COMPLETE_DELIVERY.getCode());
        entity.setCompletedTime(LocalDateTime.now());
        int rows = healthySubOrderMapper.update(entity, Wrappers.lambdaQuery(HealthySubOrder.class)
                .eq(HealthySubOrder::getId, subOrder).eq(HealthySubOrder::getOrderStatus, HealthySubOrderStatusEnum.IN_DELIVERY.getCode()));
        if (rows > 0) {
            // 通知用户已完成配送
            List<WxMpTemplateData> data = new ArrayList<>();
            data.add(new WxMpTemplateData("first", "您已成功兑换椰岛135ml鹿龟酒1瓶，恭喜恭喜~"));
            data.add(new WxMpTemplateData("keyword1", ""));
            data.add(new WxMpTemplateData("keyword2", "2元"));
            data.add(new WxMpTemplateData("remark", "点击查阅订单"));
            WxMpTemplateMessage wxMpTemplateMessage = WxMpTemplateMessage.builder().templateId("kEnXD9LGvWpcWud99dUu_A85vc5w1vT9-rMzqybrQaw")
                    .toUser(subOrder.getOpenId()).data(data).url("tgsdfghjksdfhgsdhaks").build();
            mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
        }
        return null;
    }
}
