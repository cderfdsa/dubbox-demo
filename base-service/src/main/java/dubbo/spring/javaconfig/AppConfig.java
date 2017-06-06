package dubbo.spring.javaconfig;

import org.springframework.context.annotation.*;

@Configuration
@PropertySource({"classpath:properties/common.properties", "classpath:properties/${env}.properties"})//引入资源文件(这一步要最先，因为其他配置文件都有可能引用了资源文件的配置)
@ImportResource("classpath:spring/spring.xml")//再引入spring的xml配置文件
@ComponentScan(basePackages = "com.cyf.base")
public class AppConfig {
    /**
     * 说明：
     * 1、如果出现两个配置文件之间相互引用的对方的@Bean方法的情况，如果使用@Autowired方式注入，可能会出现另一个配置文件还没有实例化的情况，这种时候，可通过
     *    在方法中参数形式来引入依赖
     * 2、整合dubbox时采用的是xml的方式，一来是因为dubbo官方推荐使用xml方式，二来是因为如果独立出一个DubboConfig文件时，EnvConfig总是注入失败，或者虽然注入成功，
     *    但是里面的属性值都是null，三来，不想因为一个配置bean的注入问题而引入spring-boot，因为这样会增加项目的学习成本，多方面考量之下采用xml配置比较稳妥
     */

}
