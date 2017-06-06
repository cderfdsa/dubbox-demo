package com.cyf.base.common.enums;

/**
 * Created by chenyf on 2017/4/9.
 */
public enum DeleteEnum {
    UNDELETED(0, "未删除"),
    DELETED(1, "删除");

    private int value;
    private String desc;

    private DeleteEnum(int value, String desc){
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
