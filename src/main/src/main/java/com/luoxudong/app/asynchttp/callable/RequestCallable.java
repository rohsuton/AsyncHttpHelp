/**
 * Title: RequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:21:52
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.callable;

import java.util.List;
import java.util.Map;

/** 
 * <pre>
 * ClassName: RequestCallable
 * Description:基础回调类，回调方法在UI线程中运行
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:21:52
 * </pre>
 */
public abstract class RequestCallable {
	/** 请求ID */
	protected long mId = 0;

	/**
	 * 开始请求
	 */
	public void onStart(){};
	
	/**
	 * 请求结束
	 */
	public void onFinish(){};
	
	/**
	 * 取消请求
	 */
	public void onCancel(){};
	
	/**
	 * 返回成功
	 * @param headers 返回的http头部信息
	 */
	public void onSuccess(Map<String, List<String>> headers){};
	
	
	/**
	 * 请求成功
	 * @param buffer byte数组
	 */
	public void onSuccess(byte[] buffer){};
	
	/**
	 * 操作失败回调
	 * @param errorCode 错误码
	 * @param errorMsg 错误信息
	 */
	public abstract void onFailed(int errorCode, String errorMsg);

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}
}
