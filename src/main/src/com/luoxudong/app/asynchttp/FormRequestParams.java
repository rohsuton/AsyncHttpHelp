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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;

import com.luoxudong.app.asynchttp.mime.HttpMultipartMode;
import com.luoxudong.app.asynchttp.mime.MultipartEntity;
import com.luoxudong.app.asynchttp.mime.content.StringBody;

/** 
 * ClassName: FormRequestParams
 * Description:表单请求参数
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午2:41:54
 */
public class FormRequestParams extends RequestParams {
	/** 表单参数 */
	protected Map<String, String> mFormParams = null;
	
	public FormRequestParams() {
        init();
    }
    
    /**
     * 带url参数的构造函数
     * @param source
     */
    public FormRequestParams(Map<String, String> source) {
        init();

        for(Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 带一对键值参数的构造函数
     * @param key
     * @param value
     */
    public FormRequestParams(String key, String value) {
        init();

        put(key, value);
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
    	MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    	StringBody stringBody = null;
    	
    	if (mFormParams != null){
    		for(HashMap.Entry<String, String> entry : mFormParams.entrySet()) {
        		try {
    				stringBody = new StringBody(entry.getValue());
    				multipartEntity.addPart(entry.getKey(), stringBody);
    			} catch (UnsupportedEncodingException e) {
    			}
            }
    	}
    	
		return multipartEntity;
    }
}
