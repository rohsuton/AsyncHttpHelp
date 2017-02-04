/**
 * Title: RequestBuilder.java
 * Description: 通用请求参数构造类
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年10月13日 下午2:53:32
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import android.text.TextUtils;

import com.luoxudong.app.asynchttp.AsyncHttpConst;
import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.request.AsyncHttpRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/** 
 * <pre>
 * ClassName: RequestBuilder
 * Description:通用请求参数构造类
 * Create by: 罗旭东
 * Date: 2016年10月13日 下午2:53:32
 * </pre>
 */
public abstract class RequestBuilder<T extends RequestBuilder<?>> {
	/** URL */
	protected String mUrl = null;
	/** tag标签 */
	protected String mTag = null;
	/** 是否在主线程中执行 */
	protected boolean mMainThread = false;
	/** url参数 */
	protected Map<String, String> mUrlParams = null;
	/** http头参数  */
	protected Map<String, String> mHeaderParams = null;
	/** Cookie内容 */
	protected Map<String, String> mCookies = null;
	/** 自定义连接超时时间 */
	protected int mConnectTimeout = 0;
    /** 读取数据超时时间 */
    protected int mReadTimeout = 0;
    /** 写数据超时时间 */
    protected int mWriteTimeout = 0;
    /** userAgent */
    protected String mUserAgent = null;

    public RequestBuilder() {
        init();
    }
    
    protected void init(){
        mUrlParams = new ConcurrentHashMap<String, String>();
        mHeaderParams = new ConcurrentHashMap<String, String>();
        mCookies = new ConcurrentHashMap<String, String>();
    }

	protected void initRequest(AsyncHttpRequest request) {
		request.setUrl(mUrl);
		request.setMainThread(mMainThread);
		request.setTag(mTag);
		request.setUrlParams(mUrlParams);
		request.setHeaderParams(mHeaderParams);
		request.setCookies(mCookies);
		request.setUserAgent(mUserAgent);
		request.setConnectTimeout(mConnectTimeout);
		request.setReadTimeout(mReadTimeout);
		request.setWriteTimeout(mWriteTimeout);
	}

    public T url(String url) {
    	mUrl = url;
    	return (T)this;
    }
    
    public T tag(String tag) {
    	mTag = tag;
    	return (T)this;
    }
    
    public T mainThread(boolean mainThread) {
		mMainThread = mainThread;
		return (T)this;
	}
    
    /**
     * 添加url参数
     * @param urlParams
     */
    public T addUrlParams(Map<String, String> urlParams) {
		if (urlParams != null) {
			for(Map.Entry<String, String> entry : urlParams.entrySet()) {
				addUrlParam(entry.getKey(), entry.getValue());
			}
		}
        
        return (T)this;
    }
    
    /**
     * 添加参数到map中
     * @param key
     * @param value
     */
    public T addUrlParam(String key, String value){
        if(key != null && value != null) {
            mUrlParams.put(key, value);
        }
        
        return (T)this;
    }

    /**
     * 添加header参数
     * @param headerParams
     */
    public T addHeaderParams(Map<String, String> headerParams){
		if (headerParams != null) {
			for(Map.Entry<String, String> entry : headerParams.entrySet()) {
				addHeaderParam(entry.getKey(), entry.getValue());
			}
		}

    	return (T)this;
    }
    
    /**
     * 添加header参数
     * @param key
     * @param value
     */
    public T addHeaderParam(String key, String value){
        if(key != null && value != null) {
        	if (AsyncHttpConst.HEADER_COOKIE.equals(key)){//提取cookie值
        		String[] cookieValues = value.split(",");
				
				for (String cookieValue : cookieValues){
					if (!TextUtils.isEmpty(cookieValue)){
						String[] values = cookieValue.split("=");
						if (values.length > 1){
							addCookie(values[0], values[1]);
						}
						
					}
				}
        	}
            mHeaderParams.put(key, value);
        }
        
        return (T)this;
    }
    
    /**
     * 设置cookie
     * @param cookies
     */
    public T addCookies(Map<String, String> cookies){
    	for(Map.Entry<String, String> entry : cookies.entrySet()) {
    		addCookie(entry.getKey(), entry.getValue());
        }
    	
    	return (T)this;
    }

    /**
     * 设置cookie
     * @param key
     * @param value
     */
    public T addCookie(String key, String value){
    	if(key != null && value != null) {
            mCookies.put(key, value);
        }

    	return (T)this;
    }

	/**
	 * 设置连接超时时间
	 * @param connectTimeout 连接超时时间（毫秒）
	 * @return
     */
	public T connectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout;
        return (T)this;
	}

	/**
	 * 设置读取数据超时时间
	 * @param readTimeout 读取数据超时时间（毫秒）
	 * @return
     */
	public T readTimeout(int readTimeout) {
        mReadTimeout = readTimeout;
        return (T)this;
    }

	/**
	 * 设置写数据超时时间
	 * @param writeTimeout 写数据超时时间（毫秒）
	 * @return
     */
	public T writeTimeout(int writeTimeout) {
        mWriteTimeout = writeTimeout;
        return (T)this;
    }

	/**
	 * 对每个请求自定义User-Agent
	 * @param userAgent User-Agent
	 * @return
     */
	public T userAgent(String userAgent) {
		mUserAgent = userAgent;
		return (T)this;
	}

	public abstract AsyncHttpTask build();
}
