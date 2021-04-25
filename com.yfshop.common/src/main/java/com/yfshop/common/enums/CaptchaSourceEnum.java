package com.yfshop.common.enums;

public enum CaptchaSourceEnum {

    LOGIN_CAPTCHA("LOGIN_CAPTCHA", 5,10,"【雨帆健康家】验证码：%s，五分钟内有效","雨帆健康家登录验证码");

    private String source;

    //单位分钟
    private int expireDuration;

    private int toDayLimit;

    private String smsTemplate;

    private String mark;

    CaptchaSourceEnum(String source, int expireDuration, int toDayLimit, String smsTemplate, String mark) {
        this.source = source;
        this.expireDuration = expireDuration;
        this.toDayLimit = toDayLimit;
        this.smsTemplate = smsTemplate;
        this.mark = mark;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getExpireDuration() {
        return expireDuration;
    }

    public void setExpireDuration(int expireDuration) {
        this.expireDuration = expireDuration;
    }

    public int getToDayLimit() {
        return toDayLimit;
    }

    public void setToDayLimit(int toDayLimit) {
        this.toDayLimit = toDayLimit;
    }
    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public static CaptchaSourceEnum getByCode(String code) {
        for (CaptchaSourceEnum afe : values()) {
            if (afe.getSource().equals(code)) {
                return afe;
            }
        }
        return null;
    }


}