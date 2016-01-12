/**
 * Title: JsonRequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月14日 下午12:04:05
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.text.TextUtils;

import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.interceptor.JsonRequestInterceptor;
import com.luoxudong.app.asynchttp.model.BaseRequest;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: JsonRequestParams
 * Description:Json请求参数
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午12:04:05
 */
public class JsonRequestParams extends RequestParams {
	private static final String TAG = JsonRequestParams.class.getSimpleName();
	
	/** 请求参数对象 */
	protected Object mRequestJsonObj = null;

	/** json请求拦截器 */
	private JsonRequestInterceptor mJsonRequestInterceptor = null;
	
	@Override
	public HttpEntity getEntity() {
		StringEntity entity = null;
    	String requestData = null;
		if (mJsonRequestInterceptor != null){
			try {
				requestData = mJsonRequestInterceptor.convertJsonToObj(getRequestJsonObj());
			} catch (Exception e) {
				throw new AsyncHttpException(AsyncHttpExceptionCode.jsonParseException.getErrorCode(), e);
			}
		}
		
    	AsyncHttpLog.i(TAG, requestData);
    	try {
    		entity = new StringEntity(requestData, AsyncHttpConst.HTTP_ENCODING);
    		
    		if (!TextUtils.isEmpty(getContentType())){
				entity.setContentType(getContentType());
			}
    		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	return entity;
	}

	public Object getRequestJsonObj() {
		return mRequestJsonObj;
	}

	public void setRequestJsonObj(Object requestJsonObj) {
		this.mRequestJsonObj = requestJsonObj;
	}

	public void setJsonRequestInterceptor(JsonRequestInterceptor jsonRequestInterceptor) {
		mJsonRequestInterceptor = jsonRequestInterceptor;
	}
}
