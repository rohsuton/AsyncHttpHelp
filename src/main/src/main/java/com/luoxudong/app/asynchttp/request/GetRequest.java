/**
 * Title: GetRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年10月13日 下午3:44:01
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.request;

import okhttp3.Request;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.builder.GetBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.handler.JsonResponseHandler;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;
import com.luoxudong.app.asynchttp.handler.StringResponseHandler;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;

/** 
 * <pre>
 * ClassName: GetRequest
 * Description:Get请求
 * Create by: 罗旭东
 * Date: 2016年10月13日 下午3:44:01
 * </pre>
 */
public class GetRequest extends AsyncHttpRequest {
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;

	public GetRequest(GetBuilder builder) {
		super(builder);
	}

	@Override
	public Request buildRequest(RequestCallable callable) {
		return mBuilder.get().build();
	}

	@Override
	public AsyncHttpTask build() {
		initRequest();
		return new AsyncHttpTask(this);
	}
	
	@Override
	public ResponseHandler buildResponseHandler(RequestCallable callable) {
		return new JsonResponseHandler(getResponseClazz(), getResponseInterceptor(), callable);
	}

	public JsonResponseInterceptor getResponseInterceptor() {
		return mResponseInterceptor;
	}

	public void setResponseInterceptor(JsonResponseInterceptor responseInterceptor) {
		mResponseInterceptor = responseInterceptor;
	}

	public Class getResponseClazz() {
		return mResponseClazz;
	}

	public void setResponseClazz(Class responseClazz) {
		mResponseClazz = responseClazz;
	}
}
