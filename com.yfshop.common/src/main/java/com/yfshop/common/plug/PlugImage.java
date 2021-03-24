package com.yfshop.common.plug;

public class PlugImage {
    private String src;//图片的地址
    private int width;//图片的宽带
    private int height;//图片的高度
    private String isRoundPic;//是否圆角
    private int radius = 50;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.src);
        if (this.src.contains("51jujibao.com")) {
            if (this.src.contains("c9.51jujibao.com")) {
                sb.append(String.format("?imageView2/1/w/%d/h/%d", width, height));

                if ("Y".equals(this.isRoundPic)) {
                    sb.append(String.format("|roundPic/radius/!%dp", this.radius));
                }
            }
        }
        return sb.toString();
    }
}

