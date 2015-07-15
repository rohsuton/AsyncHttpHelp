/**
 * Title: JsonResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月15日 下午2:46:44
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.luoxudong.app.asynchttp.adapter.BaseJsonHttpResponseAdapter;
import com.luoxudong.app.asynchttp.callable.JsonRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.model.BaseResponse;

/** 
 * ClassName: JsonResponseHandler
 * Description:json请求结果回调
 * Create by: 罗旭东
 * Date: 2015年7月15日 下午2:46:44
 */
public class JsonResponseHandler<M extends BaseResponse<M>> extends ResponseHandler {
	private Class<M> mResponseClass = null;
	
	private JsonRequestCallable<M> mJsonCallable = null;
	
	private BaseJsonHttpResponseAdapter responseAdapter = null;

	public JsonResponseHandler(Class<M> responseClass, JsonRequestCallable<M> callable)
	{
		super(callable);
		mJsonCallable = callable;
		mResponseClass = responseClass;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		if (statusCode != HttpStatus.SC_NO_CONTENT){
            try {
                M jsonResponse = parseResponse(content);
                
				if (responseAdapter != null && !responseAdapter.checkResponseData(jsonResponse)) {// 返回的数据成功
					int errorCode = responseAdapter.getErrorCode();
					String errorMsg = responseAdapter.getErrorMsg();
					onFailure(errorCode, new AsyncHttpException(errorMsg + "[" + errorCode + "]"));
				} else {
					if (mJsonCallable != null) {
						mJsonCallable.onSuccess(jsonResponse);
					}
				}
    	    } catch(AsyncHttpException e) {
    	        sendFailureMessage(e.getExceptionCode(), e);
    	    }
        } else {
            onFailure(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("statusCode错误! "));
    	}
	}

	/**
	 * 解析返回字符串
	 * @param responseBody
	 * @return
	 */
    protected M parseResponse(String responseBody) {
        M result = null;
        responseBody = responseBody.trim();
		Gson gson = new Gson();
		if(responseBody.startsWith("{") || responseBody.startsWith("[")) {//合法的json字符串
			try {
				result = gson.fromJson(responseBody, mResponseClass);
			} catch (Exception e) {
				throw new AsyncHttpException(AsyncHttpExceptionCode.jsonParseException.getErrorCode(), e);
			}
			
		}
		
		if (result == null) {
			result = null;
		}
		return result;
    }
}
