package dubbo.spring.javaconfig;

import com.cyf.shop.interceptor.AuthInterceptor;
import com.cyf.shop.libs.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @Title：应用配置入口
 * @Description：官方建议常规配置可使用 @EnableWebMvc 然后继承 WebMvcConfigurerAdapter 即可，如果有一些高级配置需求是这两个组合无法满足的，
 * 则可以直接继承 DelegatingWebMvcConfiguration 或 WebMvcConfigurationSupport，并移除 @EnableWebMvc 注解
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/7 13:05
 */
@Configuration
@PropertySource({"classpath:properties/common.properties", "classpath:properties/${env}.properties"})//引入资源文件(这一步要最先，因为其他配置文件都有可能引用了资源文件的配置)
@ImportResource("classpath:spring/spring.xml")//再引入spring的xml配置文件
@ComponentScan(basePackages = "com.cyf.shop")
@Import({EnvConfig.class, MvcConfig.class})
public class AppConfig extends DelegatingWebMvcConfiguration{
    @Autowired
    ContentNegotiatingViewResolver contentNegotiatingViewResolver;

    /**
     * 配置消息转换器
     * @param converters
     */
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setDefaultCharset(Charset.forName("UTF-8"));
        converters.add(stringHttpMessageConverter);
        converters.add(new MappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    /**
     * 配置静态资源文件
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }

    /**
     * 默认的Servlet处理器，当其他Servlet处理器都没有匹配到的时候，就会调用这个处理器处理，如静态文件的处理之类的
     * @param configurer
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    /**
     * 配置视图解析器
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(contentNegotiatingViewResolver);
    }

    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor()).excludePathPatterns("/static/**");
    }
    /**
     * 定义权限拦截器的bean
     * @return
     */
    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    /**
     * 定义全局异常处理器
     * @return
     */
    @Bean
    public HandlerExceptionResolver exceptionHandler(){
        ExceptionHandler exceptionHandler = new ExceptionHandler();
        exceptionHandler.setDefaultErrorView("common/notice_page");
        exceptionHandler.setDefaultStatusCode(500);
        return exceptionHandler;
    }
}
