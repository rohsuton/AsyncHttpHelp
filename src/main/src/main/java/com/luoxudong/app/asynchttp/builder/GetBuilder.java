/**
 * Title: GetBuilder.java
 * Description: get请求Builder
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年10月13日 下午2:54:16
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;
import com.luoxudong.app.asynchttp.request.GetRequest;

/** 
 * <pre>
 * ClassName: GetBuilder
 * Description:get请求Builder，支持返回内容为json对象
 * Create by: 罗旭东
 * Date: 2016年10月13日 下午2:54:16
 * </pre>
 */
public class GetBuilder extends RequestBuilder<GetBuilder> {
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;

	@Override
	public AsyncHttpTask build() {
		GetRequest request = new GetRequest(this);
		request.setResponseClazz(mResponseClazz);
		request.setResponseInterceptor(mResponseInterceptor);

		initRequest(request);//初始化request
		return request.build();
	}

	/**
	 * 设置返回json数据拦截器，拦截返回数据预处理，可以设置自定义json解析库
	 * @param responseInterceptor 返回结果拦截器
	 * @return
     */
	public GetBuilder responseInterceptor(JsonResponseInterceptor responseInterceptor) {
		mResponseInterceptor = responseInterceptor;
		return this;
	}

	/**
	 * 设置返回json的java类型
	 * @param responseClazz 类型
	 * @return
     */
	public GetBuilder responseClazz(Class responseClazz) {
		mResponseClazz = responseClazz;
		return this;
	}
}
