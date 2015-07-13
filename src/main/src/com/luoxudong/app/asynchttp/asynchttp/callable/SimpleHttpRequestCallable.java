/**
 * Title: SimpleHttpRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:25:48
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp.callable;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

/** 
 * ClassName: SimpleHttpRequestCallable
 * Description:http普通请求回调
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:25:48
 */
public abstract class SimpleHttpRequestCallable extends BaseHttpRequestCallable {
	/**
	 * 返回普通文本内容
	 * @param responseInfo
	 */
	public void onSuccess(String responseInfo){};
	
	/**
	 * 返回成功
	 * @param headers 返回的http头部信息
	 * @param responseInfo 返回内容
	 */
	public void onSuccess(Header[] headers, String responseInfo){};
	
	/**
	 * 返回的cookie值以及内容
	 * @param cookies 返回的cookie信息
	 * @param responseInfo 返回的json对象
	 */
	public void onSuccess(List<Cookie> cookies, String responseInfo){};
}
