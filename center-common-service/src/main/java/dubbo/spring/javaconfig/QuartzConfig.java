package dubbo.spring.javaconfig;

import com.cyf.center.common.business.component.quartz.job.AutowiringSpringBeanJobFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by chenyf on 2017/3/9.
 */
@Configuration
public class QuartzConfig {

    /**
     * 定义一个名为jobFactory的任务工厂，Quartz默认是使用这个工厂
     * @return
     */
    @Bean
    public AutowiringSpringBeanJobFactory jobFactory(){
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(){
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(jobFactory());
        return schedulerFactoryBean;
    }
}
