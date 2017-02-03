/**
 * Title: ContentType.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年11月22日 下午3:48:48
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

/** 
 * <pre>
 * ClassName: ContentType
 * Description:请求内容枚举类型
 * Create by: 罗旭东
 * Date: 2016年11月22日 下午3:48:48
 * </pre>
 */
public enum ContentType {
	text("text/plain;charset=utf-8"),
	html("text/html;charset=utf-8"),
	octetStream("application/octet-stream");

    private String value = null;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
