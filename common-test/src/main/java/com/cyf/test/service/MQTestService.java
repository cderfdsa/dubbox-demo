package com.cyf.test.service;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/7 10:07
 */
public interface MQTestService {

    //测试发送queue类型的MQ消息
    public void callSendMQQueue();

    //测试发送topic类型的MQ消息
    public void callSendMQTopic();

    //测试批量发送MQ消息(混合和queue和topic)
    public void callMQBatchSend();

    public void callSendAnGetSync();
}
