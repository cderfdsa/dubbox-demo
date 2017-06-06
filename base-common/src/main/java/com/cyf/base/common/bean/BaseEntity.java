package com.cyf.base.common.bean;

/**
 * Created by chenyf on 2017/5/29.
 */
public class BaseEntity<PK> implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    private PK id;//主键

    public PK getId(){
        return id;
    }
}
