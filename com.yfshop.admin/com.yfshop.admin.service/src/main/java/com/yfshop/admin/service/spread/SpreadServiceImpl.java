package com.yfshop.admin.service.spread;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.QueryResult;
import com.jd.open.api.sdk.domain.kplunion.promotioncommon.PromotionService.request.get.PromotionCodeReq;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderQueryRequest;
import com.jd.open.api.sdk.request.kplunion.UnionOpenPromotionCommonGetRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderQueryResponse;
import com.jd.open.api.sdk.response.kplunion.UnionOpenPromotionCommonGetResponse;
import com.yfshop.admin.api.spread.AdminSpreadService;
import com.yfshop.admin.api.spread.SpreadService;
import com.yfshop.admin.api.spread.request.SpreadBillReq;
import com.yfshop.admin.api.spread.request.SpreadItemReq;
import com.yfshop.admin.api.spread.request.SpreadOrderReq;
import com.yfshop.admin.api.spread.request.SpreadWithdrawReq;
import com.yfshop.admin.api.spread.result.SpreadBillResult;
import com.yfshop.admin.api.spread.result.SpreadItemResult;
import com.yfshop.admin.api.spread.result.SpreadOrderResult;
import com.yfshop.admin.api.spread.result.SpreadStatsResult;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@DubboService
public class SpreadServiceImpl implements SpreadService {
    @Resource
    private SpreadItemMapper spreadItemMapper;
    @Resource
    private SpreadOrderMapper spreadOrderMapper;
    @Resource
    private SpreadBillMapper spreadBillMapper;
    @Resource
    private SpreadWithdrawMapper spreadWithdrawMapper;
    @Resource
    private SpreadUrlMapper spreadUrlMapper;
    @Resource
    private MerchantMapper merchantMapper;


    static String SERVER_URL = "https://router.jd.com/api";
    static String accessToken = null;
    static String appKey = "e4ad74fd3a422b9d63f625336056516d";
    static String appSecret = "b4f7144429c34ac98276a03309a78be9";
    static String siteId = "4100490551";
    static String key="6ca822d2fda5ca49dadd82d0c6efccf3873d88f7f5474995f466c4dd91408a2de50f139fdacbb1e0";

    @Override
    public String createPromotion(Integer merchantId, Integer itemId) throws Exception {
        SpreadItem spreadItem = spreadItemMapper.selectById(itemId);
        Asserts.assertTrue(spreadItem != null, 500, "推广商品不存在！");
        SpreadUrl spreadUrl = spreadUrlMapper.selectOne(Wrappers.lambdaQuery(SpreadUrl.class).eq(SpreadUrl::getMerchantId, merchantId).eq(SpreadUrl::getItemId, itemId));
        if (spreadUrl == null) {
            JdClient client = new DefaultJdClient(SERVER_URL, accessToken, appKey, appSecret);
            UnionOpenPromotionCommonGetRequest request = new UnionOpenPromotionCommonGetRequest();
            PromotionCodeReq promotionCodeReq = new PromotionCodeReq();
            promotionCodeReq.setPositionId(merchantId);
            promotionCodeReq.setMaterialId(spreadItem.getJumpUrl());
            promotionCodeReq.setSiteId(siteId);
            request.setPromotionCodeReq(promotionCodeReq);
            request.setVersion("1.0");
            UnionOpenPromotionCommonGetResponse response = client.execute(request);
            Asserts.assertEquals("200", response.getCode(), 500, "获取推广链接失败");
            String shortUrl = response.getGetResult().getData().getClickURL();
            spreadUrl = new SpreadUrl();
            spreadUrl.setUrl(shortUrl);
            spreadUrl.setMerchantId(merchantId);
            spreadUrl.setItemId(itemId);
            spreadUrlMapper.insert(spreadUrl);
        }
        return spreadUrl.getUrl();
    }

    @Override
    public List<SpreadItemResult> getItemList() throws ApiException {
        List<SpreadItem> list = spreadItemMapper.selectList(Wrappers.lambdaQuery(SpreadItem.class).eq(SpreadItem::getIsEnable, "Y"));
        return BeanUtil.convertList(list, SpreadItemResult.class);
    }

    @Override
    public SpreadItemResult getItemDetail(Integer id) throws ApiException {
        return BeanUtil.convert(spreadItemMapper.selectById(id), SpreadItemResult.class);
    }

