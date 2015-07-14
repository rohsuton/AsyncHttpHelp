/**
 * Title: AsyncHttpUtil.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月14日 下午5:00:05
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.util.Map;

import org.apache.http.conn.ssl.SSLSocketFactory;

import com.luoxudong.app.asynchttp.adapter.BaseJsonHttpResponseAdapter;
import com.luoxudong.app.asynchttp.callable.SimpleHttpRequestCallable;
import com.luoxudong.app.threadpool.constant.ThreadPoolConst;

/** 
 * ClassName: AsyncHttpUtil
 * Description:http请求工具类 
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午5:00:05
 */
public class AsyncHttpUtil {
	private static BaseJsonHttpResponseAdapter mResponseAdapter = null;
	
	/**
	 * 设置json返回参数解析代理
	 * @param responseAdapter
	 */
	public static void setResponseAdapter(BaseJsonHttpResponseAdapter responseAdapter){
		mResponseAdapter = responseAdapter;
	}
	
	public static void setSSLSocketFactory(SSLSocketFactory sslSocketFactory){
		AsyncHttpClient.setSSLSocketFactory(sslSocketFactory);
	}
	
	public static AsyncHttpRequest simplePostHttpRequest(String url, SimpleHttpRequestCallable callable){
		return simplePostHttpRequest(url, null, null, null, 0, null, callable);
	}
	
	public static AsyncHttpRequest simplePostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, Map<String, String> cookieParams, int timeout, String requestBody, SimpleHttpRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		RequestParams params = new RequestParams();
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (requestBody != null){
			params.setRequestBody(requestBody);
		}
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.get(url, params, handler);
		return httpRequest;
	}
}
