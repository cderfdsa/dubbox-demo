package com.cyf.center.common.bean;

import java.io.Serializable;

/**
 * 消息vo
 * Created by chenyf on 2017/3/6.
 */
public class MessageVo implements Serializable{
    public final static int QUEUE_TYPE = 1;
    public final static int TOPIC_TYPE = 2;

    //queue或topic的名称
    private String name;

    //消息的类型：queue或topic
    private int type;

    //消息内容
    private String payload;

    private String messageDesc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getMessageDesc() {
        return messageDesc;
    }

    public void setMessageDesc(String messageDesc) {
        this.messageDesc = messageDesc;
    }

    public void setToQueue(){
        this.type = QUEUE_TYPE;
    }
    public void setToTopic(){
        this.type = TOPIC_TYPE;
    }

    public boolean isQueue(){
        return this.type == QUEUE_TYPE;
    }

    public boolean isTopic(){
        return this.type == TOPIC_TYPE;
    }

    public static MessageVo getQueue(){
        MessageVo messageBean = new MessageVo();
        messageBean.setToQueue();
        return messageBean;
    }
    public static MessageVo getTopic(){
        MessageVo messageBean = new MessageVo();
        messageBean.setToTopic();
        return messageBean;
    }
}
