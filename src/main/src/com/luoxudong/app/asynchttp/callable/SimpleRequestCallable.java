/**
 * Title: SimpleRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:25:48
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.callable;


/** 
 * <pre>
 * ClassName: SimpleRequestCallable
 * Description:http普通请求回调，回调方法在UI线程中运行
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:25:48
 * </pre>
 */
public abstract class SimpleRequestCallable extends BaseCallable {
	/**
	 * 返回普通文本内容
	 * @param responseInfo
	 */
	public void onSuccess(String responseInfo){};
}
