package com.cyf.center.common.business.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.center.common.business.component.quartz.dao.TimerJobBiz;
import com.cyf.center.common.bean.TimerJob;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.utils.LoggerWrapper;
import com.cyf.base.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyf on 2017/3/7.
 */
@Service
public class TimerServiceImpl implements TimerService {
    LoggerWrapper logger = LoggerWrapper.getLoggerWrapper();
    @Autowired
    TimerJobBiz timerJobBiz;

    /**
     * 添加定时任务，添加完毕之后，需要由调用者自行去监听 TimerJob.destination 这个属性里面的消息目的地
     * @param timerBean
     * @return
     */
    public BaseResponse addTimerJob(TimerJob timerBean){
        if( timerBean==null || StringUtil.isEmpty(timerBean.getJobGroup()) || StringUtil.isEmpty(timerBean.getJobName()) ){
            return BaseResponse.fail("请指定组名和任务名");
        }
        return timerJobBiz.addTimerJob(timerBean);
    }

    /**
     * 更新任务定时触发规则
     * @param timerBean
     * @return
     */
    public BaseResponse rescheduleTimerJob(TimerJob timerBean){
        return timerJobBiz.rescheduleTimerJob(timerBean);
    }

    /**
     * 暂停已设置的定时任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse pauseTimerJob(String jobGroup, String jobName){
        return timerJobBiz.pauseTimerJob(jobGroup, jobName);
    }

    /**
     * 恢复被暂停的定时任务，如果任务在quartz还不存在,则添加此任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse resumeTimerJob(String jobGroup, String jobName){
        return timerJobBiz.resumeTimerJob(jobGroup, jobName);
    }

    /**
     * 立即执行某任务一次
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse triggerJob(String jobGroup, String jobName){
        return timerJobBiz.triggerJob(jobGroup, jobName);
    }

    /**
     * 尝试把任务添加到定时计划中，对于在数据库中有，但是在quartz中没有被安排定时的任务，可以调用此方法进行安排任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse tryScheduleJob(String jobGroup, String jobName){
        return timerJobBiz.tryScheduleJob(jobGroup, jobName);
    }

    /**
     * 删除定时任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse deleteTimerJob(String jobGroup, String jobName){
        return timerJobBiz.deleteTimerJob(jobGroup, jobName);
    }

    /**
     * 根据组名、任务名取得任务，数据直接从数据库中取出
     * @param jobGroup
     * @param jobName
     * @return
     */
    public TimerJob getTimerJobByKey(String jobGroup, String jobName){
        return timerJobBiz.getTimerJobBeanByJobGroupAndJobName(jobGroup, jobName);
    }

    /**
     * 取得所有已经安排定时的任务，包括正常状态中的和暂停状态中的，数据直接从数据库中取出
     * @return
     */
    public List<TimerJob> listTimerJob(String jobGroup){
        Map<String, Object> filter = new HashMap<>();
        if(! StringUtil.isEmpty(jobGroup)){
            filter.put("jobGroup", jobGroup);
        }
        return timerJobBiz.listTimerJob(filter);
    }

}
