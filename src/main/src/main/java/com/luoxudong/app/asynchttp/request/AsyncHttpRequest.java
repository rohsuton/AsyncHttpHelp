/**
 * Title: AsyncHttpRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年10月13日 下午3:43:41
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.request;

import android.net.Uri;
import android.text.TextUtils;

import com.luoxudong.app.asynchttp.AsyncHttpConst;
import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.AsyncHttpUtil;
import com.luoxudong.app.asynchttp.builder.RequestBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Request;

/** 
 * <pre>
 * ClassName: AsyncHttpRequest
 * Description:各种请求基类
 * Create by: 罗旭东
 * Date: 2016年10月13日 下午3:43:41
 * </pre>
 */
public abstract class AsyncHttpRequest {
	private long mId = 0;

	/** URL */
	protected String mUrl = null;
	/** 是否在主线程中执行 */
	protected boolean mMainThread = false;
	/** 请求tag */
	protected String mTag = null;
	/** url参数 */
	protected Map<String, String> mUrlParams = null;
	/** http头参数  */
	protected Map<String, String> mHeaderParams = null;
	/** Cookie内容 */
	protected Map<String, String> mCookies = null;
	/** userAgent */
    protected String mUserAgent = null;
	/** 自定义连接超时时间 */
	protected int mConnectTimeout = 0;
    /** 读取数据超时时间 */
    protected int mReadTimeout = 0;
    /** 写数据超时时间 */
    protected int mWriteTimeout = 0;
	/** 发送请求之前异常 */
	private AsyncHttpException mAsyncHttpException = null;
	protected Request.Builder mBuilder = new Request.Builder();
	
	public AsyncHttpRequest(RequestBuilder<?> builder) {
		mId = System.nanoTime() - AsyncHttpUtil.sStartTime;
	}
	
	public abstract AsyncHttpTask build();

	/** 构建请求 */
	public abstract Request buildRequest(RequestCallable callable);

	public abstract ResponseHandler buildResponseHandler(RequestCallable callable);
	
	/**
	 * 初始化请求
	 */
	protected void initRequest() {
		if (TextUtils.isEmpty(mUrl)) {
			AsyncHttpLog.e(AsyncHttpConst.TAG_LOG, "url不能为空");
			mAsyncHttpException = new AsyncHttpException(AsyncHttpExceptionCode.urlIsNull.getErrorCode(), "url不能为空");
			return;
		}

		if (TextUtils.isEmpty(mTag)) {
			mTag = String.valueOf(mId);
		}

		String url = buildUrl(mUrl);
		mBuilder.url(url).tag(mTag);
		buildHeaders();
	}
	
	/**
	 * 构建url
	 * @param url
	 */
	private String buildUrl(String url) {
		if (url == null || mUrlParams == null || mUrlParams.isEmpty()) {
            return url;
        }
		
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = mUrlParams.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, mUrlParams.get(key));
        }
        return builder.build().toString();
	}
	
	/**
	 * 构建http头
	 */
	private void buildHeaders() {
		Headers.Builder headerBuilder = new Headers.Builder();
		
        if (mHeaderParams != null) {
        	for (String key : mHeaderParams.keySet()) {
            	if (key.equals(AsyncHttpConst.HEADER_USER_AGENT)) {
            		headerBuilder.removeAll(key);
            	}
            	
            	try {
            		headerBuilder.add(key, mHeaderParams.get(key));
				} catch (Exception e) {
					
				}

            }
        }

        mBuilder.headers(headerBuilder.build());
	}

	public long getId() {
		return mId;
	}

	public AsyncHttpException getAsyncHttpException() {
		return mAsyncHttpException;
	}

	public boolean isMainThread() { return mMainThread; }

	public String getUserAgent() {
		return mUserAgent;
	}

	public Map<String, String> getCookies() {
		return mCookies;
	}

	public int getConnectTimeout() {
		return mConnectTimeout;
	}

	public int getReadTimeout() {
		return mReadTimeout;
	}

	public int getWriteTimeout() {
		return mWriteTimeout;
	}


	public void setUrl(String url) {
		mUrl = url;
	}

	public void setTag(String tag) {
		mTag = tag;
	}

	public void setMainThread(boolean mainThread) { mMainThread = mainThread; }

	public void setUrlParams(Map<String, String> urlParams) {
		mUrlParams = urlParams;
	}

	public void setHeaderParams(Map<String, String> headerParams) {
		mHeaderParams = headerParams;
	}

	public void setCookies(Map<String, String> cookies) {
		mCookies = cookies;
	}

	public void setUserAgent(String userAgent) {
		mUserAgent = userAgent;
	}

	public void setConnectTimeout(int connectTimeout) {
		mConnectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		mReadTimeout = readTimeout;
	}

	public void setWriteTimeout(int writeTimeout) {
		mWriteTimeout = writeTimeout;
	}
}
