/**
 * Title: BinaryRequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月12日 下午3:14:58
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import org.apache.http.HttpEntity;

import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;
import com.luoxudong.app.asynchttp.utils.ByteUtil;


/** 
 * <pre>
 * ClassName: BinaryRequestParams
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2016年1月12日 下午3:14:58
 * </pre>
 */
public class BinaryRequestParams extends RequestParams {
	private static final String TAG = BinaryRequestParams.class.getSimpleName();
	
	/** 表单参数 */
	protected byte[] mBuffer = null;
	
	public void setBuffer(byte[] buffer) {
		mBuffer = buffer;
	}
	
	@Override
	public HttpEntity getEntity() {
		AsyncHttpLog.i(TAG, "请求参数：" + ByteUtil.getHexStr(mBuffer));
		return new BinaryEntity(mBuffer);
	}
	
	
}
