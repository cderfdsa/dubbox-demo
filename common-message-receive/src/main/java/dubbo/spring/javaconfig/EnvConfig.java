package dubbo.spring.javaconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by chenyf on 2017/3/13.
 */
@Configuration
public class EnvConfig {
    /**
     * 必须定义为静态方法,而且必不可少
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        return configurer;
    }

    @Value("${env}")
    public String env;
    //dubbo
    @Value("${applicationName}")
    public String applicationName;
    @Value("${annotationPackage}")
    public String annotationPackage;
    @Value("${registryAddress}")
    public String registryAddress;
    @Value("${registryClient}")
    public String registryClient;

    //ActiveMQ
    @Value("${amqBrokerUrl}")
    public String amqBrokerUrl;
    @Value("${defaultQueue}")
    public String defaultQueue;
    @Value("${defaultTopic}")
    public String defaultTopic;
}
