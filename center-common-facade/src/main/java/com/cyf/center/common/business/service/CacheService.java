package com.cyf.center.common.business.service;

/**
 * 常规的缓存服务类，如果需要更复杂的redis操作，可实例化下面这个对象
 * @see com.cyf.base.common.component.Redisson
 * Created by chenyf on 2017/3/31.
 */
public interface CacheService {
    public boolean set(String key, String value);

    public boolean set(String key, String value, long milliSecond);

    public String get(String key);

    public boolean del(String key);
}
