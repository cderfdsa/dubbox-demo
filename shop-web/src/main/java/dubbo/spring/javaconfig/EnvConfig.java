package dubbo.spring.javaconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by chenyf on 2017/3/26.
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

    public static final String USER_GRANTED_ROUTE_NAME = "user_granted_route_name";
    public static final String INDEX_ROUTE = "/index/index/index";


    @Value("${env}")
    public String env;
    @Value("${registryAddress}")
    public String registryAddress;
    @Value("${ssoHost}")
    public String ssoHost;
}
