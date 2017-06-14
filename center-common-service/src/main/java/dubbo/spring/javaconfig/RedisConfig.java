package dubbo.spring.javaconfig;

import com.cyf.base.common.component.Redisson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Title：redis配置类
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/4/1 13:19
 */
@Configuration
@Import(EnvConfig.class)
public class RedisConfig {
    @Autowired
    EnvConfig envConfig;

    @Bean
    public Redisson redisson(){
        return Redisson.simpleInstance(envConfig.redisUrls, envConfig.redisPass);
    }
}
