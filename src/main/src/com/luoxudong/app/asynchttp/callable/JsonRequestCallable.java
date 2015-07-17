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

import com.luoxudong.app.asynchttp.model.BaseResponse;

/** 
 * <pre>
 * ClassName: JsonRequestCallable
 * Description:http普通json请求回调，回调方法在UI线程中运行
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:30:59
 * </pre>
 */
public abstract class JsonRequestCallable<M extends BaseResponse<M>>  extends SimpleRequestCallable {
	/**
	 * 返回普通json对象
	 * @param responseInfo
	 */
	public void onSuccess(M responseInfo){};
}
