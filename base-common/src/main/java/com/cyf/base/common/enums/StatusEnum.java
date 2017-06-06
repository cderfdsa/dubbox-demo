package com.cyf.base.common.enums;

/**
 * Created by chenyf on 2017/4/9.
 */
public enum StatusEnum {
    ACTIVE(1, "有效"),
    INACTIVE(2, "无效");

    private int value;
    private String desc;

    private StatusEnum(int value, String desc){
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
