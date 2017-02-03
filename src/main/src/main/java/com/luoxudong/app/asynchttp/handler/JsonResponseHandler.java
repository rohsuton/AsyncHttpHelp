/**
 * Title: JsonResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年12月30日 下午3:28:02
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.handler;

import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

import com.luoxudong.app.asynchttp.AsyncHttpConst;
import com.luoxudong.app.asynchttp.callable.JsonRequestCallable;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.callable.StringRequestCallable;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * <pre>
 * ClassName: JsonResponseHandler
 * Description:json结果请求回调
 * Create by: 罗旭东
 * Date: 2016年12月30日 下午3:28:02
 * </pre>
 */
public class JsonResponseHandler extends StringResponseHandler {
	private final String TAG = JsonResponseHandler.class.getSimpleName();
	/** 返回json对应的映射类 */
	private Class mResponseClazz = null;
	/** 返回结果拦截器，可以自定义json解析器 */
	private JsonResponseInterceptor mResponseInterceptor = null;

	public JsonResponseHandler(Class responseClazz, JsonResponseInterceptor responseInterceptor, RequestCallable callable) {
		super(callable);
		mResponseClazz = responseClazz;
		mResponseInterceptor = responseInterceptor;
	}
	
	protected void onSuccess(String responseBody) {
    	if (mCallable != null && mCallable instanceof JsonRequestCallable) {
    		Object rspObject = null;

			AsyncHttpLog.i(TAG, responseBody);

    		if (!TextUtils.isEmpty(responseBody) && mResponseInterceptor != null) {
    			rspObject = mResponseInterceptor.convertJsonToObj(responseBody, mResponseClazz);
    			
    			if (!mResponseInterceptor.checkResponse(rspObject)) {//结果校验为通过
    				((JsonRequestCallable)mCallable).onFailed(mResponseInterceptor.getErrorCode(), mResponseInterceptor.getErrorMsg());
    				return;
    			}
    		}
    		
			((JsonRequestCallable)mCallable).onSuccess(rspObject);
		} else {
			super.onSuccess(responseBody);
		}
	}

}
