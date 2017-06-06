package com.cyf.test.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.center.common.bean.TimerJob;
import com.cyf.center.common.business.service.TimerService;
import com.cyf.base.common.mqconfig.TestMQConfig;
import com.cyf.base.common.utils.CronExpressionUtil;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.LoggerWrapper;

import java.util.Date;
import java.util.List;

/**
 * Created by chenyf on 2017/3/11.
 */
@Service
public class TimerTestServiceImpl implements TimerTestService {
    static LoggerWrapper logger = LoggerWrapper.getLoggerWrapper();

    @Reference
    TimerService timerService;

    public void addTimerJob(){
        logger.info("-----addTimerJob start-----");
        //设置cron类型任务，每10秒执行一次
        TimerJob timerJob = TimerJob.newInstance("test_cron_timer_group", "test_cron_timer_job", TestMQConfig.TEST_CRON_TIMER_JOB);
        timerJob.toCronJob(CronExpressionUtil.getSecondlyExpression(10));
        timerService.addTimerJob(timerJob);

        //按15秒间隔一直重复
        TimerJob timerJob1 = TimerJob.newInstance("test_repeat_forever_group", "test_repeat_forever_job", TestMQConfig.TEST_REPEAT_FOREVER_JOB);
        timerJob1.toSimpleJob(new Date(), 15);
        timerService.addTimerJob(timerJob1);

        //按10秒间隔，重复20次
        TimerJob timerJob2 = TimerJob.newInstance("test_limit_repeat_group", "test_limit_repeat_job", TestMQConfig.TEST_LIMIT_REPEAT_JOB);
        timerJob2.toSimpleJob(new Date(), 20, 10);
        timerService.addTimerJob(timerJob2);
        logger.info("-----addTimerJob end-----");
    }

    /**
     * 更新任务定时触发规则
     */
    public void rescheduleTimerJob(){
        logger.info("-----rescheduleTimerJob start-----");
        TimerJob timerJob = TimerJob.newInstance("test_cron_timer_group", "test_cron_timer_job", TestMQConfig.TEST_CRON_TIMER_JOB);
        timerJob.toCronJob(CronExpressionUtil.getSecondlyExpression(20));
        timerService.rescheduleTimerJob(timerJob);
        logger.info("-----rescheduleTimerJob end-----");
    }

    /**
     * 暂停任务
     */
    public void pauseTimerJob(){
        logger.info("-----pauseTimerJob start-----");
        String jobGroup = "test_cron_timer_group";
        String jobName = "test_cron_timer_job";
        timerService.pauseTimerJob(jobGroup, jobName);

        jobGroup = "test_repeat_forever_group";
        jobName = "test_repeat_forever_job";
        timerService.pauseTimerJob(jobGroup, jobName);

        jobGroup = "test_limit_repeat_group";
        jobName = "test_limit_repeat_job";
        timerService.pauseTimerJob(jobGroup, jobName);

        logger.info("-----pauseTimerJob end-----");
    }

    /**
     * 恢复被暂停任务
     */
    public void resumeTimerJob(){
        logger.info("-----resumeTimerJob start-----");
        String jobGroup = "test_cron_timer_group";
        String jobName = "test_cron_timer_job";
        timerService.resumeTimerJob(jobGroup, jobName);

        jobGroup = "test_repeat_forever_group";
        jobName = "test_repeat_forever_job";
        timerService.resumeTimerJob(jobGroup, jobName);
        logger.info("-----resumeTimerJob end-----");
    }

    /**
     * 删除任务
     */
    public void deleteTimerJob(){
        logger.info("-----deleteTimerJob start-----");
        String jobGroup = "test_cron_timer_group";
        String jobName = "test_cron_timer_job";
        timerService.deleteTimerJob(jobGroup, jobName);
        logger.info("-----deleteTimerJob end-----");
    }

    /**
     * 根据组名、任务名取得任务
     */
    public TimerJob getTimerJobByKey(){
        logger.info("-----getTimerJobByKey start-----");
        String jobGroup = "test_repeat_timer_group";
        String jobName = "test_repeat_timer_job";
        TimerJob timerJob = timerService.getTimerJobByKey(jobGroup, jobName);
        logger.info(JsonUtil.toString(timerJob));
        logger.info("-----getTimerJobByKey end-----");
        return timerJob;
    }

    /**
     * 取得所有已经安排的任务，包括正常状态中的和暂停状态中的
     * @return
     */
    public List<TimerJob> getScheduledTimerJobs(){
        logger.info("-----getScheduledTimerJobs start-----");
        List<TimerJob> timerJobList = timerService.listTimerJob("test_repeat_timer_group");
        logger.info(JsonUtil.toString(timerJobList));
        logger.info("-----getScheduledTimerJobs end-----");
        return timerJobList;
    }

}
