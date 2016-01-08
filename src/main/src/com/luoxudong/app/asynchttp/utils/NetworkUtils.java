/**
 * Title: NetworkUtils.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年12月3日 上午10:52:55
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.utils;

import org.apache.http.HttpHost;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;

/** 
 * <pre>
 * ClassName: NetworkUtils
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2015年12月3日 上午10:52:55
 * </pre>
 */
public class NetworkUtils {
	public static NetworkInfo getActiveNetworkInfo(Context context) {
		return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
	}

	public static HttpHost getProxy(Context paramContext) {
		HttpHost httpHost = null;
		NetworkInfo networkInfo = getActiveNetworkInfo(paramContext);
		if ((networkInfo != null) && (networkInfo.isAvailable())) {
			String host = Proxy.getDefaultHost();
			int post = Proxy.getDefaultPort();
			if (host != null){
				httpHost = new HttpHost(host, post);
			}
		}
		return httpHost;
	}
}
