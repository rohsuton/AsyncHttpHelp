/**
 * Title: AsyncHttpException.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:13:42
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.exception;

import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: AsyncHttpException
 * Description:HTTP网络连接异常处理类
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:13:42
 */
public class AsyncHttpException extends RuntimeException {
	private static final String TAG = AsyncHttpException.class.getName();
	
	private static final long serialVersionUID = 1L;
	
	private int exceptionCode = -1;

	public AsyncHttpException(String message) {
		super(message);
		AsyncHttpLog.e(TAG, message);
	}
	
	public AsyncHttpException(String message, Throwable throwable)
	{
		super(message, throwable);
		AsyncHttpLog.e(TAG, message);
	}
	
	public AsyncHttpException(int exceptionCode, Throwable throwable)
	{
		super(throwable.toString(), throwable);
		setExceptionCode(exceptionCode);
		AsyncHttpLog.e(TAG, exceptionCode + "");
		
	}
	
	public AsyncHttpException(int exceptionCode, String message)
	{
		super(message);
		setExceptionCode(exceptionCode);
		AsyncHttpLog.e(TAG, exceptionCode + "");
		
	}

	public int getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(int exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
}
