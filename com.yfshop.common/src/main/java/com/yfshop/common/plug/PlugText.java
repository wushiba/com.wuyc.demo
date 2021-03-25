package com.yfshop.common.plug;

import java.math.BigDecimal;


public class PlugText {
    private String text;
    private PlugFont font = PlugFont.Yahei;
    private int fontsize = 240;
    private String fill = "#000000";//水印文字颜色，RGB格式，可以是颜色名称（例如 red）或十六进制（例如 #FF0000），参考RGB颜色编码表，默认为黑色。经过URL安全的Base64编码。
    private int dissolve = 100;//透明度，取值范围1-100，默认值100（完全不透明）。
    private PlugLocation gravity;//水印位置
    private int dx = 10;//横轴边距，单位:像素(px)，默认值为10。
    private int dy = 10;//纵轴边距，单位:像素(px)，默认值为10。
    private double lineHeight = 2.0;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PlugFont getFont() {
        return font;
    }

    public void setFont(PlugFont font) {
        this.font = font;
    }

    public int getFontsize() {
        return fontsize;
    }

    public void setFontsize(int fontsize) {
        this.fontsize = fontsize;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public int getDissolve() {
        return dissolve;
    }

    public void setDissolve(int dissolve) {
        this.dissolve = dissolve;
    }

    public PlugLocation getGravity() {
        return gravity;
    }

    public void setGravity(PlugLocation gravity) {
        this.gravity = gravity;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public double getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String[] textArr = this.text.split("\\$\\$");
        int modY = 0;
        for (int i = 0; i < textArr.length; i++) {
            modY = new BigDecimal("" + this.fontsize).multiply(new BigDecimal("" + this.lineHeight)).multiply(new BigDecimal(i)).intValue();
            sb.append("/text/" + encode(textArr[i].getBytes()));
            sb.append("/font/" + encode(this.font.getName().getBytes()));
            sb.append("/fontsize/" + this.fontsize * 15);
            sb.append("/fill/" + encode(this.fill.getBytes()));
            sb.append("/dissolve/" + this.dissolve);
            sb.append("/gravity/" + this.gravity.getCode());
            sb.append("/dx/" + this.dx);
            sb.append("/dy/" + (this.dy + modY));
        }
        return sb.toString();
    }

    public static String encode(final byte[] bytes) {
        return cn.hutool.core.codec.Base64.encode(bytes).replaceAll("\\+", "-").replaceAll("/", "_");
    }
}
