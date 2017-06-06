package com.cyf.base.common.bean;

import java.io.Serializable;

/**
 * @Author: chenyf
 * @Date: 2016/8/19
 * @Description: ajax 返回 结构
 * @Version: 1.0
 * @History:
 */
public class AjaxResult<T> implements Serializable {
	private static final long serialVersionUID = 1894648231137314539L;
	// 状态码
	private int statusCode;
	// 返回消息
	private String message;
	// 返回的对象
	private T data;

	// 正确码
	public static final int OK_CODE = 200;
	// 失败码
	public static final int FAIL_CODE = 300;
	// 超时码
	public static final int TIMEOUT_CODE = 301;

	/**
	 * 成功
	 * @param <T>
	 * @return
	 */
	public static <T> AjaxResult<T> success() {
		return success("", null);
	}

	/**
	 * 成功
	 * @param message 提示语
	 * @param <T>
	 * @return
	 */
	public static <T> AjaxResult<T> success(String message) {
		return success(message, null);
	}

	/**
	 * 成功
	 * @param object 返回的数据
	 * @param <T>
	 * @return
	 */
	public static <T> AjaxResult<T> success(T object) {
		return success("", object);
	}

	/**
	 * 成功
	 * @param message 提示语
	 * @param object 返回的数据
	 * @param <T>
	 * @return
	 */
	public static <T> AjaxResult<T> success(String message, T object) {
		return getInstance(message, OK_CODE, object);
	}

	/**
	 * 失败
	 * @param message 提示语
	 * @param <T>
	 * @return
	 */
	public static <T> AjaxResult<T> fail(String message) {
		return fail(message, null);
	}

    /**
     * 失败
     * @param message 提示语
     * @param object 返回的数据
     * @param <T>
     * @return
     */
	public static <T> AjaxResult<T> fail(String message, T object) {
		return getInstance(message, FAIL_CODE, object);
	}

	/**
	 * 超时
	 * @param message 提示语
	 * @param <T>
	 * @return
	 */
	public static <T> AjaxResult<T> timeout(String message) {
		return getInstance(message, TIMEOUT_CODE, null);
	}

	/**
	 * 把 BaseResponse
	 * @param response
	 * @param <T>
	 * @return
	 */
	public static <T> AjaxResult<T> convert(BaseResponse<T> response){
		if(response == null){
			return AjaxResult.fail("response is null");
		}else if(response.isSuccess()){
			AjaxResult<T> ajaxResult = AjaxResult.success();
			ajaxResult.setMessage(response.getMessage());
			ajaxResult.setData(response.getData());
			return ajaxResult;
		}else{
			AjaxResult<T> ajaxResult = AjaxResult.fail(response.getMessage());
			ajaxResult.setData(response.getData());
			return ajaxResult;
		}
	}

	private static <T> AjaxResult<T> getInstance(String message, int code, T object) {
		AjaxResult<T> ajaxResult = new AjaxResult<T>();
		ajaxResult.setMessage(message);
		ajaxResult.setStatusCode(code);
		ajaxResult.setData(object);
		return ajaxResult;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
