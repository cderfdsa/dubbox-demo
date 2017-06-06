package com.cyf.center.common.business.component.quartz.job;

import com.cyf.center.common.bean.MessageVo;
import com.cyf.center.common.bean.TimerJob;
import com.cyf.center.common.business.service.MessageService;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.utils.LoggerWrapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Quartz的任务执行类：Quartz在触发任务的时候，默认是每一次执行都新建一个Job实例对象，所以本类的timerJobBean属性是线程安全的
 * Created by chenyf on 2017/3/10.
 */
public class JobExecutor implements Job {
    static LoggerWrapper logger = LoggerWrapper.getLoggerWrapper();
    @Autowired
    MessageService messageService;

    /**
     * Quartz会拿了timerJob这个属性的名称，自动查找JobDataMap中的key，如果发现有名称一样的，就会为我们注入进来
     * 至于为什么会取名timerJob，那是因为在添加定时任务的时候就保存为这个名称了
     */
    private TimerJob timerJob;//这个属性名需要跟 JobManager.JOB_DATA_KEY_IN_CONTEXT 的值一致
    public void setTimerJob(TimerJob timerJob) {
        this.timerJob = timerJob;
    }

    public void execute(JobExecutionContext var1) throws JobExecutionException{
        if(timerJob != null){
            MessageVo bean = MessageVo.getQueue();
            bean.setName(timerJob.getDestination());
            bean.setPayload(timerJob.getJobParamJson());
            BaseResponse resp = messageService.sendMessage(bean);
            if(resp.isError()){
                logger.error(resp.getMessage());
            }
        }else{
            throw new JobExecutionException("定时任务对象为空，或者未知的任务类型");
        }
    }
}
