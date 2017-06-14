package com.cyf.center.common.business.service;

import com.cyf.center.common.bean.TimerJob;
import com.cyf.base.common.bean.BaseResponse;

import java.util.List;

/**
 * @Title：定时任务服务
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/8
 */
public interface TimerService {

    /**
     * 添加定时任务，添加完毕之后，需要由调用者自行去监听 TimerJob.destination 这个属性里面的消息目的地
     * @param timerBean
     * @return
     */
    public BaseResponse addTimerJob(TimerJob timerBean);

    /**
     * 更新任务定时触发规则
     * @param timerBean
     * @return
     */
    public BaseResponse rescheduleTimerJob(TimerJob timerBean);

    /**
     * 暂停任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse pauseTimerJob(String jobGroup, String jobName);

    /**
     * 恢复被暂停任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse resumeTimerJob(String jobGroup, String jobName);

    /**
     * 立即执行某任务一次
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse triggerJob(String jobGroup, String jobName);

    /**
     * 尝试把任务添加到定时计划中，对于在数据库中有，但是在quartz中没有被安排定时的任务，可以调用此方法进行安排任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse tryScheduleJob(String jobGroup, String jobName);

    /**
     * 删除任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse deleteTimerJob(String jobGroup, String jobName);

    /**
     * 根据组名、任务名取得任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public TimerJob getTimerJobByKey(String jobGroup, String jobName);

    /**
     * 取得所有已经安排的任务，包括正常状态中的和暂停状态中的
     * @return
     */
    public List<TimerJob> listTimerJob(String jobGroup);
}
