package dubbo.spring.javaconfig;

import com.cyf.base.common.utils.SystemUtil;
import com.cyf.common.message.receive.factory.DurableDefaultJmsListenerContainerFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import javax.jms.Session;

/**
 * 消息配置
 * 1.发送消息需要用到 ActiveMQConnectionFactory、JmsTemplate
 * 2.接收消息需要用到 ActiveMQConnectionFactory、DefaultJmsListenerContainerFactory
 */
@Configuration
@EnableJms //只有当作为消息消费者时才需要配置此注解,有了此注解，spring才会去处理@JmsListener注解的方法
@Import(EnvConfig.class)
public class MessagingConfig {
	@Autowired
	EnvConfig envConfig;


	/**--------------------------------------------------------------- 公用配置 START --------------------------------------------------------------*/
	//真正创建跟MQ连接的工厂类
	@Bean
	public ActiveMQConnectionFactory amqConnectionFactory(){
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(envConfig.amqBrokerUrl);
		connectionFactory.setConnectionIDPrefix(SystemUtil.getLocalIp());//connectionId的前缀，方便知道是哪台服务器
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
	/**------------------------------------------------------------- 公用配置 END ------------------------------------------------------------------*/



	/**------------------------------------------------------------- 消息监听/接收配置 START -------------------------------------------------------*/
	/**
	 * 默认queue监听器工厂
	 * @return
	 */
	@Bean
	public DefaultJmsListenerContainerFactory defaultQueueLCFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(shareConnectionFactory());
		factory.setMessageConverter(defaultJmsMessageConverter());
		factory.setSessionTransacted(true);//设置消息签收启用事务管理模式，即消费方在收到消息并进行处理的过成功如果出现了异常，则会消息重投
		return factory;
	}
	/**
	 * 默认的topic监听器工厂：非持久化订阅、事务管理模式的消息签收
	 * @return
	 */
	@Bean
	public DefaultJmsListenerContainerFactory defaultTopicLCFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		//官方文档不建议使用任何缓存工厂类(如：CachingConnectionFactory、PooledConnectionFactory)，最好是让
		// DefaultJmsListenerContainerFactory自己决定要怎样缓存，setCacheLevel 可以设置缓存策略
		factory.setConnectionFactory(amqConnectionFactory());
		factory.setMessageConverter(defaultJmsMessageConverter());
		factory.setPubSubDomain(true);//设置为topic类型
		factory.setSessionTransacted(true);//设置消息签收启用事务管理模式，即消费方在收到消息并进行处理的过成功如果出现了异常，则会消息重投
		factory.setSubscriptionDurable(false);//false表示非持久化订阅
//		factory.setCacheLevel();//不用设置缓存策略，则会自动根据transaction的策略来进行缓存
		return factory;
	}
	/**
	 * 持久化的topic监听器工厂：：持久化订阅、事务管理模式的消息签收
	 * @return
	 */
	@Bean
	public DefaultJmsListenerContainerFactory durableTopicLCFactory(){
		DurableDefaultJmsListenerContainerFactory factory = new DurableDefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(amqConnectionFactory());
		factory.setPubSubDomain(true);//设置为topic类型
		factory.setMessageConverter(defaultJmsMessageConverter());
		factory.setSessionTransacted(true);//设置消息签收启用事务管理模式

		//持久化订阅消息，就算在生产者发送消息时，消费者宕机或者网络不好，消息也会保存在MQ服务器，等消费者重新连接上时，继续消费消息
		//持久化订阅是对topic而言的，对queue不存在发布-订阅的关系，且queue总是持久化的
		factory.setSubscriptionDurable(true);
		return factory;
	}
	/**-------------------------------------------------------------- 消息监听/接收配置 END --------------------------------------------------------*/



	/**-------------------------------------------------------------- 同步接收消息配置 START --------------------------------------------------------*/
	@Bean
	public DefaultJmsListenerContainerFactory syncQueueLCFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(shareConnectionFactory());
		factory.setMessageConverter(defaultJmsMessageConverter());
		factory.setSessionTransacted(false);//取消事务
		factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);//设置自动签收消息
		return factory;
	}
	/**-------------------------------------------------------------- 同步接收消息配置 END ----------------------------------------------------------*/
	
}
