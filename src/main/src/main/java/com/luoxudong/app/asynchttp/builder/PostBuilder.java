/**
 * Title: PostBuilder.java
 * Description: 普通Post请求构建类
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年10月13日 下午6:04:02
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;
import com.luoxudong.app.asynchttp.request.PostRequest;

/** 
 * <pre>
 * ClassName: PostBuilder
 * Description:普通Post请求构建类，支持返回参数为json对象
 * Create by: 罗旭东
 * Date: 2016年10月13日 下午6:04:02
 * </pre>
 */
public class PostBuilder extends RequestBuilder<PostBuilder> {
	/** 请求body体内容 */
	protected String mBody = null;
	/** 请求内容类型 */
	protected String mContentType = null;
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;
	
	@Override
	public AsyncHttpTask build() {
		return new PostRequest(this).build();
	}

	/**
	 * 设置请求内容
	 * @param body http请求体
	 * @return
     */
	public PostBuilder body(String body) {
		mBody = body;
		return this;
	}

	/**
	 * 设置请求内容类型
	 * @param contentType 内容类型
	 * @return
     */
	public PostBuilder contentType(String contentType) {
		mContentType = contentType;
        return this;
    }

	public String getBody() {
		return mBody;
	}
	
	public String getContentType() {
		return mContentType;
	}

	public JsonResponseInterceptor getResponseInterceptor() {
		return mResponseInterceptor;
	}

	/**
	 * 设置返回json数据拦截器，拦截返回数据预处理，可以设置自定义json解析库
	 * @param responseInterceptor 返回结果拦截器
	 * @return
	 */
	public PostBuilder responseInterceptor(JsonResponseInterceptor responseInterceptor) {
		mResponseInterceptor = responseInterceptor;
		return this;
	}

	public Class getResponseClazz() {
		return mResponseClazz;
	}

	/**
	 * 设置返回json的java类型
	 * @param responseClazz 类型
	 * @return
	 */
	public PostBuilder responseClazz(Class responseClazz) {
		mResponseClazz = responseClazz;
		return this;
	}
}
