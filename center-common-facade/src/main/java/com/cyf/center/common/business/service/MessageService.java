package com.cyf.center.common.business.service;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.center.common.bean.MessageVo;

import javax.jms.Destination;
import java.util.List;
import java.util.Map;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/8 14:00
 */
public interface MessageService {
    /**
     * 发送单个消息：queue或topic
     * @param messageVo
     * @return
     */
    public BaseResponse<String> sendMessage(MessageVo messageVo);

    /**
     * 发送单个消息：queue或topic
     * @param destination
     * @return
     */
    public BaseResponse<String> sendMessage2(Destination destination, String messageJson);

    /**
     * 批量发送消息：可全部为queue，或者全部为topic，或者queue和topic混合
     * @param messageBeanList
     * @return
     */
    public BaseResponse<String> batchSendMessage(List<MessageVo> messageBeanList);

    /**
     * 发送并返回结果，可实现类似于 getUserById 这样的接口的功能，适用于系统间调用解耦，另外，如果接收端执行时间很长，将会产生超时的情况
     * @param messageBean
     * @return
     */
    public BaseResponse<String> sendAndGetSync(MessageVo messageBean, Long id);
}
