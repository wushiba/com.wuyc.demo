package com.yfshop.common.plug;

public class PlugBg {
    private String bg;//图片的地址
    private int width;//图片的宽带
    private int height;//图片的高度
    private String isRoundPic;//是否圆角
    private int radius = 2;
    private String small;

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getIsRoundPic() {
        return isRoundPic;
    }

    public void setIsRoundPic(String isRoundPic) {
        this.isRoundPic = isRoundPic;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.bg);
        sb.append(String.format("?imageView2/1/w/%d/h/%d", width, height));
        if ("Y".equals(this.isRoundPic)) {
            sb.append(String.format("|roundPic/radius/!%dp", this.radius));
        }
        return sb.toString();
    }
}

