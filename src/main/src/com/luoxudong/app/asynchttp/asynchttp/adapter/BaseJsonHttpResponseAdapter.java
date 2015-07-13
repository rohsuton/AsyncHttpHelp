/**
 * Title: BaseJsonHttpResponseAdapter.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:12:00
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp.adapter;

import com.luoxudong.app.asynchttp.asynchttp.model.BaseResponse;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;

/** 
 * ClassName: BaseJsonHttpResponseAdapter
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:12:00
 */
public abstract class BaseJsonHttpResponseAdapter {
	protected int errorCode = AsyncHttpExceptionCode.success.getErrorCode();
	protected String errorMsg = null;
	
	/**
	 * 检测返回数据是否成功
	 * @param response http请求返回的数据
	 * @return true:返回的数据成功，false:返回的数据失败
	 */
	public abstract <M extends BaseResponse<M>> boolean checkResponseData(M response);
	
	public int getErrorCode() {
		return errorCode;
	};
	
	public String getErrorMsg() {
		return errorMsg;
	}
}
