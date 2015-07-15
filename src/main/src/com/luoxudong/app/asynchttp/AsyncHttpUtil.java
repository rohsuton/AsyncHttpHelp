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

import java.io.Serializable;
import java.util.Map;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.text.TextUtils;

import com.luoxudong.app.asynchttp.adapter.BaseJsonHttpResponseAdapter;
import com.luoxudong.app.asynchttp.callable.JsonRequestCallable;
import com.luoxudong.app.asynchttp.callable.SimpleRequestCallable;
import com.luoxudong.app.asynchttp.model.BaseResponse;
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
	
	public static AsyncHttpRequest simplePostHttpRequest(String url, SimpleRequestCallable callable){
		return simplePostHttpRequest(url, null, null, 0, null, null, callable);
	}
	
	public static AsyncHttpRequest simplePostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, String requestBody, SimpleRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		RequestParams params = new RequestParams();
		ResponseHandler handler = new ResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (!TextUtils.isEmpty(contentType)){
			params.setContentType(contentType);
		}
		
		if (requestBody != null){
			params.setRequestBody(requestBody);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(url, params, handler);
		return httpRequest;
	}
	
	public static <T extends Serializable, M extends BaseResponse<M>> AsyncHttpRequest jsonPostHttpRequest(String url, Map<String, String> headerParams, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable){
		return jsonPostHttpRequest(url, null, headerParams, 0, AsyncHttpConst.HEADER_CONTENT_TYPE_JSON, requestInfo, responseClass, callable);
	}
	
	public static <T extends Serializable, M extends BaseResponse<M>> AsyncHttpRequest jsonPostHttpRequest(String url, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable){
		return jsonPostHttpRequest(url, null, null, 0, AsyncHttpConst.HEADER_CONTENT_TYPE_JSON, requestInfo, responseClass, callable);
	}
	
	public static <T extends Serializable, M extends BaseResponse<M>> AsyncHttpRequest jsonPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		JsonRequestParams<T> params = new JsonRequestParams<T>();
		
		JsonResponseHandler<M> handler = new JsonResponseHandler<M>(responseClass, callable);
		
		if (mResponseAdapter != null){
			handler.setResponseAdapter(mResponseAdapter);
		}
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (!TextUtils.isEmpty(contentType)){
			params.setContentType(contentType);
		}
		
		params.setRequestJsonObj(requestInfo);
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(url, params, handler);
		return httpRequest;
	}
}
