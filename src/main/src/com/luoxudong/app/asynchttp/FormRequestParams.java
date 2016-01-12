/**
 * Title: FormRequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月14日 下午2:41:54
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: FormRequestParams
 * Description:表单请求参数
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午2:41:54
 */
public class FormRequestParams extends RequestParams {
	private static final String TAG = FormRequestParams.class.getSimpleName();
	
	/** 表单参数 */
	protected Map<String, String> mFormParams = null;
	
	public FormRequestParams() {
        init();
    }
	
	@Override
	protected void init() {
		super.init();
		mFormParams = new ConcurrentHashMap<String, String>();
	}
	
	/** 移除form表单参数中key对应的参数 */
	public void removeFormParam(String key){
		mFormParams.remove(key);
	}
	
	/**
     * 添加form表单参数
     * @param source
     */
    public void putFormParam(Map<String, String> source) {
        for(Map.Entry<String, String> entry : source.entrySet()) {
        	putFormParam(entry.getKey(), entry.getValue());
        }
    }
    
	/**
     * 添加form表单参数
     * @param key 参数key
     * @param value 参数值
     */
    public void putFormParam(String key, String value){
		if (key != null && value != null) {
			mFormParams.put(key, value);
		}
    }
    
    @Override
    public HttpEntity getEntity() {
    	UrlEncodedFormEntity entity = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		if (mFormParams != null){
    		for(HashMap.Entry<String, String> entry : mFormParams.entrySet()) {
    			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    			AsyncHttpLog.i(TAG, "请求参数：" + entry.getKey() + "=" + entry.getValue());
    		}
		}
		
		try {
			entity = new UrlEncodedFormEntity(params, AsyncHttpConst.HTTP_ENCODING);

			if (!TextUtils.isEmpty(getContentType())){
				entity.setContentType(getContentType());
			}
		} catch (UnsupportedEncodingException e) {
		}
		
		return entity;
    }
}
