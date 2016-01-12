/**
 * Title: ByteUtil.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月12日 下午5:04:01
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.utils;

/** 
 * <pre>
 * ClassName: ByteUtil
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2016年1月12日 下午5:04:01
 * </pre>
 */
public class ByteUtil {
	public static String getHexStr(byte[] buffer) {
		String hexStr = "";
		if (buffer != null) {
			for (byte b : buffer) {
				String temp = Integer.toHexString(b & 0xFF);
				if (temp.length() == 1) {
					hexStr += "0";
				}
				
				hexStr += temp + " ";
			}
			
		}
		
		return hexStr.toUpperCase();
	}
}
