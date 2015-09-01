/**
 * Title: EncodedFormRequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月16日 上午11:43:29
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.http.HttpEntity;

import com.luoxudong.app.asynchttp.mime.HttpMultipartMode;
import com.luoxudong.app.asynchttp.mime.MultipartEntity;
import com.luoxudong.app.asynchttp.mime.content.StringBody;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: EncodedFormRequestParams
 * Description:模拟form表单提交，内容url编码
 * Create by: 罗旭东
 * Date: 2015年7月16日 上午11:43:29
 */
public class FormDataRequestParams extends FormRequestParams {
	private static final String TAG = FormDataRequestParams.class.getSimpleName();
	
	@Override
	public HttpEntity getEntity() {
    	MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    	StringBody stringBody = null;
    	
    	if (mFormParams != null){
    		for(HashMap.Entry<String, String> entry : mFormParams.entrySet()) {
        		try {
    				stringBody = new StringBody(entry.getValue(), Charset.forName(AsyncHttpConst.HTTP_ENCODING));
    				entity.addPart(entry.getKey(), stringBody);
    				AsyncHttpLog.i(TAG, entry.getKey() + ">>>>" + entry.getValue());
    			} catch (UnsupportedEncodingException e) {
    			}
            }
    	}
    	
		return entity;
	}
}
