package dubbo.spring.javaconfig;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Created by chenyf on 2017/3/15.
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

    @Override
    public Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    public Class<?>[] getServletConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    public String[] getServletMappings() {
        return new String[] { "/" };
    }
}
