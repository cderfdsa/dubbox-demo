package com.cyf.base.common.component;

import com.cyf.base.common.config.PublicConfig;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @Title：操作redis的类
 * @Description：RedissonClient是redis的java客户端，能够提供多种分布式对象，特别适合在分布式环境下使用
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/11 16:46
 */
public class Redisson {
    private RedissonClient redissonClient;

    //构造方法私有化
    private Redisson(){}

    public static Redisson simpleInstance(String redisUrls, String password){
        Redisson instance = new Redisson();
        Config config = new Config();
        String [] urlArr = redisUrls.split(PublicConfig.COMMA_SEPARATOR);
        if(urlArr.length >= 3){//节点大于3时启用集群模式
            config.useClusterServers().setPassword(password).addNodeAddress(urlArr);
        }else{//其他情况都只启用单点模式
            config.useSingleServer().setPassword(password).setAddress(urlArr[0]);
        }
        instance.redissonClient = org.redisson.Redisson.create(config);
        return instance;
    }

    public static Redisson getInstance(Config config){
        Redisson instance = new Redisson();
        instance.redissonClient = org.redisson.Redisson.create(config);
        return instance;
    }

    public RedissonClient getRedissonClient(){
        return this.redissonClient;
    }

    public boolean set(String key, Object value){
        getRBucket(key).set(value);
        return true;
    }

    public boolean set(String key, Object value, long milliSecond){
        getRBucket(key).set(value, milliSecond, TimeUnit.MILLISECONDS);
        return true;
    }

    public Object get(String key){
        return getRBucket(key).get();
    }

    public boolean del(String key){
        return getRBucket(key).delete();
    }

    /**
     * 获取通用对象桶
     * @param objectName
     * @return
     */
    public <T> RBucket<T> getRBucket(String objectName){
        RBucket<T> bucket = redissonClient.getBucket(objectName);
        return bucket;
    }

    /**
     * 获取Map对象
     * @param objectName
     * @return
     */
    public <K,V> RMap<K, V> getRMap(String objectName){
        RMap<K, V> map = redissonClient.getMap(objectName);
        return map;
    }

    /**
     * 获取有序集合
     * @param objectName
     * @return
     */
    public <V> RSortedSet<V> getRSortedSet(String objectName){
        RSortedSet<V> sortedSet = redissonClient.getSortedSet(objectName);
        return sortedSet;
    }

    /**
     * 获取集合
     * @param objectName
     * @return
     */
    public <V> RSet<V> getRSet(String objectName){
        RSet<V> rSet = redissonClient.getSet(objectName);
        return rSet;
    }

    /**
     * 获取列表
     * @param objectName
     * @return
     */
    public <V> RList<V> getRList(String objectName){
        RList<V> rList = redissonClient.getList(objectName);
        return rList;
    }

    /**
     * 获取队列
     * @param objectName
     * @return
     */
    public <V> RQueue<V> getRQueue(String objectName){
        RQueue<V> rQueue = redissonClient.getQueue(objectName);
        return rQueue;
    }

    /**
     * 获取双端队列
     * @param objectName
     * @return
     */
    public <V> RDeque<V> getRDeque(String objectName){
        RDeque<V> rDeque = redissonClient.getDeque(objectName);
        return rDeque;
    }

    /**
     * @param objectName
     * @return
     */
    public <V> RBlockingQueue<V> getRBlockingQueue(String objectName){
        RBlockingQueue rb = redissonClient.getBlockingQueue(objectName);
        return rb;
    }

    /**
     * 获取锁
     * @param objectName
     * @return
     */
    public RLock getRLock(String objectName){
        RLock rLock = redissonClient.getLock(objectName);
        return rLock;
    }

    /**
     * 获取信号量
     * @param objectName
     * @return
     */
    public RSemaphore RSemaphore(String objectName){
        RSemaphore semaphore = redissonClient.getSemaphore(objectName);
        return semaphore;
    }

    /**
     * 获取原子数
     * @param objectName
     * @return
     */
    public RAtomicLong getRAtomicLong(String objectName){
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(objectName);
        return rAtomicLong;
    }

    /**
     * 获取记数锁
     * @param objectName
     * @return
     */
    public RCountDownLatch getRCountDownLatch(String objectName){
        RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch(objectName);
        return rCountDownLatch;
    }

    /**
     * 获取消息的Topic
     * @param objectName
     * @return
     */
    public <M> RTopic<M> getRTopic(String objectName){
        RTopic<M> rTopic = redissonClient.getTopic(objectName);
        return rTopic;
    }

}
