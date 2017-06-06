package dubbo.spring.javaconfig;

import org.springframework.context.annotation.*;

@Configuration
@PropertySource({"classpath:properties/common.properties", "classpath:properties/${env}.properties"})//引入资源文件(这一步要最先，因为其他配置文件都有可能引用了资源文件的配置)
@ImportResource("classpath:spring/spring.xml")//再引入spring的xml配置文件
@ComponentScan(basePackages = "com.cyf.common.message.receive")
public class AppConfig {

}
