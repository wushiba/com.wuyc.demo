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
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.model.Item;
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

@SpringBootTest
public class AdminServiceApplicationTests {

    @DubboReference(check = false)
    private AdminMallManageService adminMallManageService;
    @Resource
    private AdminMallManageService adminMallManageService2;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ApplicationContext applicationContext;

    @Test
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

    @Test
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

}
