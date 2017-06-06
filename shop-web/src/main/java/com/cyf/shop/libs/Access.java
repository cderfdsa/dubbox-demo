package com.cyf.shop.libs;

import dubbo.spring.javaconfig.EnvConfig;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 用以在html页面上判断是否有访问某个路径的权限，被Freemarker调用
 *     1、为提高效率，会先从HttpServletRequest对象中获取用户所有可访问的route数据，然后判断当前要放访问的route
 *     2、如果HttpServletRequest没有数据，则会通过rbac控件去判断是否有权限
 * Created by chenyf on 2016/9/9.
 */
@Component
public class Access implements TemplateMethodModelEx{
    @Autowired
    private Permission permission;

    /**
     * 判断用户是否有访问某个url的权限
     * args 参数的顺序说明
     *   第1个是要访问的 route，如：/index/index/index
     *   第2个是Freemarker的HttpRequestHashModel对象，第2个参数是选传参数，如果没传，则会从数据库取得用户的所有可访问路径，一定程度上会降低效率
     * @param args
     * @return
     * @throws TemplateModelException
     */
    public Boolean exec(List args) throws TemplateModelException {
        if(args == null || args.size() != 2){
            throw new TemplateModelException("参数个数必须为2个");
        }else if(args.get(0) == null){
            throw new TemplateModelException("第1个参数应为非null的数值");
        }else if(! isHttpRequestHashModel(args.get(1))){
            throw new TemplateModelException("第2个参数应为非null的HttpRequestHashModel对象");
        }

        String route = String.valueOf(args.get(0));
        HttpRequestHashModel requestModel = (HttpRequestHashModel) args.get(1);
        Map<String, String> routeMap = getRouteMap(requestModel, RequestUtil.getUserId());

        Boolean result = false;
        if(routeMap != null){
            result = routeMap.containsKey(route);
        }

        return result;
    }

    private Map<String, String> getRouteMap(HttpRequestHashModel requestModel, Long userId){
        Map<String, String> routeMap;
        //先尝试从 HttpServletRequest 对象中获取，如果获取到了，则直接返回
        Object obj = requestModel.getRequest().getAttribute(EnvConfig.USER_GRANTED_ROUTE_NAME);
        if(obj != null){
            routeMap = (Map<String, String>) obj;
        }else{//如果 HttpServletRequest 对象中没有，则从数据库中获取，然后把获得的值设置到 HttpServletRequest 对象中去，方便同一个页面的其他地方判断时使用
            routeMap = permission.getNodeMap(userId);
            this.setRoute(requestModel, routeMap);
        }
        return routeMap;
    }

    private void setRoute(HttpRequestHashModel requestModel, Map<String, String> routeMap){
        if(routeMap != null){
            requestModel.getRequest().setAttribute(EnvConfig.USER_GRANTED_ROUTE_NAME, routeMap);
        }
    }

    private boolean isHttpRequestHashModel(Object obj){
        if(obj != null && obj instanceof HttpRequestHashModel){
            return true;
        }else{
            return false;
        }
    }
}
