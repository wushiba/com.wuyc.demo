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
        WxMenu menu = new WxMenu();

        WxMenuButton healthy = new WxMenuButton();
        healthy.setName("\uD83C\uDF39健康馆");
        WxMenuButton healthyOne = new WxMenuButton();
        healthyOne.setType(MenuButtonType.VIEW);
        healthyOne.setName("职场减压");
        healthyOne.setUrl("https://mp.weixin.qq.com/s/5YNRLv_bGqpVRCcXv8BkpQ");
        healthy.getSubButtons().add(healthyOne);

        WxMenuButton healthyTwo = new WxMenuButton();
        healthyTwo.setType(MenuButtonType.VIEW);
        healthyTwo.setName("儿童成长");
        healthyTwo.setUrl("https://mp.weixin.qq.com/s/oCwG6brmj2L7SBm85TRCfA");
        healthy.getSubButtons().add(healthyTwo);

        WxMenuButton healthyThree = new WxMenuButton();
        healthyThree.setType(MenuButtonType.VIEW);
        healthyThree.setName("孝敬爸妈");
        healthyThree.setUrl("https://mp.weixin.qq.com/s/qifIGtBqE6_L8SuZ8Iu34Q");
        healthy.getSubButtons().add(healthyThree);

        WxMenuButton action = new WxMenuButton();
        healthy.setName("\uD83D\uDD25618活动");

        WxMenuButton actionOne = new WxMenuButton();
        actionOne.setType(MenuButtonType.VIEW);
        actionOne.setName("个人中心");
        actionOne.setUrl("https://m.yufanlook.com/#/MyPage");
        action.getSubButtons().add(actionOne);

        WxMenuButton actionTwo = new WxMenuButton();
        actionTwo.setType(MenuButtonType.VIEW);
        actionTwo.setName("\uD83C\uDF81父亲节99元");
        actionTwo.setUrl("https://m.yufanlook.com/#/ActPage");
        action.getSubButtons().add(actionTwo);

        WxMenuButton actionThree = new WxMenuButton();
        actionThree.setType(MenuButtonType.VIEW);
        actionThree.setName("雨帆商城");
        actionThree.setUrl("https://m.yufanlook.com/#/allPage");
        action.getSubButtons().add(actionThree);


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
        kfThree.setUrl("https://merchant.yufanlook.com/#/MerchantLogin");
        kf.getSubButtons().add(kfThree);


        menu.getButtons().add(healthy);
        menu.getButtons().add(action);
        menu.getButtons().add(kf);


        this.wxService.switchover(appid);
        return this.wxService.getMenuService().menuCreate(menu);
    }


    /**
     * <pre>
     * 自定义菜单删除接口
     * 详情请见: https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421141015&token=&lang=zh_CN
     * </pre>
     */
    @GetMapping("/delete/{pwd}")
    public void menuDelete(@PathVariable String pwd) throws WxErrorException {
        Asserts.assertEquals("64293481", pwd, 500, "无效的请求");
        String appid = wxMpProperties.getConfigs().get(0).getAppId();
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        this.wxService.switchoverTo(appid).getMenuService().menuDelete();
    }


}
