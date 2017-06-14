package com.cyf.base.common.bean;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @Title：分页查询参数
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/4/27
 */
public class PageParam implements Serializable {
    private static final long serialVersionUID = 6297178964005032338L;
    private int pageCurrent = 1; // 当前页数，默认为1，必须设置默认值，否则分页查询时容易报空指针异常
    private int pageSize = 20; // 每页记录数，默认为2，必须设置默认值，否则分页查询时容易报空指针异常
    private int totalRecord;//总记录数，不作为参数，而是返回给web页面时使用
    private String sortColumns;//排序字段及排序方向

    private Boolean isScroll = false;//elasticsearch是否需要使用scroll方式分页查询
    private String scrollId;//elasticsearch通过scroll方式分页查询时用到的参数
    private long timeValue = 6;//使用scroll分页时的持续时间，默认是6
    private TimeUnit timeUnit = TimeUnit.SECONDS;//使用scroll分页时的持续时间单位，默认是秒

    public PageParam(){
    }

    public int getPageCurrent() {
        return pageCurrent;
    }

    public void setPageCurrent(int pageCurrent) {
        this.pageCurrent = pageCurrent;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public String getSortColumns() {
        return sortColumns;
    }

    public void setSortColumns(String sortColumns) {
        this.sortColumns = sortColumns;
    }

    public String getScrollId() {
        return scrollId;
    }

    /**
     * 设置scrollId，如果是scroll的第一次查询，则设置一个空值即可
     * @param scrollId
     */
    public void setScrollId(String scrollId) {
        this.isScroll = true;//如果有设置ScrollId，则说明用户需要使用scroll模式
        this.scrollId = scrollId;
    }

    public long getTimeValue() {
        return timeValue;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public Boolean isScroll() {
        return isScroll;
    }

    /**
     * 设置毫秒时间
     * @param milliseconds
     */
    public void setMillisTimeValue(long milliseconds){
        this.timeValue = milliseconds;
        this.timeUnit = TimeUnit.MILLISECONDS;
    }

    /**
     * 设置秒
     * @param seconds
     */
    public void setSecondTimeValue(long seconds){
        this.timeValue = seconds;
        this.timeUnit = TimeUnit.SECONDS;
    }

    /**
     * 设置分钟
     * @param minutes
     */
    public void setMinuteTimeValue(long minutes){
        this.timeValue = minutes;
        this.timeUnit = TimeUnit.MINUTES;
    }

    /**
     * 设置天
     * @param days
     */
    public void setDayTimeValue(long days){
        this.timeValue = days;
        this.timeUnit = TimeUnit.DAYS;
    }

    public static PageParam getInstance(int pageCurrent, int pageSize){
        PageParam pageParam = new PageParam();
        pageParam.setPageCurrent(pageCurrent);
        pageParam.setPageSize(pageSize);
        return pageParam;
    }

    public static PageParam getInstance(int pageCurrent, int pageSize, String sortColumns){
        PageParam pageParam = new PageParam();
        pageParam.setPageCurrent(pageCurrent);
        pageParam.setPageSize(pageSize);
        pageParam.setSortColumns(sortColumns);
        return pageParam;
    }
}
