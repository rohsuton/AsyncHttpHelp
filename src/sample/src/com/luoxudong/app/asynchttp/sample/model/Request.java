/**
 * Title: Request.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月12日 下午5:32:40
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.sample.model;

import java.io.Serializable;
import java.util.List;

/** 
 * <pre>
 * ClassName: Request
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2016年1月12日 下午5:32:40
 * </pre>
 */
public class Request implements Serializable {
	private static final long serialVersionUID = 1L;

	private String key1 = null;
	
	private int key2 = 0;
	
	private boolean key3 = false;
	
	private List<String> key4 = null;

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public int getKey2() {
		return key2;
	}

	public void setKey2(int key2) {
		this.key2 = key2;
	}

	public boolean isKey3() {
		return key3;
	}

	public void setKey3(boolean key3) {
		this.key3 = key3;
	}

	public List<String> getKey4() {
		return key4;
	}

	public void setKey4(List<String> key4) {
		this.key4 = key4;
	}
}
