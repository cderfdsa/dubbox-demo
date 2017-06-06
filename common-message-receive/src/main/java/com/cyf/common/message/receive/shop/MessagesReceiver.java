package com.cyf.common.message.receive.shop;

import javax.jms.Destination;
import javax.jms.JMSException;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.center.common.bean.MessageVo;
import com.cyf.center.common.business.service.MessageService;
import com.cyf.base.common.bean.UserVo;
import com.cyf.base.common.mqconfig.TestMQConfig;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.shop.message.bean.MessageConsume;
import com.cyf.shop.message.service.MessageConsumeService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;

import java.util.Date;


/**
 * shop服务的消息接受器
 */
@Component
public class MessagesReceiver {
	static final LoggerWrapper LOG = LoggerWrapper.getLoggerWrapper();

	@Reference
	MessageService messageService;
	@Reference
	MessageConsumeService messageConsumeService;

	//接收queue消息
	@JmsListener(containerFactory="defaultQueueLCFactory", destination = TestMQConfig.TEST_QUEUE, subscription = "queue_receiver")
	public void receiveQueue(final Message<String> sprMessage) throws JMSException {
		LOG.info("-----------receiveQueue start------------");

		MessageVo messageVo = new MessageVo();
		messageVo.setName(TestMQConfig.TEST_QUEUE);
		messageVo.setPayload(sprMessage.getPayload());
		messageVo.setType(1);
		createMessageConsume("receiveQueue", messageVo);

		LOG.info("-----------receiveQueue end------------");
	}

	//接受queue消息并发回响应消息
	@JmsListener(containerFactory="syncQueueLCFactory", destination = TestMQConfig.SEND_AND_GET_SYNC, subscription = "queue_response_receiver")
	public void receiveAndReply(final Message<String> sprMessage, javax.jms.Message message) throws Exception {
		LOG.info("-----------receiveAndReply start------------");
		UserVo user = new UserVo();
		user.setCreateTime(new Date());
		user.setPhone("13800138000");
		user.setId(12L);
		Destination replyTo = message.getJMSReplyTo();
		String messageJson = JsonUtil.toStringWithNull(user);
		LOG.info("-----------reply to common: " + messageJson);
		messageService.sendMessage2(replyTo, messageJson);


		MessageVo messageVo = new MessageVo();
		messageVo.setName(TestMQConfig.SEND_AND_GET_SYNC);
		messageVo.setPayload(sprMessage.getPayload());
		messageVo.setType(1);
		createMessageConsume("receiveAndReply", messageVo);
		LOG.info("-----------receiveAndReply end------------");
	}

	/**--------------------------------------------这两个方法订阅同一个topic消息-------------------------------------------*/
	//接收topic消息1
	@JmsListener(containerFactory="durableTopicLCFactory", destination = TestMQConfig.TEST_TOPIC, subscription = "topic_receiver_1")
	public void receiveTopic1(final Message<String> sprMessage) throws JMSException {
		LOG.info("-----------receiveTopic1 start------------");

		MessageVo messageVo = new MessageVo();
		messageVo.setName(TestMQConfig.TEST_TOPIC);
		messageVo.setPayload(sprMessage.getPayload());
		messageVo.setType(2);
		createMessageConsume("receiveTopic1", messageVo);

		LOG.info("-----------receiveTopic1 end------------");
	}

	//接收topic消息2
	@JmsListener(containerFactory="durableTopicLCFactory", destination = TestMQConfig.TEST_TOPIC, subscription = "topic_receiver_2")
	public void receiveTopic2(final Message<String> sprMessage) throws Exception {
		LOG.info("-----------receiveTopic2 start------------");

		MessageVo messageVo = new MessageVo();
		messageVo.setName(TestMQConfig.TEST_TOPIC);
		messageVo.setPayload(sprMessage.getPayload());
		messageVo.setType(2);
		createMessageConsume("receiveTopic2", messageVo);

		//测试消息重投
//		if(1==1){
//			throw new Exception("故意失败，测试消息重投！");
//		}
		LOG.info("-----------receiveTopic2 end------------");
	}

	//接收批量发送的queue消息
	@JmsListener(containerFactory="defaultQueueLCFactory", destination = TestMQConfig.BATCH_TEST_QUEUE, subscription = "batch_queue_receiver")
	public void receiveBatchQueue(final Message<String> sprMessage) throws JMSException {
		LOG.info("-----------receiveBatchQueue start------------");

		MessageVo messageVo = new MessageVo();
		messageVo.setName(TestMQConfig.BATCH_TEST_QUEUE);
		messageVo.setPayload(sprMessage.getPayload());
		messageVo.setType(1);
		createMessageConsume("receiveBatchQueue", messageVo);

		LOG.info("-----------receiveBatchQueue end------------");
	}

	//接收批量发送的topic消息
	@JmsListener(containerFactory="durableTopicLCFactory", destination = TestMQConfig.BATCH_TEST_TOPIC, subscription = "batch_topic_receiver")
	public void receiveBatchTopic(final Message<String> sprMessage) throws JMSException {
		LOG.info("-----------receiveBatchTopic start------------");

		MessageVo messageVo = new MessageVo();
		messageVo.setName(TestMQConfig.BATCH_TEST_TOPIC);
		messageVo.setPayload(sprMessage.getPayload());
		messageVo.setType(2);
		createMessageConsume("receiveBatchTopic", messageVo);

		LOG.info("-----------receiveBatchTopic end------------");
	}

	private void createMessageConsume(String methodName, MessageVo messageVo){
		MessageConsume messageConsume = new MessageConsume();
		messageConsume.setName(messageVo.getName());
		messageConsume.setType(messageVo.getType());
		messageConsume.setPayload(messageVo.getPayload());
		messageConsume.setConsumer(methodName);
		messageConsume.setIsTimer(2);
		messageConsumeService.createMessageConsume(messageConsume);
	}
}
