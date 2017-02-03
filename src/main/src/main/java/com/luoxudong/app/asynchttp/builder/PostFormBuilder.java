/**
 * Title: PostFormBuilder.java
 * Description: Post提交form表单的构建类
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年11月23日 上午11:02:59
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;
import com.luoxudong.app.asynchttp.request.PostFormRequest;

/** 
 * <pre>
 * ClassName: PostFormBuilder
 * Description:Post提交form表单的构建类，支持返回参数为json对象
 * Create by: 罗旭东
 * Date: 2016年11月23日 上午11:02:59
 * </pre>
 */
public class PostFormBuilder extends RequestBuilder<PostFormBuilder> {
	/** form表单内容 */
	protected Map<String, String> mFormMap = null;
	/** 返回数据拦截器 */
	private JsonResponseInterceptor mResponseInterceptor = null;
	/** 返回的json对象类型 */
	private Class mResponseClazz = null;
	
	public PostFormBuilder() {
		init();
	}
	
	@Override
	protected void init() {
		super.init();
		mFormMap = new ConcurrentHashMap<String, String>();
	}
	
	@Override
	public AsyncHttpTask build() {
		return new PostFormRequest(this).build();
	}

	/**
	 * 添加form表单参数
	 * @param key 键
	 * @param value 键对应的值
     * @return
     */
	public PostFormBuilder addFormParam(String key, String value) {
		mFormMap.put(key, value);
		return this;
	}

	/**
	 * 批量添加form表单参数
	 * @formParams form表单键值对应表
	 * @return
	 */
	public PostFormBuilder addFormParams(Map<String, String> formParams) {
		for(Map.Entry<String, String> entry : formParams.entrySet()) {
			addFormParam(entry.getKey(), entry.getValue());
        }
		return this;
	}
	
	public Map<String, String> getFormMap() {
		return mFormMap;
	}

	public JsonResponseInterceptor getResponseInterceptor() {
		return mResponseInterceptor;
	}

	/**
	 * 设置返回json数据拦截器，拦截返回数据预处理，可以设置自定义json解析库
	 * @param responseInterceptor 返回结果拦截器
	 * @return
	 */
	public PostFormBuilder responseInterceptor(JsonResponseInterceptor responseInterceptor) {
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
	public PostFormBuilder responseClazz(Class responseClazz) {
		mResponseClazz = responseClazz;
		return this;
	}
}
