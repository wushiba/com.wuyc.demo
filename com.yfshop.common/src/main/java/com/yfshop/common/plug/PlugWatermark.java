package com.yfshop.common.plug;

import java.util.Base64;

public class PlugWatermark {
    private PlugImage image;
    private int dissolve = 100;//透明度，取值范围1-100，默认值为100（完全不透明）。
    private PlugLocation gravity = PlugLocation.Center;//水印位置
    private int dx = 10;//横轴边距，单位:像素(px)，默认值为10。
    private int dy = 10;//纵轴边距，单位:像素(px)，默认值为10。

    public PlugImage getImage() {
        return image;
    }

    public void setImage(PlugImage image) {
        this.image = image;
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("/image/" + encode(this.image.toString().getBytes()));
        sb.append("/dissolve/" + this.dissolve);
        sb.append("/gravity/" + this.gravity.getCode());
        sb.append("/dx/" + this.dx);
        sb.append("/dy/" + this.dy);
        return sb.toString();
    }


    public static String encode(final byte[] bytes) {
        return cn.hutool.core.codec.Base64.encode(bytes).replaceAll("\\+", "-").replaceAll("/", "_");
    }
}

