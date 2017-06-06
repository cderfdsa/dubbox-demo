package com.cyf.test.service;

/**
 * Created by chenyf on 2017/4/4.
 */
public interface CacheService {
    public boolean setCache(String key, String value);

    public String getCache(String key);
}
