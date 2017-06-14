package com.cyf.center.common.bean;

import com.cyf.base.common.bean.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title：定时任务vo
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/8 14:24
 */
public class TimerJob extends BaseEntity<Long> {
	public final static int CRON_JOB_TYPE = 1;
	public final static int SIMPLE_JOB_TYPE = 2;

	//columns START
	//主键id
	private Long id;
	//任务名称
	private String jobName;
	//任务分组
	private String jobGroup;
	//任务类型(1=cron任务 2=常规重复性任务)
	private Integer jobType;
	/**
	 * 任务要发送到的MQ目的地,默认值是
	 *
	 * @see #genDefaultDestination(String, String)
	 * 这个方法生成的
	 */
	private String destination;
	//任务参数(json格式)
	private String jobParamJson;
	//cron表达式，如果该字段有值，即会被添加为Cron类型的任务而忽略repeatCount、interval字段的值
	private String cronExpression;
	//任务开始时间
	private Date startTime;
	//任务结束时间
	private Date endTime;
	//重复的次数(等于-1则一直重复下去)
	private Integer repeatCount;
	//重复间隔时间(单位：秒)
	private Integer intervals;
	//任务描述
	private String jobDesc;
	//任务状态(1=正常 2=暂停 3=缓停(次数或结束时间到达后停止))
	private Integer jobStatus = 1;
	//是否删除(0=否 1=是)
	private Integer deleted = 0;
	//创建时间
	private Date createTime;
	//更新时间
	private Date updateTime;

	//是否在quartz的定时任务列表里面
	private Boolean isScheduling;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Integer getJobType() {
		return jobType;
	}

	public void setJobType(Integer jobType) {
		this.jobType = jobType;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getJobParamJson() {
		return jobParamJson;
	}

	public void setJobParamJson(String jobParamJson) {
		this.jobParamJson = jobParamJson;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Integer getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(Integer repeatCount) {
		this.repeatCount = repeatCount;
	}

	public Integer getIntervals() {
		return intervals;
	}

	public void setIntervals(Integer intervals) {
		this.intervals = intervals;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public Integer getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(Integer jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Boolean getIsScheduling() {
		return isScheduling;
	}

	public void setIsScheduling(Boolean isScheduling) {
		this.isScheduling = isScheduling;
	}

	/**
	 * 只在某个时间点执行一次的任务
	 * repeatCount=null，endTime=null，internal=null 的就会被认为是一次性任务
	 *
	 * @param startTime 任务开始时间
	 * @return
	 */
	public void toOnceJob(Date startTime) {
		initJobFieldValue();
		this.setStartTime(startTime);
		this.setRepeatCount(0);
		this.setJobStatus(3);//运行完毕之后即停止
		this.setJobType(SIMPLE_JOB_TYPE);//常规重复性任务
	}

	/**
	 * 在某个时间开始，按某个频率执行多少次之后即停止的任务
	 *
	 * @param startTime   任务开始时间
	 * @param repeatCount 任务重复的次数
	 * @param interval    任务重复间隔
	 * @return
	 */
	public void toSimpleJob(Date startTime, Integer repeatCount, Integer interval) {
		initJobFieldValue();
		this.setStartTime(startTime);
		this.setRepeatCount(repeatCount);
		this.setIntervals(interval);
		this.setJobStatus(3);//运行完毕之后即停止
		this.setJobType(SIMPLE_JOB_TYPE);//常规重复性任务
	}

	/**
	 * 在某个时间段按某个频率执行任务
	 *
	 * @param startTime
	 * @param endTime
	 * @param interval
	 * @return
	 */
	public void toSimpleJob(Date startTime, Date endTime, Integer interval) {
		initJobFieldValue();
		this.setStartTime(startTime);
		this.setEndTime(endTime);
		this.setIntervals(interval);
		this.setJobStatus(3);//运行完毕之后即停止
		this.setRepeatCount(-1);
		this.setJobType(SIMPLE_JOB_TYPE);//常规重复性任务
	}

	/**
	 * 在某个时间点开始，按某个频率一直执行下去的任务
	 *
	 * @param startTime
	 * @param interval
	 * @return
	 */
	public void toSimpleJob(Date startTime, Integer interval) {
		initJobFieldValue();
		this.setStartTime(startTime);
		this.setIntervals(interval);
		this.setRepeatCount(-1);
		this.setJobType(SIMPLE_JOB_TYPE);//常规重复性任务
	}

	/**
	 * 立即开始并按cron表达式规则一直重复下去的cron类型任务
	 *
	 * @param cronExpression
	 * @return
	 * @see #toCronJob(String, Date, Date)
	 */
	public void toCronJob(String cronExpression) {
		toCronJob(cronExpression, new Date());
	}

	/**
	 * 指定开始时间并按cron表达式规则一直重复下去的cron类型任务
	 *
	 * @param cronExpression
	 * @param startTime
	 * @see #toCronJob(String, Date, Date)
	 */
	public void toCronJob(String cronExpression, Date startTime) {
		toCronJob(cronExpression, startTime, null);
	}

	/**
	 * 指定开始、结束时间，并按cron表达式规则执行的cron类型任务(类似于 Linux Crontab 的定时任务)
	 * <p>
	 * 注意：
	 * 1、这里的任务表达式跟Linux的Crontab定时任务表达式并不一样，可使用
	 *
	 * @param cronExpression
	 * @param startTime
	 * @param endTimer
	 * @see #com.cyf.common.utils.CronExpressionUtil 来生成
	 */
	public void toCronJob(String cronExpression, Date startTime, Date endTimer) {
		initJobFieldValue();
		this.setJobType(CRON_JOB_TYPE);//cron任务
		this.setCronExpression(cronExpression);//cron表达式
		this.setStartTime(startTime);
		this.setEndTime(endTimer);
		this.setDestination(genDefaultDestination(this.jobGroup, this.jobName));
	}

	public boolean isSimpleJob() {
		return this.jobType != null && this.jobType.equals(SIMPLE_JOB_TYPE);
	}

	public boolean isCronJob() {
		return this.jobType != null && this.jobType.equals(CRON_JOB_TYPE);
	}


	public static TimerJob newInstance(String jobGroup, String jobName) {
		return newInstance(jobGroup, jobName, genDefaultDestination(jobGroup, jobName));
	}

	public static TimerJob newInstance(String jobGroup, String jobName, String destination) {
		TimerJob jobBean = new TimerJob();
		jobBean.setJobGroup(jobGroup);
		jobBean.setJobName(jobName);
		jobBean.setDestination(destination);
		if (destination == null || destination.trim().length() <= 0) {
			jobBean.setDestination(genDefaultDestination(jobGroup, jobName));
		}
		return jobBean;
	}

	/**
	 * 私有方法，清空跟job相关的属性的值
	 */
	private void initJobFieldValue() {
		this.setStartTime(null);
		this.setEndTime(null);
		this.setRepeatCount(null);
		this.setIntervals(null);
		this.setJobType(null);
	}

	/**
	 * @return
	 */
	private static String genDefaultDestination(String jobGroup, String jobName) {
		return jobGroup + "_" + jobName;
	}
}