package com.yfshop.admin.controller;

import com.yfshop.wx.api.service.MpService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Controller
class IndexController{
    @DubboReference
    MpService mpService;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping("/index.html/**")
    public ModelAndView adminIndex() {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping("/test")
    public String pay() {
        List<WxMpTemplateData> data =new ArrayList<>();
        data.add(new WxMpTemplateData("first","您有新的门店自取订单，请及时处理~"));
        data.add(new WxMpTemplateData("keyword1","1212121"));
        data.add(new WxMpTemplateData("keyword1","2元"));
        data.add(new WxMpTemplateData("remark","请核对好用户信息，避免错拿商品。"));
        WxMpTemplateMessage wxMpTemplateMessage=WxMpTemplateMessage.builder()
                .templateId("kEnXD9LGvWpcWud99dUu_A85vc5w1vT9-rMzqybrQaw")
                .toUser("o3vDm6TQEJn4BsPB3xi5p4EXvSHo")
                .data(data)
                .url("http://prev-merchant.yufan.51jujibao.com/#/MerchantBooking")
        .build();
        try {
            mpService.sendWxMpTemplateMsg(wxMpTemplateMessage);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return "1";
    }

}