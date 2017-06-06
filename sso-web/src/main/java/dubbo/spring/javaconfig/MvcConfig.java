package dubbo.spring.javaconfig;

import freemarker.template.utility.XmlEscape;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.*;

/**
 * Created by chenyf on 2017/3/15.
 */
@Configuration
public class MvcConfig {
    /**------------------------------------------------ 文件上传/下载 START --------------------------------------------------*/
    /**
     * 文件上传配置
     * @return
     */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver getMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSizePerFile(104857600);
        multipartResolver.setMaxInMemorySize(4096);
        return multipartResolver;
    }
    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource getMessageSource() {
        ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
        resource.setBasename("classpath:messages");
        resource.setDefaultEncoding("UTF-8");
        return resource;
    }
    /**------------------------------------------------ 文件上传/下载 END --------------------------------------------------*/



    /**------------------------------------------------ 视图解析器配置 START --------------------------------------------------*/
    /**
     * 多视图解析器：可以根据请求文件名后缀或者Accept头来调用不同的视图处理器
     * 可通过setContentNegotiationManager设置自定义的文件拓展名映射
     * @return
     */
    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver(){
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setContentNegotiationManager(contentNegotiationManager().getObject());

        //添加视图解析器集合
        List<ViewResolver> viewResolvers = new ArrayList<>();
        viewResolvers.add(freeMarkerViewResolver());
        viewResolver.setViewResolvers(viewResolvers);

        //默认视图集合
        List<View> defaultViews = new ArrayList<>();
        defaultViews.add(new MappingJackson2JsonView());//默认的视图
        viewResolver.setDefaultViews(defaultViews);
        return viewResolver;
    }
    /**
     * 自定义的文件拓展名映射mediaType的工厂类
     * @return
     */
    @Bean
    public ContentNegotiationManagerFactoryBean contentNegotiationManager(){
        ContentNegotiationManagerFactoryBean contentNegotiationManager = new ContentNegotiationManagerFactoryBean();//工厂类
        contentNegotiationManager.setIgnoreAcceptHeader(true);
        contentNegotiationManager.setFavorParameter(true);
        contentNegotiationManager.setFavorPathExtension(true);
        contentNegotiationManager.setDefaultContentType(MediaType.TEXT_HTML);
        Map<String, MediaType> mediaTypes = new HashMap<>();
        mediaTypes.put("xml", MediaType.APPLICATION_XML);
        mediaTypes.put("json", MediaType.APPLICATION_JSON);
        contentNegotiationManager.addMediaTypes(mediaTypes);
        return contentNegotiationManager;
    }
    /**
     * FreeMarker配置
     * @return
     */
    @Bean
    public FreeMarkerConfigurer freemarkerConfig(){
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("/views/");

        Map<String, Object> variables = new HashMap<>();
        variables.put("xml_escape", new XmlEscape());
        freeMarkerConfigurer.setFreemarkerVariables(variables);
        freeMarkerConfigurer.setDefaultEncoding("utf-8");

        Properties settings = new Properties();
        settings.setProperty("template_update_delay", "0");
        settings.setProperty("tag_syntax", "auto_detect");
        settings.setProperty("default_encoding", "UTF-8");
        settings.setProperty("output_encoding", "UTF-8");
        settings.setProperty("locale", "zh_CN");
        settings.setProperty("date_format", "yyyy-MM-dd");
        settings.setProperty("time_format", "HH:mm:ss");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        settings.setProperty("number_format", "#");//设置数字格式 以免出现 000.00
        settings.setProperty("classic_compatible", "true");
        settings.setProperty("template_exception_handler", "html_debug");//ignore,debug,html_debug,rethrow
        freeMarkerConfigurer.setFreemarkerSettings(settings);
        return freeMarkerConfigurer;
    }
    /**
     * 定义FreeMarker视图解析器
     * @return
     */
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver(){
        FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
        freeMarkerViewResolver.setViewClass(FreeMarkerView.class);
        freeMarkerViewResolver.setContentType("text/html;charset=utf-8");
        freeMarkerViewResolver.setCache(true);
        freeMarkerViewResolver.setSuffix(".html");
        freeMarkerViewResolver.setOrder(0);
        Map<String, Object> attributes = new HashMap<>();
        freeMarkerViewResolver.setAttributesMap(attributes);
        return freeMarkerViewResolver;
    }
    /**------------------------------------------------ 视图解析器配置 END --------------------------------------------------*/

}