    @Override
    public SpreadStatsResult getSpreadStats(Integer merchantId) throws ApiException {
        SpreadStatsResult spreadStatsResult = new SpreadStatsResult();
        BigDecimal total = spreadBillMapper.selectList(Wrappers.lambdaQuery(SpreadBill.class)
                .eq(SpreadBill::getMerchantId, merchantId)
                .eq(SpreadBill::getStatus, "SUCCESS")
                .eq(SpreadBill::getType, 1))
                .stream()
                .map(SpreadBill::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal settlement = spreadBillMapper.selectList(Wrappers.lambdaQuery(SpreadBill.class)
                .eq(SpreadBill::getMerchantId, merchantId)
                .eq(SpreadBill::getStatus, "WAIT")
                .eq(SpreadBill::getType, 1))
                .stream()
                .map(SpreadBill::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        spreadStatsResult.setTotal(total);
        spreadStatsResult.setSettlement(settlement);
        BigDecimal withdraw = spreadWithdrawMapper.selectList(Wrappers.lambdaQuery(SpreadWithdraw.class)
                .eq(SpreadWithdraw::getMerchantId, merchantId)
                .eq(SpreadWithdraw::getStatus, "SUCCESS"))
                .stream()
                .map(SpreadWithdraw::getWithdraw)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        spreadStatsResult.setWithdraw(withdraw);
        return spreadStatsResult;
    }

    @Override
    public IPage<SpreadOrderResult> getOrderList(SpreadOrderReq spreadOrderReq) throws ApiException {
        IPage<SpreadOrder> iPage = spreadOrderMapper.selectPage(new Page<>(spreadOrderReq.getPageIndex(), spreadOrderReq.getPageSize()),
                Wrappers.lambdaQuery(SpreadOrder.class)
                        .eq(SpreadOrder::getMerchantId, spreadOrderReq.getMerchantId())
                        .eq(StringUtils.isNotBlank(spreadOrderReq.getOrderStatus()), SpreadOrder::getOrderStatus, spreadOrderReq.getOrderStatus())
                        .and(StringUtils.isNotBlank(spreadOrderReq.getKey()),
                                wrapper -> {
                                    wrapper.like(SpreadOrder::getItemName, spreadOrderReq.getKey())
                                            .or()
                                            .like(SpreadOrder::getOrderNo, spreadOrderReq.getKey())
                                            .or()
                                            .like(SpreadOrder::getMerchantName, spreadOrderReq.getKey());
                                })
                        .gt(spreadOrderReq.getStartTime() != null, SpreadOrder::getCreateTime, spreadOrderReq.getStartTime())
                        .lt(spreadOrderReq.getEndTime() != null, SpreadOrder::getCreateTime, spreadOrderReq.getEndTime())
        );
        return BeanUtil.iPageConvert(iPage, SpreadOrderResult.class);
    }

    @Override
    public SpreadOrderResult getOrderDetail(Long id) throws ApiException {

        return BeanUtil.convert(spreadOrderMapper.selectById(id), SpreadOrderResult.class);
    }

    @Override
    public BigDecimal getBalance(Integer merchantId) throws ApiException {
        BigDecimal total = spreadBillMapper.selectList(Wrappers.lambdaQuery(SpreadBill.class)
                .eq(SpreadBill::getMerchantId, merchantId)
                .and(wrapper -> {
                    wrapper.and(w -> {
                        w.eq(SpreadBill::getType, 1).eq(SpreadBill::getStatus, "SUCCESS");
                    }).or(w -> {
                        w.eq(SpreadBill::getType, 2);
                    });
                }))
                .stream()
                .map(SpreadBill::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
    }

    @Override
    public IPage<SpreadBillResult> getBillList(SpreadBillReq spreadBillReq) throws ApiException {
        IPage<SpreadBill> iPage = spreadBillMapper.selectPage(new Page<>(spreadBillReq.getPageIndex(), spreadBillReq.getPageSize()), Wrappers.lambdaQuery(SpreadBill.class)
                .eq(SpreadBill::getMerchantId, spreadBillReq.getMerchantId())
                .and(wrapper -> {
                    wrapper.and(w -> {
                        w.eq(SpreadBill::getType, 1).eq(SpreadBill::getStatus, "SUCCESS");
                    }).or(w -> {
                        w.eq(SpreadBill::getType, 2);
                    });
                })
                .eq(spreadBillReq.getType() != null, SpreadBill::getType, spreadBillReq.getType())
                .ge(spreadBillReq.getStartTime() != null, SpreadBill::getUpdateTime, spreadBillReq.getStartTime())
                .lt(spreadBillReq.getEndTime() != null, SpreadBill::getUpdateTime, spreadBillReq.getEndTime())
        );
        return BeanUtil.iPageConvert(iPage, SpreadBillResult.class);
    }

    @Override
    public Void withDraw(SpreadWithdrawReq spreadWithdrawReq) throws ApiException {
        Asserts.assertTrue(spreadWithdrawReq.getWithdraw().compareTo(new BigDecimal("5")) >= 0, 500, "提现金额不得小于5元！");
        Merchant merchant = merchantMapper.selectById(spreadWithdrawReq.getMerchantId());
        BigDecimal total = spreadBillMapper.selectList(Wrappers.lambdaQuery(SpreadBill.class)
                .eq(SpreadBill::getMerchantId, spreadWithdrawReq.getMerchantId())
                .and(wrapper -> {
                    wrapper.and(w -> {
                        w.eq(SpreadBill::getType, 1).eq(SpreadBill::getStatus, "SUCCESS");
                    }).or(w -> {
                        w.eq(SpreadBill::getType, 2);
                    });
                }))
                .stream()
                .map(SpreadBill::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Asserts.assertTrue(total.compareTo(spreadWithdrawReq.getWithdraw()) >= 0, 500, "余额不足！");
        SpreadWithdraw withdraw = new SpreadWithdraw();
        withdraw.setWithdraw(spreadWithdrawReq.getWithdraw());
        withdraw.setMerchantId(spreadWithdrawReq.getMerchantId());
        withdraw.setMerchantMobile(merchant.getMobile());
        withdraw.setMerchantName(merchant.getContacts());
        withdraw.setStatus("WAIT");
        withdraw.setMerchantRole(merchant.getRoleAlias());
        withdraw.setBillNo(String.format("%06d", spreadWithdrawReq.getMerchantId()) + DateUtil.format(new Date(), "yyMMddHHmmSS"));
        spreadWithdrawMapper.insert(withdraw);
        SpreadBill spreadBill = new SpreadBill();
        spreadBill.setMerchantId(spreadWithdrawReq.getMerchantId());
        spreadBill.setPrice(spreadWithdrawReq.getWithdraw().negate());
        spreadBill.setPid(withdraw.getId());
        spreadBill.setType(2);
        spreadBill.setStatus("WAIT");
        spreadBill.setRemark("余额提现");
        spreadBillMapper.insert(spreadBill);
        return null;
    }
}
