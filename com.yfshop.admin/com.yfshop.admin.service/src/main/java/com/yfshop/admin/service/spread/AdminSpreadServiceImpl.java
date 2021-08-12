package com.yfshop.admin.service.spread;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderRowReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.OrderRowResp;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderRowQueryRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderRowQueryResponse;
import com.yfshop.admin.api.spread.AdminSpreadService;
import com.yfshop.admin.api.spread.request.SpreadItemReq;
import com.yfshop.admin.api.spread.request.SpreadOrderReq;
import com.yfshop.admin.api.spread.request.SpreadWithdrawReq;
import com.yfshop.admin.api.spread.result.*;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.wx.api.service.MpPayService;
import com.yfshop.wx.api.service.MpService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class AdminSpreadServiceImpl implements AdminSpreadService {
    private static Logger logger = LoggerFactory.getLogger(AdminSpreadServiceImpl.class);
    static String SERVER_URL = "https://api.jd.com/routerjson";
    static String accessToken = null;
    static String appKey = "e4ad74fd3a422b9d63f625336056516d";
    static String appSecret = "b4f7144429c34ac98276a03309a78be9";
    static String siteId = "4100490551";
    static String key = null;

    @Resource
    private SpreadItemMapper spreadItemMapper;
    @Resource
    private SpreadOrderMapper spreadOrderMapper;
    @Resource
    private SpreadBillMapper spreadBillMapper;
    @Resource
    private SpreadWithdrawMapper spreadWithdrawMapper;
    @Resource
    private SpreadWhiteMapper spreadWhiteMapper;
    @Resource
    private MerchantMapper merchantMapper;
    @DubboReference
    private MpPayService mpPayService;


    @Override
    public IPage<SpreadItemResult> getItemList(SpreadItemReq spreadItemReq) throws ApiException {
        IPage<SpreadItem> iPage = spreadItemMapper.selectPage(new Page<>(spreadItemReq.getPageIndex(), spreadItemReq.getPageSize()), Wrappers.lambdaQuery(SpreadItem.class).orderByDesc());
        return BeanUtil.iPageConvert(iPage, SpreadItemResult.class);
    }

    @Override
    public Void createItem(SpreadItemReq spreadItemReq) throws ApiException {
        spreadItemMapper.insert(BeanUtil.convert(spreadItemReq, SpreadItem.class));
        return null;
    }

    @Override
    public Void updateItem(SpreadItemReq spreadItemReq) throws ApiException {
        spreadItemMapper.updateById(BeanUtil.convert(spreadItemReq, SpreadItem.class));
        return null;
    }

    @Override
    public SpreadItemResult getItemDetail(Integer id) throws ApiException {
        return BeanUtil.convert(spreadItemMapper.selectById(id), SpreadItemResult.class);
    }

    @Override
    public IPage<SpreadOrderResult> getOrderList(SpreadOrderReq spreadOrderReq) throws ApiException {
        IPage<SpreadOrder> iPage = spreadOrderMapper.selectPage(new Page<>(spreadOrderReq.getPageIndex(), spreadOrderReq.getPageSize()),
                Wrappers.lambdaQuery(SpreadOrder.class)
                        .like(StringUtils.isNotBlank(spreadOrderReq.getOrderNo()), SpreadOrder::getOrderNo, spreadOrderReq.getOrderNo())
                        .like(StringUtils.isNotBlank(spreadOrderReq.getItemName()), SpreadOrder::getItemName, spreadOrderReq.getItemName())
                        .like(StringUtils.isNotBlank(spreadOrderReq.getMerchantName()), SpreadOrder::getMerchantName, spreadOrderReq.getMerchantName())
                        .like(StringUtils.isNotBlank(spreadOrderReq.getPidName()), SpreadOrder::getPid, spreadOrderReq.getPidName())
                        .like(StringUtils.isNotBlank(spreadOrderReq.getMerchantMobile()), SpreadOrder::getMerchantMobile, spreadOrderReq.getMerchantMobile())
                        .eq(StringUtils.isNotBlank(spreadOrderReq.getMerchantRole()), SpreadOrder::getMerchantRole, spreadOrderReq.getMerchantRole())
                        .eq(StringUtils.isNotBlank(spreadOrderReq.getOrderStatus()), SpreadOrder::getOrderStatus, spreadOrderReq.getOrderStatus())
                        .gt(spreadOrderReq.getStartTime() != null, SpreadOrder::getCreateTime, spreadOrderReq.getStartTime())
                        .lt(spreadOrderReq.getEndTime() != null, SpreadOrder::getCreateTime, spreadOrderReq.getEndTime())
        );
        return BeanUtil.iPageConvert(iPage, SpreadOrderResult.class);
    }

    @Override
    public List<SpreadOrderExport> getOrderExport(SpreadOrderReq spreadOrderReq) throws ApiException {
        List<SpreadOrder> list = spreadOrderMapper.selectList(Wrappers.lambdaQuery(SpreadOrder.class)
                .like(StringUtils.isNotBlank(spreadOrderReq.getOrderNo()), SpreadOrder::getOrderNo, spreadOrderReq.getOrderNo())
                .like(StringUtils.isNotBlank(spreadOrderReq.getItemName()), SpreadOrder::getItemName, spreadOrderReq.getItemName())
                .like(StringUtils.isNotBlank(spreadOrderReq.getMerchantName()), SpreadOrder::getMerchantName, spreadOrderReq.getMerchantName())
                .like(StringUtils.isNotBlank(spreadOrderReq.getPidName()), SpreadOrder::getPid, spreadOrderReq.getPidName())
                .like(StringUtils.isNotBlank(spreadOrderReq.getMerchantMobile()), SpreadOrder::getMerchantMobile, spreadOrderReq.getMerchantMobile())
                .eq(StringUtils.isNotBlank(spreadOrderReq.getMerchantRole()), SpreadOrder::getMerchantRole, spreadOrderReq.getMerchantRole())
                .eq(StringUtils.isNotBlank(spreadOrderReq.getOrderStatus()), SpreadOrder::getOrderStatus, spreadOrderReq.getOrderStatus())
                .gt(spreadOrderReq.getStartTime() != null, SpreadOrder::getCreateTime, spreadOrderReq.getStartTime())
                .lt(spreadOrderReq.getEndTime() != null, SpreadOrder::getCreateTime, spreadOrderReq.getEndTime()));

        return BeanUtil.convertList(list, SpreadOrderExport.class);
    }

    @Override
    public IPage<SpreadWithdrawResult> getWithdrawList(SpreadWithdrawReq spreadWithdrawReq) throws ApiException {
        IPage<SpreadWithdraw> iPage = spreadWithdrawMapper.selectPage(new Page<>(spreadWithdrawReq.getPageIndex(), spreadWithdrawReq.getPageSize()), Wrappers.lambdaQuery(SpreadWithdraw.class)
                .like(StringUtils.isNotBlank(spreadWithdrawReq.getBillNo()), SpreadWithdraw::getBillNo, spreadWithdrawReq.getBillNo())
                .like(StringUtils.isNotBlank(spreadWithdrawReq.getMerchantName()), SpreadWithdraw::getMerchantName, spreadWithdrawReq.getMerchantName())
                .like(StringUtils.isNotBlank(spreadWithdrawReq.getMerchantMobile()), SpreadWithdraw::getMerchantMobile, spreadWithdrawReq.getMerchantMobile())
                .eq(StringUtils.isNotBlank(spreadWithdrawReq.getMerchantRole()), SpreadWithdraw::getMerchantRole, spreadWithdrawReq.getMerchantRole())
                .eq(StringUtils.isNotBlank(spreadWithdrawReq.getStatus()), SpreadWithdraw::getStatus, spreadWithdrawReq.getWithdraw())
                .gt(spreadWithdrawReq.getStartTime() != null, SpreadWithdraw::getCreateTime, spreadWithdrawReq.getStartTime())
                .lt(spreadWithdrawReq.getEndTime() != null, SpreadWithdraw::getCreateTime, spreadWithdrawReq.getEndTime()));
        return BeanUtil.iPageConvert(iPage, SpreadWithdrawResult.class);
    }

    @Override
    public List<SpreadWithdrawExport> getWithdrawExport(SpreadWithdrawReq spreadWithdrawReq) {
        List<SpreadWithdraw> list = spreadWithdrawMapper.selectList(Wrappers.lambdaQuery(SpreadWithdraw.class)
                .like(StringUtils.isNotBlank(spreadWithdrawReq.getBillNo()), SpreadWithdraw::getBillNo, spreadWithdrawReq.getBillNo())
                .like(StringUtils.isNotBlank(spreadWithdrawReq.getMerchantName()), SpreadWithdraw::getMerchantName, spreadWithdrawReq.getMerchantName())
                .like(StringUtils.isNotBlank(spreadWithdrawReq.getMerchantMobile()), SpreadWithdraw::getMerchantMobile, spreadWithdrawReq.getMerchantMobile())
                .eq(StringUtils.isNotBlank(spreadWithdrawReq.getMerchantRole()), SpreadWithdraw::getMerchantRole, spreadWithdrawReq.getMerchantRole())
                .eq(StringUtils.isNotBlank(spreadWithdrawReq.getStatus()), SpreadWithdraw::getStatus, spreadWithdrawReq.getWithdraw())
                .gt(spreadWithdrawReq.getStartTime() != null, SpreadWithdraw::getCreateTime, spreadWithdrawReq.getStartTime())
                .lt(spreadWithdrawReq.getEndTime() != null, SpreadWithdraw::getCreateTime, spreadWithdrawReq.getEndTime()));
        return BeanUtil.convertList(list, SpreadWithdrawExport.class);
    }

    @Override
    public SpreadStatsResult getSpreadStats() throws ApiException {
        SpreadStatsResult spreadStatsResult = new SpreadStatsResult();
        BigDecimal total = spreadBillMapper.selectList(Wrappers.lambdaQuery(SpreadBill.class)
                .eq(SpreadBill::getStatus, "SUCCESS")
                .eq(SpreadBill::getType, 1))
                .stream()
                .map(SpreadBill::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal settlement = spreadBillMapper.selectList(Wrappers.lambdaQuery(SpreadBill.class)
                .eq(SpreadBill::getStatus, "WAIT")
                .eq(SpreadBill::getType, 1))
                .stream()
                .map(SpreadBill::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        spreadStatsResult.setTotal(total);
        spreadStatsResult.setSettlement(settlement);
        BigDecimal withdraw = spreadWithdrawMapper.selectList(Wrappers.lambdaQuery(SpreadWithdraw.class)
                .eq(SpreadWithdraw::getStatus, "SUCCESS"))
                .stream()
                .map(SpreadWithdraw::getWithdraw)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        spreadStatsResult.setWithdraw(withdraw);
        return spreadStatsResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void tryWithdraw(Long id) throws ApiException {
        SpreadWithdraw spreadWithdraw = new SpreadWithdraw();
        spreadWithdraw.setStatus("SUCCESS");
        int count = spreadWithdrawMapper.update(spreadWithdraw, Wrappers.lambdaQuery(SpreadWithdraw.class).eq(SpreadWithdraw::getId, id).eq(SpreadWithdraw::getStatus, "FAIL"));
        SpreadBill spreadBill = new SpreadBill();
        spreadBill.setStatus("SUCCESS");
        spreadBillMapper.update(spreadBill, Wrappers.lambdaQuery(SpreadBill.class).eq(SpreadBill::getPid, id).eq(SpreadBill::getType, 2));
        if (count > 0) {
            spreadWithdraw = spreadWithdrawMapper.selectById(id);
            EntPayRequest entPayRequest = new EntPayRequest();
            entPayRequest.setCheckName("OPTION_CHECK");
            entPayRequest.setReUserName(spreadWithdraw.getReUserName());
            entPayRequest.setAmount(spreadWithdraw.getWithdraw().multiply(new BigDecimal(100)).intValue());
            entPayRequest.setDescription("分销佣金提现");
            entPayRequest.setOpenid(spreadWithdraw.getOpenId());
            entPayRequest.setSpbillCreateIp(spreadWithdraw.getIpStr());
            entPayRequest.setPartnerTradeNo(spreadWithdraw.getBillNo());
            try {
                EntPayResult entPayResult = mpPayService.entPay(entPayRequest);
                spreadWithdraw.setTransactionId(entPayResult.getPaymentNo());
                spreadWithdraw.setSettlementTime(LocalDateTime.now());
                spreadWithdrawMapper.updateById(spreadWithdraw);
                logger.info(entPayResult.toString());
            } catch (Exception e) {
                e.printStackTrace();
                spreadWithdraw.setStatus("FAIL");
                spreadWithdraw.setRemark(e.getMessage());
                spreadWithdrawMapper.updateById(spreadWithdraw);
            }
        }
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doOrderTask() {
        Map<String, SpreadItem> spreadItemMap = new HashMap<>();
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, appKey, appSecret);
        UnionOpenOrderRowQueryRequest request = new UnionOpenOrderRowQueryRequest();
        OrderRowReq orderReq = new OrderRowReq();
        orderReq.setStartTime(LocalDateTime.now().plusMinutes(-10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00")));
        orderReq.setEndTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00")));
        orderReq.setPageIndex(1);
        orderReq.setPageSize(500);
        orderReq.setType(3);
        request.setOrderReq(orderReq);
        request.setVersion("1.0");
        UnionOpenOrderRowQueryResponse response = null;
        try {
            response = client.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        logger.info(JSONUtil.toJsonPrettyStr(response));
        if (response.getQueryResult().getCode() == 200) {
            for (OrderRowResp rowResp : response.getQueryResult().getData()) {
                String jdUrl = String.format("https://item.m.jd.com/product/%d.html", rowResp.getSkuId());
                SpreadItem spreadItem = spreadItemMap.get(jdUrl);
                if (spreadItem == null) {
                    spreadItem = spreadItemMapper.selectOne(Wrappers.lambdaQuery(SpreadItem.class).eq(SpreadItem::getJumpUrl, jdUrl));
                    if (spreadItem != null) {
                        spreadItemMap.put(jdUrl, spreadItem);
                    }
                }
                if (spreadItem == null) continue;
                Merchant merchant = merchantMapper.selectById(Integer.valueOf(rowResp.getPositionId() + ""));
                if (merchant == null) continue;
                Integer jxsId = null;
                if (StringUtils.isNotBlank(merchant.getPidPath())) {
                    String[] ids = merchant.getPidPath().split("\\.");
                    try {
                        if (ids.length > 2) {
                            jxsId = Integer.valueOf(ids[2]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SpreadOrder spreadOrder = new SpreadOrder();
                spreadOrder.setItemName(spreadItem.getItemName());
                spreadOrder.setItemUrl(spreadItem.getItemImageUrl());
                spreadOrder.setOrderNo(String.valueOf(rowResp.getOrderId()));
                spreadOrder.setOrderTime(LocalDateTime.parse(rowResp.getOrderTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                spreadOrder.setValidCode(rowResp.getValidCode());
                spreadOrder.setOrderPrice(new BigDecimal(rowResp.getEstimateCosPrice()));
                spreadOrder.setMerchantId(merchant.getId());
                spreadOrder.setMerchantRole(merchant.getRoleAlias());
                spreadOrder.setMerchantMobile(merchant.getMobile());
                spreadOrder.setMerchantName(merchant.getMerchantName());
                switch (rowResp.getValidCode()) {
                    // 待付款
                    case 15:
                        continue;
                        // 已付款
                    case 16:
                        spreadOrder.setOrderStatus("WAIT");
                        spreadOrder.setSecondCommission(spreadOrder.getOrderPrice().multiply(new BigDecimal(spreadItem.getSecondCommission())).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP));
                        if (jxsId != null && !jxsId.equals(merchant.getId())) {
                            SpreadWhite spreadWhite = spreadWhiteMapper.selectOne(Wrappers.lambdaQuery(SpreadWhite.class).eq(SpreadWhite::getMerchantId, jxsId));
                            if (spreadWhite != null) {
                                Merchant jxsMerchant = merchantMapper.selectById(jxsId);
                                if (jxsMerchant != null) {
                                    spreadOrder.setPid(jxsMerchant.getId());
                                    spreadOrder.setPidName(jxsMerchant.getMerchantName());
                                    spreadOrder.setFirstCommission(spreadOrder.getOrderPrice().multiply(new BigDecimal(spreadItem.getFirstCommission())).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP));
                                }
                            }
                        }
                        break;
                    // 已完成
                    case 17:
                        spreadOrder.setFinishTime(LocalDateTime.parse(rowResp.getFinishTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        spreadOrder.setOrderStatus("SUCCESS");
                        break;
                    //其他失败
                    default:
                        spreadOrder.setOrderStatus("FAIL");
                        break;
                }
                SpreadOrder older = spreadOrderMapper.selectOne(Wrappers.lambdaQuery(SpreadOrder.class).eq(SpreadOrder::getOrderNo, spreadOrder.getOrderNo()));
                if (older == null) {
                    spreadOrderMapper.insert(spreadOrder);
                } else {
                    spreadOrder.setId(older.getId());
                    spreadOrderMapper.updateById(spreadOrder);
                }
                switch (spreadOrder.getOrderStatus()) {
                    case "WAIT":
                        if (spreadOrder.getPid() != null) {
                            SpreadBill jxsBill = new SpreadBill();
                            jxsBill.setMerchantId(spreadOrder.getPid());
                            jxsBill.setPrice(spreadOrder.getFirstCommission());
                            jxsBill.setStatus(spreadOrder.getOrderStatus());
                            jxsBill.setRemark("分销收入");
                            jxsBill.setPid(spreadOrder.getId());
                            jxsBill.setType(1);
                            spreadBillMapper.insert(jxsBill);
                        }
                        SpreadBill merchantBill = new SpreadBill();
                        merchantBill.setMerchantId(spreadOrder.getMerchantId());
                        merchantBill.setPrice(spreadOrder.getSecondCommission());
                        merchantBill.setStatus(spreadOrder.getOrderStatus());
                        merchantBill.setRemark("分销收入");
                        merchantBill.setPid(spreadOrder.getId());
                        merchantBill.setType(1);
                        spreadBillMapper.insert(merchantBill);
                        break;
                    default:
                        SpreadBill spreadBill = new SpreadBill();
                        spreadBill.setStatus(spreadOrder.getOrderStatus());
                        spreadBillMapper.update(spreadBill, Wrappers.lambdaQuery(SpreadBill.class).eq(SpreadBill::getPid, spreadOrder.getId()).eq(SpreadBill::getType, 1));
                        break;
                }
            }
        }
    }


    @Override
    public void doWithdrawTask() throws Exception {
        List<SpreadWithdraw> list = spreadWithdrawMapper.selectList(Wrappers.lambdaQuery(SpreadWithdraw.class)
                .ge(SpreadWithdraw::getCreateTime, LocalDateTime.now().withSecond(0).plusHours(-24))
                .lt(SpreadWithdraw::getCreateTime, LocalDateTime.now().withSecond(0).plusHours(-24).plusMinutes(1))
                .eq(SpreadWithdraw::getStatus, "WAIT"));
        list.forEach(item -> {
            try {
                SpreadBill spreadBill = new SpreadBill();
                spreadBill.setStatus("SUCCESS");
                int count = spreadBillMapper.update(spreadBill, Wrappers.lambdaQuery(SpreadBill.class).eq(SpreadBill::getPid, item.getId()).eq(SpreadBill::getType, 2).eq(SpreadBill::getStatus, "WAIT"));
                item.setStatus("SUCCESS");
                item.setSettlementTime(LocalDateTime.now());
                count = spreadWithdrawMapper.updateById(item) + count;
                if (count > 1) {
                    EntPayRequest entPayRequest = new EntPayRequest();
                    entPayRequest.setCheckName("OPTION_CHECK");
                    entPayRequest.setReUserName(item.getReUserName());
                    entPayRequest.setAmount(item.getWithdraw().multiply(new BigDecimal(100)).intValue());
                    entPayRequest.setDescription("分销佣金提现");
                    entPayRequest.setOpenid(item.getOpenId());
                    entPayRequest.setSpbillCreateIp(item.getIpStr());
                    entPayRequest.setPartnerTradeNo(item.getBillNo());
                    EntPayResult entPayResult = mpPayService.entPay(entPayRequest);
                    logger.info(entPayResult.toString());
                    item.setTransactionId(entPayResult.getPaymentNo());
                    item.setSettlementTime(LocalDateTime.now());
                    spreadWithdrawMapper.updateById(item);
                }
            } catch (Exception e) {
                item.setStatus("FAIL");
                item.setRemark(e.getMessage());
                spreadWithdrawMapper.updateById(item);
            }
        });
    }
}
