package com.cyf.test.service;

import com.cyf.center.common.bean.TimerJob;

import java.util.List;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/7 9:08
 */
public interface TimerTestService {

    public void addTimerJob();

    /**
     * 更新任务定时触发规则
     */
    public void rescheduleTimerJob();

    /**
     * 暂停任务
     */
    public void pauseTimerJob();

    /**
     * 恢复被暂停任务
     */
    public void resumeTimerJob();

    /**
     * 删除任务
     */
    public void deleteTimerJob();

    /**
     * 根据组名、任务名取得任务
     */
    public TimerJob getTimerJobByKey();

    /**
     * 取得所有已经安排的任务，包括正常状态中的和暂停状态中的
     * @return
     */
    public List<TimerJob> getScheduledTimerJobs();
}
