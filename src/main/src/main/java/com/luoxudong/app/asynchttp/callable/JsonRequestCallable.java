/**
 * Title: JsonRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:30:59
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.callable;


/** 
 * <pre>
 * ClassName: JsonRequestCallable
 * Description:http普通json请求回调，回调方法在UI线程中运行
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:30:59
 * </pre>
 */
public abstract class JsonRequestCallable<M> extends StringRequestCallable {
	/**
	 * 返回普通json对象
	 * @param responseInfo json对象
	 */
	public abstract void onSuccess(M responseInfo);

	/**
	 * 返回内容为字符串
	 * @param responseBody 字符串内容
     */
	public void onSuccess(String responseBody) {};
}
