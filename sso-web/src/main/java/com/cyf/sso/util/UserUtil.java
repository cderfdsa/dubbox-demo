package com.cyf.sso.util;

import com.cyf.base.user.bean.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by chenyf on 2017/3/25.
 */
public class UserUtil {
    private static ConcurrentMap<String, User> CONTAINER = new ConcurrentHashMap();//保存用户登陆信息的容器，实际生成中应该用redis等替代

    /**
     * 保存用户的登录信息
     * @param user
     * @param uniqueId  唯一id，如：sessionId
     * @return
     */
    public static boolean saveLogin(User user, String uniqueId){
        CONTAINER.putIfAbsent(uniqueId, user);
        return true;
    }

    /**
     * 获取已登陆的用户信息
     * @param uniqueId
     * @return
     */
    public static User getUser(String uniqueId){
        return CONTAINER.get(uniqueId);
    }

}
