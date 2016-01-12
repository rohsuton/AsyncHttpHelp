/**
 * Title: RequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午5:15:28
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

/** 
 * ClassName: RequestParams
 * Description:Http请求参数
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午5:15:28
 */
public class RequestParams {
	/** url参数 线程安全的hashmap */
	protected Map<String, String> mUrlParams = null;
	
	/** http头参数  */
	protected Map<String, String> mHeaderParams = null;
	
	/** Cookie内容 */
	protected Map<String, String> mCookies = null;
    
	/** http请求内容体 */
	protected String mRequestBody = null;
	
	/** 自定义连接超时时间 */
    private int mConnectTimeout = 0;

    /** 读取数据超时时间 */
    private int mReadTimeout = 0;
    
    /** 请求类型 */
    private String mContentType = null;

    public RequestParams() {
        init();
    }
    
    protected void init(){
        mUrlParams = new ConcurrentHashMap<String, String>();
        mHeaderParams = new ConcurrentHashMap<String, String>();
        mCookies = new ConcurrentHashMap<String, String>();
    }

    /**
     * 添加url参数
     * @param urlParams
     */
    public void put(Map<String, String> urlParams) {
        for(Map.Entry<String, String> entry : urlParams.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 添加参数到map中
     * @param key
     * @param value
     */
    public void put(String key, String value){
        if(key != null && value != null) {
            mUrlParams.put(key, value);
        }
    }

    /**
     * 添加header参数
     * @param key
     * @param value
     */
    public void putHeaderParam(String key, String value){
        if(key != null && value != null) {
        	if (AsyncHttpConst.HEADER_COOKIE.equals(key)){//提取cookie值
        		String[] cookieValues = value.split(",");
				
				for (String cookieValue : cookieValues){
					if (!TextUtils.isEmpty(cookieValue)){
						String[] values = cookieValue.split("=");
						if (values.length > 1){
							putCookies(values[0], values[1]);
						}
						
					}
				}
        	}
            mHeaderParams.put(key, value);
        }
    }
    
    /**
     * 添加header参数
     * @param headerParams
     */
    public void putHeaderParam(Map<String, String> headerParams){
    	for(Map.Entry<String, String> entry : headerParams.entrySet()) {
    		putHeaderParam(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 设置cookie
     * @param key
     * @param value
     */
    public void putCookies(String key, String value){
    	if(key != null && value != null) {
            mCookies.put(key, value);
        }
    }
    
    /**
     * 设置cookie
     * @param cookies
     */
    public void putCookies(Map<String, String> cookies){
    	for(Map.Entry<String, String> entry : cookies.entrySet()) {
    		putCookies(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 移除url参数中指定key对应的参数
     * @param key
     */
    public void removeUrlParam(String key){
        mUrlParams.remove(key);
    }

   /**
     * Returns an HttpEntity containing all request parameters
     */
    public HttpEntity getEntity() {
    	StringEntity entity = null;

        try {
        	if (mRequestBody == null){
        		mRequestBody = "";
        	}
			entity = new StringEntity(mRequestBody, AsyncHttpConst.HTTP_ENCODING);

			if (!TextUtils.isEmpty(getContentType())){
				entity.setContentType(getContentType());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

        return entity;
    }

    /**
     * 获取url参数进行url编码
     * @return
     */
    protected String getParamString() {
        return URLEncodedUtils.format(getUrlParamsList(), AsyncHttpConst.HTTP_ENCODING);
    }
    
    /**
     * 获取url参数
     * @return
     */
    protected List<BasicNameValuePair> getUrlParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for(Map.Entry<String, String> entry : mUrlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }

	public Map<String, String> getHeaderParams() {
		return mHeaderParams;
	}
	
	public Map<String, String> getCookies() {
		return mCookies;
	}

	public int getConnectTimeout() {
        return mConnectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout;
	}

    public int getReadTimeout() {
        return mReadTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        mReadTimeout = readTimeout;
    }

    //mReadTimeout

	public String getContentType() {
		return mContentType;
	}

	public void setContentType(String contentType) {
		mContentType = contentType;
	}

	public void setRequestBody(String requestBody) {
		mRequestBody = requestBody;
	}
	
}
