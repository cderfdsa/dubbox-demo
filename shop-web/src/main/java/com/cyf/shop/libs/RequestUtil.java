package com.cyf.shop.libs;

import com.cyf.base.common.bean.UserVo;

/**
 * Created by chenyf on 2017/4/15.
 */
public class RequestUtil {
    private static ThreadLocal<UserVo> CONTAINER = new ThreadLocal<>();

    public static Long getUserId(){
        UserVo userVo = getUserVo();
        if(userVo==null){
            return null;
        }else{
            return userVo.getId();
        }
    }

    public static String getUserName(){
        UserVo userVo = getUserVo();
        if(userVo==null){
            return null;
        }else{
            return userVo.getUsername();
        }
    }

//    public static boolean isGet(){
//        RequestInfo requestInfo = getRequestInfo();
//        if(requestInfo == null){
//            return false;
//        }
//        return "GET".equalsIgnoreCase(requestInfo.getMethod());
//    }
//
//    public static boolean isPost(){
//        RequestInfo requestInfo = getRequestInfo();
//        if(requestInfo == null){
//            return false;
//        }
//        return "POST".equalsIgnoreCase(requestInfo.getMethod());
//    }
//
//    public static boolean isAjax(){
//        RequestInfo requestInfo = getRequestInfo();
//        if(requestInfo == null){
//            return false;
//        }
//        return "AJAX".equalsIgnoreCase(requestInfo.getRequestType());
//    }

    public static void setUserVo(UserVo userVo){
        if(userVo != null){
            CONTAINER.set(userVo);
        }
    }

    public static UserVo getUserVo(){
        return CONTAINER.get();
    }
}
