package com.yfshop.wx.controller;

import com.yfshop.common.exception.Asserts;
import com.yfshop.wx.config.WxMpProperties;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpGetSelfMenuInfoResult;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

import static me.chanjar.weixin.common.api.WxConsts.MenuButtonType;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@AllArgsConstructor
@RestController
@RequestMapping("/wx/menu/")
public class WxMenuController {
    private final WxMpService wxService;
    private final WxMpProperties wxMpProperties;

    /**
     * <pre>
     * 自定义菜单创建接口
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141013&token=&lang=zh_CN
     * 如果要创建个性化菜单，请设置matchrule属性
     * 详情请见：https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1455782296&token=&lang=zh_CN
     * </pre>
     *
     * @return 如果是个性化菜单，则返回menuid，否则返回null
     */

    @GetMapping("/create/{pwd}")
    //@PathVariable String appid
    public String menuCreateSample(@PathVariable String pwd) throws WxErrorException {
        Asserts.assertEquals("64293481", pwd, 500, "无效的请求");
        String appid = wxMpProperties.getConfigs().get(0).getAppId();
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
//        WxMenu menu = new WxMenu();
//
//        WxMenuButton shop = new WxMenuButton();
//        shop.setName("商城");
//
//        WxMenuButton introduce = new WxMenuButton();//跳转
//        introduce.setType(MenuButtonType.VIEW);
//        introduce.setName("进入商城");
//        introduce.setUrl("https://shop.yufanlook.com/#/ActPage");
//
//        WxMenuButton bindPhone = new WxMenuButton();
//        bindPhone.setType(MenuButtonType.VIEW);
//        bindPhone.setName("个人中心");
//        bindPhone.setUrl("https://shop.yufanlook.com/#/MyPage");
//
//        shop.getSubButtons().add(introduce);
//        shop.getSubButtons().add(bindPhone);
//        menu.getButtons().add(shop);
//
//
//        WxMenuButton button3 = new WxMenuButton();
//        button3.setName("活动");
//
//        WxMenuButton order = new WxMenuButton();
//        order.setType(MenuButtonType.VIEW);
//        order.setName("噜鹿相遇");
//        order.setUrl("https://shop.yufanlook.com/#/LuckDrawPageForWx");
//
//        button3.getSubButtons().add(order);
//        menu.getButtons().add(button3);
//
//        WxMenuButton zc = new WxMenuButton();
//        zc.setName("联系我们");
//
//        WxMenuButton kfdh = new WxMenuButton();//跳转
//        kfdh.setType(MenuButtonType.CLICK);
//        kfdh.setName("客服电话");
//        kfdh.setKey("lxwm");
//
//        WxMenuButton shdl = new WxMenuButton();
//        shdl.setType(MenuButtonType.VIEW);
//        shdl.setName("商户登录");
//        shdl.setUrl("https://merchant.yufanlook.com/#/MerchantLogin");
//
//        zc.getSubButtons().add(kfdh);
//        zc.getSubButtons().add(shdl);
//        menu.getButtons().add(zc);


        WxMenu menu = new WxMenu();

        WxMenuButton healthy = new WxMenuButton();
        healthy.setType(MenuButtonType.VIEW);
        healthy.setName("我的奖券");
        healthy.setUrl("https://shop.yufanlook.com/#/MyPage");

        WxMenuButton action = new WxMenuButton();
        action.setName("进入商城");
        action.setUrl("https://shop.yufanlook.com/#/allPage?id=3");

//        WxMenuButton actionOne = new WxMenuButton();
//        actionOne.setType(MenuButtonType.VIEW);
//        actionOne.setName("健康商城");
//        actionOne.setUrl("https://shop.yufanlook.com/#/allPage");
//        action.getSubButtons().add(actionOne);
//
//        WxMenuButton actionTwo = new WxMenuButton();
//        actionTwo.setType(MenuButtonType.VIEW);
//        actionTwo.setName("到家火锅");
//        actionTwo.setUrl("https://shop.yufanlook.com/#/allPage?id=3");
//        action.getSubButtons().add(actionTwo);
//
//        WxMenuButton actionThree = new WxMenuButton();
//        actionThree.setType(MenuButtonType.VIEW);
//        actionThree.setName("5折好货");
//        actionThree.setUrl("https://shop.yufanlook.com/#/ActPage");
//        action.getSubButtons().add(actionThree);

        WxMenuButton kf = new WxMenuButton();
        kf.setName("客服中心");

        WxMenuButton kfOne = new WxMenuButton();
        kfOne.setType(MenuButtonType.VIEW);
        kfOne.setName("在线客服");
        kfOne.setUrl("https://tb.53kf.com/code/wx/10187208/5");
        kf.getSubButtons().add(kfOne);

        WxMenuButton kfTwo = new WxMenuButton();
        kfTwo.setType(MenuButtonType.CLICK);
        kfTwo.setName("400客服");
        kfTwo.setKey("lxwm");
        kf.getSubButtons().add(kfTwo);

        WxMenuButton kfThree = new WxMenuButton();
        kfThree.setType(MenuButtonType.VIEW);
        kfThree.setName("商户登录");
        kfThree.setUrl("https://b.yufanlook.com/#/MerchantLogin");
        kf.getSubButtons().add(kfThree);

        WxMenuButton kfFour = new WxMenuButton();
        kfFour.setType(MenuButtonType.VIEW);
        kfFour.setName("积分兑换");
        kfFour.setUrl("https://m.jf.10086.cn/?keyword=%E5%99%9C%E6%B8%B4&WT.ac_id=210730_LK_prom_h5_code1#/pages/goodslist/index?keyWord=%E5%99%9C%E6%B8%B4");
        kf.getSubButtons().add(kfFour);

//        menu.getButtons().add(healthy);
        menu.getButtons().add(action);
        menu.getButtons().add(kf);

        this.wxService.switchover(appid);
        return this.wxService.getMenuService().menuCreate(menu);
    }

//
//    /**
//     * <pre>
//     * 自定义菜单删除接口
//     * 详情请见: https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141015&token=&lang=zh_CN
//     * </pre>
//     */
//    @GetMapping("/delete/{pwd}")
//    public void menuDelete(@PathVariable String pwd) throws WxErrorException {
//        Asserts.assertEquals("64293481", pwd, 500, "无效的请求");
//        String appid = wxMpProperties.getConfigs().get(0).getAppId();
//        if (!this.wxService.switchover(appid)) {
//            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
//        }
//        this.wxService.switchoverTo(appid).getMenuService().menuDelete();
//    }


}
