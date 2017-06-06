package dubbo.spring.javaconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * 静态资源配置类，从 common.properties、${env}.properties 文件中读取变量值
 * 要想使用 @Value("${xxx}") 这种占位符的方式为属性赋值，需要 @PropertySource 注解，并且定义 static @Bean 类型的方法进行 PropertySourcesPlaceholderConfigurer 注入
 * 参考： https://www.mkyong.com/spring/spring-propertysources-example/
 *
 * Created by chenyf on 2017/3/15.
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
    private String env;
    //dubbo
    @Value("${applicationName}")
    private String applicationName;
    @Value("${annotationPackage}")
    private String annotationPackage;
    @Value("${registryAddress}")
    private String registryAddress;
    @Value("${registryClient}")
    private String registryClient;

    //数据库相关
    @Value("${myBatisPath}")
    private String myBatisPath;
    @Value("${jdbcDriver}")
    private String jdbcDriver;
    @Value("${jdbcUrl}")
    private String jdbcUrl;
    @Value("${jdbcUserName}")
    private String jdbcUserName;
    @Value("${jdbcPassword}")
    private String jdbcPassword;

    //elasticsearch相关
    @Value("${esAddresses}")
    private String esAddresses;
    @Value("${esClusterName}")
    private String esClusterName;

    public String getEnv() {
        return env;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getAnnotationPackage() {
        return annotationPackage;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public String getRegistryClient() {
        return registryClient;
    }

    public String getMyBatisPath() {
        return myBatisPath;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getJdbcUserName() {
        return jdbcUserName;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public String getEsAddresses() {
        return esAddresses;
    }

    public String getEsClusterName() {
        return esClusterName;
    }
}
