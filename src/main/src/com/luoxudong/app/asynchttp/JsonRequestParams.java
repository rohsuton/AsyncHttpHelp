/**
 * Title: JsonRequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月14日 下午12:04:05
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: JsonRequestParams
 * Description:Json请求参数
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午12:04:05
 */
public class JsonRequestParams<T> extends RequestParams {
	private static final String TAG = JsonRequestParams.class.getSimpleName();
	
	/** 请求参数对象 */
	protected T mRequestJsonObj = null;
	
	@Override
	public HttpEntity getEntity() {
		StringEntity entity = null;
		//不做unicode字符转义 
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();//new Gson();
    	String requestData = gson.toJson(getRequestJsonObj());
    	AsyncHttpLog.i(TAG, requestData);
    	try {
    		entity = new StringEntity(requestData, AsyncHttpConst.HTTP_ENCODING);
    		
    		if (!TextUtils.isEmpty(getContentType())){
				entity.setContentType(getContentType());
			}
    		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
    	return entity;
	}

	public T getRequestJsonObj() {
		return mRequestJsonObj;
	}

	public void setRequestJsonObj(T requestJsonObj) {
		this.mRequestJsonObj = requestJsonObj;
	}
}
