package com.cyf.base.common.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *(C) 2014-2015, O2OMedical Inc. All rights reserved.
 *
 * @author musilin
 * @author zhangxuerong
 * @date 2015年9月24日
 */
public class CookieUtil {

	
	
	private final HttpServletRequest request;
    private final HttpServletResponse response;
    
    public CookieUtil(final HttpServletRequest request, final HttpServletResponse response){
        this.request = request;
        this.response = response;
    }
    
    /**
     * 添加Cookie
     * @param cookie 实例
     */
    public void add(Cookie cookie){
        response.addCookie(cookie);
    }
    
    /**
     * 添加Cookie
     * @param name  名称
     * @param value 值
     */
    public void add(String name, String value){
        add(name, value, -1);
    }
    
    /**
     * 添加Cookie
     * @param name  名称
     * @param value 值
     * @param secondTimeout 设定多少秒后过期。若为负数, 则浏览器关闭后Cookie失效。
     */
    public void add(String name, String value, int secondTimeout){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(secondTimeout);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * @Author: xbuding
     * @Description: 添加HttpOnly的cookie
     * @param name 名称
     * @param value 值
     * @param secondTimeout 设定多少秒后过期。若为负数, 则浏览器关闭后Cookie失效。
     * @param httpOnly true 设置HttpOnly的cookie ，false 设置普通cookie
     */
    public void add(String name, String value, int secondTimeout, Boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(secondTimeout);
        if (httpOnly) {
            cookie.setComment("; HttpOnly;");
        }
        response.addCookie(cookie);
    }
    
    /**
     * 根据名称获取Cookie对象。如果不存在则返回null。
     * @param name 名称
     * @return 返回Cookie对象
     */
    public Cookie getCookie(String name){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie;
                }
            }
        }
        return null;
    }
    
    /**
     * 根据名称获取Cookie对象的值。如果不存在则返回null。
     * @param name  名称
     * @return  返回该Cookie对象的值
     */
    public String getValue(String name){
        Cookie cookie = getCookie(name);
        return cookie == null ? null : cookie.getValue();
    }
    
    /**
     * 删除Cookie
     * @param name 名称
     */
    public void delete(String name){
        delete(new Cookie(name, null));
    }
    
    /**
     * 删除Cookie
     * @param cookie 需要删除的对象
     */
    public void delete(Cookie cookie){
        cookie.setMaxAge(0);  //0表示立即删除
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    
    /**
     * 清空Cookie
     */
    public void clear(){
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                delete(cookie);
            }
        }
    }
	
	
	
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("");

	}
	
}
