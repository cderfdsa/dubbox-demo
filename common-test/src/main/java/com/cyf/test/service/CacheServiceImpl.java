package com.cyf.test.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.center.common.business.service.CacheService;

/**
 * Created by chenyf on 2017/4/4.
 */
@Service
public class CacheServiceImpl implements com.cyf.test.service.CacheService {
    @Reference
    CacheService cacheService;

    public boolean setCache(String key, String value){
        return cacheService.set(key, value);
    }

    public String getCache(String key){
        return cacheService.get(key);
    }
}
