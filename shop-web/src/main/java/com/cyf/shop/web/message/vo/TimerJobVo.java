package com.cyf.shop.web.message.vo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/5.
 */
public class TimerJobVo {
    public static final String ONCE_SIMPLE_JOB = "onceSimpleJob";
    public static final String REPEAT_COUNT_SIMPLE_JOB = "repeatCountSimpleJob";
    public static final String REPEAT_PERIOD_SIMPLE_JOB = "repeatPeriodSimpleJob";
    public static final String REPEAT_FOREVER_SIMPLE_JOB = "repeatForeverSimpleJob";
    public static final String INTERVAL_CRON_JOB = "intervalCronJob";
    public static final String DAILY_CRON_JOB = "dailyCronJob";
    public static final String DAILY_PERIOD_CRON_JOB = "dailyPeriodCronJob";
    public static final String WEEKLY_CRON_JOB = "weeklyCronJob";
    public static final String MONTHLY_CRON_JOB = "monthlyCronJob";
    public static final String SELF_DEFINED_CRON_JOB = "selfDefinedCronJob";

    private String jobName;
    private String jobGroup;
    private String jobType;
    private String destination;
    private String jobParam;
    private String startTime;
    private String endTime;
    private Integer repeatCount;
    private Integer interval;
    private String intervalTimeUnit;
    private String jobDesc;

    private String hour;
    private String minute;
    private ArrayList<Integer> weekdays;
    private ArrayList<Integer> dates;

    private String hourStart;
    private String hourEnd;

    private String cronExpression;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getJobParam() {
        return jobParam;
    }

    public void setJobParam(String jobParam) {
        this.jobParam = jobParam;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getIntervalTimeUnit() {
        return intervalTimeUnit;
    }

    public void setIntervalTimeUnit(String intervalTimeUnit) {
        this.intervalTimeUnit = intervalTimeUnit;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public ArrayList<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(ArrayList<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public ArrayList<Integer> getDates() {
        return dates;
    }

    public void setDates(ArrayList<Integer> dates) {
        this.dates = dates;
    }

    public String getHourStart() {
        return hourStart;
    }

    public void setHourStart(String hourStart) {
        this.hourStart = hourStart;
    }

    public String getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(String hourEnd) {
        this.hourEnd = hourEnd;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }
}
