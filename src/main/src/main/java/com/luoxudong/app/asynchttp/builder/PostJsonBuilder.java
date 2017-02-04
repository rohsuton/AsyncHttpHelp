/**
 * Title: PostJsonBuilder.java
 * Description: 请求内容和返回内容都为json对象的构建类
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年12月29日 下午4:17:36
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.interceptor.JsonRequestInterceptor;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;
import com.luoxudong.app.asynchttp.request.PostJsonRequest;

/** 
 * <pre>
 * ClassName: PostJsonBuilder
 * Description:请求内容和返回内容都为json对象的构建类
 * Create by: 罗旭东
 * Date: 2016年12月29日 下午4:17:36
 * </pre>
 */
public class PostJsonBuilder extends RequestBuilder<PostJsonBuilder> {
	/** 请求对象 */
	private Object mReqObj = null;
	/** 请求数据拦截器 */
	private JsonRequestInterceptor mRequestInterceptor = null;
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;
	
	@Override
	public AsyncHttpTask build() {
		PostJsonRequest request = new PostJsonRequest(this);
		request.setResponseInterceptor(mResponseInterceptor);
		request.setResponseClazz(mResponseClazz);
		request.setReqObj(mReqObj);
		request.setRequestInterceptor(mRequestInterceptor);

		initRequest(request);//初始化request
		return request.build();
	}

	public PostJsonBuilder reqObj(Object reqObj) {
		mReqObj = reqObj;
		return this;
	}

	/**
	 * 设置请求对象拦截器，可以设置自定义json解析库
	 * @param requestInterceptor 请求参数拦截器
	 * @return
     */
	public PostJsonBuilder requestInterceptor(JsonRequestInterceptor requestInterceptor) {
		mRequestInterceptor = requestInterceptor;
		return this;
	}

	/**
	 * 设置返回json数据拦截器，拦截返回数据预处理，可以设置自定义json解析库
	 * @param responseInterceptor 返回结果拦截器
	 * @return
	 */
	public PostJsonBuilder responseInterceptor(JsonResponseInterceptor responseInterceptor) {
		mResponseInterceptor = responseInterceptor;
		return this;
	}

	/**
	 * 设置返回json的java类型
	 * @param responseClazz 类型
	 * @return
	 */
	public PostJsonBuilder responseClazz(Class responseClazz) {
		mResponseClazz = responseClazz;
		return this;
	}
}
