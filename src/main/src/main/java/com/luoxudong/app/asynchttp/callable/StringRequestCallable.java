/**
 * Title: StringRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年12月30日 下午2:50:36
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.callable;

/** 
 * <pre>
 * ClassName: StringRequestCallable
 * Description:请求结果为普通字符串的回调类
 * Create by: 罗旭东
 * Date: 2016年12月30日 下午2:50:36
 * </pre>
 */
public abstract class StringRequestCallable extends RequestCallable {
	/**
	 * 请求成功
	 * @param responseBody 普通字符串内容
     */
	public abstract void onSuccess(String responseBody);
}
