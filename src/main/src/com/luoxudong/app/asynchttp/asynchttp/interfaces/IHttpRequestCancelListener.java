/**
 * Title: IHttpRequestCancelListener.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:48:11
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp.interfaces;

/** 
 * ClassName: IHttpRequestCancelListener
 * Description:取消http请求回调
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:48:11
 */
public interface IHttpRequestCancelListener {
	public void onCanceled();
}
