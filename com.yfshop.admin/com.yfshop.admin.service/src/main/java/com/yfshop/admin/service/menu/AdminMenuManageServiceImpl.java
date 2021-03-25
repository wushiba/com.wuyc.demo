package com.yfshop.admin.service.menu;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.admin.api.menu.AdminMenuManageService;
import com.yfshop.admin.api.menu.result.MenuResult;
import com.yfshop.code.mapper.MenuMapper;
import com.yfshop.code.mapper.MerchantMapper;
import com.yfshop.code.model.Menu;
import com.yfshop.code.model.Merchant;
import com.yfshop.common.constants.CacheConstants;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.Cacheable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-23 16:44
 */
@DubboService
public class AdminMenuManageServiceImpl implements AdminMenuManageService {

    private static final NodeParser<Menu, String> NODE_PARSER = (menu, tree) -> {
        tree.setId(menu.getMenuAlias());
        tree.setParentId(menu.getParentMenuAlias());
        tree.setName(menu.getMenuName());
        tree.setWeight(menu.getSort());
        tree.putExtra("id", menu.getId());
        tree.putExtra("linkUrl", menu.getLinkUrl());
        tree.putExtra("menuIcon", menu.getMenuIcon());
        tree.putExtra("roleAlias", menu.getRoleAlias());
    };

    @Resource
    private MerchantMapper merchantMapper;
    @Resource
    private MenuMapper menuMapper;

    @Cacheable(cacheManager = CacheConstants.CACHE_MANAGE_NAME,
            cacheNames = CacheConstants.MERCHANT_MENUS_CACHE_NAME,
            key = "'" + CacheConstants.MERCHANT_MENUS_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Override
    public List<MenuResult> queryMerchantMenus(Integer merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null || merchant.getRoleAlias() == null) {
            return new ArrayList<>(0);
        }
        List<Menu> menus = menuMapper.selectList(Wrappers.lambdaQuery(Menu.class)
                .eq(Menu::getRoleAlias, merchant.getRoleAlias()));
        return buildMenu(menus);
    }

    private List<MenuResult> buildMenu(List<Menu> menus) {
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 主键字段
        treeNodeConfig.setIdKey("menuAlias");
        // 排序字段
        treeNodeConfig.setWeightKey("sort");
        // 父级ID字段
        treeNodeConfig.setParentIdKey("parentMenuAlias");
        // 节点名称
        treeNodeConfig.setNameKey("menuName");
        // 子名称
        treeNodeConfig.setChildrenKey("subMenus");
        List<Tree<String>> trees = TreeUtil.build(menus, null, treeNodeConfig, NODE_PARSER);
        return JSON.parseArray(JSON.toJSONString(trees), MenuResult.class);
    }

