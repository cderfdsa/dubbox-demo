package com.cyf.center.common.business.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.center.common.business.component.mq.MessageSender;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.center.common.bean.MessageVo;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.base.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import java.util.List;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/8 14:01
 */
@Service
public class MessageServiceImpl implements MessageService {
    static final LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(MessageServiceImpl.class);

    @Autowired
    MessageSender messageSender;
    /**
     * 发送单个消息：queue或topic
     * @param messageVo
     * @return
     */
    public BaseResponse<String> sendMessage(MessageVo messageVo){
        BaseResponse response;
        if((response = checkMessageBean(messageVo)).isError()){
            return response;
        }

        boolean result = false;
        if(messageVo.isQueue()){
            result = messageSender.sendQueue(messageVo.getName(), messageVo.getPayload());
        }else if(messageVo.isTopic()){
            result = messageSender.sendTopic(messageVo.getName(), messageVo.getPayload());
        }

        if(result){
            response = BaseResponse.success();
        }else{
            response = BaseResponse.fail("发送消息失败！");
        }

        return response;
    }

    /**
     * 发送单个消息：queue或topic
     * @param destination
     * @return
     */
    public BaseResponse<String> sendMessage2(Destination destination, String messageJson){
        if(destination == null){
            return BaseResponse.fail("请指定要发送消息目的地");
        }

        try{
            if(destination instanceof Queue){
                messageSender.sendQueue(((Queue) destination).getQueueName(), messageJson);
            }else if(destination instanceof Topic){
                messageSender.sendQueue(((Topic) destination).getTopicName(), messageJson);
            }
            return BaseResponse.success();
        }catch (Exception e){
            return BaseResponse.fail(e.getMessage());
        }
    }

    /**
     * 批量发送消息：可全部为queue，或者全部为topic，或者queue和topic混合
     * @param messageBeanList
     * @return
     */
    public BaseResponse<String> batchSendMessage(List<MessageVo> messageBeanList){
        if(messageBeanList==null || messageBeanList.isEmpty()){
            return BaseResponse.fail("请指定要发送的消息");
        }

        BaseResponse response;
        for (MessageVo bean : messageBeanList){
            response = checkMessageBean(bean);
            if(response.isError()){
                return response;
            }
        }

        boolean result = messageSender.sendBatch(messageBeanList);
        if(result){
            response = BaseResponse.success();
        }else{
            response = BaseResponse.fail("批量发送消息失败！");
        }

        return response;
    }

    /**
     * 发送并返回结果，适用于跨系统调用(类似RPC功能)，如果接收端执行时间很长，将会产生超时的情况
     * @param messageBean
     * @return
     */
    public BaseResponse<String> sendAndGetSync(MessageVo messageBean, Long id){
        BaseResponse response = checkMessageBean(messageBean);
        if(response.isError()){
            return response;
        }

        response =  messageSender.sendAndGetSync(messageBean.getName(), messageBean.getPayload());
        return response;
    }

    /**
     * 私有方法，检查MessageBean里面的属性值是否符合要求
     * @param messageBean
     * @return
     */
    private BaseResponse<String> checkMessageBean(MessageVo messageBean){
        if(messageBean == null){
            return BaseResponse.fail("传递的消息对象不能为空！");
        }else if(! (messageBean.isQueue() || messageBean.isTopic())){
            return BaseResponse.fail("请指定要发送的消息类型时queue或者topic");
        }else if(StringUtil.isEmpty(messageBean.getName())){
            return BaseResponse.fail("请指定要发送的消息的名称");
        }
        return BaseResponse.success();
    }
}
