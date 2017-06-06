package com.cyf.base.common.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * 配置工厂类: 读取当前项目下 resources/config 下的配置文件的值生成配置类，实际上是存储在Map里面的，key就是配置bean的全称，
 * 如果有两个相同的Class在同一个jvm上就会发生覆盖现象，如，在不同的项目下部署了两个项目，两个项目都有：dubbo.spring.javaconfig.EnvConfig 这个类，就会发生覆盖
 * 说明：之所以不用Spring获取.properties文件内容的方式，是因为DubboConfig文件里面总是不能注入EnvConfig实例，而导致空指针异常
 */
public class ConfigUtil {
    /**
     * 运行时的环境变量,默认是dev环境,dev=开发环境,test=测试环境,prod=生产环境
     */
    private static final LoggerWrapper logger = LoggerWrapper.getLoggerWrapper();
    private static Map<String, Object> configBeanMap = new HashMap<>();
    private static final Lock lock = new ReentrantLock();
    private static final String PROPERTIES_PATH_PREFIX = "config/";

    /**
     * 获取配置Bean
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getConfigBean(Class<T> clazz){
        Properties paramSetting = new Properties();
        try {
            String envProperties = PROPERTIES_PATH_PREFIX;
            InputStream is = new ClassPathResource(envProperties).getInputStream();
            paramSetting.load(new InputStreamReader(is, "utf-8"));
            is.close();
        }catch (Exception e) {
            logger.error(e);
        }

        return getConfigBean(clazz, "");
    }

    /**
     * 获取配置Bean，如果担心项目之间的配置文件发生冲突，传入不同的前缀 prefix，即可避免
     * @param clazz
     * @param prefix
     * @param <T>
     * @return
     */
    public static <T> T getConfigBean(Class<T> clazz, String prefix){
        T cfg = (T) configBeanMap.get(clazz.getSimpleName());
        if(cfg == null){
            lock.tryLock();
            try {
                String envProperties = PROPERTIES_PATH_PREFIX;
                String key = prefix + clazz.getName();
                configBeanMap.put(key, initConfig(clazz, getProperties(envProperties)));
                if(configBeanMap.containsKey(key)){
                    cfg = (T) configBeanMap.get(key);
                }
            }catch (Exception e){
                logger.error(e);
            }finally{
                lock.unlock();
            }
        }
        return cfg;
    }

    /**
     * 初始化单个配置，即把配置文件转换为java对象
     * @param clazz
     * @param properties
     * @param <T>
     * @return
     */
    private static <T> T initConfig(Class<T> clazz, Properties properties){
        if(properties == null) return null;
        T obj = null;
        try {
            obj = clazz.newInstance();
            for (String proName: properties.stringPropertyNames()) {
                Field field = FieldUtils.getField(clazz, proName, true);
                if(field != null){
                    writeField(field, obj, properties.getProperty(proName));
                }
            }
        }catch (Exception e){
            logger.error(e);
        }
        return obj;
    }

    /**
     * 获取配置文件
     * @param fileName
     * @return
     */
    private static Properties getProperties(String fileName){
        Properties paramSetting = new Properties();
        try {
            InputStream is = new ClassPathResource(fileName, ConfigUtil.class.getClassLoader()).getInputStream();
            paramSetting.load(new InputStreamReader(is, "utf-8"));
            is.close();
        }catch (Exception e) {
            logger.error(e);
        }
        return paramSetting;
    }

    /**
     * 为对象写入属性值
     * @param field
     * @param target
     * @param value
     * @param <T>
     * @throws Exception
     */
    private static <T> void writeField(Field field, T target, Object value) throws Exception{
        Class fieldType = field.getType();
        if(fieldType.equals(String.class)){
            FieldUtils.writeField(field,target,value);
        }else if(fieldType.equals(Integer.class)){
            FieldUtils.writeField(field,target,Integer.parseInt((String)value));
        }else if(fieldType.equals(Long.class)){
            FieldUtils.writeField(field,target,Long.parseLong((String)value));
        }else if(fieldType.equals(Double.class)){
            FieldUtils.writeField(field,target,Double.valueOf((String)value));
        }else if(fieldType.equals(Float.class)){
            FieldUtils.writeField(field,target,Float.valueOf((String)value));
        }else if(fieldType.equals(Boolean.class)){
            if("true".equalsIgnoreCase((String)value)){
                FieldUtils.writeField(field,target,true);
            }else{
                FieldUtils.writeField(field,target,false);
            }
        }
    }
}
