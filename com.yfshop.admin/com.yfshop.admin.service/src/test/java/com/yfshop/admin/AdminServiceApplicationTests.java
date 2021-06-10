package com.yfshop.admin;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.yfshop.admin.api.mall.AdminMallManageService;
import com.yfshop.admin.api.mall.request.CreateBannerReq;
import com.yfshop.admin.api.mall.request.GenerateItemSkuReq;
import com.yfshop.admin.api.mall.request.ItemCreateReq;
import com.yfshop.admin.api.mall.request.ItemSpecNameAndValue;
import com.yfshop.admin.api.mall.request.RecreateItemSkuReq;
import com.yfshop.admin.api.mall.request.SaveItemSkuReq;
import com.yfshop.admin.api.mall.request.SaveItemSkuReq.ItemCandidateSku;
import com.yfshop.admin.api.mall.result.BannerResult;
import com.yfshop.admin.api.mall.result.ItemResult;
import com.yfshop.admin.api.mall.result.ItemSkuResult;
import com.yfshop.admin.api.menu.AdminMenuManageService;
import com.yfshop.admin.api.menu.result.MenuResult;
import com.yfshop.admin.api.merchant.AdminMerchantManageService;
import com.yfshop.admin.api.merchant.request.UpdateMerchantReq;
import com.yfshop.admin.api.merchant.result.MerchantResult;
import com.yfshop.admin.utils.BaiduMapGeocoderUtil;
import com.yfshop.code.manager.MenuManager;
import com.yfshop.code.mapper.ItemContentMapper;
import com.yfshop.code.mapper.ItemImageMapper;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.mapper.MerchantDetailMapper;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.mapper.RegionMapper;
import com.yfshop.code.model.Item;
import com.yfshop.code.model.ItemContent;
import com.yfshop.code.model.ItemImage;
import com.yfshop.code.model.Menu;
import com.yfshop.code.model.Merchant;
import com.yfshop.code.model.MerchantDetail;
import com.yfshop.code.model.Region;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.util.AddressUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@SpringBootTest
public class AdminServiceApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceApplicationTests.class);

    @Resource
    private MerchantDetailMapper merchantDetailMapper;
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
    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private TransactionTemplate transactionTemplate;
    @DubboReference(check = false)
    private AdminMerchantManageService adminMerchantManageService;

    // @Test
    public void fasfasdfasdfas() {
        UpdateMerchantReq req = new UpdateMerchantReq();
        req.setOperatorId(1);
        req.setMerchantId(10424);
        req.setRoleAlias("jxs");
        req.setContacts("嘻嘻");
        req.setMobile("13333333332");
        req.setPassword("32131");
        req.setProvinceId(1);
        req.setCityId(1);
        req.setDistrictId(1);
        req.setAddress("dfasfasfas");
        req.setPid(10431);
        req.setIsRefrigerator("Y");
        req.setHeadImage("fdsfasfas");
        req.setMerchantName("fsdfasf");
        adminMerchantManageService.updateMerchant(1, req);
    }

    // @Test
    public void sdasdadada() {
        ItemCreateReq req = new ItemCreateReq();
        req.setItemTitle("ffsdfsd");
        req.setReceiveWay(ReceiveWayEnum.ALL.getCode());
        req.setCategoryId(2);
        req.setItemSubTitle("fafdf");
        req.setIsEnable("N");
        req.setItemImages(Arrays.asList("1", "2"));
        req.setItemContent("fsdfdsfsfsafsaffsafasfsafa");
        if (false) {
            adminMallManageService2.createItem(req);
        }

        ItemResult itemDetailAndSkuList = adminMallManageService2.findItemDetailAndSkuList(2034);
        System.out.println(JSON.toJSONString(itemDetailAndSkuList, true));
    }

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


    // @Test
    public void contextLoads1() {

        try {
            List<ItemSpecNameAndValue> itemSpecNameAndValues = new ArrayList<>();
            ItemSpecNameAndValue itemSpecNameAndValue1 = new ItemSpecNameAndValue();
            itemSpecNameAndValue1.setSpecName("套餐");
            itemSpecNameAndValue1.setSpecValues(Lists.newArrayList("99元火锅美食体验套餐"));
            itemSpecNameAndValue1.setSort(1);
            itemSpecNameAndValues.add(itemSpecNameAndValue1);

            Item item = new Item();
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setCategoryId(189);
            item.setReceiveWay(ReceiveWayEnum.PS.getCode());
            item.setItemTitle("99元火锅美食体验套餐");
            item.setItemSubTitle("99元火锅美食体验套餐");
            item.setItemPrice(BigDecimal.valueOf(99));
            item.setItemMarketPrice(BigDecimal.valueOf(99));
            item.setFreight(BigDecimal.ZERO);
            item.setItemStock(100000000);
            item.setItemCover("");
            item.setIsEnable("Y");
            item.setIsDelete("N");
            item.setSpecNum(1);
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
        createSysMenus22222222();
        createZongBuMenus22222222();
        createFGSmenus22222222();
        createSQmenus22222222();
        createJXSmenus22222222();
        createYWY_FXS_CXY_menus();
        createWLMenus();
        queryMenus();
    }

    public void createSysMenus22222222() {
        String roleCode = GroupRoleEnum.SYS.getCode();
        List<Menu> list = new ArrayList<>();
        // 超管
        Menu parent商户管理 = new Menu();
        parent商户管理.setMenuName("商户管理");
        parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
        parent商户管理.setMenuIcon(null);
        parent商户管理.setLinkUrl("/merchantsManagement");
        parent商户管理.setRoleAlias(roleCode);
        parent商户管理.setSort(1);
        parent商户管理.setParentMenuAlias(null);
        Menu 商户管理 = new Menu();
        商户管理.setMenuName("商户管理");
        商户管理.setMenuAlias("shgl_" + roleCode);
        商户管理.setMenuIcon(null);
        商户管理.setLinkUrl("/merchantsManagement/merchantsList");
        商户管理.setRoleAlias(roleCode);
        商户管理.setSort(1);
        商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        Menu 网点类型配置 = new Menu();
        网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
        网点类型配置.setMenuName("网点类型配置");
        网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
        网点类型配置.setMenuIcon(null);
        网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
        网点类型配置.setRoleAlias(roleCode);
        网点类型配置.setSort(2);
        Menu 码源工厂管理 = new Menu();
        码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        码源工厂管理.setMenuName("码源工厂管理");
        码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
        码源工厂管理.setMenuIcon(null);
        码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
        码源工厂管理.setRoleAlias(roleCode);
        码源工厂管理.setSort(3);
        list.add(parent商户管理);
        list.add(商户管理);
        list.add(网点类型配置);
        list.add(码源工厂管理);


        Menu parent二维码管理 = new Menu();
        parent二维码管理.setParentMenuAlias(null);
        parent二维码管理.setMenuName("二维码管理");
        parent二维码管理.setMenuAlias("parent_ewmgl_" + roleCode);
        parent二维码管理.setMenuIcon(null);
        parent二维码管理.setLinkUrl("/networkCodeManagement");
        parent二维码管理.setRoleAlias(roleCode);
        parent二维码管理.setSort(2);
        Menu 网点码管理 = new Menu();
        网点码管理.setMenuName("网点码管理");
        网点码管理.setMenuAlias("wdmgl_" + roleCode);
        网点码管理.setMenuIcon(null);
        网点码管理.setLinkUrl("/networkCodeManagement/networkCodeList");
        网点码管理.setRoleAlias(roleCode);
        网点码管理.setSort(1);
        网点码管理.setParentMenuAlias(parent二维码管理.getMenuAlias());
        Menu 活动码管理 = new Menu();
        活动码管理.setMenuName("活动码管理");
        活动码管理.setMenuAlias("hdmgl_" + roleCode);
        活动码管理.setMenuIcon(null);
        活动码管理.setLinkUrl("/networkCodeManagement/activityCodeList");
        活动码管理.setRoleAlias(roleCode);
        活动码管理.setSort(2);
        活动码管理.setParentMenuAlias(parent二维码管理.getMenuAlias());
        list.add(parent二维码管理);
        list.add(网点码管理);
        list.add(活动码管理);


        Menu parent供应商后台 = new Menu();
        parent供应商后台.setParentMenuAlias(null);
        parent供应商后台.setMenuName("供应商后台");
        parent供应商后台.setMenuAlias("parent_gysht_" + roleCode);
        parent供应商后台.setMenuIcon(null);
        parent供应商后台.setLinkUrl("/networkCodeManagement");
        parent供应商后台.setRoleAlias(roleCode);
        parent供应商后台.setSort(3);
        Menu 商户码管理 = new Menu();
        商户码管理.setParentMenuAlias(parent供应商后台.getMenuAlias());
        商户码管理.setMenuName("商户码管理");
        商户码管理.setMenuAlias("shmgl_" + roleCode);
        商户码管理.setMenuIcon(null);
        商户码管理.setLinkUrl("/networkCodeManagement/networkCodeListForGys");
        商户码管理.setRoleAlias(roleCode);
        商户码管理.setSort(1);
        list.add(parent供应商后台);
        list.add(商户码管理);


        Menu parent活动管理 = new Menu();
        parent活动管理.setParentMenuAlias(null);
        parent活动管理.setMenuName("活动管理");
        parent活动管理.setMenuAlias("parent_hdgl_" + roleCode);
        parent活动管理.setMenuIcon(null);
        parent活动管理.setLinkUrl("/activitysManagement");
        parent活动管理.setRoleAlias(roleCode);
        parent活动管理.setSort(4);
        Menu 活动管理 = new Menu();
        活动管理.setParentMenuAlias(parent活动管理.getMenuAlias());
        活动管理.setMenuName("活动管理");
        活动管理.setMenuAlias("hdgl_" + roleCode);
        活动管理.setMenuIcon(null);
        活动管理.setLinkUrl("/activitysManagement/activitysList");
        活动管理.setRoleAlias(roleCode);
        活动管理.setSort(1);
        Menu 活动数据 = new Menu();
        活动数据.setParentMenuAlias(parent活动管理.getMenuAlias());
        活动数据.setMenuName("活动数据");
        活动数据.setMenuAlias("hdsj_" + roleCode);
        活动数据.setMenuIcon(null);
        活动数据.setLinkUrl("/activitysManagement/activitysDataList");
        活动数据.setRoleAlias(roleCode);
        活动数据.setSort(2);
        Menu 活动报表统计 = new Menu();
        活动报表统计.setParentMenuAlias(parent活动管理.getMenuAlias());
        活动报表统计.setMenuName("活动报表统计");
        活动报表统计.setMenuAlias("hdbbtj_" + roleCode);
        活动报表统计.setMenuIcon(null);
        活动报表统计.setLinkUrl("/activitysManagement/activitysEcharts");
        活动报表统计.setRoleAlias(roleCode);
        活动报表统计.setSort(3);
        list.add(parent活动管理);
        list.add(活动管理);
        list.add(活动数据);
        list.add(活动报表统计);


        Menu parent商城管理 = new Menu();
        parent商城管理.setParentMenuAlias(null);
        parent商城管理.setMenuName("商城管理");
        parent商城管理.setMenuAlias("parent_scgl_" + roleCode);
        parent商城管理.setMenuIcon(null);
        parent商城管理.setLinkUrl("/shopManagement");
        parent商城管理.setRoleAlias(roleCode);
        parent商城管理.setSort(5);
        Menu 分类管理 = new Menu();
        分类管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        分类管理.setMenuName("分类管理");
        分类管理.setMenuAlias("flgl_" + roleCode);
        分类管理.setMenuIcon(null);
        分类管理.setLinkUrl("/shopManagement/shopTypeList");
        分类管理.setRoleAlias(roleCode);
        分类管理.setSort(1);
        Menu 商品管理 = new Menu();
        商品管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        商品管理.setMenuName("商品管理");
        商品管理.setMenuAlias("spgl_" + roleCode);
        商品管理.setMenuIcon(null);
        商品管理.setLinkUrl("/shopManagement/goodsList");
        商品管理.setRoleAlias(roleCode);
        商品管理.setSort(2);
        Menu 订单管理 = new Menu();
        订单管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        订单管理.setMenuName("订单管理");
        订单管理.setMenuAlias("ddgl_" + roleCode);
        订单管理.setMenuIcon(null);
        订单管理.setLinkUrl("/shopManagement/orderList");
        订单管理.setRoleAlias(roleCode);
        订单管理.setSort(3);
        Menu banner配置 = new Menu();
        banner配置.setParentMenuAlias(parent商城管理.getMenuAlias());
        banner配置.setMenuName("banner配置");
        banner配置.setMenuAlias("bannerpz_" + roleCode);
        banner配置.setMenuIcon(null);
        banner配置.setLinkUrl("/shopManagement/bannerList");
        banner配置.setRoleAlias(roleCode);
        banner配置.setSort(4);
        Menu 首页配置 = new Menu();
        首页配置.setParentMenuAlias(parent商城管理.getMenuAlias());
        首页配置.setMenuName("首页配置");
        首页配置.setMenuAlias("sypz_" + roleCode);
        首页配置.setMenuIcon(null);
        首页配置.setLinkUrl("/shopManagement/homepageList");
        首页配置.setRoleAlias(roleCode);
        首页配置.setSort(5);
        Menu 优惠券管理 = new Menu();
        优惠券管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        优惠券管理.setMenuName("优惠券管理");
        优惠券管理.setMenuAlias("yhqgl_" + roleCode);
        优惠券管理.setMenuIcon(null);
        优惠券管理.setLinkUrl("/shopManagement/couponList");
        优惠券管理.setRoleAlias(roleCode);
        优惠券管理.setSort(6);
        list.add(parent商城管理);
        list.add(分类管理);
        list.add(商品管理);
        list.add(订单管理);
        list.add(banner配置);
        list.add(首页配置);
        list.add(优惠券管理);

        Menu parent基础配置 = new Menu();
        parent基础配置.setParentMenuAlias(null);
        parent基础配置.setMenuName("基础配置");
        parent基础配置.setMenuAlias("parent_jcpz_" + roleCode);
        parent基础配置.setMenuIcon(null);
        parent基础配置.setLinkUrl("/settingManagement");
        parent基础配置.setRoleAlias(roleCode);
        parent基础配置.setSort(6);
        Menu 模板消息网点码报表 = new Menu();
        模板消息网点码报表.setParentMenuAlias(parent基础配置.getMenuAlias());
        模板消息网点码报表.setMenuName("模板消息网点码报表");
        模板消息网点码报表.setMenuAlias("mbxxwdmbb_" + roleCode);
        模板消息网点码报表.setMenuIcon(null);
        模板消息网点码报表.setLinkUrl("/settingManagement/messageData");
        模板消息网点码报表.setRoleAlias(roleCode);
        模板消息网点码报表.setSort(1);
        Menu 模板消息优惠券过期 = new Menu();
        模板消息优惠券过期.setParentMenuAlias(parent基础配置.getMenuAlias());
        模板消息优惠券过期.setMenuName("模板消息优惠券过期");
        模板消息优惠券过期.setMenuAlias("mbxxyhqgq_" + roleCode);
        模板消息优惠券过期.setMenuIcon(null);
        模板消息优惠券过期.setLinkUrl("/settingManagement/messageTimeout");
        模板消息优惠券过期.setRoleAlias(roleCode);
        模板消息优惠券过期.setSort(2);
        list.add(parent基础配置);
        list.add(模板消息网点码报表);
        list.add(模板消息优惠券过期);

        menuManager.saveBatch(list);
    }

    public void createZongBuMenus22222222() {
       /*
        一级总部 ：能访问所有菜单、供应商的网点码管理不可见
        */
        String roleCode = GroupRoleEnum.ZB.getCode();
        List<Menu> list = new ArrayList<>();
        // 超管
        Menu parent商户管理 = new Menu();
        parent商户管理.setMenuName("商户管理");
        parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
        parent商户管理.setMenuIcon(null);
        parent商户管理.setLinkUrl("/merchantsManagement");
        parent商户管理.setRoleAlias(roleCode);
        parent商户管理.setSort(1);
        parent商户管理.setParentMenuAlias(null);
        Menu 商户管理 = new Menu();
        商户管理.setMenuName("商户管理");
        商户管理.setMenuAlias("shgl_" + roleCode);
        商户管理.setMenuIcon(null);
        商户管理.setLinkUrl("/merchantsManagement/merchantsList");
        商户管理.setRoleAlias(roleCode);
        商户管理.setSort(1);
        商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        Menu 网点类型配置 = new Menu();
        网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
        网点类型配置.setMenuName("网点类型配置");
        网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
        网点类型配置.setMenuIcon(null);
        网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
        网点类型配置.setRoleAlias(roleCode);
        网点类型配置.setSort(2);
        Menu 码源工厂管理 = new Menu();
        码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        码源工厂管理.setMenuName("码源工厂管理");
        码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
        码源工厂管理.setMenuIcon(null);
        码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
        码源工厂管理.setRoleAlias(roleCode);
        码源工厂管理.setSort(3);
        list.add(parent商户管理);
        list.add(商户管理);
        list.add(网点类型配置);
        list.add(码源工厂管理);


        Menu parent二维码管理 = new Menu();
        parent二维码管理.setParentMenuAlias(null);
        parent二维码管理.setMenuName("二维码管理");
        parent二维码管理.setMenuAlias("parent_ewmgl_" + roleCode);
        parent二维码管理.setMenuIcon(null);
        parent二维码管理.setLinkUrl("/networkCodeManagement");
        parent二维码管理.setRoleAlias(roleCode);
        parent二维码管理.setSort(2);
        Menu 网点码管理 = new Menu();
        网点码管理.setMenuName("网点码管理");
        网点码管理.setMenuAlias("wdmgl_" + roleCode);
        网点码管理.setMenuIcon(null);
        网点码管理.setLinkUrl("/networkCodeManagement/networkCodeList");
        网点码管理.setRoleAlias(roleCode);
        网点码管理.setSort(1);
        网点码管理.setParentMenuAlias(parent二维码管理.getMenuAlias());
        Menu 活动码管理 = new Menu();
        活动码管理.setMenuName("活动码管理");
        活动码管理.setMenuAlias("hdmgl_" + roleCode);
        活动码管理.setMenuIcon(null);
        活动码管理.setLinkUrl("/networkCodeManagement/activityCodeList");
        活动码管理.setRoleAlias(roleCode);
        活动码管理.setSort(2);
        活动码管理.setParentMenuAlias(parent二维码管理.getMenuAlias());
        list.add(parent二维码管理);
        list.add(网点码管理);
        list.add(活动码管理);

        Menu parent活动管理 = new Menu();
        parent活动管理.setParentMenuAlias(null);
        parent活动管理.setMenuName("活动管理");
        parent活动管理.setMenuAlias("parent_hdgl_" + roleCode);
        parent活动管理.setMenuIcon(null);
        parent活动管理.setLinkUrl("/activitysManagement");
        parent活动管理.setRoleAlias(roleCode);
        parent活动管理.setSort(3);
        Menu 活动管理 = new Menu();
        活动管理.setParentMenuAlias(parent活动管理.getMenuAlias());
        活动管理.setMenuName("活动管理");
        活动管理.setMenuAlias("hdgl_" + roleCode);
        活动管理.setMenuIcon(null);
        活动管理.setLinkUrl("/activitysManagement/activitysList");
        活动管理.setRoleAlias(roleCode);
        活动管理.setSort(1);
        Menu 活动数据 = new Menu();
        活动数据.setParentMenuAlias(parent活动管理.getMenuAlias());
        活动数据.setMenuName("活动数据");
        活动数据.setMenuAlias("hdsj_" + roleCode);
        活动数据.setMenuIcon(null);
        活动数据.setLinkUrl("/activitysManagement/activitysDataList");
        活动数据.setRoleAlias(roleCode);
        活动数据.setSort(2);
        Menu 活动报表统计 = new Menu();
        活动报表统计.setParentMenuAlias(parent活动管理.getMenuAlias());
        活动报表统计.setMenuName("活动报表统计");
        活动报表统计.setMenuAlias("hdbbtj_" + roleCode);
        活动报表统计.setMenuIcon(null);
        活动报表统计.setLinkUrl("/activitysManagement/activitysEcharts");
        活动报表统计.setRoleAlias(roleCode);
        活动报表统计.setSort(3);
        list.add(parent活动管理);
        list.add(活动管理);
        list.add(活动数据);
        list.add(活动报表统计);


        Menu parent商城管理 = new Menu();
        parent商城管理.setParentMenuAlias(null);
        parent商城管理.setMenuName("商城管理");
        parent商城管理.setMenuAlias("parent_scgl_" + roleCode);
        parent商城管理.setMenuIcon(null);
        parent商城管理.setLinkUrl("/shopManagement");
        parent商城管理.setRoleAlias(roleCode);
        parent商城管理.setSort(4);
        Menu 分类管理 = new Menu();
        分类管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        分类管理.setMenuName("分类管理");
        分类管理.setMenuAlias("flgl_" + roleCode);
        分类管理.setMenuIcon(null);
        分类管理.setLinkUrl("/shopManagement/shopTypeList");
        分类管理.setRoleAlias(roleCode);
        分类管理.setSort(1);
        Menu 商品管理 = new Menu();
        商品管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        商品管理.setMenuName("商品管理");
        商品管理.setMenuAlias("spgl_" + roleCode);
        商品管理.setMenuIcon(null);
        商品管理.setLinkUrl("/shopManagement/goodsList");
        商品管理.setRoleAlias(roleCode);
        商品管理.setSort(2);
        Menu 订单管理 = new Menu();
        订单管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        订单管理.setMenuName("订单管理");
        订单管理.setMenuAlias("ddgl_" + roleCode);
        订单管理.setMenuIcon(null);
        订单管理.setLinkUrl("/shopManagement/orderList");
        订单管理.setRoleAlias(roleCode);
        订单管理.setSort(3);
        Menu banner配置 = new Menu();
        banner配置.setParentMenuAlias(parent商城管理.getMenuAlias());
        banner配置.setMenuName("banner配置");
        banner配置.setMenuAlias("bannerpz_" + roleCode);
        banner配置.setMenuIcon(null);
        banner配置.setLinkUrl("/shopManagement/bannerList");
        banner配置.setRoleAlias(roleCode);
        banner配置.setSort(4);
        Menu 首页配置 = new Menu();
        首页配置.setParentMenuAlias(parent商城管理.getMenuAlias());
        首页配置.setMenuName("首页配置");
        首页配置.setMenuAlias("sypz_" + roleCode);
        首页配置.setMenuIcon(null);
        首页配置.setLinkUrl("/shopManagement/homepageList");
        首页配置.setRoleAlias(roleCode);
        首页配置.setSort(5);
        Menu 优惠券管理 = new Menu();
        优惠券管理.setParentMenuAlias(parent商城管理.getMenuAlias());
        优惠券管理.setMenuName("优惠券管理");
        优惠券管理.setMenuAlias("yhqgl_" + roleCode);
        优惠券管理.setMenuIcon(null);
        优惠券管理.setLinkUrl("/shopManagement/couponList");
        优惠券管理.setRoleAlias(roleCode);
        优惠券管理.setSort(6);
        list.add(parent商城管理);
        list.add(分类管理);
        list.add(商品管理);
        list.add(订单管理);
        list.add(banner配置);
        list.add(首页配置);
        list.add(优惠券管理);

        Menu parent基础配置 = new Menu();
        parent基础配置.setParentMenuAlias(null);
        parent基础配置.setMenuName("基础配置");
        parent基础配置.setMenuAlias("parent_jcpz_" + roleCode);
        parent基础配置.setMenuIcon(null);
        parent基础配置.setLinkUrl("/settingManagement");
        parent基础配置.setRoleAlias(roleCode);
        parent基础配置.setSort(5);
        Menu 模板消息网点码报表 = new Menu();
        模板消息网点码报表.setParentMenuAlias(parent基础配置.getMenuAlias());
        模板消息网点码报表.setMenuName("模板消息网点码报表");
        模板消息网点码报表.setMenuAlias("mbxxwdmbb_" + roleCode);
        模板消息网点码报表.setMenuIcon(null);
        模板消息网点码报表.setLinkUrl("/settingManagement/messageData");
        模板消息网点码报表.setRoleAlias(roleCode);
        模板消息网点码报表.setSort(1);
        Menu 模板消息优惠券过期 = new Menu();
        模板消息优惠券过期.setParentMenuAlias(parent基础配置.getMenuAlias());
        模板消息优惠券过期.setMenuName("模板消息优惠券过期");
        模板消息优惠券过期.setMenuAlias("mbxxyhqgq_" + roleCode);
        模板消息优惠券过期.setMenuIcon(null);
        模板消息优惠券过期.setLinkUrl("/settingManagement/messageTimeout");
        模板消息优惠券过期.setRoleAlias(roleCode);
        模板消息优惠券过期.setSort(2);
        list.add(parent基础配置);
        list.add(模板消息网点码报表);
        list.add(模板消息优惠券过期);

        menuManager.saveBatch(list);
    }

    public void createFGSmenus22222222() {
       /*
        二级分公司、省区：商户管理（管理自己和下级商户账号）
        三级经销商：商户管理（管理自己和下级商户账号）
        四级业务员 分销商：商户管理（管理自己和下级商户账号）
        五级促销员：商户管理（（管理自己和下级商户账号）
        六级网点：不可登录后台
        物料供应商：网点码管理（物料供应商的网点码管理）
        */
        String roleCode = GroupRoleEnum.FGS.getCode();
        List<Menu> list = new ArrayList<>();
        Menu parent商户管理 = new Menu();
        parent商户管理.setMenuName("商户管理");
        parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
        parent商户管理.setMenuIcon(null);
        parent商户管理.setLinkUrl("/merchantsManagement");
        parent商户管理.setRoleAlias(roleCode);
        parent商户管理.setSort(1);
        parent商户管理.setParentMenuAlias(null);
        Menu 商户管理 = new Menu();
        商户管理.setMenuName("商户管理");
        商户管理.setMenuAlias("shgl_" + roleCode);
        商户管理.setMenuIcon(null);
        商户管理.setLinkUrl("/merchantsManagement/merchantsList");
        商户管理.setRoleAlias(roleCode);
        商户管理.setSort(1);
        商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        Menu 网点类型配置 = new Menu();
        网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
        网点类型配置.setMenuName("网点类型配置");
        网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
        网点类型配置.setMenuIcon(null);
        网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
        网点类型配置.setRoleAlias(roleCode);
        网点类型配置.setSort(2);
        Menu 码源工厂管理 = new Menu();
        码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        码源工厂管理.setMenuName("码源工厂管理");
        码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
        码源工厂管理.setMenuIcon(null);
        码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
        码源工厂管理.setRoleAlias(roleCode);
        码源工厂管理.setSort(3);
        list.add(parent商户管理);
        list.add(商户管理);
        list.add(网点类型配置);
        list.add(码源工厂管理);

        menuManager.saveBatch(list);
    }

    public void createSQmenus22222222() {
        /*
        二级分公司、省区：商户管理（管理自己和下级商户账号）
        */
        String roleCode = GroupRoleEnum.SQ.getCode();
        List<Menu> list = new ArrayList<>();
        Menu parent商户管理 = new Menu();
        parent商户管理.setMenuName("商户管理");
        parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
        parent商户管理.setMenuIcon(null);
        parent商户管理.setLinkUrl("/merchantsManagement");
        parent商户管理.setRoleAlias(roleCode);
        parent商户管理.setSort(1);
        parent商户管理.setParentMenuAlias(null);
        Menu 商户管理 = new Menu();
        商户管理.setMenuName("商户管理");
        商户管理.setMenuAlias("shgl_" + roleCode);
        商户管理.setMenuIcon(null);
        商户管理.setLinkUrl("/merchantsManagement/merchantsList");
        商户管理.setRoleAlias(roleCode);
        商户管理.setSort(1);
        商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        Menu 网点类型配置 = new Menu();
        网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
        网点类型配置.setMenuName("网点类型配置");
        网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
        网点类型配置.setMenuIcon(null);
        网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
        网点类型配置.setRoleAlias(roleCode);
        网点类型配置.setSort(2);
        Menu 码源工厂管理 = new Menu();
        码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        码源工厂管理.setMenuName("码源工厂管理");
        码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
        码源工厂管理.setMenuIcon(null);
        码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
        码源工厂管理.setRoleAlias(roleCode);
        码源工厂管理.setSort(3);
        list.add(parent商户管理);
        list.add(商户管理);
        list.add(网点类型配置);
        list.add(码源工厂管理);

        menuManager.saveBatch(list);
    }

    public void createJXSmenus22222222() {
        /*
        三级经销商：商户管理（管理自己和下级商户账号）
        四级业务员 分销商：商户管理（管理自己和下级商户账号）
        五级促销员：商户管理（（管理自己和下级商户账号）
        六级网点：不可登录后台
        物料供应商：网点码管理（物料供应商的网点码管理）
        */

        String roleCode = GroupRoleEnum.JXS.getCode();
        List<Menu> list = new ArrayList<>();
        Menu parent商户管理 = new Menu();
        parent商户管理.setMenuName("商户管理");
        parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
        parent商户管理.setMenuIcon(null);
        parent商户管理.setLinkUrl("/merchantsManagement");
        parent商户管理.setRoleAlias(roleCode);
        parent商户管理.setSort(1);
        parent商户管理.setParentMenuAlias(null);
        Menu 商户管理 = new Menu();
        商户管理.setMenuName("商户管理");
        商户管理.setMenuAlias("shgl_" + roleCode);
        商户管理.setMenuIcon(null);
        商户管理.setLinkUrl("/merchantsManagement/merchantsList");
        商户管理.setRoleAlias(roleCode);
        商户管理.setSort(1);
        商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        Menu 网点类型配置 = new Menu();
        网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
        网点类型配置.setMenuName("网点类型配置");
        网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
        网点类型配置.setMenuIcon(null);
        网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
        网点类型配置.setRoleAlias(roleCode);
        网点类型配置.setSort(2);
        Menu 码源工厂管理 = new Menu();
        码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
        码源工厂管理.setMenuName("码源工厂管理");
        码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
        码源工厂管理.setMenuIcon(null);
        码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
        码源工厂管理.setRoleAlias(roleCode);
        码源工厂管理.setSort(3);
        list.add(parent商户管理);
        list.add(商户管理);
        list.add(网点类型配置);
        list.add(码源工厂管理);

        menuManager.saveBatch(list);
    }

    public void createYWY_FXS_CXY_menus() {
        /*
        四级业务员 分销商：商户管理（管理自己和下级商户账号）
        五级促销员：商户管理（（管理自己和下级商户账号）
        */

        {
            String roleCode = GroupRoleEnum.YWY.getCode();
            List<Menu> list = new ArrayList<>();
            Menu parent商户管理 = new Menu();
            parent商户管理.setMenuName("商户管理");
            parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
            parent商户管理.setMenuIcon(null);
            parent商户管理.setLinkUrl("/merchantsManagement");
            parent商户管理.setRoleAlias(roleCode);
            parent商户管理.setSort(1);
            parent商户管理.setParentMenuAlias(null);
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + roleCode);
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl("/merchantsManagement/merchantsList");
            商户管理.setRoleAlias(roleCode);
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
            网点类型配置.setRoleAlias(roleCode);
            网点类型配置.setSort(2);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
            码源工厂管理.setRoleAlias(roleCode);
            码源工厂管理.setSort(3);
            list.add(parent商户管理);
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }

        {
            String roleCode = GroupRoleEnum.FXS.getCode();
            List<Menu> list = new ArrayList<>();
            Menu parent商户管理 = new Menu();
            parent商户管理.setMenuName("商户管理");
            parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
            parent商户管理.setMenuIcon(null);
            parent商户管理.setLinkUrl("/merchantsManagement");
            parent商户管理.setRoleAlias(roleCode);
            parent商户管理.setSort(1);
            parent商户管理.setParentMenuAlias(null);
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + roleCode);
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl("/merchantsManagement/merchantsList");
            商户管理.setRoleAlias(roleCode);
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
            网点类型配置.setRoleAlias(roleCode);
            网点类型配置.setSort(2);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
            码源工厂管理.setRoleAlias(roleCode);
            码源工厂管理.setSort(3);
            list.add(parent商户管理);
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
        }

        {
            String roleCode = GroupRoleEnum.CXY.getCode();
            List<Menu> list = new ArrayList<>();
            Menu parent商户管理 = new Menu();
            parent商户管理.setMenuName("商户管理");
            parent商户管理.setMenuAlias("parent_shgl_" + roleCode);
            parent商户管理.setMenuIcon(null);
            parent商户管理.setLinkUrl("/merchantsManagement");
            parent商户管理.setRoleAlias(roleCode);
            parent商户管理.setSort(1);
            parent商户管理.setParentMenuAlias(null);
            Menu 商户管理 = new Menu();
            商户管理.setMenuName("商户管理");
            商户管理.setMenuAlias("shgl_" + roleCode);
            商户管理.setMenuIcon(null);
            商户管理.setLinkUrl("/merchantsManagement/merchantsList");
            商户管理.setRoleAlias(roleCode);
            商户管理.setSort(1);
            商户管理.setParentMenuAlias(parent商户管理.getMenuAlias());
            Menu 网点类型配置 = new Menu();
            网点类型配置.setParentMenuAlias(parent商户管理.getMenuAlias());
            网点类型配置.setMenuName("网点类型配置");
            网点类型配置.setMenuAlias("wdlxpz_" + roleCode);
            网点类型配置.setMenuIcon(null);
            网点类型配置.setLinkUrl("/merchantsManagement/netWorkTypeList");
            网点类型配置.setRoleAlias(roleCode);
            网点类型配置.setSort(2);
            Menu 码源工厂管理 = new Menu();
            码源工厂管理.setParentMenuAlias(parent商户管理.getMenuAlias());
            码源工厂管理.setMenuName("码源工厂管理");
            码源工厂管理.setMenuAlias("mygcgl_" + roleCode);
            码源工厂管理.setMenuIcon(null);
            码源工厂管理.setLinkUrl("/merchantsManagement/codeFactory");
            码源工厂管理.setRoleAlias(roleCode);
            码源工厂管理.setSort(3);
            list.add(parent商户管理);
            list.add(商户管理);
            list.add(网点类型配置);
            list.add(码源工厂管理);

            menuManager.saveBatch(list);
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
            List<Menu> list = new ArrayList<>();
            String roleCode = GroupRoleEnum.WL.getCode();
            Menu parent供应商后台 = new Menu();
            parent供应商后台.setParentMenuAlias(null);
            parent供应商后台.setMenuName("供应商后台");
            parent供应商后台.setMenuAlias("parent_gysht_" + roleCode);
            parent供应商后台.setMenuIcon(null);
            parent供应商后台.setLinkUrl("/networkCodeManagement");
            parent供应商后台.setRoleAlias(roleCode);
            parent供应商后台.setSort(1);
            Menu 商户码管理 = new Menu();
            商户码管理.setParentMenuAlias(parent供应商后台.getMenuAlias());
            商户码管理.setMenuName("商户码管理");
            商户码管理.setMenuAlias("shmgl_" + roleCode);
            商户码管理.setMenuIcon(null);
            商户码管理.setLinkUrl("/networkCodeManagement/networkCodeListForGys");
            商户码管理.setRoleAlias(roleCode);
            商户码管理.setSort(1);
            list.add(parent供应商后台);
            list.add(商户码管理);
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

    public void importMerchantsFromExcel(String excelPath, String fgsProvince) {
        List<MerchantExcel1> list = ExcelUtils.importExcel(excelPath, 1, 1, MerchantExcel1.class);
        if (list == null) {
            return;
        }
        list = list.stream().filter(excel -> StringUtils.isNotBlank(excel.fenGongSi)).collect(Collectors.toList());
        list = list.stream().filter(excel -> StringUtils.isNotBlank(excel.jingXiaoShangMerchantName)).collect(Collectors.toList());
        System.out.println(JSON.toJSONString(list, true));

        Map<String, List<MerchantExcel1>> indexMap = list.stream()
                .collect(Collectors.groupingBy(excel -> excel.fenGongSi + "-" + excel.fenGongSiMobile));

        transactionTemplate.executeWithoutResult(status -> {
            int count = 1;
            for (Entry<String, List<MerchantExcel1>> entry : indexMap.entrySet()) {
                String[] split = StringUtils.split(entry.getKey(), "-");
                // 分公司
                String fenGongSi = split[0];
                String fenGongSiMobile = split[1];
                Merchant fgs = new Merchant();
                fgs.setCreateTime(LocalDateTime.now());
                fgs.setUpdateTime(LocalDateTime.now());
                fgs.setOpenId(null);
                fgs.setPid(10389);
                fgs.setPMerchantName("雨帆总公司");
                fgs.setPidPath("10389." + fgs.getId() + ".");
                fgs.setRoleAlias(GroupRoleEnum.FGS.getCode());
                fgs.setRoleName(GroupRoleEnum.FGS.getDescription());
                fgs.setMerchantName(fgsProvince + "分公司" + count++);
                fgs.setMobile(fenGongSiMobile);
                fgs.setPassword(SecureUtil.md5(SecureUtil.md5("123456")));
                fgs.setContacts(fenGongSi);
                fgs.setHeadImgUrl(null);
                fgs.setProvince(fgsProvince);
                fgs.setProvinceId(15);
                fgs.setCity(null);
                fgs.setCityId(null);
                fgs.setDistrict(null);
                fgs.setDistrictId(null);
                fgs.setAddress(null);
                fgs.setSubAddress(null);
                fgs.setIsEnable("Y");
                fgs.setIsDelete("N");

                Merchant entity = new Merchant();
                try {
                    merchantMapper.insert(fgs);

                    entity.setId(fgs.getId());
                    entity.setPidPath("10389." + fgs.getId() + ".");
                    merchantMapper.updateById(entity);
                } catch (DuplicateKeyException e) {
                    Merchant merchant = merchantMapper.selectOne(Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getMobile, fenGongSiMobile));
                    if (!merchant.getRoleAlias().equals(GroupRoleEnum.FGS.getCode())) {
                        throw e;
                    }
                    fgs = merchant;
                    entity.setId(fgs.getId());
                    entity.setPidPath("10389." + fgs.getId() + ".");
                }

                // 经销商
                for (MerchantExcel1 excel : entry.getValue()) {
                    if (!StringUtils.startsWith(excel.getAddress(), fgs.getProvince())) {
                        excel.setAddress(fgs.getProvince() + excel.getAddress());
                    }

                    Map<String, String> addressResolution = AddressUtil.addressResolution(excel.getAddress());
                    String province = null;
                    String city = null;
                    String county = null;
                    String town = null;
                    if (addressResolution != null) {
                        province = addressResolution.get("province");
                        city = addressResolution.get("city");
                        county = addressResolution.get("county");
                        town = addressResolution.get("town");
                    }

                    Merchant sub = new Merchant();
                    sub.setCreateTime(LocalDateTime.now());
                    sub.setUpdateTime(LocalDateTime.now());
                    sub.setOpenId(null);
                    sub.setPid(fgs.getId());
                    sub.setPMerchantName(fgs.getMerchantName());
                    sub.setPidPath(fgs.getPidPath() + sub.getId() + ".");
                    sub.setRoleAlias(GroupRoleEnum.JXS.getCode());
                    sub.setRoleName(GroupRoleEnum.JXS.getDescription());
                    sub.setMerchantName(excel.getJingXiaoShangMerchantName());
                    sub.setMobile(excel.getMobile());
                    sub.setPassword(SecureUtil.md5(SecureUtil.md5("123456")));
                    sub.setContacts(excel.contacts);
                    sub.setHeadImgUrl(null);
                    sub.setProvince(fgs.getProvince());
                    sub.setProvinceId(fgs.getProvinceId());

                    sub.setCity(city);
                    sub.setCityId(null);
                    sub.setDistrict(county);
                    sub.setDistrictId(null);

                    sub.setAddress(town);
                    sub.setSubAddress(null);
                    sub.setIsEnable("Y");
                    sub.setIsDelete("N");
                    sub.setSettlement(excel.yingShouShangZhangPiPeiQuYu);

                    buildAddress(sub, addressResolution);
                    try {
                        merchantMapper.insert(sub);
                    } catch (DuplicateKeyException e) {
                        String errorMsg = e.getMessage();
                        logger.error("重复的经销商手机号" + errorMsg);
                        String mobile = StringUtils.substring(errorMsg,
                                StringUtils.indexOf(errorMsg, "Duplicate entry '") + "Duplicate entry '".length(),
                                StringUtils.indexOf(errorMsg, "' for key 'yf_merchant.uk_mobile'")
                        );
                        FileUtil.writeLines(new ArrayList<>(Arrays.asList(mobile)), "C:\\Users\\xulg\\Desktop\\重复手机号.txt", "utf-8", true);
                    }
                    Merchant newBean = new Merchant();
                    newBean.setId(sub.getId());
                    newBean.setPidPath(entity.getPidPath() + sub.getId() + ".");
                    merchantMapper.updateById(newBean);
                }
            }
        });
    }

    //    @Test
    public void importMerchantsFromExcel1() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\（山东）-618大促账号信息收集(2)(1)(1).xlsx";
        String province = "山东省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel2() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\海南618大促账号信息收集.xlsx";
        String province = "海南省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel3() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集（安徽）(1)(1).xlsx";
        String province = "安徽省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel4() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集（上海）.xlsx";
        String province = "上海";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel5() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\山西省618大促账号信息收集(5)(2).xlsx";
        String province = "山西省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel6() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集（重庆）(2).xlsx";
        String province = "重庆";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel7() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集(四川）4(1).xlsx";
        String province = "四川省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel8() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集（云南）(1).xlsx";
        String province = "云南省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel9() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集（陕西）(1).xlsx";
        String province = "陕西省";//error
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel10() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集（贵州）(1).xlsx";
        String province = "贵州省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel11() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集(广东)(1).xlsx";
        String province = "广东省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel12() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-04\\618大促账号信息收集（江苏）(2)(1).xlsx";
        String province = "江苏省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    //    public void importMerchantsFromExcel13() {
    //        String path1 = "f:\\618大促账号信息收集（湖北）(1).xlsx";
    //        String province = "湖北省";
    //        importMerchantsFromExcel(path1, province);
    //    }

    //@Test
    public void importMerchantsFromExcel14() {
        String path1 = "f:\\福建收集资料模板(1)(1).xlsx";
        String province = "福建省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel15() {
        String path1 = "f:\\618大促账号信息收集（湖北）(1).xlsx";
        String province = "湖北省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel16() {
        String path1 = "f:\\618大促账号信息收集-湖南(1)(1).xlsx";
        String province = "湖南省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel17() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-05\\618大促账号信息收集（江西）(1).xlsx";
        String province = "江西省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel18() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-05\\618大促账号信息收集（浙江）(1).xlsx";
        String province = "浙江省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel19() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-05\\618大促账号信息收集（陕西）(1).xlsx";
        String province = "陕西省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel20() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-05\\618大促账号信息收集(广西)(1).xlsx";
        String province = "广西省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel21() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-05\\618大促账号信息收集（江西）(2).xlsx";
        String province = "江西省";
        importMerchantsFromExcel(path1, province);
    }

    //    @Test
    public void importMerchantsFromExcel22() {
        String path1 = "C:\\Users\\xulg\\Documents\\WeChat Files\\wxid_z5mrg8zx4b3v21\\FileStorage\\File\\2021-05\\618大促账号信息收集(河南).xlsx";
        String province = "河南省";
        importMerchantsFromExcel(path1, province);
    }

    @ExcelTarget("MerchantExcel1")
    @Data
    public static class MerchantExcel1 {
        @Excel(name = "分公司/（若无分公司则填省区负责人）", width = 18)
        private String fenGongSi;

        @Excel(name = "分公司或省区负责人联系电话", width = 18)
        private String fenGongSiMobile;

        @Excel(name = "经销商商户名", width = 18)
        private String jingXiaoShangMerchantName;

        @Excel(name = "联系人", width = 18)
        private String contacts;

        @Excel(name = "联系电话", width = 18)
        private String mobile;

        @Excel(name = "省份", width = 18)
        private String province;

        @Excel(name = "地市", width = 18)
        private String city;

        @Excel(name = "详细地址", width = 18)
        private String address;

        @Excel(name = "应收上账匹配区域", width = 18)
        private String yingShouShangZhangPiPeiQuYu;
    }

    public void buildAddress(Merchant merchant, Map<String, String> addressResolution) {
        if (CollectionUtil.isNotEmpty(addressResolution)) {
            merchant.setProvince(addressResolution.get("province"));
            Region province = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                    .eq(Region::getName, addressResolution.get("province")));
            if (province != null) {
                merchant.setProvinceId(province.getId());
                Region city = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                        .eq(Region::getName, addressResolution.get("city"))
                        .eq(Region::getPid, province.getId()));
                if (city != null) {
                    merchant.setCityId(city.getId());
                    Region county = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                            .eq(Region::getName, addressResolution.get("county"))
                            .eq(Region::getPid, city.getId()));
                    if (county != null) {
                        merchant.setDistrictId(county.getId());
                    }
                }
            }
            merchant.setCity(addressResolution.get("city"));
            merchant.setDistrict(addressResolution.get("county"));
            merchant.setAddress(addressResolution.get("town"));
        }
    }

    //    @Test
    public void adasdADAD() {
        IPage<MerchantResult> merchantResultIPage = adminMerchantManageService.pageQueryMerchantsByPidAndRoleAlias(
                13000, "fgs", "北京分公司", 1, 10000);
        System.out.println(JSON.toJSONString(merchantResultIPage, true));
    }

    //    @Test
    public void dadada() {
        Page<Merchant> page = merchantMapper.selectPage(new Page<>(1, 10000),
                Wrappers.lambdaQuery(Merchant.class).eq(Merchant::getRoleAlias, "fgs")
                        .like(StringUtils.isNotBlank("北京分公司"), Merchant::getMerchantName, "北京分公司")
        );
        System.out.println(JSON.toJSONString(page, true));
    }

    @Test
    public void asa() {
        List<String> list = FileUtil.readUtf8Lines(new File("H://5.txt"));
        List<String> tag = new ArrayList<>();
        list.forEach(item -> {
            String[] s = item.split(",");
            Map<String, String> temp = BaiduMapGeocoderUtil.getAddressInfoByLngAndLat(s[1], s[2]);

            if (temp != null) {
                Region province = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                        .eq(Region::getName, temp.get("province")));
                if (province != null) {
                    temp.put("provinceId", province.getId() + "");
                    Region city = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                            .eq(Region::getName, temp.get("city"))
                            .eq(Region::getPid, province.getId()));
                    if (city != null) {
                        temp.put("cityId", city.getId() + "");
                        Region county = regionMapper.selectOne(Wrappers.<Region>lambdaQuery()
                                .eq(Region::getName, temp.get("district"))
                                .eq(Region::getPid, city.getId()));
                        if (county != null) {
                            temp.put("districtId", county.getId() + "");
                        }
                    }
                }
                tag.add(String.format("update yf_merchant m set m.province='%s',m.city='%s',m.district='%s',m.province_id=%s,m.city_id=%s,m.district_id=%s where m.id=%s;",
                        temp.get("province"), temp.get("city"), temp.get("district"), temp.get("provinceId"), temp.get("cityId"), temp.get("districtId"), s[0]
                ));
            }

        });
        FileUtil.writeLines(tag, new File("H://6.txt"), "UTF-8");
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // @Test
    public void testRedisGeo() {
        List<MerchantDetail> merchantDetails = merchantDetailMapper.selectList(null);

        GeoOperations<String, Object> opsForGeo = redisTemplate.opsForGeo();
        // 添加门店坐标信息到redis
        for (MerchantDetail merchantDetail : merchantDetails) {
            Point point = new Point(merchantDetail.getLongitude(), merchantDetail.getLatitude());
            Long count = opsForGeo.add(CacheConstants.MERCHANT_GRO_DATA, point, merchantDetail.getMerchantId());
            System.out.println(merchantDetail.getMerchantId() + "-" + count);
        }

        // 移除坐标
        Long count = redisTemplate.opsForZSet().remove(CacheConstants.MERCHANT_GRO_DATA, merchantDetails.get(0).getMerchantId());
        System.out.println(count);

        // 中心位置
        double myLongitude = 120.22403408501432D;
        double myLatitude = 30.2090974734466D;
        int radius = 100;
        int limit = 100;
        // 中心位置半径100km内的前100个门店
        Circle circle = new Circle(new Point(myLongitude, myLatitude), new Distance(radius, DistanceUnit.KILOMETERS));
        GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().includeDistance().sortAscending().limit(limit);
        GeoResults<GeoLocation<Object>> geoLocationGeoResults = opsForGeo.radius(CacheConstants.MERCHANT_GRO_DATA, circle, args);
        if (geoLocationGeoResults != null) {
            for (GeoResult<GeoLocation<Object>> locationGeoResult : geoLocationGeoResults) {
                GeoLocation<Object> content = locationGeoResult.getContent();
                // 店的坐标
                Point point = content.getPoint();
                // 距离
                Distance dist = locationGeoResult.getDistance();
                // 店id
                Object merchantId = content.getName();
                System.out.println("member=" + merchantId.toString() + "&distance=" + dist.toString() + "&coordinate=" + point.toString());
            }
        }
    }

}
