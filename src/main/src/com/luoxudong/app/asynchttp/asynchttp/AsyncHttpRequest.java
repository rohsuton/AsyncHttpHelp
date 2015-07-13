/**
 * Title: AsyncHttpRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午4:59:07
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

import com.luoxudong.app.asynchttp.asynchttp.interfaces.IHttpRequestCancelListener;
import com.luoxudong.app.threadpool.constant.ThreadPoolConst;
import com.luoxudong.app.threadpool.manager.ThreadTaskObject;

/** 
 * ClassName: AsyncHttpRequest
 * Description:发送HTTP请求
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午4:59:07
 */
public class AsyncHttpRequest {
	/** httpClient实例 */
	private DefaultHttpClient httpClient = null;
	
	/** http请求上下文,非线程安全，每个请求一个实例对象 */
    private HttpContext httpContext = null;
    
    private int timeout = -1;
    
    private Map<String, String> headerParams = null;
    
    private AsyncHttpTask asyncHttpTask = null;
    
    /**
     * 线程池类型
     */
    private int threadPoolType = ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP;
    
    public AsyncHttpRequest()
    {
    	httpContext = new BasicHttpContext();
    	httpClient = AsyncHttpClient.getAsyncHttpClient();
    }
    
    /**
     * Perform a HTTP GET request, without any parameters.
     * @param url the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(String url, AsyncHttpResponseHandler responseHandler) {
    	sendRequest(httpClient, httpContext, url, null, HttpGet.METHOD_NAME, null, null, responseHandler, null);
    }

    /**
     * Perform a HTTP GET request with parameters.
     * @param url the URL to send the request to.
     * @param params additional GET parameters to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        get(null, url, null, params, responseHandler);
    }

    /**
     * Perform a HTTP GET request without any parameters and track the Android Context which initiated the request.
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(Context context, String url, AsyncHttpResponseHandler responseHandler) {
    	sendRequest(httpClient, httpContext, url, null, HttpGet.METHOD_NAME, null, null, responseHandler, context);
    }

    /**
     * Perform a HTTP GET request and track the Android Context which initiated the request.
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param params additional GET parameters to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void get(Context context, String url, RequestParams params) {
        sendRequest(httpClient, httpContext, url, null, HttpGet.METHOD_NAME, null, params, params.getResponseHandler(), context);
    }
    
    /**
     * Perform a HTTP GET request and track the Android Context which initiated
     * the request with customized headers
     * 
     * @param url the URL to send the request to.
     * @param headers set headers only for this request
     * @param params additional GET parameters to send with the request.
     * @param responseHandler the response handler instance that should handle
     *        the response.
     */
    public void get(Context context, String url, Map<String, String> headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext, url, headers, HttpGet.METHOD_NAME, null, params, responseHandler, context);
    }


    //
    // HTTP POST Requests
    //

    /**
     * Perform a HTTP POST request, without any parameters.
     * @param url the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void post(String url, AsyncHttpResponseHandler responseHandler) {
        post(url, null, responseHandler);
    }

    /**
     * Perform a HTTP POST request with parameters.
     * @param url the URL to send the request to.
     * @param params additional POST parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        post(null, url, params, null, responseHandler);
    }
    
    public void post(Context context, String url, RequestParams params) {
    	post(context, url, params, null, params.getResponseHandler());
    }

    /**
     * Perform a HTTP POST request and track the Android Context which initiated the request.
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param params additional POST parameters or files to send with the request.
     * @param contentType the content type of the payload you are sending, for example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void post(Context context, String url, RequestParams params, String contentType, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext, url, null, HttpPost.METHOD_NAME, contentType, params, responseHandler, context);
    }

    /**
     * Perform a HTTP POST request and track the Android Context which initiated
     * the request. Set headers only for this request
     * 
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param headers set headers only for this request
     * @param params additional POST parameters to send with the request.
     * @param contentType the content type of the payload you are sending, for
     *        example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle
     *        the response.
     */
    public void post(Context context, String url, Map<String, String> headers, RequestParams params, String contentType,
            AsyncHttpResponseHandler responseHandler) {
    	
        sendRequest(httpClient, httpContext, url, headers, HttpPost.METHOD_NAME, contentType, params, responseHandler, context);
    }

    //
    // HTTP PUT Requests
    //

    /**
     * Perform a HTTP PUT request, without any parameters.
     * @param url the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(String url, AsyncHttpResponseHandler responseHandler) {
        put(null, url, null, responseHandler);
    }

    /**
     * Perform a HTTP PUT request with parameters.
     * @param url the URL to send the request to.
     * @param params additional PUT parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        put(null, url, params, responseHandler);
    }

    /**
     * Perform a HTTP PUT request and track the Android Context which initiated the request.
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param params additional PUT parameters or files to send with the request.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        put(context, url, params, null, responseHandler);
    }

    /**
     * Perform a HTTP PUT request and track the Android Context which initiated the request.
     * And set one-time headers for the request
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param params additional PUT parameters or files to send with the request.
     * @param contentType the content type of the payload you are sending, for example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(Context context, String url, RequestParams params, String contentType, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext, url, null, HttpPut.METHOD_NAME, contentType, params, responseHandler, context);
    }
    
    /**
     * Perform a HTTP PUT request and track the Android Context which initiated the request.
     * And set one-time headers for the request
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param headers set one-time headers for this request
     * @param params additional PUT parameters or files to send with the request.
     * @param contentType the content type of the payload you are sending, for example application/json if sending a json payload.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void put(Context context, String url, Map<String, String> headers, RequestParams params, String contentType, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext, url, headers, HttpPut.METHOD_NAME, contentType, params, responseHandler, context);
    }

    //
    // HTTP DELETE Requests
    //

    /**
     * Perform a HTTP DELETE request.
     * @param url the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void delete(String url, AsyncHttpResponseHandler responseHandler) {
        delete(null, url, responseHandler);
    }

    /**
     * Perform a HTTP DELETE request.
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void delete(Context context, String url, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext, url, null, HttpDelete.METHOD_NAME, null, null, responseHandler, context);
    }
    
    /**
     * Perform a HTTP DELETE request.
     * @param context the Android Context which initiated the request.
     * @param url the URL to send the request to.
     * @param headers set one-time headers for this request
     * @param responseHandler the response handler instance that should handle the response.
     */
    public void delete(Context context, String url, Map<String, String> headers, AsyncHttpResponseHandler responseHandler) {
        sendRequest(httpClient, httpContext, url, headers, HttpDelete.METHOD_NAME, null, null, responseHandler, context);
    }
    
 // Private stuff
    protected void sendRequest(DefaultHttpClient client, HttpContext httpContext, String url, Map<String, String> headers, String requestMethodName, String contentType, RequestParams params, AsyncHttpResponseHandler responseHandler, Context context) {
    	if (headers == null)
    	{
    		headers = new HashMap<String, String>();
    	}
    	
    	//自定义请求类型
    	if(contentType != null) {
    		headers.put("Content-Type", contentType);
        }

    	//增加http头部信息
    	if (headerParams != null && headerParams.size() > 0)//自定义http头部信息
    	{
    		headers.putAll(headerParams);
    	}
    	
    	asyncHttpTask = new AsyncHttpTask(threadPoolType, httpClient, httpContext, url, requestMethodName, params, responseHandler);
    	
    	if (timeout != -1)//设自定义超时时间
    	{
    		asyncHttpTask.setTimeout(timeout);
    		//uriRequest.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);
    	}
    	asyncHttpTask.setHeaders(headers);
    	
    	//异步网络请求
    	asyncHttpTask.start();
        
    }
    
    public void setThreadPoolType(int threadPoolType) {
        this.threadPoolType = threadPoolType;
    }
    
    /**
     * 
     * @description:关闭HTTP请求,不能在主线程中关闭网络请求
     * @return void
     * @throws
     */
    public void cancel()
    {
    	cancel(null);
    }
    
    public void cancel(final IHttpRequestCancelListener httpRequestCancelListener){
    	new ThreadTaskObject(ThreadPoolConst.THREAD_TYPE_WORK, null)
    	{
    		public void run() {
    			if (httpRequestCancelListener != null){
    				asyncHttpTask.setHttpRequestCancelListener(httpRequestCancelListener);
    			}
    			asyncHttpTask.cancel();
    		};
    	}.start();
    }
    
    /**
     * 
     * @description:设置请求的连接超时时间
     * @param timeout
     * @return void
     * @throws
     */
    public void setTimeout(int timeout)
    {
    	this.timeout = timeout;
    }
    
    /**
     * 
     * @description:设置http头部信息
     * @param name
     * @param value
     * @return void
     * @throws
     */
    public void addHeadParam(String name, String value)
    {
    	if (headerParams == null)
    	{
    		headerParams = new HashMap<String, String>();
    	}
    	
    	headerParams.put(name, value);
    }
    
    /**
     * 
     * @description:设置cookie
     * @param cookieStore
     * @return void
     * @throws
     */
    public void setCookieStore(CookieStore cookieStore)
    {
    	if (httpContext != null)
    	{
    		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    	}
    }
}
