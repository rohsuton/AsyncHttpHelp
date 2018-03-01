/**
 * Title: AsyncHttpUtil.java
 * Description:
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年10月13日 下午2:57:44
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import com.luoxudong.app.asynchttp.builder.DownloadFileBuilder;
import com.luoxudong.app.asynchttp.builder.GetBuilder;
import com.luoxudong.app.asynchttp.builder.PostBuilder;
import com.luoxudong.app.asynchttp.builder.PostBytesBuilder;
import com.luoxudong.app.asynchttp.builder.PostFormBuilder;
import com.luoxudong.app.asynchttp.builder.PostJsonBuilder;
import com.luoxudong.app.asynchttp.builder.UploadFileBuilder;
import com.luoxudong.app.asynchttp.https.MySslSocketFactory;
import com.luoxudong.app.asynchttp.https.SSLParams;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * <pre>
 * ClassName: AsyncHttpUtil
 * Description:http请求工具类
 * Create by: 罗旭东
 * Date: 2016年10月13日 下午2:57:44
 * </pre>
 */
public class AsyncHttpUtil {
	/** 全局使用同一个AsyncHttpClient对象 */
	private static AsyncHttpClient mHttpClient = new AsyncHttpClient();

	public static long sStartTime = System.nanoTime();

	/**
	 * 普通get请求
	 * @return
     */
	public static GetBuilder get() {
		return new GetBuilder();
	}

	/**
	 * 普通post请求
	 * @return
     */
	public static PostBuilder post() {
		return new PostBuilder();
	}

	/**
	 * form表单形式的提交请求
	 * @return
     */
	public static PostFormBuilder postForm() {
		return new PostFormBuilder();
	}

	/**
	 * 已json对象的方式的请求
	 * @return
     */
	public static PostJsonBuilder postJson() {
		return new PostJsonBuilder();
	}

	/**
	 * 数据内容为byte数组的请求
	 * @return
     */
	public static PostBytesBuilder postBytes() {
		return new PostBytesBuilder();
	}

	/**
	 * 文件上传
	 * @return
     */
	public static UploadFileBuilder uploadFile() {
		return new UploadFileBuilder();
	}

	/**
	 * 文件下载
	 * @return
     */
	public static DownloadFileBuilder downloadFile() {
		return new DownloadFileBuilder();
	}

	/**
	 * 显示日志
	 */
	public static void enableLog() {
		AsyncHttpLog.enableLog();
	}

	/**
	 * 创建一个新的http客户端
	 * @return
	 */
	public static AsyncHttpClient newAsyncHttpClient() {
		return new AsyncHttpClient();
	}

	/**
	 * 增加证书
	 * @param cerDatas 证书字符串
	 */
	public static void setSslSocketFactory(String[] cerDatas) {
		setSslSocketFactory(getHttpClient(), cerDatas);
	}

	public static void setSslSocketFactory(AsyncHttpClient asyncHttpClients, String[] cerDatas) {
		if (asyncHttpClients == null) {
			return;
		}
		asyncHttpClients.setSslSocketFactory(cerDatas);
	}

	/**
	 * 增加证书
	 * @param cerStreams 证书数据流
	 */
	public static void setSslSocketFactory(InputStream[] cerStreams) {
		setSslSocketFactory(getHttpClient(), cerStreams);
	}

	public static void setSslSocketFactory(AsyncHttpClient asyncHttpClients,InputStream[] cerStreams) {
		if (asyncHttpClients == null) {
			return;
		}

		asyncHttpClients.setSslSocketFactory(cerStreams);
	}

	/**
	 * 信任所有证书，不安全
	 */
	public static void setNoSafeSslSocketFactory() {
		setSslSocketFactory((String[])null);
	}

	public static void setNoSafeSslSocketFactory(AsyncHttpClient asyncHttpClients) {
		if (asyncHttpClients == null) {
			return;
		}

		setSslSocketFactory(asyncHttpClients, (String[])null);
	}

	/**
	 * 设置UA
	 * @param userAgent
	 */
	public static void setUserAgent(String userAgent) {
		setUserAgent(getHttpClient(), userAgent);
	}

	public static void setUserAgent(AsyncHttpClient asyncHttpClients,String userAgent) {
		if (asyncHttpClients == null) {
			return;
		}

		asyncHttpClients.userAgent(userAgent).build();
	}

	/**
	 * 隐藏日志
	 */
	public static void disableLog() {
		AsyncHttpLog.disableLog();
	}

	/**
	 * 根据tag中断请求
	 * @param tag
     */
	public static void cancelTag(String tag) {
		AsyncHttpClient asyncHttpClient = getHttpClient();
		for (Call call : asyncHttpClient.getOkHttpClient().dispatcher().queuedCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
			}
		}

		for (Call call : asyncHttpClient.getOkHttpClient().dispatcher().runningCalls()) {
			if (tag.equals(call.request().tag())) {
				call.cancel();
			}
		}
	}

	/**
	 * 中断所有请求
	 */
	public static void cancelAll() {
		AsyncHttpClient asyncHttpClient = getHttpClient();
		for (Call call : asyncHttpClient.getOkHttpClient().dispatcher().queuedCalls()) {
			call.cancel();
		}

		for (Call call : asyncHttpClient.getOkHttpClient().dispatcher().runningCalls()) {
			call.cancel();
		}
	}

	private AsyncHttpUtil(AsyncHttpClient httpClient) {
		if (httpClient == null) {
			mHttpClient = new AsyncHttpClient();
		} else {
			mHttpClient = httpClient;
		}
	}

	/**
	 * 获取HttpClient
	 * @return
	 */
	public static AsyncHttpClient getHttpClient() {
		if (mHttpClient == null) {
			synchronized (AsyncHttpUtil.class) {
				if (mHttpClient == null) {
					mHttpClient = new AsyncHttpClient();
				}
			}
		}

		return mHttpClient;
	}
}
