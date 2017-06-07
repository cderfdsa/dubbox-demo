package com.cyf.base.common.component;

import com.cyf.base.common.config.PublicConfig;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * @Title：操作redis的类
 * @Description：RedissonClient是redis的java客户端，跟redis连接只是其最基本的功能，并且还提供自动重连的机制，除此之外，
 *      它还提供多种分布式对象，特别适合在分布式环境下使用，RSet、RList、RMap、RLock等等对象的使用方法，完全跟java原生的
 *      Set、List、Lock的使用方法一样，因为Redisson提供的对象都是有重写父类中的方法的，都是能够支持分布式操作的，而对于分布式锁
 *      {@link org.redisson.RedissonLock}，如果客户端连接断开，则会自动释放锁，更多资料可以查看：https://github.com/redisson/redisson/wiki
 *
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/11 16:46
 */
public class Redisson {
    /**
     * 实际上实例化的是 {@link org.redisson.Redisson} 这个类，这个类是 {@link org.redisson.api.RedissonClient} 的实现类
     */
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
        return redissonClient.getKeys().delete(key) > 0;
    }

    /**
     * 获取RKey对象，通过key可以CRUD
     * @return {@link org.redisson.RedissonKeys}
     */
    public RKeys getRKeys(){
        return redissonClient.getKeys();
    }

    /**
     * 获取通用对象桶
     * @param objectName
     * @return {@link org.redisson.RedissonBucket}
     */
    public <T> RBucket<T> getRBucket(String objectName){
        RBucket<T> bucket = redissonClient.getBucket(objectName);
        return bucket;
    }

    /**
     * 获取Map对象，是有序的，最大数量受限于redis，为4 294 967 295
     * @param name
     * @return {@link org.redisson.RedissonMap}
     */
    public <K,V> RMap<K, V> getRMap(String name){
        RMap<K, V> map = redissonClient.getMap(name);
        return map;
    }

    /**
     * 获取RListMultimap对象，允许在一个key下存储多个值，有点类似数据表的按列存储
     * @param <K>
     * @param <V>
     * @return {@link org.redisson.RedissonListMultimap}
     */
    public <K, V> RListMultimap<K, V> getRListMultimap(String name){
        return redissonClient.getListMultimap(name);
    }

    /**
     * 获取RSetMultimap对象，允许在一个key下存储多个不重复的值，有点类似数据表的按列存储
     * @param <K>
     * @param <V>
     * @return {@link org.redisson.RedissonSetMultimap}
     */
    public <K, V> RSetMultimap<K, V> getRSetMultimap(String name){
        return redissonClient.getSetMultimap(name);
    }

    /**
     * 获取有序集合
     * @param objectName
     * @return {@link org.redisson.RedissonSortedSet}
     */
    public <V> RSortedSet<V> getRSortedSet(String objectName){
        RSortedSet<V> sortedSet = redissonClient.getSortedSet(objectName);
        return sortedSet;
    }

    /**
     * 获取集合
     * @param objectName
     * @return {@link org.redisson.RedissonSet}
     */
    public <V> RSet<V> getRSet(String objectName){
        RSet<V> rSet = redissonClient.getSet(objectName);
        return rSet;
    }

    /**
     * 获取列表
     * @param objectName
     * @return {@link org.redisson.RedissonList}
     */
    public <V> RList<V> getRList(String objectName){
        RList<V> rList = redissonClient.getList(objectName);
        return rList;
    }

    /**
     * 获取队列
     * @param objectName
     * @return {@link org.redisson.RedissonQueue}
     */
    public <V> RQueue<V> getRQueue(String objectName){
        RQueue<V> rQueue = redissonClient.getQueue(objectName);
        return rQueue;
    }

    /**
     * 获取双端队列
     * @param objectName
     * @return {@link org.redisson.RedissonDeque}
     */
    public <V> RDeque<V> getRDeque(String objectName){
        RDeque<V> rDeque = redissonClient.getDeque(objectName);
        return rDeque;
    }

    /**
     * @param objectName
     * @return {@link org.redisson.RedissonBlockingQueue}
     */
    public <V> RBlockingQueue<V> getRBlockingQueue(String objectName){
        RBlockingQueue rb = redissonClient.getBlockingQueue(objectName);
        return rb;
    }

    /**
     * 获取锁
     * @param objectName
     * @return {@link org.redisson.RedissonLock}
     */
    public RLock getRLock(String objectName){
        RLock rLock = redissonClient.getLock(objectName);
        return rLock;
    }

    /**
     * 获取信号量
     * @param objectName
     * @return {@link org.redisson.RedissonSemaphore}
     */
    public RSemaphore RSemaphore(String objectName){
        RSemaphore semaphore = redissonClient.getSemaphore(objectName);
        return semaphore;
    }

    /**
     * 获取原子数
     * @param objectName
     * @return {@link org.redisson.RedissonAtomicLong}
     */
    public RAtomicLong getRAtomicLong(String objectName){
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(objectName);
        return rAtomicLong;
    }

    /**
     * 获取记数锁
     * @param objectName
     * @return {@link org.redisson.RedissonCountDownLatch}
     */
    public RCountDownLatch getRCountDownLatch(String objectName){
        RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch(objectName);
        return rCountDownLatch;
    }

    /**
     * 获取消息的Topic
     * @param objectName
     * @return {@link org.redisson.RedissonTopic}
     */
    public <M> RTopic<M> getRTopic(String objectName){
        RTopic<M> rTopic = redissonClient.getTopic(objectName);
        return rTopic;
    }

}
