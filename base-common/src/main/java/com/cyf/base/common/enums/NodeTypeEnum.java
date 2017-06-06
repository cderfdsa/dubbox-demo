package com.cyf.base.common.enums;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/4/17 12:40
 */
public enum NodeTypeEnum {
    MENU(1, "菜单"),
    ELEMENT(1, "元素，ajax等");

    private int value;
    private String desc;

    private NodeTypeEnum(int value, String desc){
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
