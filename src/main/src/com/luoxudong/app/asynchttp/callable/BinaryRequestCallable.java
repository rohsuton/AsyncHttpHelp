/**
 * Title: BinaryRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月12日 下午4:33:25
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.callable;

/** 
 * <pre>
 * ClassName: BinaryRequestCallable
 * Description:返回二进制数据
 * Create by: 罗旭东
 * Date: 2016年1月12日 下午4:33:25
 * </pre>
 */
public abstract class BinaryRequestCallable extends SimpleRequestCallable {
	public void onSuccess(byte[] buffer){};
}
