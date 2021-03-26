package com.yfshop.common.plug;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-24 19:05
 */
class Demo {

    public static void main(String[] args) {
        //String qrCodeUrl = "http://qr.topscan.com/api.php?w=186&m=0&text=1232312313131";
        String qrCodeUrl = "https://www.liantu.com/images/2013/weixin.png";
        String headImage = "https://c9.51jujibao.com/upload/2019/11/29/201911291118309346";
        String nickname = "嘻嘻哈哈";
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            try {
                String composePic = composePic1(qrCodeUrl, headImage, nickname);
                System.out.println(composePic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.err.println(System.currentTimeMillis() - start);
        System.out.println();
        try {
            String composePic = composePic2(qrCodeUrl, headImage, nickname);
            System.out.println(composePic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String composePic1(String qrCodeUrl, String headImage, String nickname) {
        // 背景图
        String backgroundImage = "https://c9.51jujibao.com/youPlus/2020/06/30/17/45/331593510333";

        PlugBg bg = new PlugBg();
        // 背景图片地址
        bg.setBg(backgroundImage);
        bg.setWidth(660);
        bg.setHeight(1060);
        bg.setIsRoundPic("N");

        PlugWatermark watermark = new PlugWatermark();
        PlugImage image = new PlugImage();
        // 分享地址二维码图片
        image.setSrc(qrCodeUrl);
        image.setWidth(186);
        image.setHeight(186);
        watermark.setImage(image);
        watermark.setGravity(PlugLocation.SouthEast);
        watermark.setDy(92);
        watermark.setDx(236);

        PlugWatermark watermark1 = new PlugWatermark();
        PlugImage image1 = new PlugImage();
        image1.setSrc(headImage);
        image1.setWidth(80);
        image1.setHeight(80);
        image1.setIsRoundPic("Y");
        image1.setRadius(50);
        watermark1.setImage(image1);
        watermark1.setGravity(PlugLocation.NorthWest);
        watermark1.setDy(25);
        watermark1.setDx(19);

        PlugText plugText = new PlugText();
        // 用户昵称
        plugText.setText(nickname);
        plugText.setFontsize(36);
        plugText.setFill("#444444");
        plugText.setGravity(PlugLocation.NorthWest);
        plugText.setDx(126);
        plugText.setDy(30);
        plugText.setLineHeight(0.0D);

        List<PlugText> textList = new ArrayList<>();
        textList.add(plugText);
        List<PlugWatermark> watermarkList = new ArrayList<>();
        watermarkList.add(watermark);
        watermarkList.add(watermark1);
        String ret = PlugPictureUtil.compose(bg, watermarkList, textList);
        System.out.println(ret);
        return "data:image/jpg;base64," + PlugPictureUtil.picToBase64(ret);
    }

    private static String composePic2(String qrCodeUrl, String headImage, String nickname) {
        String backgroundImage = "https://c9.51jujibao.com/upload/2020/02/19/202002191737519325";
        PlugBg bg = new PlugBg();
        // 背景图片地址
        bg.setBg(backgroundImage);
        bg.setWidth(668);
        bg.setHeight(1091);
        bg.setIsRoundPic("N");

        PlugWatermark watermark = new PlugWatermark();
        PlugImage image = new PlugImage();
        // 二维码图片地址
        image.setSrc(qrCodeUrl);
        image.setWidth(126);
        image.setHeight(126);
        watermark.setImage(image);
        watermark.setGravity(PlugLocation.SouthEast);
        watermark.setDy(82);
        watermark.setDx(47);

        PlugWatermark watermark1 = new PlugWatermark();
        PlugImage image1 = new PlugImage();
        image1.setSrc(headImage);
        image1.setWidth(80);
        image1.setHeight(80);
        image1.setIsRoundPic("Y");
        image1.setRadius(50);
        watermark1.setImage(image1);
        watermark1.setGravity(PlugLocation.NorthWest);
        watermark1.setDy(25);
        watermark1.setDx(39);

        PlugText plugText = new PlugText();
        plugText.setText(nickname);
        plugText.setFontsize(36);
        plugText.setFill("#444444");
        plugText.setGravity(PlugLocation.NorthWest);
        plugText.setDx(138);
        plugText.setDy(30);
        plugText.setLineHeight(0.0D);

        List<PlugText> textList = new ArrayList<>();
        textList.add(plugText);
        List<PlugWatermark> watermarkList = new ArrayList<PlugWatermark>();
        watermarkList.add(watermark);
        watermarkList.add(watermark1);
        String ret = PlugPictureUtil.compose(bg, watermarkList, textList);
        System.out.println(ret);
        return "data:image/jpg;base64," + PlugPictureUtil.picToBase64(ret);
    }

}
