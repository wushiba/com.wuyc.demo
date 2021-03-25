package com.yfshop.common.plug;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlugPictureUtil {
    public static String compose(PlugBg bg, List<PlugWatermark> watermarkList, List<PlugText> textList) {
        List<String> list = new ArrayList<>();

        if (watermarkList != null && watermarkList.size() > 0) {
            for (PlugWatermark item : watermarkList) {
                list.add(item.toString());
            }
        }
        if (textList != null && textList.size() > 0) {
            for (PlugText item : textList) {
                list.add(item.toString());
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(bg);
        if (list.size() > 0) {
            sb.append("|watermark/3");
            for (String str : list) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String url = "https://www.baidu.com";
        String erurl = "http://qr.topscan.com/api.php?w=185&text=" + url;
        System.out.println(t(erurl));
    }

    public static String picToBase64(String imageUrl) {
        //new一个URL对象
        try {
            URL url = new URL(imageUrl);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(60 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = IoUtil.readBytes(inStream);
            //new一个文件对象用来保存图片，默认保存当前工程根目录
            return cn.hutool.core.codec.Base64.encode(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String packageStr(String str, String headImage, String nickname) {
        PlugBg bg = new PlugBg();
        bg.setBg("https://c9.51jujibao.com/upload/2019/12/03/201912032132329761");//背景图片地址
        bg.setWidth(668);
        bg.setHeight(1091);
        bg.setIsRoundPic("N");

        PlugWatermark watermark = new PlugWatermark();
        PlugImage image = new PlugImage();
        image.setSrc(str);//二维码图片地址
        image.setWidth(126);
        image.setHeight(126);
        watermark.setImage(image);
        watermark.setGravity(PlugLocation.SouthEast);
        watermark.setDy(85);
        watermark.setDx(39);

        PlugWatermark watermark1 = new PlugWatermark();
        PlugImage image1 = new PlugImage();
        image1.setSrc(headImage);//二维码图片地址
        image1.setWidth(80);
        image1.setHeight(80);
        image1.setIsRoundPic("Y");
        image1.setRadius(50);
        watermark1.setImage(image1);
        watermark1.setGravity(PlugLocation.NorthWest);
        watermark1.setDy(24);
        watermark1.setDx(24);

        PlugText plugText = new PlugText();
        plugText.setText(nickname);
        plugText.setFontsize(28);
        plugText.setFill("#444444");
        plugText.setGravity(PlugLocation.NorthWest);
        plugText.setDx(125);
        plugText.setDy(30);
        plugText.setLineHeight(0.0D);
        List<PlugText> textList = new ArrayList<>();
        textList.add(plugText);
        List<PlugWatermark> watermarkList = new ArrayList<PlugWatermark>();
        watermarkList.add(watermark);
        watermarkList.add(watermark1);
        String ret = PlugPictureUtil.compose(bg, watermarkList, textList);
        System.out.println(ret);
        return "data:image/jpg;base64," + picToBase64(ret);
    }


    public static String packageStr2(String str, String headImage, String nickname) {
        PlugBg bg = new PlugBg();
        bg.setBg("https://c9.51jujibao.com/upload/2019/12/31/201912312235589821");//背景图片地址
        bg.setWidth(668);
        bg.setHeight(1091);
        bg.setIsRoundPic("N");

        PlugWatermark watermark = new PlugWatermark();
        PlugImage image = new PlugImage();
        image.setSrc(str);//二维码图片地址
        image.setWidth(126);
        image.setHeight(126);
        watermark.setImage(image);
        watermark.setGravity(PlugLocation.SouthEast);
        watermark.setDy(85);
        watermark.setDx(39);

        PlugWatermark watermark1 = new PlugWatermark();
        PlugImage image1 = new PlugImage();
        image1.setSrc(headImage);//二维码图片地址
        image1.setWidth(80);
        image1.setHeight(80);
        image1.setIsRoundPic("Y");
        image1.setRadius(50);
        watermark1.setImage(image1);
        watermark1.setGravity(PlugLocation.NorthWest);
        watermark1.setDy(25);
        watermark1.setDx(24);

        PlugText plugText = new PlugText();
        plugText.setText(nickname);
        plugText.setFontsize(28);
        plugText.setFill("#444444");
        plugText.setGravity(PlugLocation.NorthWest);
        plugText.setDx(125);
        plugText.setDy(50);
        plugText.setLineHeight(0.0D);
        List<PlugText> textList = new ArrayList<>();
        textList.add(plugText);
        List<PlugWatermark> watermarkList = new ArrayList<PlugWatermark>();
        watermarkList.add(watermark);
        watermarkList.add(watermark1);
        String ret = PlugPictureUtil.compose(bg, watermarkList, textList);
        System.out.println(ret);
        return "data:image/jpg;base64," + picToBase64(ret);
    }


    public static String ltPackageStr(String headImage, String nickname) {
        PlugBg bg = new PlugBg();
        bg.setBg("https://c9.51jujibao.com/upload/2019/12/12/201912121518119988");//背景图片地址
        bg.setWidth(660);
        bg.setHeight(1091);
        bg.setIsRoundPic("N");

        PlugWatermark watermark1 = new PlugWatermark();
        PlugImage image1 = new PlugImage();
        image1.setSrc(headImage);//二维码图片地址
        image1.setWidth(80);
        image1.setHeight(80);
        image1.setIsRoundPic("Y");
        image1.setRadius(50);
        watermark1.setImage(image1);
        watermark1.setGravity(PlugLocation.NorthWest);
        watermark1.setDy(24);
        watermark1.setDx(24);

        PlugText plugText = new PlugText();
        plugText.setText(nickname);
        plugText.setFontsize(28);
        plugText.setFill("#444444");
        plugText.setGravity(PlugLocation.NorthWest);
        plugText.setDx(123);
        plugText.setDy(30);
        plugText.setLineHeight(0.0D);
        List<PlugText> textList = new ArrayList<>();
        textList.add(plugText);
        List<PlugWatermark> watermarkList = new ArrayList<PlugWatermark>();
        watermarkList.add(watermark1);
        String ret = PlugPictureUtil.compose(bg, watermarkList, textList);
        System.out.println(ret);
        return "data:image/jpg;base64," + picToBase64(ret);
    }

    public static String ltPackageStr2(String headImage, String nickname) {
        PlugBg bg = new PlugBg();
        bg.setBg("https://c9.51jujibao.com/upload/2019/12/31/201912312239419472");//背景图片地址
        bg.setWidth(660);
        bg.setHeight(1091);
        bg.setIsRoundPic("N");

        PlugWatermark watermark1 = new PlugWatermark();
        PlugImage image1 = new PlugImage();
        image1.setSrc(headImage);//二维码图片地址
        image1.setWidth(80);
        image1.setHeight(80);
        image1.setIsRoundPic("Y");
        image1.setRadius(50);
        watermark1.setImage(image1);
        watermark1.setGravity(PlugLocation.NorthWest);
        watermark1.setDy(25);
        watermark1.setDx(24);

        PlugText plugText = new PlugText();
        plugText.setText(nickname);
        plugText.setFontsize(28);
        plugText.setFill("#444444");
        plugText.setGravity(PlugLocation.NorthWest);
        plugText.setDx(123);
        plugText.setDy(50);
        plugText.setLineHeight(0.0D);
        List<PlugText> textList = new ArrayList<>();
        textList.add(plugText);
        List<PlugWatermark> watermarkList = new ArrayList<PlugWatermark>();
        watermarkList.add(watermark1);
        String ret = PlugPictureUtil.compose(bg, watermarkList, textList);
        System.out.println(ret);
        return "data:image/jpg;base64," + picToBase64(ret);
    }


    public static String t(String str) {
        PlugBg bg = new PlugBg();
        bg.setBg("https://c9.51jujibao.com/upload/2019/08/29/201908290928549048");//背景图片地址
        bg.setWidth(686);
        bg.setHeight(960);
        bg.setIsRoundPic("N");

        PlugWatermark watermark = new PlugWatermark();
        PlugImage image = new PlugImage();
        image.setSrc(str);//二维码图片地址
        image.setWidth(150);
        image.setHeight(150);
        watermark.setImage(image);
        watermark.setGravity(PlugLocation.South);
        watermark.setDy(340);
        watermark.setDx(0);

        PlugWatermark watermark1 = new PlugWatermark();
        PlugImage image1 = new PlugImage();
        image1.setSrc("https://c9.51jujibao.com/upload/2019/08/29/201908290929229688");//二维码图片地址
        image1.setWidth(210);
        image1.setHeight(210);
        watermark1.setImage(image1);
        watermark1.setGravity(PlugLocation.South);
        watermark1.setDy(332);
        watermark1.setDx(0);
        List<PlugWatermark> watermarkList = new ArrayList<PlugWatermark>();
        watermarkList.add(watermark);
        watermarkList.add(watermark1);
        String ret = PlugPictureUtil.compose(bg, watermarkList, null);
        System.out.println(ret);
        return "data:image/jpg;base64," + picToBase64(ret);
    }


    public static String share(String str) {
        PlugBg bg = new PlugBg();
        bg.setBg("https://c9.51jujibao.com/upload/2019/12/31/201912312238159648");//背景图片地址
        bg.setWidth(750);
        bg.setHeight(1206);
        bg.setIsRoundPic("N");

        PlugWatermark watermark = new PlugWatermark();
        PlugImage image = new PlugImage();
        image.setSrc(str);//二维码图片地址
        image.setWidth(159);
        image.setHeight(159);
        watermark.setImage(image);
        watermark.setGravity(PlugLocation.SouthEast);
        watermark.setDy(80);
        watermark.setDx(55);
        List<PlugWatermark> watermarkList = new ArrayList<PlugWatermark>();
        watermarkList.add(watermark);
        String ret = PlugPictureUtil.compose(bg, watermarkList, null);
        System.out.println(ret);
        return "data:image/jpg;base64," + picToBase64(ret);
    }


    /**
     * 生成二维码
     *
     * @return 二维码图片地址
     */
    public static String makeQrCode(String content, int width, int height) throws WriterException, IOException {
        String format = "png";// 图像类型
        Map<EncodeHintType, Object> hints = new HashMap<>(3);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //容错率
        hints.put(EncodeHintType.MARGIN, 0);  //二维码边框宽度，这里文档说设置0-4，但是设置后没有效果，不知原因，
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
        // 文件信息
        String dest = UUID.randomUUID().toString(true) + ".jpg";
        String date = DateTime.now().toString("yyyyMMdd");
        File dir = new File("/mnt/static/images/" + date);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdir();
        }
        File file = new File("/mnt/static/images/" + date + "/" + dest);
        MatrixToImageWriter.writeToFile(bitMatrix, format, file);
        String url = "https://c1.51jujibao.com/images/" + date + "/" + dest;
        return url;
    }
}
