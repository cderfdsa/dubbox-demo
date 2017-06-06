package com.cyf.test.bean;

import java.io.Serializable;

/**
 * Created by chenyf on 2017/3/9.
 */
public class UserBean implements Serializable{
    private Long id;
    private String name;
    private int gender;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
