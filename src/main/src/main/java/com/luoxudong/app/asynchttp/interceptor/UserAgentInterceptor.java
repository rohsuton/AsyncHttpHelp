/**
 * Title: UserAgentInterceptor.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年11月23日 上午11:55:05
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.interceptor;

import android.os.Build;
import android.webkit.WebSettings;

import java.io.IOException;

import com.luoxudong.app.asynchttp.AsyncHttpConst;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;

/** 
 * <pre>
 * ClassName: UserAgentInterceptor
 * Description:userAgent拦截器
 * Create by: 罗旭东
 * Date: 2016年11月23日 上午11:55:05
 * </pre>
 */
public class UserAgentInterceptor implements Interceptor {
	private String mUserAgent = null;
	
	public UserAgentInterceptor() {
		mUserAgent = AsyncHttpConst.sUserAgent;
	}
	
	public UserAgentInterceptor(String userAgent) {
		mUserAgent = userAgent;
	}
	
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        Request newRequest = request.newBuilder()
        .removeHeader(AsyncHttpConst.HEADER_USER_AGENT)
        .addHeader(AsyncHttpConst.HEADER_USER_AGENT, checkUserAgent(mUserAgent))
        .build();
        return chain.proceed(newRequest);
    }

    /**
     * 对不合法字符处理，见Headers->checkNameAndValue
     * @param userAgent
     * @return
     */
    private String checkUserAgent(String userAgent) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' && c!='\u0009' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
