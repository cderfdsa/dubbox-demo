/*
 * Powered By [o2omedical.com]
 */
package com.cyf.center.common.business.component.quartz.dao;

import com.cyf.center.common.bean.TimerJob;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.dao.MyBatisDao;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TimerJobDao extends MyBatisDao<TimerJob, Long> {

    public BaseResponse<List<TimerJob>> listTimerJobExact(Map<String, Object> filter, PageParam pageParam){
        return this.listByCond(filter, pageParam);
    }


    public List<TimerJob> listTimerJobExact(Map<String, Object> filter){
        return this.listByCond(filter);
    }

    /**
     * 只会找出没有被删除的
     * @param jobGroup
     * @param jobName
     * @return
     */
    public TimerJob getTimerJobByJobGroupAndJobName(String jobGroup, String jobName){
        Map<String, Object> filter = new HashMap<>();
        filter.put("jobGroup", jobGroup);
        filter.put("jobName", jobName);
        filter.put("deleted", 0);
        return this.getOne(filter);
    }

    public List<TimerJob> listTimerJobByGroup(String jobGroup){
        Map<String, Object> filter = new HashMap<>();
        filter.put("jobGroup", jobGroup);
        return this.listByCond(filter);
    }

    public TimerJob getTimerJobById(Long jobId){
        return this.getById(jobId);
    }

    public List<TimerJob> listTimerJobByIdList(List<Long> jobIdList){
        return this.listByIdList(jobIdList);
    }

    public boolean updateTimerJob(TimerJob jobBean){
        if(jobBean.getId() == null) return false;
        jobBean.setUpdateTime(new Date());
        Integer count = this.updateIfNotNull(jobBean);
        if(count> 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean createTimerJob(TimerJob jobBean){
        jobBean.setCreateTime(new Date());
        jobBean.setUpdateTime(new Date());
        if(jobBean.getJobStatus() == null) jobBean.setJobStatus(1);//有效
        if(jobBean.getDeleted() == null) jobBean.setDeleted(0);//有效

        Integer count = this.insert(jobBean);
        if(count> 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除记录
     * @param id
     * @return
     */
    public boolean deleteTimerJob(Long id){
        Integer count = this.deleteById(id);
        if(count > 0){
            return true;
        }else{
            return false;
        }
    }
}
