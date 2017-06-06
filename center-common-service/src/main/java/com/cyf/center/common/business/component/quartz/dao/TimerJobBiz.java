package com.cyf.center.common.business.component.quartz.dao;

import com.cyf.base.common.utils.StringUtil;
import com.cyf.center.common.bean.TimerJob;
import com.cyf.center.common.business.component.quartz.job.JobManager;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.enums.DeleteEnum;
import com.cyf.base.common.utils.LoggerWrapper;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作用：对任务业务操作，包括：添加、重定时、暂停、删除
 * 说明：
 *     1、会同时往数据库、Quartz框架中操作，保证两边的数据一致性
 *     2、如果人为的操作数据库，会导致数据库、Quartz框架两边的数据不一致，所以不建议手动去操作数据库
 * Created by chenyf on 2017/3/12.
 */
@Transactional
@Component
public class TimerJobBiz {
    LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(TimerJobBiz.class);

    @Autowired
    TimerJobDao timerJobDao;
    @Autowired
    JobManager jobManager;

    /**
     * 加上@PostConstruct注解之后，spring启动的时候会自动调用此方法，把数据库里面有效的定时任务添加到定时器里面，如果担心分布式问题，
     * 则需要把Quartz配置为集群模式，具体做法参考Quartz官方文档
     * @throws Exception
     */
    @PostConstruct
    public void loadJobs() throws Exception{
        Integer pageCurrent = 1;
        Integer pageSize = 200;
        Integer currentPageCount = 0;

        do{
            currentPageCount = 0;
            BaseResponse<List<TimerJob>> response = listDbActiveJobByPage(pageCurrent, pageSize);
            List<TimerJob> timerJobList = response.getData();
            if(timerJobList!=null && !timerJobList.isEmpty()){
                currentPageCount = timerJobList.size();
                for(TimerJob timerJob : timerJobList){
                    if(! timerJob.getJobStatus().equals(1))
                        continue;

                    if(! jobManager.checkJobExist(timerJob)){
                        jobManager.addTimerJob(timerJob);
                    }
                }
            }

            pageCurrent ++;
        }while(currentPageCount >= pageSize);
    }

    public BaseResponse<List<TimerJob>> listDbActiveJobByPage(Integer pageCurrent, Integer pageSize){
        String sortColumns = "id ASC";
        Map<String, Object> filter = new HashMap<>();
        if(filter == null) new HashMap<>();
        filter.put("deleted", DeleteEnum.UNDELETED.getValue());
        PageParam pageParam = PageParam.getInstance(pageCurrent, pageSize);
        pageParam.setSortColumns(sortColumns);
        return timerJobDao.listTimerJobExact(filter, pageParam);
    }

    /**
     * 根据多条件精确查询
     * @param filter
     * @return
     */
    public List<TimerJob> listTimerJob(Map<String, Object> filter){
        if(filter == null) new HashMap<>();
        filter.put("deleted", DeleteEnum.UNDELETED.getValue());
        return timerJobDao.listTimerJobExact(filter);
    }

    /**
     * 根据任务的Key获取任务的记录，会直接从数据库读取数据
     * @param jobGroup
     * @param jobName
     * @return
     */
    public TimerJob getTimerJobBeanByJobGroupAndJobName(String jobGroup, String jobName){
        TimerJob timerJob = timerJobDao.getTimerJobByJobGroupAndJobName(jobGroup, jobName);
        if(timerJob != null){
            try{
                boolean isScheduling = jobManager.checkJobExist(timerJob);
                timerJob.setIsScheduling(isScheduling);
            }catch(Exception e){
                logger.error(e);
            }
        }
        return timerJob;
    }

