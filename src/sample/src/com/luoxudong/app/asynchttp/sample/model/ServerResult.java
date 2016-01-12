/**
 * Title: ServerResult.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月11日 下午4:47:55
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.sample.model;

/** 
 * <pre>
 * ClassName: ServerResult
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2016年1月11日 下午4:47:55
 * </pre>
 */
public class ServerResult {
	private int resultCode = 0;
	
	private String resultMessage = null;
	
	private String responseTime = null;

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public String getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
}
