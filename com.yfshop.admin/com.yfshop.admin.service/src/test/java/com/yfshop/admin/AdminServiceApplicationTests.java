package com.yfshop.admin;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.mall.request.CreateBannerReq;
import com.yfshop.admin.api.mall.request.GenerateItemSkuReq;
import com.yfshop.admin.api.mall.request.ItemSpecNameAndValue;
import com.yfshop.admin.api.mall.request.RecreateItemSkuReq;
import com.yfshop.admin.api.mall.request.SaveItemSkuReq;
import com.yfshop.admin.api.mall.request.SaveItemSkuReq.ItemCandidateSku;
import com.yfshop.admin.api.mall.result.BannerResult;
import com.yfshop.admin.api.mall.result.ItemSkuResult;
import com.yfshop.admin.api.menu.AdminMenuManageService;
import com.yfshop.admin.api.menu.result.MenuResult;
import com.yfshop.code.manager.MenuManager;
import com.yfshop.code.mapper.ItemContentMapper;
import com.yfshop.code.mapper.ItemImageMapper;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.model.Item;
import com.yfshop.code.model.ItemContent;
import com.yfshop.code.model.ItemImage;
import com.yfshop.code.model.Menu;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.enums.ReceiveWayEnum;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class AdminServiceApplicationTests {

    @DubboReference(check = false)
    private AdminMallManageService adminMallManageService;
    @Resource
    private AdminMallManageService adminMallManageService2;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ItemImageMapper itemImageMapper;
    @Resource
    private ItemContentMapper itemContentMapper;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private MenuManager menuManager;
    @Resource
    private AdminMenuManageService adminMenuManageService;

    //    @Test
    void contextLoads2222222() {
        if (true) {
            return;
        }
        System.out.println();
        try {
            applicationContext.getBean("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Test
    void contextLoads() {
        if (true) {
            return;
        }
        try {
            CreateBannerReq req = new CreateBannerReq();
            req.setBannerName("test");
            req.setPositions("home");
            req.setImageUrl("https://www.baidu.com");
            req.setJumpUrl("https://www.baidu.com");
            req.setIsEnable("Y");
            req.setSort(1);
            adminMallManageService.createBanner(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            IPage<BannerResult> page = adminMallManageService.pageQueryBanner(1, 10, "home");
            System.out.println(JSON.toJSONString(page, true));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
            ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
            itemSpecNameAndValue1.setSpecName("颜色");
            itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("红", "白", "黑"));
            itemSpecNameAndValue1.setSort(1);
            ItemSpecNameAndValue itemSpecNameAndValue2 = new ItemSpecNameAndValue();
            itemSpecNameAndValue2.setSpecName("尺寸");
            itemSpecNameAndValue2.setSpecValues(Lists.newArrayList("大", "中", "小"));
            itemSpecNameAndValue2.setSort(1);
            itemSpecNameAndValues.add(itemSpecNameAndValue1);
            itemSpecNameAndValues.add(itemSpecNameAndValue2);

            Item item = new Item();
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setCategoryId(1);
            item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
            item.setItemTitle("title");
            item.setItemSubTitle("subTitle");
            item.setItemPrice(BigDecimal.ONE);
            item.setItemMarketPrice(BigDecimal.ONE);
            item.setFreight(BigDecimal.ZERO);
            item.setItemStock(1);
            item.setItemCover("111");
            item.setIsEnable("Y");
            item.setIsDelete("N");
            item.setSpecNum(2);
            item.setSort(1);
            itemMapper.insert(item);

            GenerateItemSkuReq req = new GenerateItemSkuReq();
            req.setItemId(item.getId());
            req.setSpecNameAndValues(itemSpecNameAndValues);
            List<ItemSkuResult> itemSkuResults = adminMallManageService.previewItemSku(req);
            System.out.println(JSON.toJSONString(itemSkuResults, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Item item = new Item();
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setCategoryId(2);
            item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
            item.setItemTitle("title2");
            item.setItemSubTitle("subTitle2");
            item.setItemPrice(BigDecimal.ONE);
            item.setItemMarketPrice(BigDecimal.ONE);
            item.setFreight(BigDecimal.ZERO);
            item.setItemStock(1);
            item.setItemCover("222");
            item.setIsEnable("Y");
            item.setIsDelete("N");
            item.setSpecNum(2);
            item.setSort(1);
            itemMapper.insert(item);

            List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
            ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
            itemSpecNameAndValue1.setSpecName("颜色");
            itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("红", "白", "黑"));
            itemSpecNameAndValue1.setSort(1);
            ItemSpecNameAndValue itemSpecNameAndValue2 = new ItemSpecNameAndValue();
            itemSpecNameAndValue2.setSpecName("尺寸");
            itemSpecNameAndValue2.setSpecValues(Lists.newArrayList("大", "中", "小"));
            itemSpecNameAndValue2.setSort(1);
            itemSpecNameAndValues.add(itemSpecNameAndValue1);
            itemSpecNameAndValues.add(itemSpecNameAndValue2);

            RecreateItemSkuReq req = new RecreateItemSkuReq();
            req.setItemId(item.getId());
            req.setSpecNameAndValues(itemSpecNameAndValues);
            adminMallManageService.recreateItemSku(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Item item = new Item();
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setCategoryId(2);
            item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
            item.setItemTitle("title2");
            item.setItemSubTitle("subTitle2");
            item.setItemPrice(BigDecimal.ONE);
            item.setItemMarketPrice(BigDecimal.ONE);
            item.setFreight(BigDecimal.ZERO);
            item.setItemStock(1);
            item.setItemCover("222");
            item.setIsEnable("Y");
            item.setIsDelete("N");
            item.setSpecNum(2);
            item.setSort(1);
            itemMapper.insert(item);

            List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
            ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
            itemSpecNameAndValue1.setSpecName("颜色");
            itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("红", "黑"));
            itemSpecNameAndValue1.setSort(1);
            ItemSpecNameAndValue itemSpecNameAndValue2 = new ItemSpecNameAndValue();
            itemSpecNameAndValue2.setSpecName("尺寸");
            itemSpecNameAndValue2.setSpecValues(Lists.newArrayList("大", "中", "小"));
            itemSpecNameAndValue2.setSort(1);
            itemSpecNameAndValues.add(itemSpecNameAndValue1);
            itemSpecNameAndValues.add(itemSpecNameAndValue2);
            ItemSpecNameAndValue itemSpecNameAndValue3 = new ItemSpecNameAndValue();
            itemSpecNameAndValue3.setSpecName("适用人群");
            itemSpecNameAndValue3.setSpecValues(Lists.newArrayList("男人", "女人", "老人"));
            itemSpecNameAndValue3.setSort(1);
            itemSpecNameAndValues.add(itemSpecNameAndValue3);

            GenerateItemSkuReq generateItemSkuReq = new GenerateItemSkuReq();
            generateItemSkuReq.setItemId(item.getId());
            generateItemSkuReq.setSpecNameAndValues(itemSpecNameAndValues);

            List<ItemCandidateSku> itemCandidateSkuList = new ArrayList<>();

            ItemCandidateSku itemCandidateSku = new ItemCandidateSku();
            itemCandidateSku.setItemId(item.getId());
            itemCandidateSku.setSpecNameValueJson("{\"颜色\":\"红\",\"尺寸\":\"小\"}");
            itemCandidateSku.setIsEnable("Y");
            itemCandidateSku.setStock(2);
            itemCandidateSku.setPrice(BigDecimal.ONE);
            itemCandidateSku.setMarketPrice(BigDecimal.TEN);
            itemCandidateSkuList.add(itemCandidateSku);

            SaveItemSkuReq req = new SaveItemSkuReq();
            req.setSpecInfo(generateItemSkuReq);
            req.setCandidateSkus(itemCandidateSkuList);

            adminMallManageService.saveItemSku(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Test
    public void aVoid() {
        if (true) {
            try {
                List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
                ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
                itemSpecNameAndValue1.setSpecName("颜色");
                itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("红", "白", "黑"));
                itemSpecNameAndValue1.setSort(2);
                ItemSpecNameAndValue itemSpecNameAndValue2 = new ItemSpecNameAndValue();
                itemSpecNameAndValue2.setSpecName("尺寸");
                itemSpecNameAndValue2.setSpecValues(Lists.newArrayList("大", "中", "小"));
                itemSpecNameAndValue2.setSort(3);
                ItemSpecNameAndValue itemSpecNameAndValue3 = new ItemSpecNameAndValue();
                itemSpecNameAndValue3.setSpecName("适用人群");
                itemSpecNameAndValue3.setSpecValues(Lists.newArrayList("男人", "女人", "老人"));
                itemSpecNameAndValue3.setSort(1);
                itemSpecNameAndValues.add(itemSpecNameAndValue1);
                itemSpecNameAndValues.add(itemSpecNameAndValue2);
                itemSpecNameAndValues.add(itemSpecNameAndValue3);

                Item item = new Item();
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(LocalDateTime.now());
                item.setCategoryId(1);
                item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
                item.setItemTitle("title");
                item.setItemSubTitle("subTitle");
                item.setItemPrice(BigDecimal.ONE);
                item.setItemMarketPrice(BigDecimal.ONE);
                item.setFreight(BigDecimal.ZERO);
                item.setItemStock(1);
                item.setItemCover("111");
                item.setIsEnable("Y");
                item.setIsDelete("N");
                item.setSpecNum(2);
                item.setSort(1);
                itemMapper.insert(item);

                GenerateItemSkuReq req = new GenerateItemSkuReq();
                req.setItemId(item.getId());
                req.setSpecNameAndValues(itemSpecNameAndValues);
                List<ItemSkuResult> itemSkuResults = adminMallManageService.previewItemSku(req);
                System.out.println(JSON.toJSONString(itemSkuResults, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Item item = new Item();
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setCategoryId(2);
            item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
            item.setItemTitle("title2");
            item.setItemSubTitle("subTitle2");
            item.setItemPrice(BigDecimal.ONE);
            item.setItemMarketPrice(BigDecimal.ONE);
            item.setFreight(BigDecimal.ZERO);
            item.setItemStock(1);
            item.setItemCover("222");
            item.setIsEnable("Y");
            item.setIsDelete("N");
            item.setSpecNum(2);
            item.setSort(1);
            itemMapper.insert(item);

            List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
            ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
            itemSpecNameAndValue1.setSpecName("颜色");
            itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("红", "黑"));
            itemSpecNameAndValue1.setSort(2);
            ItemSpecNameAndValue itemSpecNameAndValue2 = new ItemSpecNameAndValue();
            itemSpecNameAndValue2.setSpecName("尺寸");
            itemSpecNameAndValue2.setSpecValues(Lists.newArrayList("大", "中", "小"));
            itemSpecNameAndValue2.setSort(3);
            itemSpecNameAndValues.add(itemSpecNameAndValue1);
            itemSpecNameAndValues.add(itemSpecNameAndValue2);
            ItemSpecNameAndValue itemSpecNameAndValue3 = new ItemSpecNameAndValue();
            itemSpecNameAndValue3.setSpecName("适用人群");
            itemSpecNameAndValue3.setSpecValues(Lists.newArrayList("男人", "女人", "老人"));
            itemSpecNameAndValue3.setSort(1);
            itemSpecNameAndValues.add(itemSpecNameAndValue3);

            GenerateItemSkuReq generateItemSkuReq = new GenerateItemSkuReq();
            generateItemSkuReq.setItemId(item.getId());
            generateItemSkuReq.setSpecNameAndValues(itemSpecNameAndValues);

            List<ItemCandidateSku> itemCandidateSkuList = new ArrayList<>();

            ItemCandidateSku itemCandidateSku = new ItemCandidateSku();
            itemCandidateSku.setItemId(item.getId());
            itemCandidateSku.setSpecNameValueJson("{\"适用人群\":\"老人\",\"颜色\":\"黑\",\"尺寸\":\"小\"}");
            itemCandidateSku.setIsEnable("Y");
            itemCandidateSku.setStock(2);
            itemCandidateSku.setPrice(BigDecimal.ONE);
            itemCandidateSku.setMarketPrice(BigDecimal.TEN);
            itemCandidateSkuList.add(itemCandidateSku);

            SaveItemSkuReq req = new SaveItemSkuReq();
            req.setSpecInfo(generateItemSkuReq);
            req.setCandidateSkus(itemCandidateSkuList);

            adminMallManageService.saveItemSku(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    @Test
    public void createMenus() {
        createSystemMenus();
        createZongBuMenus();
        createFGSmenus();
        createSQmenus();
        createJXSmenus();
        createYWY_FXS_CXY_menus();
        createWLMenus();
        queryMenus();
    }

    public void createSystemMenus() {
        /*
        超管：能访问所有菜单
        */

        {
            // 超管

            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.SYS.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.SYS.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.SYS.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.SYS.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            码源工厂管理.setSort(2);

            Menu 网点码管理 = new Menu();
            网点码管理.setMenuName("网点码管理");
            网点码管理.setMenuAlias("wdmgl_" + GroupRoleEnum.SYS.getCode());
            网点码管理.setMenuIcon(null);
            网点码管理.setLinkUrl(null);
            网点码管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            网点码管理.setSort(2);
            网点码管理.setParentMenuAlias(null);
            Menu 活动码管理 = new Menu();
            活动码管理.setParentMenuAlias(网点码管理.getMenuAlias());
            活动码管理.setMenuName("活动码管理");
            活动码管理.setMenuAlias("hdmgl_" + GroupRoleEnum.SYS.getCode());
            活动码管理.setMenuIcon(null);
            活动码管理.setLinkUrl("/");
            活动码管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            活动码管理.setSort(1);

            Menu 活动管理 = new Menu();
            活动管理.setParentMenuAlias(null);
            活动管理.setMenuName("活动管理");
            活动管理.setMenuAlias("hdgl_" + GroupRoleEnum.SYS.getCode());
            活动管理.setMenuIcon(null);
            活动管理.setLinkUrl(null);
            活动管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            活动管理.setSort(3);
            Menu 活动数据 = new Menu();
            活动数据.setParentMenuAlias(活动管理.getMenuAlias());
            活动数据.setMenuName("活动数据");
            活动数据.setMenuAlias("hdsj_" + GroupRoleEnum.SYS.getCode());
            活动数据.setMenuIcon(null);
            活动数据.setLinkUrl("/");
            活动数据.setRoleAlias(GroupRoleEnum.SYS.getCode());
            活动数据.setSort(1);
            Menu 活动报表统计 = new Menu();
            活动报表统计.setParentMenuAlias(活动管理.getMenuAlias());
            活动报表统计.setMenuName("活动报表统计");
            活动报表统计.setMenuAlias("hdbbtj_" + GroupRoleEnum.SYS.getCode());
            活动报表统计.setMenuIcon(null);
            活动报表统计.setLinkUrl("/");
            活动报表统计.setRoleAlias(GroupRoleEnum.SYS.getCode());
            活动报表统计.setSort(2);

            Menu 分类管理 = new Menu();
            分类管理.setParentMenuAlias(null);
            分类管理.setMenuName("分类管理");
            分类管理.setMenuAlias("flgl_" + GroupRoleEnum.SYS.getCode());
            分类管理.setMenuIcon(null);
            分类管理.setLinkUrl(null);
            分类管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            分类管理.setSort(4);

            Menu 商品管理 = new Menu();
            商品管理.setParentMenuAlias(null);
            商品管理.setMenuName("商品管理");
            商品管理.setMenuAlias("spgl_" + GroupRoleEnum.SYS.getCode());
            商品管理.setMenuIcon(null);
            商品管理.setLinkUrl(null);
            商品管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            商品管理.setSort(5);

            Menu 订单管理 = new Menu();
            订单管理.setParentMenuAlias(null);
            订单管理.setMenuName("订单管理");
            订单管理.setMenuAlias("ddgl_" + GroupRoleEnum.SYS.getCode());
            订单管理.setMenuIcon(null);
            订单管理.setLinkUrl(null);
            订单管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            订单管理.setSort(6);

            Menu banner配置 = new Menu();
            banner配置.setParentMenuAlias(null);
            banner配置.setMenuName("banner配置");
            banner配置.setMenuAlias("bannerpz_" + GroupRoleEnum.SYS.getCode());
            banner配置.setMenuIcon(null);
            banner配置.setLinkUrl(null);
            banner配置.setRoleAlias(GroupRoleEnum.SYS.getCode());
            banner配置.setSort(7);

            Menu 首页配置 = new Menu();
            首页配置.setParentMenuAlias(null);
            首页配置.setMenuName("首页配置");
            首页配置.setMenuAlias("sypz_" + GroupRoleEnum.SYS.getCode());
            首页配置.setMenuIcon(null);
            首页配置.setLinkUrl(null);
            首页配置.setRoleAlias(GroupRoleEnum.SYS.getCode());
            首页配置.setSort(8);

            Menu 优惠券管理 = new Menu();
            优惠券管理.setParentMenuAlias(null);
            优惠券管理.setMenuName("优惠券管理");
            优惠券管理.setMenuAlias("yhqgl_" + GroupRoleEnum.SYS.getCode());
            优惠券管理.setMenuIcon(null);
            优惠券管理.setLinkUrl(null);
            优惠券管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            优惠券管理.setSort(9);

            Menu 公众号消息模板配置 = new Menu();
            公众号消息模板配置.setParentMenuAlias(null);
            公众号消息模板配置.setMenuName("公众号消息模板配置");
            公众号消息模板配置.setMenuAlias("gzhxxmbpz_" + GroupRoleEnum.SYS.getCode());
            公众号消息模板配置.setMenuIcon(null);
            公众号消息模板配置.setLinkUrl(null);
            公众号消息模板配置.setRoleAlias(GroupRoleEnum.SYS.getCode());
            公众号消息模板配置.setSort(10);

            Menu 物料管理 = new Menu();
            物料管理.setParentMenuAlias(null);
            物料管理.setMenuName("物料管理");
            物料管理.setMenuAlias("wlgl_" + GroupRoleEnum.SYS.getCode());
            物料管理.setMenuIcon(null);
            物料管理.setLinkUrl(null);
            物料管理.setRoleAlias(GroupRoleEnum.SYS.getCode());
            物料管理.setSort(11);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            list.add(网点码管理);
            list.add(活动码管理);

            list.add(活动管理);
            list.add(活动数据);
            list.add(活动报表统计);

            list.add(分类管理);

            list.add(商品管理);

            list.add(订单管理);

            list.add(banner配置);

            list.add(首页配置);

            list.add(优惠券管理);

            list.add(公众号消息模板配置);

            list.add(物料管理);

            menuManager.saveBatch(list);
        }


    }

    public void createZongBuMenus() {
        /*
        一级总部 ：能访问所有菜单、供应商的网点码管理不可见
        */

        {
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.ZB.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.ZB.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.ZB.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.ZB.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            码源工厂管理.setSort(2);

            Menu 网点码管理 = new Menu();
            网点码管理.setMenuName("网点码管理");
            网点码管理.setMenuAlias("wdmgl_" + GroupRoleEnum.ZB.getCode());
            网点码管理.setMenuIcon(null);
            网点码管理.setLinkUrl(null);
            网点码管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            网点码管理.setSort(2);
            网点码管理.setParentMenuAlias(null);
            Menu 活动码管理 = new Menu();
            活动码管理.setParentMenuAlias(网点码管理.getMenuAlias());
            活动码管理.setMenuName("活动码管理");
            活动码管理.setMenuAlias("hdmgl_" + GroupRoleEnum.ZB.getCode());
            活动码管理.setMenuIcon(null);
            活动码管理.setLinkUrl("/");
            活动码管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            活动码管理.setSort(1);

            Menu 活动管理 = new Menu();
            活动管理.setParentMenuAlias(null);
            活动管理.setMenuName("活动管理");
            活动管理.setMenuAlias("hdgl_" + GroupRoleEnum.ZB.getCode());
            活动管理.setMenuIcon(null);
            活动管理.setLinkUrl(null);
            活动管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            活动管理.setSort(3);
            Menu 活动数据 = new Menu();
            活动数据.setParentMenuAlias(活动管理.getMenuAlias());
            活动数据.setMenuName("活动数据");
            活动数据.setMenuAlias("hdsj_" + GroupRoleEnum.ZB.getCode());
            活动数据.setMenuIcon(null);
            活动数据.setLinkUrl("/");
            活动数据.setRoleAlias(GroupRoleEnum.ZB.getCode());
            活动数据.setSort(1);
            Menu 活动报表统计 = new Menu();
            活动报表统计.setParentMenuAlias(活动管理.getMenuAlias());
            活动报表统计.setMenuName("活动报表统计");
            活动报表统计.setMenuAlias("hdbbtj_" + GroupRoleEnum.ZB.getCode());
            活动报表统计.setMenuIcon(null);
            活动报表统计.setLinkUrl("/");
            活动报表统计.setRoleAlias(GroupRoleEnum.ZB.getCode());
            活动报表统计.setSort(2);

            Menu 分类管理 = new Menu();
            分类管理.setParentMenuAlias(null);
            分类管理.setMenuName("分类管理");
            分类管理.setMenuAlias("flgl_" + GroupRoleEnum.ZB.getCode());
            分类管理.setMenuIcon(null);
            分类管理.setLinkUrl(null);
            分类管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            分类管理.setSort(4);

            Menu 商品管理 = new Menu();
            商品管理.setParentMenuAlias(null);
            商品管理.setMenuName("商品管理");
            商品管理.setMenuAlias("spgl_" + GroupRoleEnum.ZB.getCode());
            商品管理.setMenuIcon(null);
            商品管理.setLinkUrl(null);
            商品管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            商品管理.setSort(5);

            Menu 订单管理 = new Menu();
            订单管理.setParentMenuAlias(null);
            订单管理.setMenuName("订单管理");
            订单管理.setMenuAlias("ddgl_" + GroupRoleEnum.ZB.getCode());
            订单管理.setMenuIcon(null);
            订单管理.setLinkUrl(null);
            订单管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            订单管理.setSort(6);

            Menu banner配置 = new Menu();
            banner配置.setParentMenuAlias(null);
            banner配置.setMenuName("banner配置");
            banner配置.setMenuAlias("bannerpz_" + GroupRoleEnum.ZB.getCode());
            banner配置.setMenuIcon(null);
            banner配置.setLinkUrl(null);
            banner配置.setRoleAlias(GroupRoleEnum.ZB.getCode());
            banner配置.setSort(7);

            Menu 首页配置 = new Menu();
            首页配置.setParentMenuAlias(null);
            首页配置.setMenuName("首页配置");
            首页配置.setMenuAlias("sypz_" + GroupRoleEnum.ZB.getCode());
            首页配置.setMenuIcon(null);
            首页配置.setLinkUrl(null);
            首页配置.setRoleAlias(GroupRoleEnum.ZB.getCode());
            首页配置.setSort(8);

            Menu 优惠券管理 = new Menu();
            优惠券管理.setParentMenuAlias(null);
            优惠券管理.setMenuName("优惠券管理");
            优惠券管理.setMenuAlias("yhqgl_" + GroupRoleEnum.ZB.getCode());
            优惠券管理.setMenuIcon(null);
            优惠券管理.setLinkUrl(null);
            优惠券管理.setRoleAlias(GroupRoleEnum.ZB.getCode());
            优惠券管理.setSort(9);

            Menu 公众号消息模板配置 = new Menu();
            公众号消息模板配置.setParentMenuAlias(null);
            公众号消息模板配置.setMenuName("公众号消息模板配置");
            公众号消息模板配置.setMenuAlias("gzhxxmbpz_" + GroupRoleEnum.ZB.getCode());
            公众号消息模板配置.setMenuIcon(null);
            公众号消息模板配置.setLinkUrl(null);
            公众号消息模板配置.setRoleAlias(GroupRoleEnum.ZB.getCode());
            公众号消息模板配置.setSort(10);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            list.add(网点码管理);
            list.add(活动码管理);

            list.add(活动管理);
            list.add(活动数据);
            list.add(活动报表统计);

            list.add(分类管理);

            list.add(商品管理);

            list.add(订单管理);

            list.add(banner配置);

            list.add(首页配置);

            list.add(优惠券管理);

            list.add(公众号消息模板配置);

            menuManager.saveBatch(list);
        }


    }

    public void createFGSmenus() {
        /*
        二级分公司、省区：商户管理（管理自己和下级商户账号）
        三级经销商：商户管理（管理自己和下级商户账号）
        四级业务员 分销商：商户管理（管理自己和下级商户账号）
        五级促销员：商户管理（（管理自己和下级商户账号）
        六级网点：不可登录后台
        物料供应商：网点码管理（物料供应商的网点码管理）
        */

        {
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.FGS.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.FGS.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.FGS.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.FGS.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.FGS.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.FGS.getCode());
            码源工厂管理.setSort(2);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }
    }

    public void createSQmenus() {
        /*
        二级分公司、省区：商户管理（管理自己和下级商户账号）
        */

        {
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.SQ.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.SQ.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.SQ.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.SQ.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.SQ.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.SQ.getCode());
            码源工厂管理.setSort(2);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }
    }

    public void createJXSmenus() {
        /*
        三级经销商：商户管理（管理自己和下级商户账号）
        四级业务员 分销商：商户管理（管理自己和下级商户账号）
        五级促销员：商户管理（（管理自己和下级商户账号）
        六级网点：不可登录后台
        物料供应商：网点码管理（物料供应商的网点码管理）
        */

        {
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.JXS.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.JXS.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.JXS.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.JXS.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.JXS.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.JXS.getCode());
            码源工厂管理.setSort(2);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }
    }

    public void createYWY_FXS_CXY_menus() {
        /*
        四级业务员 分销商：商户管理（管理自己和下级商户账号）
        五级促销员：商户管理（（管理自己和下级商户账号）
        */

        {
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.YWY.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.YWY.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.YWY.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.YWY.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.YWY.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.YWY.getCode());
            码源工厂管理.setSort(2);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }

        {
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.FXS.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.FXS.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.FXS.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.FXS.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.FXS.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.FXS.getCode());
            码源工厂管理.setSort(2);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }

        {
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + GroupRoleEnum.CXY.getCode());
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl(null);
            商户管理.setRoleAlias(GroupRoleEnum.CXY.getCode());
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(null);
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + GroupRoleEnum.CXY.getCode());
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/");
            网点类型配置.setRoleAlias(GroupRoleEnum.CXY.getCode());
            网点类型配置.setSort(1);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + GroupRoleEnum.CXY.getCode());
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/");
            码源工厂管理.setRoleAlias(GroupRoleEnum.CXY.getCode());
            码源工厂管理.setSort(2);

            List<Menu> list = new ArrayList<>();
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }

        {
            List<MenuResult> menus = adminMenuManageService.queryMerchantMenus(10359);
            System.out.println(JSON.toJSONString(menus, true));
        }

        {
            List<MenuResult> menus = adminMenuManageService.queryMerchantMenus(10362);
            System.out.println(JSON.toJSONString(menus, true));
        }
    }

    public void createWLMenus() {
        /*
            超管：能访问所有菜单
            一级总部 ：能访问所有菜单、供应商的网点码管理不可见
            二级分公司、省区：商户管理（管理自己和下级商户账号）
            三级经销商：商户管理（管理自己和下级商户账号）
            四级业务员 分销商：商户管理（管理自己和下级商户账号）
            五级促销员：商户管理（（管理自己和下级商户账号）
            六级网点：不可登录后台
            物料供应商：网点码管理（物料供应商的网点码管理）
        */

        {
            Menu 物料管理 = new Menu();
            物料管理.setParentMenuAlias(null);
            物料管理.setMenuName("物料管理");
            物料管理.setMenuAlias("wlgl_" + GroupRoleEnum.WL.getCode());
            物料管理.setMenuIcon(null);
            物料管理.setLinkUrl(null);
            物料管理.setRoleAlias(GroupRoleEnum.WL.getCode());
            物料管理.setSort(11);

            List<Menu> list = new ArrayList<>();
            list.add(物料管理);

            menuManager.saveBatch(list);
        }


    }

    public void queryMenus() {
        for (GroupRoleEnum roleEnum : GroupRoleEnum.values()) {
            List<MenuResult> menus = adminMenuManageService.queryMenusByRoleAlias(roleEnum.getCode());
            System.out.println(JSON.toJSONString(menus, true));
        }
    }

    //    @Test
    public void createItemSpecSku1() {
        // 商品信息
        Item item = new Item();
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setCategoryId(2);
        item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
        item.setItemTitle("椰岛鹿龟酒1688轻著版礼盒");
        item.setItemSubTitle("椰岛鹿龟酒1688轻著版礼盒");
        item.setItemPrice(BigDecimal.valueOf(1688));
        item.setItemMarketPrice(BigDecimal.valueOf(1888));
        item.setFreight(BigDecimal.ZERO);
        item.setItemStock(0);
        item.setItemCover("http://img.alicdn.com/imgextra/i2/2567390825/O1CN01CUexZo1HxtNvsPEnp_!!2567390825.jpg_430x430q90.jpg");
        item.setIsEnable("Y");
        item.setIsDelete("N");
        item.setSpecNum(1);
        item.setSort(1);
        itemMapper.insert(item);
        // 商品详情
        ItemContent content = new ItemContent();
        content.setCreateTime(LocalDateTime.now());
        content.setUpdateTime(LocalDateTime.now());
        content.setItemId(item.getId());
        content.setContent("详情");
        itemContentMapper.insert(content);
        // 商品图片
        String[] imageUrls = {"http://img.alicdn.com/imgextra/i2/2567390825/O1CN01CUexZo1HxtNvsPEnp_!!2567390825.jpg_430x430q90.jpg"};
        for (int i = 0; i < imageUrls.length; i++) {
            String imageUrl = imageUrls[i];
            ItemImage image = new ItemImage();
            image.setCreateTime(LocalDateTime.now());
            image.setUpdateTime(LocalDateTime.now());
            image.setItemId(item.getId());
            image.setImageUrl(imageUrl);
            image.setSort(i + 1);
            itemImageMapper.insert(image);
        }

        // 规格信息
        List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
        ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
        itemSpecNameAndValue1.setSpecName("净含量");
        itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("600"));
        itemSpecNameAndValue1.setSort(1);
        itemSpecNameAndValues.add(itemSpecNameAndValue1);

        // 预览sku
        GenerateItemSkuReq previewReq = new GenerateItemSkuReq();
        previewReq.setItemId(item.getId());
        previewReq.setSpecNameAndValues(itemSpecNameAndValues);
        List<ItemSkuResult> previewSkuList = adminMallManageService.previewItemSku(previewReq);

        // 保存sku
        List<ItemCandidateSku> candidateSkuList = previewSkuList.stream().map(skuResult -> {
            ItemCandidateSku itemCandidateSku = new ItemCandidateSku();
            itemCandidateSku.setItemId(item.getId());
            itemCandidateSku.setSpecNameValueJson(skuResult.getSpecNameValueJson());
            itemCandidateSku.setIsEnable("Y");
            itemCandidateSku.setStock(10);
            itemCandidateSku.setPrice(BigDecimal.ONE);
            itemCandidateSku.setMarketPrice(BigDecimal.TEN);
            return itemCandidateSku;
        }).collect(Collectors.toList());
        GenerateItemSkuReq generateItemSkuReq = new GenerateItemSkuReq();
        generateItemSkuReq.setItemId(item.getId());
        generateItemSkuReq.setSpecNameAndValues(itemSpecNameAndValues);
        SaveItemSkuReq req = new SaveItemSkuReq();
        req.setSpecInfo(generateItemSkuReq);
        req.setCandidateSkus(candidateSkuList);
        adminMallManageService.saveItemSku(req);
    }

    //    @Test
    public void createItemSpecSku2() {
        // 商品信息
        Item item = new Item();
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setCategoryId(2);
        item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
        item.setItemTitle("椰岛鹿龟酒养生保健酒33度");
        item.setItemSubTitle("椰岛鹿龟酒养生保健酒33度");
        item.setItemPrice(BigDecimal.valueOf(266));
        item.setItemMarketPrice(BigDecimal.valueOf(666));
        item.setFreight(BigDecimal.ZERO);
        item.setItemStock(0);
        item.setItemCover("https://img.alicdn.com/imgextra/i3/2567390825/O1CN01aXU8R61HxtGSCMjtN_!!2567390825.png_430x430q90.jpg");
        item.setIsEnable("Y");
        item.setIsDelete("N");
        item.setSpecNum(1);
        item.setSort(1);
        itemMapper.insert(item);
        // 商品详情
        ItemContent content = new ItemContent();
        content.setCreateTime(LocalDateTime.now());
        content.setUpdateTime(LocalDateTime.now());
        content.setItemId(item.getId());
        content.setContent("详情");
        itemContentMapper.insert(content);
        // 商品图片
        String[] imageUrls = {"https://img.alicdn.com/imgextra/i3/2567390825/O1CN01aXU8R61HxtGSCMjtN_!!2567390825.png_430x430q90.jpg"};
        for (int i = 0; i < imageUrls.length; i++) {
            String imageUrl = imageUrls[i];
            ItemImage image = new ItemImage();
            image.setCreateTime(LocalDateTime.now());
            image.setUpdateTime(LocalDateTime.now());
            image.setItemId(item.getId());
            image.setImageUrl(imageUrl);
            image.setSort(i + 1);
            itemImageMapper.insert(image);
        }

        // 规格信息
        List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
        ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
        itemSpecNameAndValue1.setSpecName("净含量");
        itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("500ml"));
        itemSpecNameAndValue1.setSort(1);
        itemSpecNameAndValues.add(itemSpecNameAndValue1);

        // 预览sku
        GenerateItemSkuReq previewReq = new GenerateItemSkuReq();
        previewReq.setItemId(item.getId());
        previewReq.setSpecNameAndValues(itemSpecNameAndValues);
        List<ItemSkuResult> previewSkuList = adminMallManageService.previewItemSku(previewReq);

        // 保存sku
        List<ItemCandidateSku> candidateSkuList = previewSkuList.stream().map(skuResult -> {
            ItemCandidateSku itemCandidateSku = new ItemCandidateSku();
            itemCandidateSku.setItemId(item.getId());
            itemCandidateSku.setSpecNameValueJson(skuResult.getSpecNameValueJson());
            itemCandidateSku.setIsEnable("Y");
            itemCandidateSku.setStock(10);
            itemCandidateSku.setPrice(item.getItemPrice());
            itemCandidateSku.setMarketPrice(item.getItemMarketPrice());
            return itemCandidateSku;
        }).collect(Collectors.toList());
        GenerateItemSkuReq generateItemSkuReq = new GenerateItemSkuReq();
        generateItemSkuReq.setItemId(item.getId());
        generateItemSkuReq.setSpecNameAndValues(itemSpecNameAndValues);
        SaveItemSkuReq req = new SaveItemSkuReq();
        req.setSpecInfo(generateItemSkuReq);
        req.setCandidateSkus(candidateSkuList);
        adminMallManageService.saveItemSku(req);
    }

    //    @Test
    public void createItemSpecSku3() {
        // 商品信息
        Item item = new Item();
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setCategoryId(2);
        item.setReceiveWay(ReceiveWayEnum.ALL.getCode());
        item.setItemTitle("火锅食材");
        item.setItemSubTitle("火锅食材");
        item.setItemPrice(BigDecimal.valueOf(35));
        item.setItemMarketPrice(BigDecimal.valueOf(65));
        item.setFreight(BigDecimal.ZERO);
        item.setItemStock(0);
        item.setItemCover("https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i2/3342239014/O1CN01B2Nmhc2GSSmYwXYrI_!!3342239014.jpg_430x430q90.jpg");
        item.setIsEnable("Y");
        item.setIsDelete("N");
        item.setSpecNum(2);
        item.setSort(1);
        itemMapper.insert(item);
        // 商品详情
        ItemContent content = new ItemContent();
        content.setCreateTime(LocalDateTime.now());
        content.setUpdateTime(LocalDateTime.now());
        content.setItemId(item.getId());
        content.setContent("详情");
        itemContentMapper.insert(content);
        // 商品图片
        String[] imageUrls = {
                "https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i2/3342239014/O1CN01B2Nmhc2GSSmYwXYrI_!!3342239014.jpg_430x430q90.jpg",
                "https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i2/3342239014/O1CN01B2Nmhc2GSSmYwXYrI_!!3342239014.jpg_430x430q90.jpg"
        };
        for (int i = 0; i < imageUrls.length; i++) {
            String imageUrl = imageUrls[i];
            ItemImage image = new ItemImage();
            image.setCreateTime(LocalDateTime.now());
            image.setUpdateTime(LocalDateTime.now());
            image.setItemId(item.getId());
            image.setImageUrl(imageUrl);
            image.setSort(i + 1);
            itemImageMapper.insert(image);
        }

        // 规格信息
        List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
        ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
        itemSpecNameAndValue1.setSpecName("口味");
        itemSpecNameAndValue1.setSpecValues(Lists.newArrayList(
                "【3荤2素】毛血旺310克*3盒+麻辣火锅300克*2盒",
                "【3荤3素】肉丸锅350g+鸡肉锅330g+脆皮肠锅350g+3盒麻辣火锅300g")
        );
        itemSpecNameAndValue1.setSort(1);
        itemSpecNameAndValues.add(itemSpecNameAndValue1);

        // 预览sku
        GenerateItemSkuReq previewReq = new GenerateItemSkuReq();
        previewReq.setItemId(item.getId());
        previewReq.setSpecNameAndValues(itemSpecNameAndValues);
        List<ItemSkuResult> previewSkuList = adminMallManageService.previewItemSku(previewReq);

        // 保存sku
        List<ItemCandidateSku> candidateSkuList = previewSkuList.stream().map(skuResult -> {
            ItemCandidateSku itemCandidateSku = new ItemCandidateSku();
            itemCandidateSku.setItemId(item.getId());
            itemCandidateSku.setSpecNameValueJson(skuResult.getSpecNameValueJson());
            itemCandidateSku.setIsEnable("Y");
            itemCandidateSku.setStock(10);
            itemCandidateSku.setPrice(item.getItemPrice());
            itemCandidateSku.setMarketPrice(item.getItemMarketPrice());
            return itemCandidateSku;
        }).collect(Collectors.toList());
        GenerateItemSkuReq generateItemSkuReq = new GenerateItemSkuReq();
        generateItemSkuReq.setItemId(item.getId());
        generateItemSkuReq.setSpecNameAndValues(itemSpecNameAndValues);
        SaveItemSkuReq req = new SaveItemSkuReq();
        req.setSpecInfo(generateItemSkuReq);
        req.setCandidateSkus(candidateSkuList);
        adminMallManageService.saveItemSku(req);
    }
}
