package com.cyf.sso.web;

/**
 * Created by chenyf on 2017/3/25.
 */
import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.user.bean.User;
import com.cyf.base.user.service.UserService;
import com.cyf.center.common.business.service.CacheService;
import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.UserVo;
import com.cyf.base.common.utils.*;
import com.cyf.sso.service.TokenService;
import dubbo.spring.javaconfig.EnvConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {
    private LoggerWrapper log = LoggerWrapper.getLoggerWrapper();

    @Autowired
    TokenService tokenService;
    @Autowired
    EnvConfig envConfig;
    @Reference
    CacheService cacheService;
    @Reference
    UserService userService;

    /**
     * 请求注册
     * @return
     */
    @RequestMapping("register")
    public ModelAndView register(String fromUrl){
        ModelAndView mv = new ModelAndView();
        mv.addObject("fromUrl", fromUrl);
        return mv;
    }

    /**
     * 保存注册
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("do_register")
    public AjaxResult doRegister(User user){
        BaseResponse registerResp = userService.register(user);
        if(registerResp.isError()){
            return AjaxResult.fail(registerResp.getMessage());
        }else{
            return AjaxResult.success("注册成功！");
        }
    }

    /**
     * 用户请求登陆：
     *     1.根据用户的sessionId查看session是否已经有用户的信息，如果已经有，则生成token然后跳转到来源系统，如果还没有，则展示登陆页面
     * @return
     */
    @RequestMapping("login")
    public ModelAndView login(String fromUrl, HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv = new ModelAndView();

        if(fromUrl==null || fromUrl.trim().length() <= 0){
            mv.addObject("message", "禁止访问，请传入来源地址");
            mv.setViewName("common/error");
            return mv;
        }else{
            String userJson = null;
            CookieUtil cookieUtil = new CookieUtil(request, response);
            String userLoginCookieValue = cookieUtil.getValue(envConfig.userLoginCookieKey);
            if(! StringUtil.isEmpty(userLoginCookieValue)){
                String userCacheKey = getUserLoginCacheKey(decryptUserLoginCookieValue(userLoginCookieValue, envConfig.cookieCryptSecretKey));
                userJson = cacheService.get(userCacheKey);
            }

            if(! StringUtil.isEmpty(userJson)){
                String token = tokenService.generateToken();
                fromUrl = appendToken(fromUrl, token);

                try{
                    cacheService.set(token, userJson, getTokenExpire());
                    response.sendRedirect(fromUrl);
                    return null;
                }catch(Exception e){
                    log.error(e);
                }
            }

            mv.addObject("fromUrl", fromUrl);
            return mv;
        }
    }

    /**
     * 用户确认登陆，即用户输入用户名、密码、验证码等信息，然后点击登陆按钮进行登陆
     * 如果登陆验证失败，则返回json格式的信息，如果登陆成功，则跳转到来源系统
     * @return
     */
    @ResponseBody
    @RequestMapping("do_login")
    public AjaxResult doLogin(@RequestParam(name="loginName") String loginName, @RequestParam(name="password")String password,
                              @RequestParam(name="fromUrl", required=true)String fromUrl,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception{
        User user = userService.getByValidPassword(loginName, password);
        if(user == null){
            return AjaxResult.fail("用户名或密码错误");
        }else{
            String token = tokenService.generateToken();

            //以token为key，User对象为value，保存到缓存中，有效期为两分钟
            boolean isCacheSuccess = cacheService.set(token, JsonUtil.toStringWithNull(user), getTokenExpire());

            //把用户id加密后保存到cookie，有效期为7天
            String userLoginCacheValue = getUserLoginCookieValue(user);
            userLoginCacheValue = encryptUserLoginCookieValue(userLoginCacheValue, envConfig.cookieCryptSecretKey);
            CookieUtil cookieUtil = new CookieUtil(request, response);
            cookieUtil.add(envConfig.userLoginCookieKey, userLoginCacheValue, userLoginCacheExpire());

            if(isCacheSuccess){
                fromUrl = appendToken(fromUrl, token);
                return AjaxResult.success("", fromUrl);
            }else{
                return AjaxResult.fail("验证通过，但系统保存登陆信息失败！");
            }
        }
    }

    /**
     * 根据token返回用户名等信息，以token为key到缓存中取值，
     * 如果不能取到值，则返回fail
     * 如果能取到值，则删除token缓存，之后取得用户名等信息，再之后把信息保存缓存中，并返回success，之所以要删除token缓存是为了保证一个token只能使用一次
     *
     * @param token
     * @return
     */
    @ResponseBody
    @RequestMapping("access_by_token")
    public UserVo accessByToken(@RequestParam(name="token") String token){
        User user = tokenService.getUserFromCache(token);
        if(user != null){
            UserVo vo = new UserVo();
            vo.setId(user.getId());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setUsercode(user.getUsercode());
            vo.setUsername(user.getUsername());

            //把用户登陆信息保存到缓存中
            String userLoginCookieValue = getUserLoginCookieValue(user);
            String userLoginCacheKey = getUserLoginCacheKey(userLoginCookieValue);
            setUserCache(userLoginCacheKey, user, userLoginCacheExpire());

            return vo;
        }else{
            return null;
        }
    }

    /**
     * 用户退出
     * @return
     */
    @RequestMapping("logout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mv  = new ModelAndView();
        CookieUtil cookieUtil = new CookieUtil(request, response);
        String userLoginCookieValue = cookieUtil.getValue(envConfig.userLoginCookieKey);
        if(! StringUtil.isEmpty(userLoginCookieValue)){
            String userCacheKey = getUserLoginCacheKey(decryptUserLoginCookieValue(userLoginCookieValue, envConfig.cookieCryptSecretKey));
            cacheService.del(userCacheKey);
        }
        request.getRequestDispatcher("/user/login").forward(request, response);
        return mv;
    }

    /**
     * 忘记密码
     * @return
     */
    @RequestMapping("forgot_password")
    public ModelAndView forgotPassword(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }

    /**
     * 重置密码
     * @return
     */
    @RequestMapping("reset_password")
    public ModelAndView resetPassword(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }

    private String appendToken(String url, String token){
        if(! StringUtil.isEmpty(url)){
            Map<String, String> param = new HashMap<>();
            param.put("token", token);
            url = HttpUtil.encode(url, param);
        }
        return url;
    }

    private String getUserLoginCookieValue(User user){
        return user.getId().toString();
    }

    private String encryptUserLoginCookieValue(String value, String key) throws Exception{
        value = CryptUtil.encrypt(value, key);
        return Base64Utils.encodeToString(value.getBytes("utf-8"));
    }

    private String decryptUserLoginCookieValue(String value, String key) throws Exception{
        value = new String(Base64Utils.decode(value.getBytes("utf-8")), "utf-8");//先进行BASE64解码
        value = CryptUtil.decrypt(value, key);//再解密字符串
        return value;
    }

    private String getUserLoginCacheKey(String userLoginCookieValue){
        return MD5Util.md5("user_login_cache_key_" + userLoginCookieValue);
    }

    private void setUserCache(String key, User user, int expire){
        cacheService.set(key, JsonUtil.toStringWithNull(user), expire);
    }

    private int userLoginCacheExpire(){
        return 1000 * 60 * 60 * 24 * 7;//用户信息最长保留7天
    }

    private int getTokenExpire(){
        return 1000 * 60 * 2;//token保留两分钟;
    }
}
