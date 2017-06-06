package dubbo.spring.javaconfig;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by chenyf on 2017/3/11.
 */
@Configuration
@EnableTransactionManagement//启用注解式事务
@Import(EnvConfig.class)
public class MyBatisConfig {
    @Autowired
    EnvConfig envConfig;

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(envConfig.myBatisPath));
        return sqlSessionFactoryBean;
    }

    @Bean
    public DruidDataSource dataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(envConfig.jdbcDriver);
        dataSource.setUrl(envConfig.jdbcUrl);
        dataSource.setUsername(envConfig.jdbcUserName);
        dataSource.setPassword(envConfig.jdbcPassword);
        dataSource.setDefaultAutoCommit(false);
        dataSource.setMaxActive(1000);//连接池最大使用连接数量
        dataSource.setInitialSize(2);//初始化大小
        dataSource.setMaxWait(6000);//获取连接最大等待时间
        dataSource.setMinIdle(2);//连接池最小空闲
        dataSource.setTimeBetweenEvictionRunsMillis(3000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        try{
            dataSource.setFilters("stat");
        }catch(Exception e){
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(){
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

}
