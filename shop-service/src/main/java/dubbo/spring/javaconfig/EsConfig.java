package dubbo.spring.javaconfig;

import com.cyf.base.common.component.EsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by Administrator on 2017/5/29.
 */
@Configuration
@Import(EnvConfig.class)
public class EsConfig {
    @Autowired
    private EnvConfig envConfig;

    @Bean
    public EsClient esClient(){
        return new EsClient(envConfig.getEsClusterName(), envConfig.getEsAddresses());
    }
}
