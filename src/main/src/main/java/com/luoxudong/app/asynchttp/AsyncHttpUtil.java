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
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

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
	/** 单例 */
	private volatile static AsyncHttpUtil sInstance = null;
	/** 全局使用同一个AsyncHttpClient对象 */
	private AsyncHttpClient mHttpClient = null;

	public static long sStartTime = System.nanoTime();

	public static void main(String[] args) {

	}

	/**
	 * 初始化AsyncHttpClient
	 * @return
     */
	public static AsyncHttpClient initHttpClient() {
		return getInstance().getHttpClient();
	}

	/**
	 * 初始化AsyncHttpClient
	 * @return
	 */
	public static AsyncHttpUtil getInstance() {
		return initHttpClient(null);
	}

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
		AsyncHttpClient asyncHttpClient = getInstance().getHttpClient();
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
		AsyncHttpClient asyncHttpClient = getInstance().getHttpClient();
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
	 * 初始化自定义HttpClient
	 * @param httpClient
	 * @return
	 */
	private static AsyncHttpUtil initHttpClient(AsyncHttpClient httpClient) {
		if (sInstance == null) {
			synchronized (AsyncHttpUtil.class) {
				if (sInstance == null) {
					sInstance = new AsyncHttpUtil(httpClient);
				}
			}
		}

		return sInstance;
	}

	/**
	 * 获取HttpClient
	 * @return
	 */
	public AsyncHttpClient getHttpClient() {
		return mHttpClient;
	}
}
