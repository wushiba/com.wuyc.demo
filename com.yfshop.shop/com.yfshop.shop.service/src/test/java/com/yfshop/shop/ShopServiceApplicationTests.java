package com.yfshop.shop;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.HealthyActMapper;
import com.yfshop.code.mapper.HealthyItemMapper;
import com.yfshop.code.mapper.HealthyOrderMapper;
import com.yfshop.code.mapper.HealthySubOrderMapper;
import com.yfshop.code.mapper.UserAddressMapper;
import com.yfshop.code.mapper.UserMapper;
import com.yfshop.code.model.HealthyAct;
import com.yfshop.code.model.HealthyItem;
import com.yfshop.code.model.HealthyOrder;
import com.yfshop.code.model.HealthySubOrder;
import com.yfshop.code.model.User;
import com.yfshop.code.model.UserAddress;
import com.yfshop.shop.service.healthy.HealthyService;
import com.yfshop.shop.service.healthy.req.QueryHealthyOrdersReq;
import com.yfshop.shop.service.healthy.req.SubmitHealthyOrderReq;
import com.yfshop.shop.service.healthy.result.HealthyActResult;
import com.yfshop.shop.service.healthy.result.HealthyItemResult;
import com.yfshop.shop.service.healthy.result.HealthyOrderResult;
import com.yfshop.shop.service.healthy.result.HealthySubOrderResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ShopServiceApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(ShopServiceApplicationTests.class);

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private HealthyService healthyService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAddressMapper addressMapper;
    @Resource
    private HealthyItemMapper healthyItemMapper;
    @Resource
    private HealthyOrderMapper healthyOrderMapper;
    @Resource
    private HealthySubOrderMapper healthySubOrderMapper;
    @Resource
    private HealthyActMapper healthyActMapper;

    // @Test
    public void test() {
        User user = new User();
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setOpenId("111111");
        user.setNickname("test");
        user.setMobile("12323123131");
        user.setHeadImgUrl("safafsf");
        user.setSex(1);
        user.setSubscribe("Y");
        userMapper.insert(user);

        UserAddress address = new UserAddress();
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        address.setUserId(user.getId());
        address.setIsDefault("Y");
        address.setRealname("sdasd");
        address.setMobile("13500000000");
        address.setSex(1);
        address.setProvinceId(1);
        address.setCityId(1);
        address.setDistrictId(1);
        address.setProvince("浙江省");
        address.setCity("杭州市");
        address.setDistrict("滨江区");
        address.setAddress("浙江省杭州市滨江区");
        addressMapper.insert(address);

        HealthyItem item = new HealthyItem();
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setCategoryId(0);
        item.setItemTitle("光敏牛奶");
        item.setItemSubTitle("牛奶小盒");
        item.setItemPrice(new BigDecimal("1"));
        item.setItemMarketPrice(new BigDecimal("2"));
        item.setFreight(new BigDecimal("0"));
        item.setItemStock(0);
        item.setItemCover("dffas");
        item.setIsEnable("Y");
        item.setIsDelete("N");
        item.setSort(1);
        item.setSpec(21);
        item.setPostRule("W-4");
        item.setItemDesc("口味原味");
        healthyItemMapper.insert(item);

        HealthyAct act = new HealthyAct();
        act.setCreateTime(LocalDateTime.now());
        act.setUpdateTime(LocalDateTime.now());
        act.setActName("fsadfsafas");
        act.setItemId(item.getId());
        act.setImageUrl("sfdsfasfas");
        act.setSort(1);
        act.setIsEnable("Y");
        healthyActMapper.insert(act);
        try {
            // submit order
            SubmitHealthyOrderReq req = new SubmitHealthyOrderReq();
            req.setItemId(item.getId());
            req.setAddressId(address.getId());
            req.setBuyCount(1);
            req.setPostRule(StringUtils.split(item.getPostRule(), ",")[0]);
            req.setClientIp("127.0.0.1");
            healthyService.submitOrder(user.getId(), req);

            List<HealthyOrder> healthyOrders = healthyOrderMapper.selectList(Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getUserId, user.getId()));
            HealthyOrder healthyOrder = healthyOrders.get(0);

            // notify order
            //healthyService.notifyByWechatPay(healthyOrder.getOrderNo(), "dsfsdafsdafasdfasd234fas");

            QueryHealthyOrdersReq queryReq = new QueryHealthyOrdersReq();
            queryReq.setUserId(user.getId());
            queryReq.setOrderStatus("ALL");
            IPage<HealthyOrderResult> orders = healthyService.pageQueryUserHealthyOrders(queryReq);
            System.out.println(JSON.toJSONString(orders, true));

            // order details
            List<HealthySubOrderResult> healthySubOrderResults = healthyService.pageQueryHealthyOrderDetail(user.getId(), healthyOrder.getId());
            System.out.println(JSON.toJSONString(healthySubOrderResults, true));

            // query items
            List<HealthyItemResult> healthyItemResults = healthyService.queryHealthyItems();
            System.out.println(JSON.toJSONString(healthyItemResults, true));

            // query act
            List<HealthyActResult> healthyActResults = healthyService.queryHealthyActivities();
            System.out.println(JSON.toJSONString(healthyActResults, true));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (user.getId() != null) {
                userMapper.deleteById(user.getId());
            }
            if (address.getId() != null) {
                addressMapper.deleteById(address.getId());
            }
            if (item.getId() != null) {
                healthyItemMapper.deleteById(item.getId());
            }
            healthyOrderMapper.delete(Wrappers.lambdaQuery(HealthyOrder.class).eq(HealthyOrder::getUserId, user.getId()));
            healthySubOrderMapper.delete(Wrappers.lambdaQuery(HealthySubOrder.class).eq(HealthySubOrder::getUserId, user.getId()));
            healthyActMapper.deleteById(act);
        }
    }

}
