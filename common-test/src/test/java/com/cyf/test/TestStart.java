package com.cyf.test;

import com.cyf.center.common.bean.TimerJob;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.test.service.CacheService;
import com.cyf.test.service.DubboService;
import com.cyf.test.service.MQTestService;
import com.cyf.test.service.TimerTestService;
import com.cyf.test.util.SpringContextUtil;

import java.util.List;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/7 10:11
 */
public class TestStart {

    public static void main(String[] args){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] customArgs = new String[]{"javaconfig"};
                com.alibaba.dubbo.container.Main.main(customArgs);
            }
        });
        thread.start();

        //先等待几秒，等待dubbo启动完成
        try{
            Thread.sleep(10 * 1000L);
        }catch(Exception e){
            e.printStackTrace();
        }

//        dubboService();

//        cache();

//        mqTest();

        timerTest();
    }

    private static void dubboService(){
        DubboService dubboService = SpringContextUtil.getBean(DubboService.class);
        System.out.println("");
    }

    private static void cache(){
        CacheService cacheService = SpringContextUtil.getBean(CacheService.class);

        String key = "testKey";
        System.out.println("set cache result: "+cacheService.setCache(key, "testValue"));

        System.out.println("get cache value: "+cacheService.getCache(key));
    }

    private static void mqTest(){
        MQTestService mqTestService = SpringContextUtil.getBean(MQTestService.class);

        mqTestService.callSendMQQueue();

        mqTestService.callSendMQTopic();

        mqTestService.callMQBatchSend();

        mqTestService.callSendAnGetSync();
    }

    private static void timerTest(){
        TimerTestService timerTestService = SpringContextUtil.getBean(TimerTestService.class);
        //第一遍时开启这个，其他关闭
//        timerTestService.addTimerJob();

        //第二遍时开这个，其他关闭
//        timerTestService.rescheduleTimerJob();

        //第三遍时开启这个，其他关闭
//        timerTestService.pauseTimerJob();

        //第四遍时开启这个，其他关闭
        timerTestService.resumeTimerJob();

        //第五遍时开启这个，其他关闭
//        timerTestService.deleteTimerJob();

        //第六遍时开启这个，其他关闭
//        timerTestService.getTimerJobByKey();

        //可一直开启
        List<TimerJob> jobList = timerTestService.getScheduledTimerJobs();
        System.out.println(JsonUtil.toString(jobList));
    }
}