    public static void main(String[] args) {
        String jsonStr = "[{\n" +
                "\t\"id\": 1,\n" +
                "\t\"name\": \"H5定制化\",\n" +
                "\t\"icon\": \"el-icon-box\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_page\",\n" +
                "\t\"sort\": 1,\n" +
                "\t\"linkUrl\": \"/h5\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 1,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/h5\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"页面模板\",\n" +
                "\t\t\"permission\": \"mod_page_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 2\n" +
                "\t}]\n" +
                "}, {\n" +
                "\t\"id\": 3,\n" +
                "\t\"name\": \"权益会员管理\",\n" +
                "\t\"icon\": \"el-icon-user\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_user\",\n" +
                "\t\"sort\": 2,\n" +
                "\t\"linkUrl\": \"/vipPrice\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/vipRecord\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"会员订单记录\",\n" +
                "\t\t\"permission\": \"user_order_manager\",\n" +
                "\t\t\"sort\": 1,\n" +
                "\t\t\"id\": 6\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/VipApplyImport\",\n" +
                "\t\t\"icon\": \"el-icon-user\",\n" +
                "\t\t\"name\": \"会员导入\",\n" +
                "\t\t\"permission\": \"member_user_application\",\n" +
                "\t\t\"sort\": 1,\n" +
                "\t\t\"id\": 211\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/VipApplyAudit\",\n" +
                "\t\t\"icon\": \"el-icon-user\",\n" +
                "\t\t\"name\": \"会员导入审核\",\n" +
                "\t\t\"permission\": \"member_user_application_super\",\n" +
                "\t\t\"sort\": 1,\n" +
                "\t\t\"id\": 213\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/vipUser\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"会员用户\",\n" +
                "\t\t\"permission\": \"user_manager\",\n" +
                "\t\t\"sort\": 2,\n" +
                "\t\t\"id\": 5\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/vipRedeemCode\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"兑换码管理\",\n" +
                "\t\t\"permission\": \"mod_ticket\",\n" +
                "\t\t\"sort\": 3,\n" +
                "\t\t\"id\": 103\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/vipPrice\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"会员结算价\",\n" +
                "\t\t\"permission\": \"user_card_open_manager\",\n" +
                "\t\t\"sort\": 4,\n" +
                "\t\t\"id\": 4\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/spVipRecord\",\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"name\": \"SP业务订单记录\",\n" +
                "\t\t\"permission\": \"admin_sp\",\n" +
                "\t\t\"sort\": 4,\n" +
                "\t\t\"id\": 195\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 3,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/spVipReportForm\",\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"name\": \"SP业务对账表\",\n" +
                "\t\t\"permission\": \"admin_sp_reconciliation\",\n" +
                "\t\t\"sort\": 4,\n" +
                "\t\t\"id\": 224\n" +
                "\t}]\n" +
                "}, {\n" +
                "\t\"id\": 7,\n" +
                "\t\"name\": \"卡券库\",\n" +
                "\t\"icon\": \"el-icon-picture\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_coupon\",\n" +
                "\t\"sort\": 3,\n" +
                "\t\"linkUrl\": \"/CardManage\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 7,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/CardManage\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"卡券管理\",\n" +
                "\t\t\"permission\": \"coupon_manager\",\n" +
                "\t\t\"sort\": 1,\n" +
                "\t\t\"id\": 8\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 7,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/GoodLibray\",\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"name\": \"卡券详情\",\n" +
                "\t\t\"permission\": \"coupon_details_manager\",\n" +
                "\t\t\"sort\": 2,\n" +
                "\t\t\"id\": 9\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 7,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/SupplierManage\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"供应商管理\",\n" +
                "\t\t\"permission\": \"coupon_provider_manager\",\n" +
                "\t\t\"sort\": 3,\n" +
                "\t\t\"id\": 10\n" +
                "\t}]\n" +
                "}, {\n" +
                "\t\"id\": 123,\n" +
                "\t\"name\": \"本地权益库\",\n" +
                "\t\"icon\": \"el-icon-s-ticket\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"management_cash_ticket\",\n" +
                "\t\"sort\": 4,\n" +
                "\t\"linkUrl\": \"/merchantCouponCheck\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantCouponCheck\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"商品券审核\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 1,\n" +
                "\t\t\"id\": 125\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/localGoods\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"本地商品\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 2,\n" +
                "\t\t\"id\": 234\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantVerificateRecord\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"券码核销记录\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 3,\n" +
                "\t\t\"id\": 129\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantDiscoutQy\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"会员码折扣设置\",\n" +
                "\t\t\"permission\": \"management_cash_discount_opt\",\n" +
                "\t\t\"sort\": 4,\n" +
                "\t\t\"id\": 215\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantDiscoutUseRecord\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"会员码使用记录\",\n" +
                "\t\t\"permission\": \"management_cash_discount_opt\",\n" +
                "\t\t\"sort\": 5,\n" +
                "\t\t\"id\": 217\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/storeClassification\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"门店类目\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 6,\n" +
                "\t\t\"id\": 233\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantStoreManage\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"门店管理\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 7,\n" +
                "\t\t\"id\": 131\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/consumeCouponSetting\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"消费券门店审核\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 7,\n" +
                "\t\t\"id\": 241\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantStoreAccountRecord\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"门店存款记录\",\n" +
                "\t\t\"permission\": \"management_cash_discount_opt\",\n" +
                "\t\t\"sort\": 8,\n" +
                "\t\t\"id\": 219\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantStorePrevAccount\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"门店预存款\",\n" +
                "\t\t\"permission\": \"management_cash_discount_opt\",\n" +
                "\t\t\"sort\": 9,\n" +
                "\t\t\"id\": 221\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 123,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/withdrawalList\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"提现列表\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 10,\n" +
                "\t\t\"id\": 165\n" +
                "\t}]\n" +
                "}, {\n" +
                "\t\"id\": 11,\n" +
                "\t\"name\": \"权益库\",\n" +
                "\t\"icon\": \"el-icon-menu\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_equity\",\n" +
                "\t\"sort\": 5,\n" +
                "\t\"linkUrl\": \"/EquittClass\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 11,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/EquittClass\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"权益类目\",\n" +
                "\t\t\"permission\": \"category_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 12\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 11,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/Brand\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"权益品牌\",\n" +
                "\t\t\"permission\": \"item_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 13\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 11,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/EquityGoods\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"权益商品\",\n" +
                "\t\t\"permission\": \"sku_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 14\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 11,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/Provider\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"供应商管理\",\n" +
                "\t\t\"permission\": \"provider_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 15\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 11,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/Storeroom\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"供应商商品库\",\n" +
                "\t\t\"permission\": \"provider_item_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 16\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 11,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/SKEquityGoods\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"秒杀权益商品\",\n" +
                "\t\t\"permission\": \"admin_seckill\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 173\n" +
                "\t}]\n" +
                "}, {\n" +
                "\t\"id\": 17,\n" +
                "\t\"name\": \"订单数据\",\n" +
                "\t\"icon\": \"el-icon-document-copy\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_order\",\n" +
                "\t\"sort\": 8,\n" +
                "\t\"linkUrl\": \"/order\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 17,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/order\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"商品订单数据\",\n" +
                "\t\t\"permission\": \"order_manager\",\n" +
                "\t\t\"sort\": 1,\n" +
                "\t\t\"id\": 18\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 17,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/orderVoucherCode\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"订单券码编号\",\n" +
                "\t\t\"permission\": \"order_manager\",\n" +
                "\t\t\"sort\": 2,\n" +
                "\t\t\"id\": 145\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 17,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/dataGeneration\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"订单数据2\",\n" +
                "\t\t\"permission\": \"order_manager\",\n" +
                "\t\t\"sort\": 3,\n" +
                "\t\t\"id\": 243\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 89,\n" +
                "\t\"name\": \"外部回收卡券\",\n" +
                "\t\"icon\": \"el-icon-picture-outline-round\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_sys_recycle\",\n" +
                "\t\"sort\": 9,\n" +
                "\t\"linkUrl\": \"/recycleCoupon\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 89,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/recycleCoupon\",\n" +
                "\t\t\"icon\": \"el-icon-picture-outline-round\",\n" +
                "\t\t\"name\": \"外部回收卡券\",\n" +
                "\t\t\"permission\": \"sys_recycle_brand\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 90\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 89,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/recycleCheck\",\n" +
                "\t\t\"icon\": \"el-icon-picture-outline-round\",\n" +
                "\t\t\"name\": \"外部卡券审核\",\n" +
                "\t\t\"permission\": \"sys_recycle_batch\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 91\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 89,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/recycleDetail\",\n" +
                "\t\t\"icon\": \"el-icon-picture-outline-round\",\n" +
                "\t\t\"name\": \"外部卡券详情\",\n" +
                "\t\t\"permission\": \"sys_recycle_batch\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 93\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 89,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/recycleOrder\",\n" +
                "\t\t\"icon\": \"el-icon-picture-outline-round\",\n" +
                "\t\t\"name\": \"外部卡券订单\",\n" +
                "\t\t\"permission\": \"sys_recycle_order\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 95\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 89,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/recycleRecord\",\n" +
                "\t\t\"icon\": \"el-icon-picture-outline-round\",\n" +
                "\t\t\"name\": \"外部卡券结算记录\",\n" +
                "\t\t\"permission\": \"sys_recycle_withdraw\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 97\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 89,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/recycleProvider\",\n" +
                "\t\t\"icon\": \"el-icon-picture-outline-round\",\n" +
                "\t\t\"name\": \"外部卡券供应商管理\",\n" +
                "\t\t\"permission\": \"sys_recycle_merchant\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 99\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 133,\n" +
                "\t\"name\": \"网站预约使用\",\n" +
                "\t\"icon\": \"el-icon-edit-outline\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_website\",\n" +
                "\t\"sort\": 10,\n" +
                "\t\"linkUrl\": \"/appointmentRecord\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 133,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/appointmentRecord\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"网站预约使用\",\n" +
                "\t\t\"permission\": \"mod_website_manager\",\n" +
                "\t\t\"sort\": 0,\n" +
                "\t\t\"id\": 135\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 19,\n" +
                "\t\"name\": \"商户管理\",\n" +
                "\t\"icon\": \"el-icon-bank-card\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_merchant\",\n" +
                "\t\"sort\": 11,\n" +
                "\t\"linkUrl\": \"/merchantManage\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 19,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantManage\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"商户管理\",\n" +
                "\t\t\"permission\": \"merchant_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 20\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 19,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantDeposit\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"商户预存\",\n" +
                "\t\t\"permission\": \"prestore_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 21\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 19,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/merchantRecord\",\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"name\": \"商户预存记录\",\n" +
                "\t\t\"permission\": \"prestore_log_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 22\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 23,\n" +
                "\t\"name\": \"权限管理\",\n" +
                "\t\"icon\": \"el-icon-setting\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_persmission\",\n" +
                "\t\"sort\": 12,\n" +
                "\t\"linkUrl\": \"/control\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 23,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/control\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"角色管理\",\n" +
                "\t\t\"permission\": \"role_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 24\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 23,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/controlUser\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"用户管理\",\n" +
                "\t\t\"permission\": \"role_user_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 25\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 189,\n" +
                "\t\"name\": \"消息通知\",\n" +
                "\t\"icon\": \"el-icon-bell\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_admin_mnotify_manager\",\n" +
                "\t\"sort\": 999,\n" +
                "\t\"linkUrl\": \"\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 189,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/ContactsManage\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"联系人管理\",\n" +
                "\t\t\"permission\": \"admin_mnotify_sms_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 191\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 189,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/MessageTemplate\",\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"name\": \"短信模板\",\n" +
                "\t\t\"permission\": \"admin_mnotify_sms_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 193\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 197,\n" +
                "\t\"name\": \"财务报表\",\n" +
                "\t\"icon\": \"el-icon-bell\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_day_order\",\n" +
                "\t\"sort\": 999,\n" +
                "\t\"linkUrl\": \"/OtherCollect\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 197,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/OtherCollect\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"财务报表\",\n" +
                "\t\t\"permission\": \"day_order_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 199\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 201,\n" +
                "\t\"name\": \"营销中心\",\n" +
                "\t\"icon\": \"el-icon-bell\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"mod_redBag\",\n" +
                "\t\"sort\": 999,\n" +
                "\t\"linkUrl\": \"/marketCenter\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 201,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/marketCenter\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"营销中心\",\n" +
                "\t\t\"permission\": \"red_bag_manager\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 203\n" +
                "\t}]\n" +
                "},\n" +
                "\n" +
                "\n" +
                "{\n" +
                "\t\"id\": 209,\n" +
                "\t\"name\": \"券码库\",\n" +
                "\t\"icon\": \"el-icon-bell\",\n" +
                "\t\"parentMenuId\": 0,\n" +
                "\t\"permission\": \"management_gift_package\",\n" +
                "\t\"sort\": 999,\n" +
                "\t\"linkUrl\": \"/couponCode\",\n" +
                "\t\"children\": [{\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/couponCode\",\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"name\": \"券码管理\",\n" +
                "\t\t\"permission\": \"management_gift_package_ticket\",\n" +
                "\t\t\"sort\": 1,\n" +
                "\t\t\"id\": 107\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/sendCouponSmsRecord\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"发放记录\",\n" +
                "\t\t\"permission\": \"management_package_opt\",\n" +
                "\t\t\"sort\": 2,\n" +
                "\t\t\"id\": 238\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/offlineMacketing\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"线下扫码营销\",\n" +
                "\t\t\"permission\": \"management_package_opt\",\n" +
                "\t\t\"sort\": 2,\n" +
                "\t\t\"id\": 244\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/consumerVoucher\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"消费券管理\",\n" +
                "\t\t\"permission\": \"management_package_opt\",\n" +
                "\t\t\"sort\": 3,\n" +
                "\t\t\"id\": 239\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/releaseRecordVoucher\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"消费券发放记录\",\n" +
                "\t\t\"permission\": \"management_package_opt\",\n" +
                "\t\t\"sort\": 4,\n" +
                "\t\t\"id\": 240\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"merchandiseVouchers\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"商品券兑换管理\",\n" +
                "\t\t\"permission\": \"management_cash_ticket_opt\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 242\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/voucherPackageManagement\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"券包管理\",\n" +
                "\t\t\"permission\": \"management_package_opt\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 245\n" +
                "\t}, {\n" +
                "\t\t\"parentMenuId\": 209,\n" +
                "\t\t\"children\": null,\n" +
                "\t\t\"linkUrl\": \"/voucherPackageUsage\",\n" +
                "\t\t\"icon\": null,\n" +
                "\t\t\"name\": \"券包使用情况\",\n" +
                "\t\t\"permission\": \"management_package_opt\",\n" +
                "\t\t\"sort\": 999,\n" +
                "\t\t\"id\": 246\n" +
                "\t}]\n" +
                "}\n" +
                "\n" +
                "]";

        List<Data> list = new ArrayList<>();
        JSONArray jsonArray = JSON.parseArray(jsonStr);
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            Data data = new Data();
            data.setId(jsonObject.getInteger("id"));
            data.setName(jsonObject.getString("name"));
            data.setParentMenuId(jsonObject.getInteger("parentMenuId"));
            data.setSort(jsonObject.getInteger("sort"));
            data.setLinkUrl(jsonObject.getString("linkUrl"));
            data.setIcon(jsonObject.getString("icon"));
            list.add(data);

            JSONArray children = jsonObject.getJSONArray("children");
            if (children != null) {
                for (Object child : children) {
                    JSONObject childObject = (JSONObject) child;
                    Data subData = new Data();
                    subData.setId(childObject.getInteger("id"));
                    subData.setName(childObject.getString("name"));
                    subData.setParentMenuId(childObject.getInteger("parentMenuId"));
                    subData.setSort(childObject.getInteger("sort"));
                    subData.setLinkUrl(childObject.getString("linkUrl"));
                    subData.setIcon(childObject.getString("icon"));
                    list.add(subData);
                }
            }
        }

        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 主键字段
        treeNodeConfig.setIdKey("id");
        // 排序字段
        treeNodeConfig.setWeightKey("sort");
        // 父级ID字段
        treeNodeConfig.setParentIdKey("parentMenuId");
        treeNodeConfig.setNameKey("name");
        treeNodeConfig.setChildrenKey("subMenus");
        List<Tree<Integer>> trees = TreeUtil.build(list, 0, treeNodeConfig,
                new NodeParser<Data, Integer>() {
                    @Override
                    public void parse(Data data, Tree<Integer> treeNode) {
                        treeNode.setId(data.getId());
                        treeNode.setParentId(data.getParentMenuId());
                        treeNode.setName(data.getName());
                        treeNode.setWeight(data.getSort());
                        treeNode.putExtra("icon", data.getIcon());
                        treeNode.putExtra("linkUrl", data.getLinkUrl());
                    }
                });

        System.out.println(JSON.toJSONString(trees, true));
    }

    @lombok.Data
    private static class Data {
        Integer id;
        String name;
        Integer parentMenuId;
        Integer sort;
        String linkUrl;
        String icon;
    }
}
