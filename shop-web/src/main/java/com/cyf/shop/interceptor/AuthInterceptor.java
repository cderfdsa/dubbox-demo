package com.cyf.shop.interceptor;

import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.bean.UserVo;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.shop.libs.Permission;
import com.cyf.shop.libs.RequestUtil;
import com.cyf.shop.libs.UserComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/7 13:03
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(AuthInterceptor.class);

    @Autowired
    UserComponent user;
    @Autowired
    Permission permission;

    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {

    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView view) throws Exception {
        String contextPath = request.getContextPath();
        if (view != null) {
            request.setAttribute("base", contextPath);
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId = null;
        UserVo userVo = user.tryLogin(request);
        boolean isLogin = userVo != null;
        if(isLogin){
            RequestUtil.setUserVo(userVo);//保存用户信息到 RequestUtil
            userId = userVo.getId();
        }

        //进行权限验证
        String path = user.getRequestPath(request);
        if(permission.isPermit(userId, path)){//有访问权限
            return true;
        }else if(! isLogin){//无访问权限又还没有登陆，则跳转到登陆
            if(user.isForwardLogin(path)){//如果是访问登陆页面则直接通过
                return true;
            }else{
                user.forwardLogin(request, response);
                return false;
            }
        }else{//无访问权限但已登陆，则跳转到提示页面
            returnJson(response, AjaxResult.fail("对不起，您无访问权限！"));
            return false;
        }
    }

    private void returnJson(HttpServletResponse response, Object obj) throws Exception{
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(JsonUtil.toString(obj));
        } catch (IOException e) {
            logger.error(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
