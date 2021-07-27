package com.yfshop.admin.service.spread;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.kplunion.OrderService.request.query.OrderReq;
import com.jd.open.api.sdk.domain.kplunion.OrderService.response.query.QueryResult;
import com.jd.open.api.sdk.request.kplunion.UnionOpenOrderQueryRequest;
import com.jd.open.api.sdk.response.kplunion.UnionOpenOrderQueryResponse;
import com.yfshop.admin.api.spread.AdminSpreadService;
import com.yfshop.admin.api.spread.request.SpreadItemReq;
import com.yfshop.admin.api.spread.request.SpreadOrderReq;
import com.yfshop.admin.api.spread.request.SpreadWithdrawReq;
import com.yfshop.admin.api.spread.result.*;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.SpreadBill;
import com.yfshop.code.model.SpreadItem;
import com.yfshop.code.model.SpreadOrder;
import com.yfshop.code.model.SpreadWithdraw;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@DubboService
public class AdminSpreadServiceImpl implements AdminSpreadService {
    static String SERVER_URL = "";
    static String accessToken = "";
    static String appKey = "";
    static String appSecret = "";
    static String siteId = "";
    static String key;
    @Resource
    private SpreadItemMapper spreadItemMapper;
    @Resource
    private SpreadOrderMapper spreadOrderMapper;
    @Resource
    private SpreadBillMapper spreadBillMapper;
    @Resource
    private SpreadWithdrawMapper spreadWithdrawMapper;
    @Resource
    private MerchantMapper merchantMapper;

    /**
     * @param type      订单时间查询类型(1：下单时间，2：完成时间（购买用户确认收货时间），3：更新时间
     * @param time      查询时间，建议使用分钟级查询，格式：yyyyMMddHH、yyyyMMddHHmm或yyyyMMddHHmmss，如201811031212 的查询范围从12:12:00--12:12:59
     * @param pageIndex
     * @param pageSize
     * @throws Exception
     */
    public QueryResult orderQuery(Integer type, String time, Integer pageIndex, Integer pageSize) throws Exception {
        JdClient client = new DefaultJdClient(SERVER_URL, accessToken, appKey, appSecret);
        UnionOpenOrderQueryRequest request = new UnionOpenOrderQueryRequest();
        OrderReq orderReq = new OrderReq();
        orderReq.setTime(time);
        orderReq.setType(type);
        orderReq.setKey(key);
        orderReq.setPageNo(pageIndex);
        orderReq.setPageSize(pageSize);
        request.setOrderReq(orderReq);
        request.setVersion("1.0");
        UnionOpenOrderQueryResponse response = client.execute(request);
        Asserts.assertEquals("200", response.getCode(), 500, "查询订单接口失败");
        return response.getQueryResult();
    }

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
                        .eq(StringUtils.isNotBlank(spreadOrderReq.getMerchantRole()),SpreadOrder::getMerchantRole, spreadOrderReq.getMerchantRole())
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
        return null;
    }
}
