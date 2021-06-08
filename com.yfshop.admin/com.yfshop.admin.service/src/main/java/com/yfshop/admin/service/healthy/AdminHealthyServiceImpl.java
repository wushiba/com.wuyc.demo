package com.yfshop.admin.service.healthy;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yfshop.admin.api.healthy.AdminHealthyService;
import com.yfshop.admin.api.healthy.request.*;
import com.yfshop.admin.api.healthy.result.*;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.healthy.enums.HealthyOrderStatusEnum;
import com.yfshop.common.healthy.enums.HealthySubOrderStatusEnum;
import com.yfshop.common.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class AdminHealthyServiceImpl implements AdminHealthyService {
    @Resource
    private HealthyOrderMapper healthyOrderMapper;
    @Resource
    private HealthySubOrderMapper healthySubOrderMapper;

    @Resource
    private HealthyActMapper healthyActMapper;

    @Resource
    private HealthyItemMapper healthyItemMapper;

    @Resource
    private HealthyItemImageMapper healthyItemImageMapper;

    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private HealthyActContentMapper healthyActContentMapper;

    @Override
    public IPage<HealthyOrderResult> findOrderList(QueryHealthyOrderReq req) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(HealthyOrder.class)
                .eq(StringUtils.isNotBlank(req.getOrderNo()), HealthyOrder::getOrderNo, req.getOrderNo())
                .eq(StringUtils.isNotBlank(req.getContracts()), HealthyOrder::getContracts, req.getContracts())
                .eq(StringUtils.isNotBlank(req.getOrderStatus()), HealthyOrder::getOrderStatus, req.getOrderStatus())
                .notIn(HealthyOrder::getOrderStatus, HealthyOrderStatusEnum.CANCEL.getCode(), HealthyOrderStatusEnum.PAYING.getCode())
                .ge(req.getStartTime() != null, HealthyOrder::getPayTime, req.getStartTime())
                .lt(req.getEndTime() != null, HealthyOrder::getPayTime, req.getEndTime());
        IPage<HealthyOrder> iPage = healthyOrderMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        return BeanUtil.iPageConvert(iPage, HealthyOrderResult.class);
    }

    @Override
    public HealthyOrderDetailResult getOrderDetail(Integer id) {
        HealthyOrder healthyOrder = healthyOrderMapper.selectById(id);
        HealthyOrderDetailResult healthyOrderDetailResult = BeanUtil.convert(healthyOrder, HealthyOrderDetailResult.class);
        List<HealthySubOrder> list = healthySubOrderMapper.selectList(Wrappers.lambdaQuery(HealthySubOrder.class).eq(HealthySubOrder::getPOrderId, id));
        healthyOrderDetailResult.setList(BeanUtil.convertList(list, HealthySubOrderResult.class));
        return healthyOrderDetailResult;
    }

    @Override
    public IPage<HealthySubOrderResult> findSubOrderList(QueryHealthySubOrderReq req) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(HealthySubOrder.class)
                .eq(StringUtils.isNotBlank(req.getPOrderNo()), HealthySubOrder::getPOrderNo, req.getPOrderNo())
                .eq(StringUtils.isNotBlank(req.getOrderNo()), HealthySubOrder::getOrderNo, req.getOrderNo())
                .eq(StringUtils.isNotBlank(req.getContracts()), HealthySubOrder::getContracts, req.getContracts())
                .eq(StringUtils.isNotBlank(req.getMobile()), HealthySubOrder::getMobile, req.getMobile())
                .eq(StringUtils.isNotBlank(req.getAddress()), HealthySubOrder::getAddress, req.getAddress())
                .eq(StringUtils.isNotBlank(req.getOrderStatus()), HealthySubOrder::getOrderStatus, req.getOrderStatus())
                .eq(StringUtils.isNotBlank(req.getPostWay()), HealthySubOrder::getPostWay, req.getPostWay())
                .eq(req.getProvinceId() != null, HealthySubOrder::getProvinceId, req.getProvinceId())
                .eq(req.getCityId() != null, HealthySubOrder::getCityId, req.getCityId())
                .eq(req.getDistrictId() != null, HealthySubOrder::getDistrictId, req.getDistrictId())
                .ge(req.getStartTime() != null, HealthySubOrder::getExpectShipTime, req.getStartTime())
                .and(StringUtils.isNotBlank(req.getPostKey()), wrapper -> {
                    wrapper.like(HealthySubOrder::getMerchantContacts, req.getPostKey())
                            .or()
                            .like(HealthySubOrder::getMerchantName, req.getPostKey())
                            .or()
                            .like(HealthySubOrder::getMerchantContacts, req.getPostKey())
                            .or()
                            .like(HealthySubOrder::getExpressNo, req.getPostKey())
                            .or()
                            .like(HealthySubOrder::getExpressNo, req.getPostKey());
                })
                .lt(req.getEndTime() != null, HealthySubOrder::getExpectShipTime, req.getEndTime());
        IPage<HealthySubOrder> iPage = healthySubOrderMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), queryWrapper);
        return BeanUtil.iPageConvert(iPage, HealthySubOrderResult.class);
    }


    @Override
    public Void addAct(HealthyActReq req) {
        HealthyAct healthyAct = BeanUtil.convert(req, HealthyAct.class);
        healthyActMapper.insert(healthyAct);
        HealthyActContent healthyActContent = new HealthyActContent();
        healthyActContent.setActId(healthyAct.getId());
        healthyActContent.setContent(req.getContent());
        healthyActContentMapper.insert(healthyActContent);
        return null;
    }

    @Override
    public HealthyActResult getActDetail(Integer id) {
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
    public IPage<HealthyActResult> getActList(HealthyActReq req) {
        IPage<HealthyAct> healthyActIPage = healthyActMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), Wrappers.lambdaQuery());
        return BeanUtil.iPageConvert(healthyActIPage, HealthyActResult.class);
    }

    @Override
    public Void updateAct(HealthyActReq req) {
        HealthyAct healthyAct = BeanUtil.convert(req, HealthyAct.class);
        healthyActMapper.updateById(healthyAct);
        HealthyActContent healthyActContent = new HealthyActContent();
        healthyActContent.setContent(req.getContent());
        healthyActContentMapper.update(healthyActContent, Wrappers.lambdaQuery(HealthyActContent.class)
                .eq(HealthyActContent::getActId, req.getId()));
        return null;
    }

    @Override
    public Void addItem(HealthyItemReq req) {
        HealthyItem healthyItem = BeanUtil.convert(req, HealthyItem.class);
        if (!CollectionUtils.isEmpty(req.getItemImages())) {
            healthyItem.setItemCover(req.getItemImages().get(0));
        }
        healthyItemMapper.insert(healthyItem);
        req.getItemImages().forEach(item -> {
            HealthyItemImage itemImage = new HealthyItemImage();
            itemImage.setCreateTime(LocalDateTime.now());
            itemImage.setUpdateTime(LocalDateTime.now());
            itemImage.setItemId(healthyItem.getId());
            itemImage.setImageUrl(item);
            itemImage.setSort(0);
            healthyItemImageMapper.insert(itemImage);
        });
        return null;
    }

    @Override
    public IPage<HealthyItemResult> getItemList(HealthyItemReq req) {
        IPage<HealthyItem> healthyItemIPage = healthyItemMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), Wrappers.lambdaQuery(HealthyItem.class)
                .eq(req.getId() != null, HealthyItem::getId, req.getId())
                .eq(StringUtils.isNotBlank(req.getItemTitle()), HealthyItem::getItemTitle, req.getItemTitle())
                .eq(HealthyItem::getIsDelete, "N"));

        return BeanUtil.iPageConvert(healthyItemIPage, HealthyItemResult.class);
    }

    @Override
    public IPage<JxsMerchantResult> findJxsMerchant(QueryJxsMerchantReq req) {
        LambdaQueryWrapper<Merchant> wrapper = Wrappers.lambdaQuery(Merchant.class)
                .eq(req.getProvinceId() != null, Merchant::getProvinceId, req.getProvinceId())
                .eq(req.getCityId() != null, Merchant::getCityId, req.getCityId())
                .eq(req.getDistrictId() != null, Merchant::getDistrictId, req.getDistrictId())
                .eq(Merchant::getRoleAlias, "jxs")
                .eq(Merchant::getIsEnable, "Y")
                .eq(Merchant::getIsDelete, "N")
                .like(StringUtils.isNotBlank(req.getMobile()), Merchant::getMobile, req.getMobile())
                .like(StringUtils.isNotBlank(req.getMerchantName()), Merchant::getMerchantName, req.getMerchantName())
                .like(StringUtils.isNotBlank(req.getAddress()), Merchant::getAddress, req.getAddress())
                .like(StringUtils.isNotBlank(req.getContracts()), Merchant::getContacts, req.getContracts())
                .like(StringUtils.isNotBlank(req.getMobile()), Merchant::getMobile, req.getMobile());
        IPage<Merchant> iPage = merchantMapper.selectPage(new Page<>(req.getPageIndex(), req.getPageSize()), wrapper);
        return BeanUtil.iPageConvert(iPage, JxsMerchantResult.class);
    }

    @Override
    public Void updateSubOrderPostWay(SubOrderPostWay req) {
        Asserts.assertTrue(!req.getIds().isEmpty(), 500, "请选择你要指派的订单！");
        HealthySubOrder subOrder = new HealthySubOrder();
        subOrder.setPostWay(req.getPostWay());
        subOrder.setExpressCompany(req.getExpressCompany());
        subOrder.setExpressNo(req.getExpressNo());
        if (req.getMerchantId() != null) {
            Merchant merchant = merchantMapper.selectById(req.getMerchantId());
            subOrder.setMerchantId(req.getMerchantId());
            subOrder.setMerchantContacts(merchant.getContacts());
            subOrder.setMerchantMobile(merchant.getMobile());
            subOrder.setMerchantName(merchant.getMerchantName());
            subOrder.setCurrentMerchantId(req.getMerchantId());
            subOrder.setAllocateMerchantPath(req.getMerchantId() + "");
            subOrder.setOrderStatus(HealthySubOrderStatusEnum.IN_CIRCULATION.getCode());
        } else {
            Asserts.assertStringNotBlank(req.getExpressNo(), 500, "请填写快递信息");
            subOrder.setOrderStatus(HealthySubOrderStatusEnum.IN_DELIVERY.getCode());
        }

        healthySubOrderMapper.update(subOrder, Wrappers.lambdaQuery(HealthySubOrder.class).in(HealthySubOrder::getId, req.getIds()));
        return null;
    }

    @Override
    public HealthyItemResult getItemDetail(Integer id) {
        HealthyItem healthyItem = healthyItemMapper.selectById(id);
        HealthyItemResult result = BeanUtil.convert(healthyItem, HealthyItemResult.class);
        List<String> images = healthyItemImageMapper.selectList(Wrappers.lambdaQuery(HealthyItemImage.class).eq(HealthyItemImage::getItemId, id)).stream().map(HealthyItemImage::getImageUrl).collect(Collectors.toList());
        result.setItemImages(images);
        return result;
    }

    @Override
    public Void deleteAct(Integer id) {
        healthyActMapper.deleteById(id);
        return null;
    }

    @Override
    public Void updateItem(HealthyItemReq req) {
        HealthyItem healthyItem = BeanUtil.convert(req, HealthyItem.class);
        if (!CollectionUtils.isEmpty(req.getItemImages())) {
            healthyItem.setItemCover(req.getItemImages().get(0));
        }
        healthyItemMapper.updateById(healthyItem);
        if (!CollectionUtils.isEmpty(req.getItemImages())) {
            healthyItemImageMapper.delete(Wrappers.lambdaQuery(HealthyItemImage.class).eq(HealthyItemImage::getItemId, req.getId()));
            req.getItemImages().forEach(item -> {
                HealthyItemImage itemImage = new HealthyItemImage();
                itemImage.setCreateTime(LocalDateTime.now());
                itemImage.setUpdateTime(LocalDateTime.now());
                itemImage.setItemId(healthyItem.getId());
                itemImage.setImageUrl(item);
                itemImage.setSort(0);
                healthyItemImageMapper.insert(itemImage);
            });
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void notifyByWechatPay(String orderNo, String wechatBillNo) throws ApiException {
        Asserts.assertStringNotBlank(orderNo, 500, "订单ID不能为空");
        Asserts.assertStringNotBlank(wechatBillNo, 500, "支付流水号不能为空");
        HealthyOrder order = healthyOrderMapper.selectOne(Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getOrderNo, orderNo));
        Asserts.assertNonNull(order, 500, "订单不存在");
        HealthyOrder bean = new HealthyOrder();
        bean.setOrderNo(orderNo);
        bean.setBillNo(wechatBillNo);
        bean.setOrderStatus(HealthyOrderStatusEnum.SERVICING.getCode());
        bean.setPayTime(LocalDateTime.now());
        int rows = healthyOrderMapper.update(bean, Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getOrderNo, orderNo)
                .eq(HealthyOrder::getOrderStatus, HealthyOrderStatusEnum.PAYING.getCode()));
        if (rows <= 0) {
            return null;
        }

        String[] postRule = StringUtils.split(order.getPostRule(), "-");
        // 每次配送数量
        int count = Integer.parseInt(postRule[1]);

        // 今日11点时刻
        LocalDateTime today11Clock = LocalDateTime.of(LocalDate.now().getYear(), LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(), 11, 0, 0, 0);

        // 第一次配送时间
        Date firstPostTime;
        if (bean.getPayTime().isAfter(today11Clock)) {
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
        for (int time = 1; time < order.getChildOrderCount(); time++) {
            if ("W".equals(postRule[0])) {
                temp = DateUtils.addWeeks(temp, 1);
                postDateTimes.add(temp);
            } else if ("M".equals(postRule[0])) {
                temp = DateUtils.addMonths(temp, 1);
                postDateTimes.add(temp);
            }
        }

        // create sub order
        int remain = order.getItemSpec() % count;
        for (int i = 0; i < postDateTimes.size(); i++) {
            LocalDateTime expectShipTime = LocalDateTime.ofInstant(postDateTimes.get(i).toInstant(), ZoneId.systemDefault());
            // 最后一次配送加上余量
            int postItemCount = (i == postDateTimes.size() - 1) ? count + remain : count;
            HealthySubOrder subOrder = new HealthySubOrder();
            subOrder.setCreateTime(LocalDateTime.now());
            subOrder.setUpdateTime(LocalDateTime.now());
            subOrder.setUserId(order.getUserId());
            subOrder.setOpenId(order.getOpenId());
            subOrder.setUserName(null);
            subOrder.setPOrderId(order.getId());
            subOrder.setPOrderNo(order.getOrderNo());
            subOrder.setOrderNo(order.getOrderNo() + (i + 1));
            subOrder.setMerchantId(null);
            subOrder.setPostWay(null);
            // 待分配
            subOrder.setOrderStatus(HealthySubOrderStatusEnum.WAIT_ALLOCATE.getCode());
            subOrder.setConfirmTime(null);
            subOrder.setExpectShipTime(expectShipTime);
            subOrder.setShipTime(null);
            subOrder.setExpressCompany(null);
            subOrder.setExpressNo(null);
            subOrder.setProvince(order.getProvince());
            subOrder.setCity(order.getCity());
            subOrder.setDistrict(order.getDistrict());
            subOrder.setProvinceId(order.getProvinceId());
            subOrder.setCityId(order.getCityId());
            subOrder.setDistrictId(order.getDistrictId());
            subOrder.setAddress(order.getAddress());
            subOrder.setMobile(order.getMobile());
            subOrder.setContracts(order.getContracts());
            subOrder.setPostItemCount(postItemCount);
            subOrder.setItemId(order.getItemId());
            subOrder.setItemTitle(order.getItemTitle());
            subOrder.setItemSubTitle(order.getItemSubTitle());
            subOrder.setItemCover(order.getItemCover());
            healthySubOrderMapper.insert(subOrder);
        }
        return null;
    }

}
