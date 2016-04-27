/**
 * Title: JsonRequestInterceptor.java
 * Description:
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月7日 下午3:00:00
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.interceptor;


/**
 * ClassName: JsonRequestInterceptor
 * Description:发送Json请求时拦截器，在发送请求之前处理数据，需要把json对象转换成字符串
 * Create by: 罗旭东
 * Date: 2016年1月7日 下午3:00:00
 */
public abstract class JsonRequestInterceptor {
    /**
     * 把指定对象转换成json字符串
     * @param requestObj
     * @return
     */
    public abstract String convertJsonToObj(Object requestObj);
}
