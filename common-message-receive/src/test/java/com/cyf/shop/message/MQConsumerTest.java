package com.cyf.shop.message;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/3 9:20
 */
public class MQConsumerTest {

    public static void main(String[] args){
        String[] customArgs = new String[]{"javaconfig"};
        com.alibaba.dubbo.container.Main.main(customArgs);
    }
}