    /**
     * 添加任务，会同时往数据库、Quartz添加任务，可保证两者事务性，如果数据库里面有记录而Quartz里面没有，则只往Quartz中添加任务，并且会更新数据库中记录
     * @param timerJob
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse addTimerJob(TimerJob timerJob){
        TimerJob jobBean = timerJobDao.getTimerJobByJobGroupAndJobName(timerJob.getJobGroup(), timerJob.getJobName());

        boolean isJobExistInDb = jobBean != null;
        if(isJobExistInDb){//如果db中已经存在，则报错
            return BaseResponse.fail("JobGroup="+timerJob.getJobGroup()+", JobName="+timerJob.getJobName()+" 的任务已存在，请勿重复添加");
        }

        boolean isJobExistInQuartz;
        try{
            isJobExistInQuartz = jobManager.checkJobExist(timerJob);
        }catch(SchedulerException e){
            return BaseResponse.fail("添加任务时发生异常，"+e.getMessage());
        }

        if(isJobExistInQuartz){ //db中不存在，quartz中存在，则删除在quartz中的任务
            BaseResponse response = jobManager.deleteTimerJob(timerJob.getJobGroup(), timerJob.getJobName());
            if(response.isError()){
                return BaseResponse.fail("添加任务时，发现quartz中遗留有相同的任务，尝试删除任务时失败！");
            }
        }

        boolean result = timerJobDao.createTimerJob(timerJob);//创建成功的话，timerBean对象里面已经有id了
        if(result == false){
            return BaseResponse.fail("添加任务失败！");
        }
        BaseResponse response = jobManager.addTimerJob(timerJob);
        if(response.isError()){
            throw new RuntimeException("添加任务失败，"+response.getMessage());
        }else{
            return BaseResponse.success();
        }
    }

    /**
     * 重新安排任务的定时规则，会同时更新数据库、Quartz的任务，可保证两者数据一致性
     * @param timerBean
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse rescheduleTimerJob(TimerJob timerBean){
        TimerJob jobBean = timerJobDao.getTimerJobByJobGroupAndJobName(timerBean.getJobGroup(), timerBean.getJobName());
        if(jobBean == null){
            return BaseResponse.fail("JobGroup="+timerBean.getJobGroup()+", JobName="+timerBean.getJobName()+" 的任务db中不存在");
        }

        timerBean.setId(jobBean.getId());
        boolean result = timerJobDao.updateTimerJob(timerBean);
        if(result == false){
            return BaseResponse.fail("重定时任务失败");
        }else{
            BaseResponse response = jobManager.rescheduleTimerJob(timerBean);
            if(response.isError()){
                throw new RuntimeException("重定时任务失败，"+response.getMessage());
            }else{
                return BaseResponse.success();
            }
        }
    }

    /**
     * 暂停任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse pauseTimerJob(String jobGroup, String jobName){
        TimerJob timerBean = timerJobDao.getTimerJobByJobGroupAndJobName(jobGroup, jobName);
        if(timerBean == null){
            return BaseResponse.fail("JobGroup="+jobGroup+", JobName="+jobName+" 的任务不存在");
        }else{
            timerBean.setJobStatus(2);
            boolean result = timerJobDao.updateTimerJob(timerBean);
            if(result == false){
                return BaseResponse.fail("暂停任务失败");
            }else{
                BaseResponse response = jobManager.pauseTimerJob(jobGroup, jobName);
                if(response.isError()){
                    throw new RuntimeException("暂停任务失败，"+response.getMessage());
                }else{
                    return BaseResponse.success();
                }
            }
        }
    }

    /**
     * 恢复被暂停的任务,如果任务在quartz还不存在,则添加此任务到quartz
     * @param jobGroup
     * @param jobName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse resumeTimerJob(String jobGroup, String jobName){
        TimerJob timerJob = timerJobDao.getTimerJobByJobGroupAndJobName(jobGroup, jobName);
        if(timerJob == null){
            return BaseResponse.fail("JobGroup="+jobGroup+", JobName="+jobName+" 的任务不存在");
        }else{
            boolean isJobExist;
            try{
                isJobExist = jobManager.checkJobExist(timerJob);
            }catch(Exception e){
                logger.error(e);
                return BaseResponse.fail("恢复任务时发生系统异常！");
            }

            timerJob.setJobStatus(1);
            boolean result = timerJobDao.updateTimerJob(timerJob);
            if(result == false){
                return BaseResponse.fail("恢复任务失败");
            }else if(isJobExist){ //如果任务已存在，则恢复任务
                BaseResponse response = jobManager.resumeTimerJob(jobGroup, jobName);
                if(response.isError()){
                    throw new RuntimeException("恢复任务失败，"+response.getMessage());
                }else{
                    return BaseResponse.success();
                }
            }else{ //如果任务还不存在，则添加到quartz定时任务中
                BaseResponse response = jobManager.addTimerJob(timerJob);
                if(response.isError()){
                    throw new RuntimeException("尝试添加任务失败，"+response.getMessage());
                }else{
                    return BaseResponse.success();
                }
            }
        }
    }

    /**
     * 立即执行某任务一次
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse triggerJob(String jobGroup, String jobName){
        if(StringUtil.isEmpty(jobGroup)){
            return BaseResponse.fail("jobGroup不能为空");
        }else if(StringUtil.isEmpty(jobName)){
            return BaseResponse.fail("jobName不能为空");
        }
        TimerJob timerJob = timerJobDao.getTimerJobByJobGroupAndJobName(jobGroup, jobName);
        if(timerJob == null){
            return BaseResponse.fail("JobGroup="+jobGroup+", JobName="+jobName+" 的任务不存在");
        }else if(timerJob.getJobStatus().equals(2)){
            return BaseResponse.fail("JobGroup="+jobGroup+", JobName="+jobName+" 的任务暂停中，不能执行");
        }else{
            return jobManager.triggerJob(jobGroup, jobName);//jobManager.triggerJob内部会检查任务是否存在
        }
    }

    /**
     * 尝试把任务添加到定时计划中，对于在数据库中有，但是在quartz中没有被安排定时的任务，可以调用此方法进行安排任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    public BaseResponse tryScheduleJob(String jobGroup, String jobName){
        if(StringUtil.isEmpty(jobGroup)){
            return BaseResponse.fail("jobGroup不能为空");
        }else if(StringUtil.isEmpty(jobName)){
            return BaseResponse.fail("jobName不能为空");
        }
        TimerJob timerJob = timerJobDao.getTimerJobByJobGroupAndJobName(jobGroup, jobName);
        if(timerJob == null){
            return BaseResponse.fail("JobGroup="+jobGroup+", JobName="+jobName+" 的任务不存在");
        }else{
            boolean isExist;
            try{
                isExist = jobManager.checkJobExist(timerJob);
            }catch(Exception e){
                logger.error(e);
                return BaseResponse.fail("发生异常");
            }

            if(isExist){
                return BaseResponse.fail("JobGroup="+jobGroup+", JobName="+jobName+" 的任务已在定时计划中，勿重复添加");
            }else{
                return jobManager.addTimerJob(timerJob);
            }
        }
    }

    /**
     * 删除任务
     * @param jobGroup
     * @param jobName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse deleteTimerJob(String jobGroup, String jobName){
        TimerJob timerJob = timerJobDao.getTimerJobByJobGroupAndJobName(jobGroup, jobName);
        if(timerJob != null){
            boolean result = timerJobDao.deleteTimerJob(timerJob.getId());
            if(result == false){
                return BaseResponse.fail("删除任务失败");
            }
        }
        BaseResponse response = jobManager.deleteTimerJob(jobGroup, jobName);
        if(response.isError()){
            throw new RuntimeException("删除任务失败，"+response.getMessage());
        }else{
            return BaseResponse.success();
        }
    }
}
