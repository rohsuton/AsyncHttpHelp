/**
 * Title: JsonResponseInterceptor.java
 * Description:
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月7日 下午3:01:00
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.interceptor;

import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;

/**
 * ClassName: JsonResponseInterceptor
 * Description:发送Json返回拦截器，需要将json字符串转换成对象
 * Create by: 罗旭东
 * Date: 2016年1月7日 下午3:01:00
 */
public abstract class JsonResponseInterceptor<M> {
    /** 错误码 */
	private int mErrorCode = AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode();
	/** 错误信息 */
	private String mErrorMsg = "";
	
    /**
     * 把json字符串转换成对象
     * @param responseStr
     * @return
     */
    public abstract M convertJsonToObj(String responseStr, Class<M> responseClass);

    /**
     * 检测该结果是否成功
     * @param response
     * @return
     */
    public abstract boolean checkResponse(M response);

    public void setErrorCode(int errorCode) {
		mErrorCode = errorCode;
	}
    
    public void setErrorMsg(String errorMsg) {
		mErrorMsg = errorMsg;
	}
    
    /**
     * 错误码
     * @return
     */
    public int getErrorCode(){
        return mErrorCode;
    }

    /**
     * 错误信息
     * @return
     */
    public String getErrorMsg(){
        return mErrorMsg;
    }

}
