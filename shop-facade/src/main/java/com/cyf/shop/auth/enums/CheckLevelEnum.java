package com.cyf.shop.auth.enums;

/**
 * Created by chenyf on 2017/4/11.
 */
public enum CheckLevelEnum {
    GUEST(1, "游客可访问"),
    LOGIN(2, "登陆可访问"),
    AUTHORIZE(3, "授权可访问");

    private int value;
    private String desc;

    private CheckLevelEnum(int value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
