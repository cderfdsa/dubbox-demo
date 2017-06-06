package com.cyf.base.common.bean;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @Title：
 * @Description：基础返回信息,可用于service统一返回格式
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/8 14:31
 */
public class BaseResponse<T> implements Serializable {
    // 成功码
    private static final int SUCCESS_CODE = 200;
    // 失败码
    private static final int FAIL_CODE = 300;

    // 状态码
    private int statusCode;
    // 错误码
    private int errorCode;
    // 返回消息
    private String message;
    // 返回的数据
    private T data;

    //当前页数
    private Integer pageCurrent;
    //每页数量
    private Integer pageSize;
    //总记录数
    private Integer totalRecord;

    private String scrollId;//elasticsearch通过scroll方法分页查询时用到的参数

    public void setStatusCode(int status){
        this.statusCode = status;
    }

    public Integer getStatusCode(){
        return this.statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

    public void setData(T data){
        this.data = data;
    }

    public T getData(){
        return this.data;
    }

    public Integer getPageCurrent() {
        return pageCurrent;
    }

    public void setPageCurrent(Integer pageCurrent) {
        this.pageCurrent = pageCurrent;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public String getScrollId() {
        return scrollId;
    }

    public void setScrollId(String scrollId) {
        this.scrollId = scrollId;
    }

    /**
     * 计算当前 data 的长度
     * @return
     */
    public int length(){
        return this.calcDataLength(this.data).intValue();
    }

    /**
     * 如果 totalRecord != null 则直接使用 totalRecord 的值，否则，计算 data 的长度
     * @param data
     */
    public Integer calcDataLength(T data){
        if(data == null){
            return 0;
        }else if(data instanceof Collection){
            return ((Collection) data).size();
        }else if(data instanceof Map){
            return ((Map) data).size();
        }else if(data.getClass().isArray()){
            return Array.getLength(data);
        }else{//其他的 String、Integer、Object等对象则统计计算为1
            return 1;
        }
    }

    /**
     * 成功
     * @return
     */
    public static <T> BaseResponse<T> success(){
        return success("");
    }

    /**
     * 成功
     * @return
     */
    public static <T> BaseResponse<T> success(String message){
        return success(null, message, null);
    }

    /**
     * 成功
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return success(data, null);
    }

    /**
     * 成功
     * @param data 返回的数据
     * @param totalRecord 总记录数
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data, Integer totalRecord){
        return success(data, "", totalRecord);
    }

    public static <T> BaseResponse<T> success(T data, PageParam pageParam, Integer totalRecord){
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setStatusCode(SUCCESS_CODE);
        baseResponse.setErrorCode(0);
        baseResponse.setMessage("");
        baseResponse.setData(data);
        baseResponse.setTotalRecord(totalRecord);
        baseResponse.setPageCurrent(pageParam.getPageCurrent());
        baseResponse.setPageSize(pageParam.getPageSize());
        return baseResponse;
    }

    /**
     * 成功
     * @param totalRecord 总记录数
     * @param message 返回提示信息
     * @param data 返回的数据
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data, String message, Integer totalRecord){
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setStatusCode(SUCCESS_CODE);
        baseResponse.setErrorCode(0);
        baseResponse.setMessage(message);
        baseResponse.setData(data);

        if(totalRecord != null){
            baseResponse.setTotalRecord(totalRecord);
        }else{
            baseResponse.setTotalRecord(baseResponse.calcDataLength(data));
        }
        baseResponse.setPageCurrent(1);
        baseResponse.setPageSize(baseResponse.getTotalRecord());
        return baseResponse;
    }

    /**
     * 失败
     * @param message 提示语
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> fail(String message) {
        return fail(message, null);
    }

    /**
     * 失败
     * @param message 提示语
     * @param data 返回的数据
     * @param
     * @return
     */
    public static <T> BaseResponse<T> fail(String message, T data) {
        return fail(0, message, data);
    }

    /**
     * 失败
     * @param errorCode 错误码
     * @param message 提示语
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> fail(int errorCode, String message) {
        return fail(errorCode, message, null);
    }

    /**
     * 失败
     * @param errorCode
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> fail(int errorCode, String message, T data){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setPageCurrent(0);
        baseResponse.setPageSize(0);
        baseResponse.setStatusCode(FAIL_CODE);
        baseResponse.setErrorCode(errorCode);
        baseResponse.setMessage(message);
        baseResponse.setData(data);
        baseResponse.setTotalRecord(0);
        return baseResponse;
    }

    public Boolean isSuccess(){
        return this.statusCode == SUCCESS_CODE;
    }

    public Boolean isError(){
        return this.statusCode == FAIL_CODE;
    }
}
