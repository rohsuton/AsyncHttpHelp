/**
 * Title: AsyncHttpExceptionCode.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:14:26
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.exception;

/** 
 * ClassName: AsyncHttpExceptionCode
 * Description:异常错误码
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:14:26
 */
public enum AsyncHttpExceptionCode {
	success(0),//正常情况
	defaultExceptionCode(-10000 - 1),//默认异常码
	objectNullCode(-10000 - 2),//对象为空
	jsonParseException(-10000 - 3),//json解析错误
	jsonResponseException(-10000 - 4),//json网路请求返回异常
	httpRequestException(-10000 - 5),//http请求异常
	httpResponseException(-10000 - 6),//http请求返回异常
	unknownHostException(-10000 - 7),//无法解析主机
	httpSocketException(-10000 - 8),//socket异常
	socketTimeoutException(-10000 - 9),//socket超时
	invalidJsonString(-10000 - 10),//无效的json字符串
	sslPeerUnverifiedException(-10000 - 11),//不支持ssl协议
	buildRequestError(-10000 - 12),//构建请求异常
	serviceAddrError(-10000 - 13);//服务器地址错误
	
	private AsyncHttpExceptionCode(int errorCode)
	{
		this.errorCode = errorCode;
	}
	
	private int errorCode = -1;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
