/**
 * Title: PostFormRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年11月23日 上午11:15:45
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.request;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.builder.PostFormBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.handler.JsonResponseHandler;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/** 
 * <pre>
 * ClassName: PostFormRequest
 * Description:form提交数据请求
 * Create by: 罗旭东
 * Date: 2016年11月23日 上午11:15:45
 * </pre>
 */
public class PostFormRequest extends AsyncHttpRequest {
	protected Map<String, String> mFormMap = null;
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;
	
	public PostFormRequest(PostFormBuilder builder) {
		super(builder);
	}
	
	@Override
	public Request buildRequest(RequestCallable callable) {
		FormBody.Builder builder = new FormBody.Builder();
        addParams(builder);
        FormBody formBody = builder.build();
		return mBuilder.post(formBody).build();
	}

	@Override
	public AsyncHttpTask build() {
		initRequest();
		return new AsyncHttpTask(this);
	}

	/**
	 * 添加form表单参数
	 * @param builder
	 */
	private void addParams(FormBody.Builder builder) {
		if (mFormMap != null) {
			for (String key : mFormMap.keySet()) {
				builder.add(key, mFormMap.get(key));
			}
		}
	}

	@Override
	public ResponseHandler buildResponseHandler(RequestCallable callable) {
		return new JsonResponseHandler(getResponseClazz(), getResponseInterceptor(), callable);
	}

	public JsonResponseInterceptor getResponseInterceptor() {
		return mResponseInterceptor;
	}

	public Class getResponseClazz() {
		return mResponseClazz;
	}

	public void setFormMap(Map<String, String> formMap) {
		mFormMap = formMap;
	}

	public void setResponseInterceptor(JsonResponseInterceptor responseInterceptor) {
		mResponseInterceptor = responseInterceptor;
	}

	public void setResponseClazz(Class responseClazz) {
		mResponseClazz = responseClazz;
	}
}
