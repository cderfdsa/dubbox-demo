package com.cyf.shop.web.message.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cyf.center.common.bean.TimerJob;
import com.cyf.center.common.business.service.TimerService;
import com.cyf.base.common.bean.AjaxResult;
import com.cyf.base.common.utils.CronExpressionUtil;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.base.common.utils.StringUtil;
import com.cyf.base.common.utils.TimeUtil;
import com.cyf.shop.web.message.vo.TimerJobVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/5/4
 */
@Controller
@RequestMapping("message/timer")
public class TimerController {
    @Reference
    TimerService timerService;

    @RequestMapping("timerJobList")
    public ModelAndView timerList(){
        ModelAndView mv = new ModelAndView();
        mv.addObject("timerJobList", JsonUtil.toStringBroswer(timerService.listTimerJob(null)));
        return mv;
    }

    @RequestMapping("addTimerJobView")
    public ModelAndView addTimerJobView(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }

    @ResponseBody
    @RequestMapping("addTimerJobSave")
    public AjaxResult addTimerJobSave(TimerJobVo jobVo){
        TimerJob timerJob = TimerJob.newInstance(jobVo.getJobGroup(), jobVo.getJobName(), jobVo.getDestination());
        timerJob.setJobDesc(jobVo.getJobDesc());
        Date startTime = StringUtil.isEmpty(jobVo.getStartTime()) ? null : TimeUtil.parseDate(jobVo.getStartTime(), "yyyy-MM-dd HH:mm:ss");
        Date endTime = StringUtil.isEmpty(jobVo.getEndTime()) ? null : TimeUtil.parseDate(jobVo.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        if(TimerJobVo.ONCE_SIMPLE_JOB.equals(jobVo.getJobType())){
            timerJob.toOnceJob(startTime);
        }else if(TimerJobVo.REPEAT_COUNT_SIMPLE_JOB.equals(jobVo.getJobType())){
            timerJob.toSimpleJob(startTime, jobVo.getRepeatCount(), jobVo.getInterval());
        }else if(TimerJobVo.REPEAT_PERIOD_SIMPLE_JOB.equals(jobVo.getJobType())){
            timerJob.toSimpleJob(startTime, endTime, jobVo.getInterval());
        }else if(TimerJobVo.REPEAT_FOREVER_SIMPLE_JOB.equals(jobVo.getJobType())){
            timerJob.toSimpleJob(startTime, jobVo.getInterval());
        }else if(TimerJobVo.INTERVAL_CRON_JOB.equals(jobVo.getJobType())){
            String cronExpression = "";
            Integer intervalTimeUnit = jobVo.getInterval();
            if("hour".equals(jobVo.getIntervalTimeUnit())){
                cronExpression = CronExpressionUtil.getHourlyExpression(intervalTimeUnit);
            }else if("minute".equals(jobVo.getIntervalTimeUnit())){
                cronExpression = CronExpressionUtil.getMinutelyExpression(intervalTimeUnit);
            }else if("second".equals(jobVo.getIntervalTimeUnit())){
                cronExpression = CronExpressionUtil.getSecondlyExpression(intervalTimeUnit);
            }
            timerJob.toCronJob(cronExpression, startTime, endTime);
        }else if(TimerJobVo.DAILY_CRON_JOB.equals(jobVo.getJobType())){
            String cronExpression = CronExpressionUtil.getDailyExpression(StringUtil.getInteger(jobVo.getHour()),
                    StringUtil.getInteger(jobVo.getMinute()));
            timerJob.toCronJob(cronExpression, startTime, endTime);
        }else if(TimerJobVo.DAILY_PERIOD_CRON_JOB.equals(jobVo.getJobType())){
            String cronExpression = CronExpressionUtil.getDailyPeriodExpression(StringUtil.getInteger(jobVo.getHourStart()),
                    StringUtil.getInteger(jobVo.getHourEnd()), jobVo.getInterval());
            timerJob.toCronJob(cronExpression, startTime, endTime);
        }else if(TimerJobVo.WEEKLY_CRON_JOB.equals(jobVo.getJobType())){
            Integer[] weekdays = {};
            weekdays = jobVo.getWeekdays().toArray(weekdays);
            String cronExpression = CronExpressionUtil.getWeeklyExpression(StringUtil.getInteger(jobVo.getHour()),
                    StringUtil.getInteger(jobVo.getMinute()), weekdays);
            timerJob.toCronJob(cronExpression, startTime, endTime);
        }else if(TimerJobVo.MONTHLY_CRON_JOB.equals(jobVo.getJobType())){
            Integer[] dates = {};
            dates = jobVo.getDates().toArray(dates);
            String cronExpression = CronExpressionUtil.getMonthlyExpression(StringUtil.getInteger(jobVo.getHour()),
                    StringUtil.getInteger(jobVo.getMinute()), dates);
            timerJob.toCronJob(cronExpression, startTime, endTime);
        }else if(TimerJobVo.SELF_DEFINED_CRON_JOB.equals(jobVo.getJobType())){
            timerJob.toCronJob(jobVo.getCronExpression(), startTime, endTime);
        }
        return AjaxResult.convert(timerService.addTimerJob(timerJob));
    }

    @RequestMapping("timerJobDetail")
    public ModelAndView timerJobDetail(String jobGroup, String jobName){
        ModelAndView mv = new ModelAndView();
        TimerJob timerJob = timerService.getTimerJobByKey(jobGroup, jobName);
        mv.addObject("timerJob", JsonUtil.toStringBroswer(timerJob));
        return mv;
    }

    @ResponseBody
    @RequestMapping("pauseTimerJob")
    public AjaxResult pauseTimerJob(String jobGroup, String jobName){
        return AjaxResult.convert(timerService.pauseTimerJob(jobGroup, jobName));
    }

    @ResponseBody
    @RequestMapping("resumeTimerJob")
    public AjaxResult resumeTimerJob(String jobGroup, String jobName){
        return AjaxResult.convert(timerService.resumeTimerJob(jobGroup, jobName));
    }

    @ResponseBody
    @RequestMapping("executeOnceNow")
    public AjaxResult executeOnceNow(String jobGroup, String jobName){
        return AjaxResult.convert(timerService.triggerJob(jobGroup, jobName));
    }

    @ResponseBody
    @RequestMapping("tryScheduleJob")
    public AjaxResult tryScheduleJob(String jobGroup, String jobName){
        return AjaxResult.convert(timerService.tryScheduleJob(jobGroup, jobName));
    }

    @ResponseBody
    @RequestMapping("deleteTimerJob")
    public AjaxResult deleteTimerJob(String jobGroup, String jobName){
        System.out.println(jobGroup+jobName);
        return AjaxResult.convert(timerService.deleteTimerJob(jobGroup, jobName));
    }

}
