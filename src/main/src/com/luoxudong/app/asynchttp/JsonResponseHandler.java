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

import com.luoxudong.app.asynchttp.callable.JsonRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;

/** 
 * ClassName: JsonResponseHandler
 * Description:json请求结果回调
 * Create by: 罗旭东
 * Date: 2015年7月15日 下午2:46:44
 */
public class JsonResponseHandler<M> extends ResponseHandler {
	private Class<M> mResponseClass = null;

	/** json回调拦截器 */
	private JsonResponseInterceptor mJsonResponseInterceptor = null;

	public JsonResponseHandler(Class<M> responseClass, JsonRequestCallable<M> callable) {
		super(callable);
		mResponseClass = responseClass;
	}

	@Override
	public void onSuccess(int statusCode, Object content) {
		if (statusCode != HttpStatus.SC_NO_CONTENT){
            try {
                M jsonResponse = parseResponse(content.toString());
                
				if (mJsonResponseInterceptor != null && !mJsonResponseInterceptor.checkResponse(jsonResponse)) {// 返回的数据失败
					int errorCode = mJsonResponseInterceptor.getErrorCode();
					String errorMsg = mJsonResponseInterceptor.getErrorMsg();
					onFailure(errorCode, new AsyncHttpException(errorMsg));
				} else {
					if (mCallable != null) {
						((JsonRequestCallable<M>)mCallable).onSuccess(jsonResponse);
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
        if (responseBody == null){
        	return null;
        }
        responseBody = responseBody.trim();

		if (mJsonResponseInterceptor != null) {// 返回的数据成功
			try {
				result = (M)mJsonResponseInterceptor.convertJsonToObj(responseBody, mResponseClass);
			} catch (Exception e) {
				throw new AsyncHttpException(AsyncHttpExceptionCode.jsonParseException.getErrorCode(), e);
			}
		}else{
			throw new AsyncHttpException(AsyncHttpExceptionCode.jsonParseException.getErrorCode(), "没有设置json解析器");
		}

		return result;
    }

	public void setJsonResponseInterceptor(JsonResponseInterceptor jsonResponseInterceptor) {
		mJsonResponseInterceptor = jsonResponseInterceptor;
	}
    
}
