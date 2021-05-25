package com.yfshop.open.blpshop;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.UserOrderStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.open.api.blpshop.request.*;
import com.yfshop.open.api.blpshop.result.*;
import com.yfshop.open.api.blpshop.service.OrderService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@DubboService
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RlItemHotpotMapper rlItemHotpotMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;
    @Resource
    private ItemSkuMapper itemSkuMapper;
    @Resource
    private ItemMapper itemMapper;

    @Override
    public OrderResult getOrder(OrderReq orderReq) {
        List<RlItemHotpot> list = rlItemHotpotMapper.selectList(Wrappers.emptyWrapper());
        Map<Integer, RlItemHotpot> skuMap = list.stream().collect(Collectors.toMap(RlItemHotpot::getSkuId, Function.identity()));
        List<Integer> skuIds = new ArrayList<>();
        list.stream().forEach(item -> {
            skuIds.add(item.getSkuId());
        });
        OrderResult orderResult = new OrderResult();
        List<OrderResult.Order> orderList = new ArrayList<>();
        int numTotalOrder = 0;
        if (!CollectionUtils.isEmpty(skuIds)) {
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = Wrappers.<OrderDetail>lambdaQuery()
                    .in(OrderDetail::getSkuId, skuIds)
                    .eq(OrderDetail::getOrderStatus, UserOrderStatusEnum.WAIT_DELIVERY.getCode())
                    .eq(StringUtils.isNotBlank(orderReq.getPlatOrderNo()), OrderDetail::getOrderId, orderReq.getPlatOrderNo())
                    .ge(orderReq.getStartTime() != null, "JH_02".equals(orderReq.getTimeType()) ? OrderDetail::getCreateTime : OrderDetail::getUpdateTime, orderReq.getStartTime())
                    .le(orderReq.getEndTime() != null, "JH_02".equals(orderReq.getTimeType()) ? OrderDetail::getCreateTime : OrderDetail::getUpdateTime, orderReq.getEndTime());
            IPage<OrderDetail> iPage = orderDetailMapper.selectPage(new Page(orderReq.getPageIndex(), orderReq.getPageSize()), lambdaQueryWrapper);
            List<OrderDetail> orderDetails = iPage.getRecords();
            Map<Long, List<OrderDetail>> OrderDetailList = orderDetails.stream().collect(Collectors.groupingBy(OrderDetail::getOrderId));
            List<Long> orderIds = new ArrayList<>();
            OrderDetailList.forEach((key, value) -> {
                orderIds.add(key);
            });
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<Order> orders = orderMapper.selectBatchIds(orderIds);
                List<OrderAddress> orderAddresses = orderAddressMapper.selectList(Wrappers.<OrderAddress>lambdaQuery().in(OrderAddress::getOrderId, orderIds));
                Map<Long, Order> orderMap = orders.stream().collect(Collectors.toMap(Order::getId, Function.identity()));
                Map<Long, OrderAddress> orderAddressMap = orderAddresses.stream().collect(Collectors.toMap(OrderAddress::getOrderId, Function.identity()));
                OrderDetailList.forEach((key, value) -> {
                    List<OrderResult.GoodInfo> goodInfos = new ArrayList<>();
                    OrderAddress orderAddress = orderAddressMap.get(key);
                    OrderResult.Order order = new OrderResult.Order();
                    Order o = orderMap.get(key);
                    order.setPlatOrderNo(key + "");
                    order.setTradeStatus("JH_02");
                    order.setTradeTime(cn.hutool.core.date.DateUtil.format(o.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                    order.setPayOrderNo(o.getBillNo());
                    order.setProvince(orderAddress.getProvince());
                    order.setCity(orderAddress.getCity());
                    order.setArea(orderAddress.getDistrict());
                    order.setAddress(orderAddress.getAddress());
                    order.setMobile(orderAddress.getMobile());
                    order.setPostFee(o.getFreight());
                    order.setGoodsFee(o.getOrderPrice());
                    order.setTotalMoney(o.getOrderPrice().add(o.getOrderPrice()));
                    order.setRealPayMoney(o.getPayPrice());
                    order.setFavourableMoney(o.getCouponPrice());
                    order.setPayTime(cn.hutool.core.date.DateUtil.format(o.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
                    order.setReceiverName(orderAddress.getRealname());
                    order.setNick(orderAddress.getRealname());
                    order.setPayType("JH_WXWeb");
                    order.setShouldPayType("担保交易");
                    for (OrderDetail detail : value) {
                        RlItemHotpot rlItemHotpot = skuMap.get(detail.getSkuId());
                        OrderResult.GoodInfo goodInfo = new OrderResult.GoodInfo();
                        goodInfo.setProductId(detail.getSkuId() + "");
                        goodInfo.setSubOrderNo(detail.getSkuId() + "");
                        goodInfo.setTradeGoodsNo(rlItemHotpot.getOutSkuNo());
                        goodInfo.setPlatGoodsId(rlItemHotpot.getOutItemNo());
                        goodInfo.setPlatSkuId(rlItemHotpot.getOutSkuNo());
                        goodInfo.setOutItemId(rlItemHotpot.getOutItemNo());
                        goodInfo.setOutSkuId(rlItemHotpot.getOutSkuNo());
                        goodInfo.setTradeGoodsName(detail.getItemTitle());
                        goodInfo.setTradeGoodsSpec(jsonFormatText(detail.getSpecNameValueJson()));
                        goodInfo.setGoodsCount(detail.getItemCount());
                        goodInfo.setPrice(detail.getItemPrice());
                        goodInfo.setRemark(rlItemHotpot.getRemark());
                        goodInfos.add(goodInfo);
                    }
                    order.setGoodInfos(goodInfos);
                    orderList.add(order);
                });
            }
            numTotalOrder = orderDetailMapper.selectCount(lambdaQueryWrapper);
        }
        orderResult.setCode("10000");
        orderResult.setMessage("SUCCESS");
        orderResult.setOrders(orderList);
        orderResult.setNumTotalOrder(numTotalOrder);
        return orderResult;
    }

    @Override
    public SendResult send(SendReq sendReq) throws ApiException {
        List<Long> subPlatOrderNo = Arrays.stream(sendReq.getSubPlatOrderNo().split("|")).map(a -> Long.parseLong(a)).collect(Collectors.toList());
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderStatus(UserOrderStatusEnum.WAIT_RECEIVE.getCode());
        orderDetail.setOrderId(Long.parseLong(sendReq.getPlatOrderNo()));
        orderDetail.setExpressCompany(sendReq.getLogisticName());
        orderDetail.setExpressNo(sendReq.getLogisticNo());
        orderDetailMapper.update(orderDetail, Wrappers.<OrderDetail>lambdaQuery()
                .eq(OrderDetail::getOrderId, Long.parseLong(sendReq.getPlatOrderNo()))
                .in(OrderDetail::getId, subPlatOrderNo));
        SendResult sendResult = new SendResult();
        sendResult.setCode("10000");
        sendResult.setMessage("Success");
        sendResult.setSubMessage("发货成功");
        return sendResult;
    }

    @Override
    public CheckRefundStatusResult checkRefundStatus(CheckRefundStatusReq checkRefundStatusReq) throws ApiException {
        CheckRefundStatusResult checkRefundStatusResult = new CheckRefundStatusResult();
        checkRefundStatusResult.setCode("10000");
        checkRefundStatusResult.setMessage("SUCCESS");
        checkRefundStatusResult.setSubMessage("没有退款");
        checkRefundStatusResult.setRefundStatus("JH_07");
        checkRefundStatusResult.setRefundStatusDescription("退款成功");

        return checkRefundStatusResult;
    }

    @Override
    public DownloadProductResult downloadProduct(@RequestBody DownloadProductReq downloadProductReq) throws ApiException {
        DownloadProductResult downloadProductResult = new DownloadProductResult();
        List<RlItemHotpot> list = rlItemHotpotMapper.selectList(Wrappers.emptyWrapper());
        Map<Integer, RlItemHotpot> skuMapKey = list.stream().collect(Collectors.toMap(RlItemHotpot::getSkuId, Function.identity()));
        List<Integer> skuIds = new ArrayList<>();
        list.stream().forEach(item -> {
            skuIds.add(item.getSkuId());
        });
        List<DownloadProductResult.Goods> goodsList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(skuIds)) {
            IPage<ItemSku> ipage = itemSkuMapper.selectPage(new Page<>(downloadProductReq.getPageIndex(), downloadProductReq.getPageSize()), Wrappers.<ItemSku>lambdaQuery().in(ItemSku::getId, skuIds));
            downloadProductResult.setCode("10000");
            downloadProductResult.setMessage("SUCCESS");
            Map<Integer, List<ItemSku>> skuMap = ipage.getRecords().stream().collect(Collectors.groupingBy(ItemSku::getItemId));
            List<Integer> items = new ArrayList<>();
            skuMap.forEach((key, value) -> {
                items.add(key);
            });
            List<Item> itemList = itemMapper.selectBatchIds(items);
            Map<Integer, Item> itemMap = itemList.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
            skuMap.forEach((key, value) -> {
                Item item = itemMap.get(key);
                DownloadProductResult.Goods goods = new DownloadProductResult.Goods();
                goods.setPlatProductId(item.getId() + "");
                goods.setName(item.getItemTitle());
                goods.setOuterId(list.get(0).getOutItemNo());
                goods.setPrice(item.getItemPrice());
                goods.setNum(item.getItemStock());
                goods.setPictureUrl(item.getItemCover());
                List<DownloadProductResult.Sku> skus = new ArrayList<>();
                for (ItemSku sku : value) {
                    RlItemHotpot rlItemHotpot = skuMapKey.get(sku.getId());
                    DownloadProductResult.Sku s = new DownloadProductResult.Sku();
                    s.setSkuId(sku.getId() + "");
                    s.setSkuOuterId(rlItemHotpot.getOutSkuNo());
                    s.setSkuPrice(sku.getSkuSalePrice());
                    s.setSkuQuantity(sku.getSkuStock());
                    s.setSkuName(sku.getSkuTitle());
                    s.setSkuProperty(jsonFormatText(sku.getSpecNameValueJson()));
                    s.setSkuPictureUrl(sku.getSkuCover());
                    skus.add(s);
                }
                goods.setSkus(skus);
                goodsList.add(goods);

            });
        }
        downloadProductResult.setGoodsList(goodsList);
        downloadProductResult.setTotalCount(skuIds.size());
        return downloadProductResult;
    }

    @Override
    public SyncStockResult syncStock(SyncStockReq syncStockReq) throws ApiException {
        ItemSku itemSku = new ItemSku();
        itemSku.setItemId(Integer.valueOf(syncStockReq.getSkuId()));
        itemSku.setSkuStock(syncStockReq.getQuantity());
        itemSkuMapper.updateById(itemSku);
        SyncStockResult syncStockResult = new SyncStockResult();
        syncStockResult.setCode("10000");
        syncStockResult.setMessage("SUCCESS");
        syncStockResult.setQuantity(syncStockReq.getQuantity() + "");
        return syncStockResult;
    }

    @Override
    public RefundResult getRefund(RefundReq refundReq) throws ApiException {
        RefundResult refundResult = new RefundResult();
        refundResult.setRefunds(new ArrayList<>());
        refundResult.setMessage("SUCCESS");
        refundResult.setCode("10000");
        refundResult.setIsSuccess(true);
        refundResult.setTotalCount(0);
        return refundResult;
    }

    public String jsonFormatText(String json) {
        if (StringUtils.isNotBlank(json)) {
            json = json.replace("\"", "").replace("{", "").replace("}", "");
        }
        return json;
    }
}
