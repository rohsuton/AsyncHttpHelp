/**
 * Title: AsyncHttpRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午4:59:07
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.luoxudong.app.asynchttp.interfaces.IHttpRequestCancelListener;
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
	private DefaultHttpClient mHttpClient = null;
	
	/** http请求上下文,非线程安全，每个请求一个实例对象 */
    private HttpContext mHttpContext = null;
    
    /** 发送http请求任务 */
    private AsyncHttpTask mAsyncHttpTask = null;
    
    /** 线程池类型 */
    private int mThreadPoolType = ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP;
    
    public AsyncHttpRequest()
    {
    	mHttpContext = new BasicHttpContext();
    	mHttpClient = AsyncHttpClient.getAsyncHttpClient();
    }
    
    /**************** GET请求 ***********************/
    
    /**
     * 不带参数的get请求
     * @param url
     * @param responseHandler
     */
    public void get(String url, AsyncHttpResponseHandler responseHandler) {
    	get(url, null, responseHandler);
    }

    /**
     * 带参数的get请求
     * @param url
     * @param params
     * @param responseHandler
     */
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        sendRequest(mHttpClient, mHttpContext, url, HttpGet.METHOD_NAME, params, responseHandler);
    }
    

    /**************** POST请求 ***********************/

    /**
     * 不带参数的post请求
     * @param url
     * @param responseHandler
     */
    public void post(String url, AsyncHttpResponseHandler responseHandler) {
        post(url, null, responseHandler);
    }

    /**
     * 带参数的post请求
     * @param url
     * @param params
     * @param responseHandler
     */
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	sendRequest(mHttpClient, mHttpContext, url, HttpPost.METHOD_NAME, params, responseHandler);
    }

    /**************** PUT请求 ***********************/

    /**
     * 不带参数的put请求
     * @param url
     * @param responseHandler
     */
    public void put(String url, AsyncHttpResponseHandler responseHandler) {
        put(url, null, responseHandler);
    }

    /**
     * 带参数的put请求
     * @param url
     * @param params
     * @param responseHandler
     */
    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	sendRequest(mHttpClient, mHttpContext, url, HttpPut.METHOD_NAME, params, responseHandler);
    }
    
    /**************** DELETE请求 ***********************/

    /**
     * 不带参数的delete请求
     * @param url
     * @param responseHandler
     */
    public void delete(String url, AsyncHttpResponseHandler responseHandler) {
        delete(url, null, responseHandler);
    }

    /**
     * 带参数的delete请求
     * @param url
     * @param params
     * @param responseHandler
     */
    public void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        sendRequest(mHttpClient, mHttpContext, url, HttpDelete.METHOD_NAME, params, responseHandler);
    }
    
    
    /**************** OPTIONS请求 ***********************/
    /**
     * 不带参数的options请求
     * @param url
     * @param responseHandler
     */
    public void options(String url, AsyncHttpResponseHandler responseHandler) {
        delete(url, null, responseHandler);
    }

    /**
     * 带参数的options请求
     * @param url
     * @param params
     * @param responseHandler
     */
    public void options(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        sendRequest(mHttpClient, mHttpContext, url, HttpOptions.METHOD_NAME, params, responseHandler);
    }
    
    // Private stuff
    protected void sendRequest(DefaultHttpClient client, HttpContext httpContext, String url, String requestMethodName, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	mAsyncHttpTask = new AsyncHttpTask(mThreadPoolType, client, httpContext, url, requestMethodName, params, responseHandler);
    	//异步网络请求
    	mAsyncHttpTask.start();
        
    }
    
    public void setThreadPoolType(int threadPoolType) {
        mThreadPoolType = threadPoolType;
    }
    
    /**
     * 
     * @description:关闭HTTP请求,不能在主线程中关闭网络请求
     * @return void
     * @throws
     */
	public void cancel() {
		cancel(null);
	}
    
    /**
     * 取消请求
     * @param httpRequestCancelListener
     */
	public void cancel(final IHttpRequestCancelListener httpRequestCancelListener) {
		new ThreadTaskObject(ThreadPoolConst.THREAD_TYPE_WORK, null) {
			public void run() {
				if (httpRequestCancelListener != null) {
					mAsyncHttpTask
							.setHttpRequestCancelListener(httpRequestCancelListener);
				}
				mAsyncHttpTask.cancel();
			};
		}.start();
	}
    
    /**
     * 
     * @description:设置cookie
     * @param cookieStore
     * @return void
     * @throws
     */
	public void setCookieStore(CookieStore cookieStore) {
		if (mHttpContext != null) {
			mHttpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		}
	}
}
