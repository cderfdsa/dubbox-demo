package dubbo.spring.javaconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.cyf.test")
@Import({DubboConfig.class})
public class AppConfig {

}
