package dubbo.spring.javaconfig;

import org.springframework.context.annotation.*;

@Configuration
@PropertySource({"classpath:properties/common.properties", "classpath:properties/${env}.properties"})//引入资源文件(这一步要最先，因为其他配置文件都有可能引用了资源文件的配置)
@ImportResource("classpath:spring/spring.xml")//再引入spring的xml配置文件
@ComponentScan(basePackages = "com.cyf.center.common")
public class AppConfig {
	
	//其他全局配置可放在下面

    //接下来的工作
      //1.把这个项目做成消息中心，对外提供接口即可
      //2.项目启动时自动加载数据库中的数据，添加到定时任务中去
      //3.如果是一次性任务，则添加到数据库中的时候，直接设置为删除状态，并在desc字段中标明一次性任务
      //4.配置文件可以区分不同环境，在打包的时候只加载特定的环境文件，并在系统启动的时候把跟环境配置相关的内容设置到java配置类里面去
      //5.打包完之后，可以直接使用jar命令启动应用，或者打包完之后直接生成了文件夹，文件夹里面有jar包和执行的shell文件

}
