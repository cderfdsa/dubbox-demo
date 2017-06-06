package com.cyf.center.common.business.component.quartz.job;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * 继承自SpringBeanJobFactory，目的是为了当Quartz为Job的实现类进行实例化时，能够为其进行依赖注入
 * Created by chenyf on 2017/3/10.
 */
public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
    @Autowired
    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);  //进行依赖注入(只能注入本地Spring中的bean)
        return job;
    }
}
