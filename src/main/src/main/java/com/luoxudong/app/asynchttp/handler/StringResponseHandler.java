/**
 * Title: StringResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年11月22日 下午2:46:50
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.handler;

import java.io.UnsupportedEncodingException;

import com.luoxudong.app.asynchttp.AsyncHttpConst;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.callable.StringRequestCallable;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * <pre>
 * ClassName: StringResponseHandler
 * Description:返回结果为字符串类型的回调
 * Create by: 罗旭东
 * Date: 2016年11月22日 下午2:46:50
 * </pre>
 */
public class StringResponseHandler extends ResponseHandler {
	private final String TAG = StringResponseHandler.class.getSimpleName();
	
	/**
	 * @param callable
	 */
	public StringResponseHandler(RequestCallable callable) {
		super(callable);
	}
	
	@Override
	protected void onSuccess(byte[] buffer) {
		try {
			onSuccess(buffer == null ? null : new String(buffer, AsyncHttpConst.HTTP_ENCODING));
		} catch (UnsupportedEncodingException e) {
			AsyncHttpLog.e(TAG, "不支持该编码");
		}
	}
	
	protected void onSuccess(String responseBody) {
		AsyncHttpLog.i(TAG, responseBody);
		
    	if (mCallable != null && mCallable instanceof StringRequestCallable) {
			((StringRequestCallable)mCallable).onSuccess(responseBody);
		} else {
			AsyncHttpLog.w(TAG, "回调类型错误！");
		}
	}

}
