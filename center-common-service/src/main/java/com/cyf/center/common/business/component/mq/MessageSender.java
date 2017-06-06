package com.cyf.center.common.business.component.mq;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.center.common.bean.MessageVo;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.base.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageSender {
	private static LoggerWrapper logger = LoggerWrapper.getLoggerWrapper();

	@Autowired
	JmsTemplate queueJmsTemplate;
    @Autowired
    JmsTemplate topicJmsTemplate;
    @Autowired
	JmsTemplate syncQueueJmsTemplate;

	/**
	 * 发送queue消息
	 * 注意：所有消息都会被转成Json字符串发送，所以接收端接收到的将会是json字符串
	 * @param queueName
	 * @param messageJson
	 */
	public boolean sendQueue(String queueName, final String messageJson){
		try{
			queueJmsTemplate.convertAndSend(queueName, setNullToEmptyString(messageJson));
			return true;
		}catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
	}

	/**
	 * 发送topic消息
	 * 注意：所有消息都会被转成Json字符串发送，所以接收端接收到的将会是json字符串
	 * @param topicName
	 * @param messageJson
	 */
	public boolean sendTopic(String topicName, final String messageJson){
		try{
			topicJmsTemplate.convertAndSend(topicName, setNullToEmptyString(messageJson));
			return true;
		}catch(Exception e){
			logger.error(e.getMessage());
			return false;
		}
	}

	private String setNullToEmptyString(String messageJson){
		if(messageJson == null) messageJson = "";
		return messageJson;
	}

	/**
	 * 批量发送消息,请确保每一个queue/topic的name不为空
	 * @param beanList
	 * @return
	 * @throws Exception
	 */
	public boolean sendBatch(List<MessageVo> beanList){
		boolean isAtLeastSendOneMessage = false;//至少有发送一条消息成功
		Session session= null;

		try{
			Connection connection = queueJmsTemplate.getConnectionFactory().createConnection();//也可使用topicJmsTemplate获取连接工厂
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);//一提交就会自动确认发送成功

			//封装生产者、消息体
			Map<MessageProducer, TextMessage> producerMapMessage = new HashMap<>();
			for(MessageVo bean : beanList){
				Destination destination;
				if(bean.isTopic()){
					destination = session.createTopic(bean.getName());
				}else{
					destination = session.createQueue(bean.getName());
				}
				TextMessage textMessage = session.createTextMessage(bean.getPayload());
				MessageProducer producer = session.createProducer(destination);
				producerMapMessage.put(producer, textMessage);
			}

			//遍历生产者并发送消息
			for(Map.Entry<MessageProducer, TextMessage> entry : producerMapMessage.entrySet()){
				entry.getKey().send(entry.getValue());
				isAtLeastSendOneMessage = true;
			}

			session.commit();//提交事务

			return true;
		}catch(Exception e){
			logger.error(e.getMessage());
			if(session != null && isAtLeastSendOneMessage){
				try{
					session.rollback();
				}catch (Exception ex){
					logger.error("rollback fail ！"+ex.getMessage());//回滚失败，可以不处理，等到过期之后，MQ服务器会自动清理没提交的消息
				}
			}
			return false;
		}
	}


	/**
	 * 发送消息并获取对方返回的结果，返回结果是Json字符串
	 * @param queueName
	 * @param messageJson
	 * @return
	 */
	public BaseResponse sendAndGetSync(String queueName, final String messageJson){
		try{
			//TODO 由于ActiveMQ性能不够好，吞吐量不够高，建议采用RabbitMQ或RocketMQ等性能比较高的MQ产品替代
			TextMessage textMessage = (TextMessage) syncQueueJmsTemplate.sendAndReceive(queueName, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return syncQueueJmsTemplate.getMessageConverter().toMessage(messageJson, syncQueueJmsTemplate.getConnectionFactory().createConnection().
							createSession(false, Session.AUTO_ACKNOWLEDGE));
				}
			});

			String messageReturn = textMessage.getText();
			return BaseResponse.success(messageReturn, StringUtil.isEmpty(messageReturn) ? 0 : 1);
		}catch(Exception e){
			return BaseResponse.fail(e.getMessage());
		}
	}


}
