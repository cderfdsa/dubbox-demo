package dubbo.spring.javaconfig;

import com.cyf.base.common.utils.SystemUtil;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.DeliveryMode;
import javax.jms.Session;

/**
 * 一、消息配置：
 * 	 1.公共配置：
 * 	 	ActiveMQConnectionFactory	真正建立跟ActiveMQ连接的连接工厂
 * 	 	MessageConverter			发送、接收消息时的消息转换器
 * 	 	JmsTemplate					发送、接收消息的对象
 *
 * 二、注意说明：
 *   1、为保证消息不丢失，所有发送出去的消息都定义为持久化消息
 *   2、如果消费者有集群，那么监听queue类型的消息必须设置为自动确认，因为避免同一条消息被多个消费者消费
 *   3、不管是JmsTemplate还是JmsListener，实例化Connection的任务都是由ConnectionFactory完成的，而在JmsListener设置的clientId最终肯定是传递给了ConnectionFactory的，
 *      而ConnectionFactory在创建Connection的时候再把clientId传递给Connection，所以，如果要追查clientId怎么设置的，最终要追查到ConnectionFactory的实现类才行
 *
 * @return
 */
@Configuration
@EnableJms //只有在需要作为消息消费者时才需要配置此注解
@Import(EnvConfig.class)
public class MessagingConfig {
	private static final String CLIENT_ID_PREFIX = SystemUtil.getLocalIp() + "_";//clientId的前缀，方便知道是哪台服务器
	@Autowired
	EnvConfig envConfig;

	/**——————————————公用配置 START————————————————*/
	//真正创建跟MQ连接的工厂类
	@Bean
	public ActiveMQConnectionFactory amqConnectionFactory(){
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(envConfig.amqBrokerUrl);
		return connectionFactory;
	}
	/**
	 * 默认的消息转换器
	 * @return
	 */
	@Bean
	public MessageConverter defaultJmsMessageConverter(){
		SimpleMessageConverter converter = new SimpleMessageConverter();//已经能满足大部分的对象转换：String、byte[]、Map、Serializable、javax.jms.Message
		return converter;
	}
	/**
	 * 可共享的Spring连接工厂，是spring用来管理各种ConnectionFactory实现类的
	 * @return
	 */
	@Bean
	public CachingConnectionFactory shareConnectionFactory(){
		//内部使用了连接池，提高效率和性能
		CachingConnectionFactory connectionFactoryPool = new CachingConnectionFactory();
		connectionFactoryPool.setTargetConnectionFactory(amqConnectionFactory());
		return connectionFactoryPool;
	}
	/**——————————————公用配置 END————————————————*/




	/**——————————————消息发送配置 START————————————————*/
	/**
	 * ActiveMQ为我们提供了一个PooledConnectionFactory，通过往里面注入一个ActiveMQConnectionFactory
	 * 可以用来将Connection、Session和MessageProducer池化，这样可以大大的减少我们的资源消耗。 要依赖于 activemq-pool2 包
	 * 主要用以发送mq消息
	 * PooledConnectionFactory、CachingConnectionFactory 选择请参考：
	 * http://stackoverflow.com/questions/19560479/which-is-better-pooledconnectionfactory-or-cachingconnectionfactory
	 * @return
	 */
	@Bean
	public JmsTemplate queueJmsTemplate(){
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(shareConnectionFactory());
		template.setDeliveryPersistent(true);//发送的消息为持久化消息
		template.setMessageConverter(defaultJmsMessageConverter());
		template.setDefaultDestinationName(envConfig.defaultQueue);
		return template;
	}
	@Bean
	public JmsTemplate topicJmsTemplate(){
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(shareConnectionFactory());
		template.setPubSubDomain(true);//true表明是topic类型的消息
		template.setMessageConverter(defaultJmsMessageConverter());
		template.setDeliveryPersistent(true);//发送的消息为持久化消息
		template.setDefaultDestinationName(envConfig.defaultTopic);
		return template;
	}
	/**——————————————消息发送配置 END————————————————*/




	/**——————————————消息监听/接收配置 START————————————————*/
	/**
	 * 默认的queue监听器工厂
	 * @return
	 */
	@Bean
	public DefaultJmsListenerContainerFactory defaultQueueLCFactory(){
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(amqConnectionFactory());
		factory.setMessageConverter(defaultJmsMessageConverter());
		//设置消息签收启用事务管理模式，即消费方在收到消息并进行处理的过程中如果抛出了异常，则这个消息会被重新投递
		// 如果没出现异常，则spring会在接收消息的方法返回之后自动签收消息
		factory.setSessionTransacted(true);
		return factory;
 	}
	/**
	 * 默认的topic监听器工厂
	 * @return
	 */
	@Bean
	public DefaultJmsListenerContainerFactory defaultTopicLCFactory(){
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(amqConnectionFactory());
		factory.setPubSubDomain(true);//设置为topic类型
		factory.setMessageConverter(defaultJmsMessageConverter());
		factory.setSessionTransacted(true);
		factory.setSubscriptionDurable(false);//非持久订阅
//		factory.setSubscriptionShared(false);//要求Broker支持JMS 2.0规范，而ActiveMQ仅支持JMS 1.1，所以不能开启
		return factory;
	}
	/**——————————————消息监听/接收配置 END————————————————*/


	/**——————————————同步发/收消息配置 START————————————————*/
	@Bean
	public JmsTemplate syncQueueJmsTemplate(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setTargetConnectionFactory(amqConnectionFactory());//直接使用ActiveMQ的连接工厂
		connectionFactory.setCacheConsumers(false);//不缓存生产者
		connectionFactory.setCacheProducers(false);//不缓存消费者等
		connectionFactory.setSessionCacheSize(50);//设置Session的缓存数量

		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory);
		template.setPubSubDomain(false);
		template.setMessageConverter(defaultJmsMessageConverter());
		template.setDeliveryPersistent(false);//不启用事务
		template.setDeliveryMode(DeliveryMode.NON_PERSISTENT);//发送非持久化消息
		template.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);//设置自动签收消息
		return template;
	}
	/**——————————————同步发/收消息配置 END————————————————*/
	
}
