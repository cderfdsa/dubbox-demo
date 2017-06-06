package dubbo.spring.javaconfig;

import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/2 13:16
 */
@Configuration
public class DubboConfig {
    public static final String APPLICATION_NAME = "common-test";
    public static final String REGISTRY_ADDRESS = "zookeeper://127.0.0.1:2181";
    public static final String ANNOTATION_PACKAGE = "com.cyf.test";

    /**与<dubbo:application/>相当.*/
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(APPLICATION_NAME);
        return applicationConfig;
    }

    /**与<dubbo:annotation/>相当.提供方扫描带有@com.alibaba.dubbo.config.annotation.Service的注解类*/
    @Bean
    public AnnotationBean annotationBean() {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(ANNOTATION_PACKAGE);
        return annotationBean;
    }

    /**与<dubbo:registry/>相当*/
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(REGISTRY_ADDRESS);
        registryConfig.setProtocol("zookeeper");
        registryConfig.setClient("curator");
        return registryConfig;
    }

    /**与<dubbo:protocol/>相当*/
    @Bean
    public ProtocolConfig protocolConfig(){
        ProtocolConfig protocolConfig = new ProtocolConfig("dubbo", 20889);
        protocolConfig.setSerialization("kryo");//默认为hessian2,但不支持无参构造函数类,而这种方式的效率很低
        return protocolConfig;
    }

    /**与<dubbo:provider/>相当*/
    @Bean
    public ProviderConfig providerConfig(){
        ProviderConfig providerConfig = new ProviderConfig();
        return providerConfig;
    }

    /**与<dubbo:consumer/>相当*/
    @Bean
    public ConsumerConfig consumerConfig(){
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setCheck(false);
        return consumerConfig;
    }
}
