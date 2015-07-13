/**
 * Title: SimpleJsonRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:30:59
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp.callable;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import com.luoxudong.app.asynchttp.asynchttp.model.BaseResponse;

/** 
 * ClassName: SimpleJsonRequestCallable
 * Description:http普通json请求回调
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:30:59
 */
public abstract class SimpleJsonRequestCallable<M extends BaseResponse<M>>  extends BaseHttpRequestCallable {
	/**
	 * 返回普通json对象
	 * @param responseInfo
	 */
	public void onSuccess(M responseInfo){};
	
	/**
	 * 返回成功
	 * @param headers 返回的http头部信息
	 * @param responseInfo 返回内容json对象
	 */
	public void onSuccess(Header[] headers, M responseInfo){};
	
	/**
	 * 返回的cookie值以及json对象
	 * @param cookies 返回的cookie信息
	 * @param responseInfo 返回内容json对象
	 */
	public void onSuccess(List<Cookie> cookies, M responseInfo){};
}
