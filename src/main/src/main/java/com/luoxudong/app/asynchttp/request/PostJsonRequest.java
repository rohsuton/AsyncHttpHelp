/**
 * Title: PostJsonRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年12月29日 下午4:23:05
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.request;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.ContentType;
import com.luoxudong.app.asynchttp.builder.PostJsonBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.handler.JsonResponseHandler;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;
import com.luoxudong.app.asynchttp.interceptor.JsonRequestInterceptor;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/** 
 * <pre>
 * ClassName: PostJsonRequest
 * Description:提交json数据请求类
 * Create by: 罗旭东
 * Date: 2016年12月29日 下午4:23:05
 * </pre>
 */
public class PostJsonRequest extends AsyncHttpRequest {
	/** 请求对象 */
	private Object mReqObj = null;
	/** 请求数据拦截器 */
	private JsonRequestInterceptor mRequestInterceptor = null;
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;
	
	public PostJsonRequest(PostJsonBuilder builder) {
		super(builder);
		mReqObj = builder.getReqObj();
		mRequestInterceptor = builder.getRequestInterceptor();
		mResponseInterceptor = builder.getResponseInterceptor();
		mResponseClazz = builder.getResponseClazz();
	}
	
	@Override
	public AsyncHttpTask build() {
		initRequest();
		return new AsyncHttpTask(this);
	}

	@Override
	public Request buildRequest(RequestCallable callable) {
		String body = "";
		
		if (getRequestInterceptor() != null && getReqObj() != null) {//需要拦截处理
			body = getRequestInterceptor().convertJsonToObj(getReqObj());
		} else if (getReqObj() != null){
			body = getReqObj().toString();
		}
		
		return mBuilder.post(RequestBody.create(MediaType.parse(ContentType.text.getValue()), body)).build();
	}

	@Override
	public ResponseHandler buildResponseHandler(RequestCallable callable) {
		return new JsonResponseHandler(getResponseClazz(), getResponseInterceptor(), callable);
	}
	
	public Object getReqObj() {
		return mReqObj;
	}

	public JsonRequestInterceptor getRequestInterceptor() {
		return mRequestInterceptor;
	}

	public JsonResponseInterceptor getResponseInterceptor() {
		return mResponseInterceptor;
	}

	public Class getResponseClazz() {
		return mResponseClazz;
	}

}
