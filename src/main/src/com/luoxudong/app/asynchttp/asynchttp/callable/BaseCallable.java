/**
 * Title: BaseCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:21:52
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp.callable;

/** 
 * ClassName: BaseCallable
 * Description:基础回调类
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:21:52
 */
public abstract class BaseCallable {
	/**
	 * 操作成功回调
	 * @param obj 成功信息
	 */
	public void onSuccess(Object obj){};
	
	/**
	 * 操作失败回调
	 * @param errorCode 错误码
	 * @param errorMsg 错误信息
	 */
	public abstract void onFailed(int errorCode, String errorMsg);
}
