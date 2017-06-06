package com.cyf.test.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.center.common.business.service.MessageService;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.center.common.bean.MessageVo;
import com.cyf.base.common.mqconfig.TestMQConfig;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.test.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/7 9:07
 */
@Service
public class MQTestServiceImpl implements MQTestService{
    static LoggerWrapper logger = LoggerWrapper.getLoggerWrapper();

    @Reference
    MessageService messageService;

    //发送一个queue消息
    public void callSendMQQueue(){
        logger.info("-----callSendMQQueue start-----");
        MessageVo bean = new MessageVo();
        bean.setToQueue();
        bean.setName(TestMQConfig.TEST_QUEUE);
        bean.setPayload("a queue test");
        messageService.sendMessage(bean);
        logger.info("-----callSendMQQueue end-----");
    }

    //发送一个topic消息
    public void callSendMQTopic(){
        logger.info("-----callSendMQTopic start-----");
        MessageVo bean = new MessageVo();
        bean.setToTopic();
        bean.setName(TestMQConfig.TEST_TOPIC);
        bean.setPayload("a topic test");
        messageService.sendMessage(bean);

        bean.setName(TestMQConfig.TEST_TOPIC_2);
        bean.setPayload("a topic 2 test");
        messageService.sendMessage(bean);
        logger.info("-----callSendMQTopic end-----");
    }

    //批量发送queue、topic的混合消息
    public void callMQBatchSend(){
        logger.info("-----callMQBatchSend start-----");
        List<MessageVo> beanList = new ArrayList<>();
        for(int i=0; i<10000; i++){
            MessageVo bean = new MessageVo();
            if(i < 0){
                bean.setToQueue();
//                bean.setName(TestMQConfig.BATCH_TEST_QUEUE+"."+i);
                bean.setName(TestMQConfig.TEST_QUEUE);
                bean.setPayload("a batch queue test "+i);
            }else{
                bean.setToTopic();
//                bean.setName(TestMQConfig.BATCH_TEST_TOPIC+"."+i);
                bean.setName(TestMQConfig.TEST_TOPIC);
                bean.setPayload("a batch topic test "+i);
            }
            beanList.add(bean);
        }

        messageService.batchSendMessage(beanList);
        logger.info("-----callMQBatchSend end-----");
    }

    //发送消息并等待响应
    public void callSendAnGetSync(){
        logger.info("-----callSendAnGetSync start-----");
        UserBean userBean = new UserBean();
        userBean.setId(100l);
        userBean.setName("张三");
        userBean.setGender(1);

        MessageVo messageBean = new MessageVo();
        messageBean.setName(TestMQConfig.SEND_AND_GET_SYNC);
        messageBean.setToQueue();
        messageBean.setPayload(JsonUtil.toString(userBean));
        BaseResponse<String> response = messageService.sendAndGetSync(messageBean, Long.valueOf(1));
        System.out.println("data："+response.getData());
        System.out.println("message："+response.getMessage());
        logger.info("-----callSendAnGetSync end-----");
    }
}
