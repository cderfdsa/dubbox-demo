package com.cyf.base.test;

import dubbo.spring.javaconfig.AppConfig;
import dubbo.spring.javaconfig.EnvConfig;
import dubbo.spring.javaconfig.MyBatisConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//下面需要配置所有配置文件，当然也可以迁移到AppConfig下使用@Import
@ContextConfiguration(classes={AppConfig.class, EnvConfig.class, MyBatisConfig.class})
public class BaseTestCase {

}