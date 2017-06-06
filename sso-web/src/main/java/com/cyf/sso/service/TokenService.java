package com.cyf.sso.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.base.user.bean.User;
import com.cyf.center.common.business.service.CacheService;
import com.cyf.base.common.utils.*;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @Author: chenyf
 * @Date: 2016/8/22
 * @Description: 描述
 * @Version: 1.0
 * @History:
 */
@Service
public class TokenService {
    @Reference
    CacheService cacheService;

    /**
     * @Author: chenyf
     * @Description: 通过md5 生成token 生成规则：uuid + 时间戳 + 0-10的随机数
     * @return String 经过md5后的token
     */
    public String generateToken() {
        String token = UidUtil.getUniqueId() + TimeUtil.timeNow() + new Random().nextInt(10);
        return MD5Util.md5(token);
    }

    /**
     * @Author: chenyf
     * @Description: 设置token到缓存（redis、memcache等）
     * @param user
     * @param token
     * @return String 如果参数均不为空，返回已存入缓存的token，否者返回 null
     */
    public boolean setUserToCache(User user, String token) {
        if (user == null || StringUtil.isEmpty(token)) {
            return false;
        }
        //将用户信息写入 redis
        return cacheService.set(token, JsonUtil.toStringWithNull(user));
    }

    /**
     * @Author: chenyf
     * @Description: 从缓存中获取用户的信息
     * @param token
     * @return 存在 返回 用户信息，不存在 null
     */
    public User getUserFromCache(String token) {
        String userJson = cacheService.get(token);
        if (! StringUtil.isEmpty(userJson)) {
            cacheService.del(token);
            User user = JsonUtil.toBean(userJson, User.class);
            return user;
        } else {
            return null;
        }
    }
}
