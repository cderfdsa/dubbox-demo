package com.cyf.center.common.business.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.base.common.component.Redisson;
import com.cyf.base.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chenyf on 2017/3/31.
 */
@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    Redisson redisson;

    public boolean set(String key, String value){
        redisson.set(key, value);
        return true;
    }

    public boolean set(String key, String value, long milliSecond){
        return redisson.set(key, value, milliSecond);
    }

    public String get(String key){
        Object obj = redisson.get(key);
        if(obj == null){
            return null;
        }else if(!(obj instanceof String)){
            return JsonUtil.toStringWithNull(obj);
        }else{
            return (String) obj;
        }
    }

    public boolean del(String key){
        return redisson.del(key);
    }
}
