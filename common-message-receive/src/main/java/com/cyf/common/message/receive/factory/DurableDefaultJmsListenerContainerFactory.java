package com.cyf.common.message.receive.factory;

import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * 持久订阅时的DefaultJmsListenerContainerFactory
 * 作用：为每一个Endpoint都设置一个clientId
 * 注意：
 *      1、此类只适用于使用 @JmsListener 的注解式监听器，因为在genClientId方法里会把JmsListenerEndpoint强转为MethodJmsListenerEndpoint，并且
 *          如果在同一个方法上监听多个持久化消息，则会报错的
 *      2、如果同一个项目做了分布式部署，那么这个项目在所有的服务器都会接收到
 * Created by Administrator on 2017/5/2.
 */
public class DurableDefaultJmsListenerContainerFactory extends DefaultJmsListenerContainerFactory{

    @Override
    public DefaultMessageListenerContainer createListenerContainer(JmsListenerEndpoint endpoint) {
        DefaultMessageListenerContainer container = super.createListenerContainer(endpoint);
        MethodJmsListenerEndpoint methodEndpoint = (MethodJmsListenerEndpoint) endpoint;
        container.setClientId(genClientId(methodEndpoint));
        return container;
    }

    /**
     * 使用方法名作为clientId
     * @param methodEndpoint
     * @return
     */
    private String genClientId(MethodJmsListenerEndpoint methodEndpoint){
        return methodEndpoint.getMethod().getName();
    }
}
