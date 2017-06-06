package com.cyf.shop.libs;

import com.cyf.base.common.bean.UserVo;
import com.cyf.base.common.config.PublicConfig;
import com.cyf.base.common.utils.HttpUtil;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.base.common.utils.StringUtil;
import dubbo.spring.javaconfig.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenyf on 2017/3/26.
 */
@Component
public class UserComponent{
    private LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(UserComponent.class);
    private static String LOGIN_URL = "/index/index/login";
    private static String USER_SESSION_KEY = "userInfo";
    @Autowired
    private EnvConfig envConfig;

    /**
     * 判断用户是否已经登陆
     * @param request
     * @return
     * @throws Exception
     */
    public boolean isLogin(HttpServletRequest request) throws Exception{
        if(request.getSession().getAttribute(USER_SESSION_KEY) != null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 登陆
     * @param request
     * @throws Exception
     */
    public UserVo tryLogin(HttpServletRequest request) throws Exception{
        UserVo userVo = getUserBySession(request);
        if(userVo != null){
            return userVo;
        }

        userVo = getUserByToken(request);
        if(userVo != null){
            saveUser(userVo, request);
            return userVo;
        }

        return null;
    }

    /**
     * 获取用户当前访问的路由
     * @param request
     * @return
     */
    public String getRequestPath(HttpServletRequest request){
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);//获取访问路径
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        int index = bestMatchPattern.indexOf("{");//如果是通过 pathInfo 方式访问，需要把参数值去掉
        if (index >= 0){
            path = path.substring(0, index-1);
        }
        return path;
    }

    /**
     * 跳转到sso进行登陆
     * @param request
     * @param response
     * @throws Exception
     */
    public void forwardLogin(HttpServletRequest request, HttpServletResponse response) throws Exception{
        request.getRequestDispatcher(LOGIN_URL).forward(request, response);
    }

    public boolean isForwardLogin(String path){
        return LOGIN_URL.equalsIgnoreCase(path);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
        request.getSession().removeAttribute(USER_SESSION_KEY);
        response.sendRedirect(getSsoLogoutUrl(request));
    }

    public String getSsoLoginUrl(HttpServletRequest request){
        String fromUrl = this.getFromUrl(request);
        String redirectUrl = HttpUtil.getUrl(envConfig.ssoHost, PublicConfig.ssoLogin);
        redirectUrl += "?"+PublicConfig.ssoFromUrlParam+"="+fromUrl;
        redirectUrl = HttpUtil.encode(redirectUrl, null);
        return redirectUrl;
    }

    public String getSsoLogoutUrl(HttpServletRequest request){
        String fromUrl = this.getFromUrl(request);
        String redirectUrl = HttpUtil.getUrl(envConfig.ssoHost, PublicConfig.ssoLogout);
        redirectUrl += "?"+PublicConfig.ssoFromUrlParam+"="+fromUrl;
        redirectUrl = HttpUtil.encode(redirectUrl, null);
        return redirectUrl;
    }

    /**
     * 尝试根据token从sso系统取得用户登陆信息
     * 说明：
     *  1.先从request中取得token，如果token为空，则返回null
     *  2.如果token不为空，则根据token从sso系统取得UserVo，如果UserVo为空，则返回null
     *  3.如果UserVo不为空，则返回实体对象
     * @param request
     * @return
     */
    private UserVo getUserByToken(HttpServletRequest request){
        String token = request.getParameter(PublicConfig.ssoTokenParam);
        if(StringUtil.isEmpty(token)){
            return null;
        }

        UserVo userVo = null;
        Map<String, Object> params = new HashMap<>();
        params.put(PublicConfig.ssoTokenParam, token);
        String resultStr = HttpUtil.getJson(HttpUtil.getUrl(envConfig.ssoHost, PublicConfig.ssoAccessByToken), params);
        if(! StringUtil.isEmpty(resultStr)){
            userVo = JsonUtil.toBean(resultStr, UserVo.class);
        }
        return userVo;
    }

    /**
     * 尝试从session取得用户信息
     * @param request
     * @return
     */
    private UserVo getUserBySession(HttpServletRequest request){
        Object obj = request.getSession().getAttribute(USER_SESSION_KEY);
        if(obj == null){
            return null;
        }else{
            return (UserVo) obj;
        }
    }

    /**
     * 把用户登陆信息保存到Session
     * @param userVo
     * @param request
     */
    private void saveUser(UserVo userVo, HttpServletRequest request){
        if(userVo != null){
            request.getSession().setAttribute(USER_SESSION_KEY, userVo);
        }
    }

    private String getFromUrl(HttpServletRequest request){
        String fromUrl = request.getScheme() + "://" + request.getServerName();
        int port = request.getServerPort();
        if(port != 80) fromUrl += ":" + port;
        fromUrl += EnvConfig.INDEX_ROUTE;
        try{
            fromUrl = URLEncoder.encode(fromUrl, "utf-8");
        }catch(Exception e){
            logger.error(e);
        }
        return fromUrl;
    }

    public boolean isAjax(HttpServletRequest request){
        return request.getHeader("x-requested-with") != null &&
                request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest");
    }
}
