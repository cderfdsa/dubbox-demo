package com.cyf.center.common.business.component.quartz.job;

import com.cyf.base.common.utils.JsonUtil;
import com.cyf.center.common.bean.TimerJob;
import com.cyf.base.common.bean.BaseResponse;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenyf on 2017/3/10.
 *
 * 说明：
 *     1、定时任务其实分为两个东西：trigger、jobDetail，其中trigger描述何时触发、怎样触发，jobDetail描述这是一个什么样的job，有哪些要传递的参数
 *     2、Quartz本身的设计是一个jobDetail可以有多个trigger，而一个trigger只能有一个jobDetail的，但是为了简单方便，在此类中的方法都是设计为成一个trigger只有
 *     一个jobDetail，一个jobDetail也只有一个trigger，添加任务时会同时添加trigger和jobDetail，修改时也会同时修改，删除时也会同时删除，并且两者的jobGroup、jobName是一样的
 *
 */
@Component
public class JobManager {
    public static final String JOB_DATA_KEY_IN_CONTEXT = "timerJob";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 添加一个新的任务，如果添加成功，会返回一个开始时间
     * @param timerJob
     * @return
     */
    public BaseResponse addTimerJob(TimerJob timerJob){
        BaseResponse resp = checkAndInitTimerJob(timerJob);
        if(resp.isError()) return resp;

        String result;
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = JobBuilder
                    .newJob(JobExecutor.class)
                    .withIdentity(timerJob.getJobName(), timerJob.getJobGroup())
                    .build();
            jobDetail.getJobDataMap().put(JOB_DATA_KEY_IN_CONTEXT, timerJob);

            Trigger trigger = genTrigger(timerJob);

            Date startTime = scheduler.scheduleJob(jobDetail, trigger);
            if(startTime == null){
                return BaseResponse.fail("添加定时计划失败："+ JsonUtil.toStringPretty(timerJob));
            }else{
                return BaseResponse.success(sdf.format(startTime), 1);
            }
        }catch(Exception e){
            return BaseResponse.fail(e.getMessage());
        }
    }

    /**
     * 更新任务的触发trigger
     * @param timerJob
     * @return
     */
    public BaseResponse<Date> rescheduleTimerJob(TimerJob timerJob){
        BaseResponse resp = checkAndInitTimerJob(timerJob);
        if(resp.isError()) return resp;

        try{
            if(! checkJobExist(timerJob)){
                return BaseResponse.fail("JobGroup="+timerJob.getJobGroup()+",JobName="+timerJob.getJobName()+" 的任务不在定时计划中，无法更新");
            }
        }catch(Exception e){
            return BaseResponse.fail("发生异常："+e.getMessage());
        }

        Date result;
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(timerJob.getJobName(), timerJob.getJobGroup());
            //获取trigger
            Trigger trigger = genTrigger(timerJob);
            //按新的Trigger重新设置job执行
            result = scheduler.rescheduleJob(triggerKey, trigger);
        }catch(Exception e){
            return BaseResponse.fail(e.getMessage());
        }

        return BaseResponse.success(result);
    }

    /**
     * 暂停某个任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse pauseTimerJob(String jobGroup, String jobName){
        TimerJob timerJob = TimerJob.newInstance(jobGroup, jobName);
        try{
            if(! checkJobExist(timerJob)){
                return BaseResponse.fail("JobGroup="+timerJob.getJobGroup()+",JobName="+timerJob.getJobName()+" 的任务不在定时计划中，无法暂停");
            }
        }catch(Exception e){
            return BaseResponse.fail("发生异常："+e.getMessage());
        }

        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(timerJob.getJobName(), timerJob.getJobGroup());
            scheduler.pauseJob(jobKey);
            return BaseResponse.success();
        }catch(Exception e){
            return BaseResponse.fail("发生异常："+e.getMessage());
        }
    }

    /**
     * 恢复某个任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse resumeTimerJob(String jobGroup, String jobName){
        TimerJob timerJob = TimerJob.newInstance(jobGroup, jobName);
        try{
            if(! checkJobExist(timerJob)){
                return BaseResponse.fail("JobGroup="+timerJob.getJobGroup()+",JobName="+timerJob.getJobName()+" 的任务不在定时计划中，无法恢复");
            }
        }catch(Exception e){
            return BaseResponse.fail("发生异常："+e.getMessage());
        }

        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(timerJob.getJobName(), timerJob.getJobGroup());
            scheduler.resumeJob(jobKey);
            return BaseResponse.success();
        }catch(Exception e){
            return BaseResponse.fail(e.getMessage());
        }
    }

    /**
     * 立即执行某任务一次
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse triggerJob(String jobGroup, String jobName){
        TimerJob timerJob = TimerJob.newInstance(jobGroup, jobName);
        try{
            if(! checkJobExist(timerJob)){
                return BaseResponse.fail("JobGroup="+timerJob.getJobGroup()+",JobName="+timerJob.getJobName()+" 的任务不在定时计划中，无法执行");
            }
        }catch(Exception e){
            return BaseResponse.fail("发生异常："+e.getMessage());
        }

        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(timerJob.getJobName(), timerJob.getJobGroup());
            scheduler.triggerJob(jobKey);
            return BaseResponse.success();
        }catch(Exception e){
            return BaseResponse.fail(e.getMessage());
        }
    }

    /**
     * 删除某个任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse deleteTimerJob(String jobGroup, String jobName){
        TimerJob timerJob = TimerJob.newInstance(jobGroup, jobName);
        try{
            if(! checkJobExist(timerJob)){
                return BaseResponse.success();
            }
        }catch(Exception e){
            return BaseResponse.fail("发生异常："+e.getMessage());
        }

        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = JobKey.jobKey(timerJob.getJobName(), timerJob.getJobGroup());
            if(scheduler.deleteJob(jobKey)){
                return BaseResponse.success();
            }else{
                return BaseResponse.fail("删除任务失败");
            }
        }catch(Exception e){
            return BaseResponse.fail("删除任务时发生异常，"+e.getMessage());
        }
    }

    /**
     * 获得当前正在执行的任务
     * @return
     */
    public BaseResponse<List<TimerJob>> listRunningJob(){
        List<TimerJob> jobList;
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            jobList = new ArrayList<>(executingJobs.size());
            for (JobExecutionContext executingJob : executingJobs) {
                JobDetail jobDetail = executingJob.getJobDetail();
                TimerJob jobBean = (TimerJob) jobDetail.getJobDataMap().get(JOB_DATA_KEY_IN_CONTEXT);
                jobList.add(jobBean);
            }
            return BaseResponse.success(jobList);
        }catch(Exception e){
            return BaseResponse.fail(e.getMessage());
        }
    }

    /**
     * 根据jobGroup、jobName检查一个任务是否已经存在
     * @param timerJob
     * @return
     */
    public boolean checkJobExist(TimerJob timerJob) throws SchedulerException{
        if(timerJob == null) return false;

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobKey jobKey = JobKey.jobKey(timerJob.getJobName(), timerJob.getJobGroup());//以 jobName 和 jobGroup 作为唯一key
        //获取JobDetail
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if(jobDetail != null){//已存在
            return true;
        }else{
            return false;
        }
    }

    /**
     * 检查TimerJob的各属性的参数是否合格
     * @param timerJob
     * @return
     */
    private BaseResponse checkAndInitTimerJob(TimerJob timerJob){
        if(timerJob == null){
            return BaseResponse.fail("TimerJob不能为空");
        }else if(timerJob.getJobGroup()==null || timerJob.getJobGroup().trim().length() <= 0){
            return BaseResponse.fail("任务的组名(jobGroup)不能为空");
        }else if(timerJob.getJobName()==null || timerJob.getJobName().trim().length() <= 0){
            return BaseResponse.fail("任务名(jobName)不能为空");
        }else if(timerJob.getDestination()==null || timerJob.getDestination().trim().length() <= 0){
            return BaseResponse.fail("任务的发送目的地(destination)不能为空");
        }else if(! timerJob.isSimpleJob() && ! timerJob.isCronJob()){
            return BaseResponse.fail("未知的任务类型(jobType)");
        }else if(timerJob.isSimpleJob()){
            if(timerJob.getRepeatCount()==null){
                return BaseResponse.fail("Simple类型的任务，repeatCount不能为空");//repeatCount为空时，quartz不会触发
            }else if(timerJob.getStartTime()==null){
                timerJob.setStartTime(new Date());
            }
        }
        return BaseResponse.success();
    }

    private Trigger genTrigger(TimerJob timerJob){
        Trigger trigger;
        if(timerJob.isSimpleJob()){ //用SimpleTrigger触发任务
            //表达式调度构建器
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            if(timerJob.getIntervals() != null) scheduleBuilder.withIntervalInSeconds(timerJob.getIntervals());
            if(timerJob.getRepeatCount() != null){
                if(timerJob.getRepeatCount().equals(SimpleTrigger.REPEAT_INDEFINITELY)){
                    scheduleBuilder.repeatForever();
                }else{
                    scheduleBuilder.withRepeatCount(timerJob.getRepeatCount());
                }
            }

            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger() //生成一个TriggerBuilder
                    .withIdentity(timerJob.getJobName(), timerJob.getJobGroup())//用以生成Trigger的key
                    .withSchedule(scheduleBuilder);
            if(timerJob.getStartTime() != null) triggerBuilder.startAt(timerJob.getStartTime());
            if(timerJob.getEndTime() != null) triggerBuilder.endAt(timerJob.getEndTime());
            trigger = triggerBuilder.build();
        }else if(timerJob.isCronJob()){ //按cronExpression表达式构建CronTrigger来触发任务
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(timerJob.getCronExpression());//表达式调度构建器

            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger() //生成一个TriggerBuilder
                    .withIdentity(timerJob.getJobName(), timerJob.getJobGroup())//用以生成Trigger的key
                    .withSchedule(scheduleBuilder);

            if(timerJob.getStartTime() != null) triggerBuilder.startAt(timerJob.getStartTime());
            if(timerJob.getEndTime() != null) triggerBuilder.endAt(timerJob.getEndTime());
            trigger = triggerBuilder.build();
        }else{
            trigger = null;
        }
        return trigger;
    }
}
