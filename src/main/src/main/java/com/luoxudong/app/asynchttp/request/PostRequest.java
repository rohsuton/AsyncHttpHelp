/**
 * Title: PostRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年10月13日 下午7:18:22
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.request;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.ContentType;
import com.luoxudong.app.asynchttp.builder.PostBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.handler.JsonResponseHandler;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/** 
 * <pre>
 * ClassName: PostRequest
 * Description:POST请求
 * Create by: 罗旭东
 * Date: 2016年10月13日 下午7:18:22
 * </pre>
 */
public class PostRequest extends AsyncHttpRequest {
	private String mBody = null;
	private String mContentType = null;
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;

	public PostRequest(PostBuilder builder) {
		super(builder);
	}

	@Override
	public Request buildRequest(RequestCallable callable) {
		if (getContentType() == null) {
			setContentType(ContentType.text.getValue());
        }
		
		if (getBody() == null) {
			setBody("");
		}
		return mBuilder.post(RequestBody.create(MediaType.parse(getContentType()), getBody())).build();
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


	public String getBody() {
		return mBody;
	}

	public void setBody(String body) {
		mBody = body;
	}

	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String contentType) {
		mContentType = contentType;
	}

	public JsonResponseInterceptor getResponseInterceptor() {
		return mResponseInterceptor;
	}

	public Class getResponseClazz() {
		return mResponseClazz;
	}

	public void setResponseInterceptor(JsonResponseInterceptor responseInterceptor) {
		mResponseInterceptor = responseInterceptor;
	}

	public void setResponseClazz(Class responseClazz) {
		mResponseClazz = responseClazz;
	}
}
