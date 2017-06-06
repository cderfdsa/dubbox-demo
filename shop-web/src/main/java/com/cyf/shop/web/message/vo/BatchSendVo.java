package com.cyf.shop.web.message.vo;

/**
 * Created by Administrator on 2017/5/3.
 */
public class BatchSendVo {
    private String queueName;
    private String queuePayload;
    private Integer queueNumber;
    private String topicName;
    private String topicPayload;
    private Integer topicNumber;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueuePayload() {
        return queuePayload;
    }

    public void setQueuePayload(String queuePayload) {
        this.queuePayload = queuePayload;
    }

    public Integer getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(Integer queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicPayload() {
        return topicPayload;
    }

    public void setTopicPayload(String topicPayload) {
        this.topicPayload = topicPayload;
    }

    public Integer getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(Integer topicNumber) {
        this.topicNumber = topicNumber;
    }
}
