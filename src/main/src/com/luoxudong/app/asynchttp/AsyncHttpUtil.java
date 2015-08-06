/**
 * Title: AsyncHttpUtil.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月14日 下午5:00:05
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luoxudong.app.asynchttp.adapter.BaseJsonHttpResponseAdapter;
import com.luoxudong.app.asynchttp.callable.DownloadRequestCallable;
import com.luoxudong.app.asynchttp.callable.JsonRequestCallable;
import com.luoxudong.app.asynchttp.callable.SimpleRequestCallable;
import com.luoxudong.app.asynchttp.callable.UploadRequestCallable;
import com.luoxudong.app.asynchttp.model.BaseResponse;
import com.luoxudong.app.asynchttp.model.FileWrapper;
import com.luoxudong.app.threadpool.constant.ThreadPoolConst;

/** 
 * <pre>
 * ClassName: AsyncHttpUtil
 * Description:http请求工具类 
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午5:00:05
 * </pre>
 */
public class AsyncHttpUtil {
	private static BaseJsonHttpResponseAdapter mResponseAdapter = null;
	
	/**
	 * 设置json返回参数解析规则
	 * @param responseAdapter
	 */
	public static void setResponseAdapter(BaseJsonHttpResponseAdapter responseAdapter){
		mResponseAdapter = responseAdapter;
	}
	
	/**
	 * 设置ssl请求
	 * @param sslSocketFactory
	 */
	public static void setSSLSocketFactory(SSLSocketFactory sslSocketFactory){
		AsyncHttpClient.setSSLSocketFactory(sslSocketFactory);
	}
	
	/**
	 * 发送简单的http get请求
	 * @param url 请求url地址
	 * @param callable 返回结果回调
	 */
	public static void simpleGetHttpRequest(String url, SimpleRequestCallable callable){
		simpleGetHttpRequest(url, null, callable);
	}
	
	/**
	 * 发送简单的http get请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param callable 返回结果回调
	 */
	public static void simpleGetHttpRequest(String url, Map<String, String> urlParams, SimpleRequestCallable callable){
		simpleGetHttpRequest(url, urlParams, null, 0, callable);
	}
	
	/**
	 * 发送简单的http get请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义链接超时时间
	 * @param callable 返回结果回调
	 */
	public static void simpleGetHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, SimpleRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		RequestParams params = new RequestParams();
		ResponseHandler handler = new ResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.get(url, params, handler);
	}
	
	/**
	 * 发送http get请求，返回结果为json对象
	 * @param url 请求url地址
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void jsonGetHttpRequest(String url, Class<M> responseClass, JsonRequestCallable<M> callable){
		jsonGetHttpRequest(url, null, null, 0, responseClass, callable);
	}
	
	/**
	 * 发送http get请求，返回结果为json对象
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义链接超时时间
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void jsonGetHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, Class<M> responseClass, JsonRequestCallable<M> callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		RequestParams params = new RequestParams();
		JsonResponseHandler<M> handler = new JsonResponseHandler<M>(responseClass, callable);
		
		if (mResponseAdapter != null){
			handler.setResponseAdapter(mResponseAdapter);
		}
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.get(url, params, handler);
	}
	
	/**
	 * 发送简单http post请求
	 * @param url 请求url地址
	 * @param callable 返回结果回调
	 */
	public static void simplePostHttpRequest(String url, SimpleRequestCallable callable){
		simplePostHttpRequest(url, null, null, 0, null, null, callable);
	}
	
	/**
	 * 发送简单http post请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义链接超时时间
	 * @param contentType 请求内容类型
	 * @param requestBody 消息体内容
	 * @param callable 返回结果回调
	 */
	public static void simplePostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, String requestBody, SimpleRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		RequestParams params = new RequestParams();
		ResponseHandler handler = new ResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (!TextUtils.isEmpty(contentType)){
			params.setContentType(contentType);
		}
		
		if (requestBody != null){
			params.setRequestBody(requestBody);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(url, params, handler);
	}
	
	/**
	 * 发送post请求，请求内容为json对象，返回结果为json对象
	 * @param url 请求url地址
	 * @param requestInfo 请求内容的json对象
	 * @param responseClass 返回结果对象类型
	 * @param callable 返回结果回调
	 */
	public static <T extends Serializable, M extends BaseResponse<M>> void jsonPostHttpRequest(String url, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable){
		jsonPostHttpRequest(url, null, requestInfo, responseClass, callable);
	}
	
	/**
	 * 发送post请求，请求内容为json对象，返回结果为json对象
	 * @param url 请求url地址
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param requestInfo 请求内容的json对象
	 * @param responseClass 返回结果对象类型
	 * @param callable 返回结果回调
	 */
	public static <T extends Serializable, M extends BaseResponse<M>> void jsonPostHttpRequest(String url, Map<String, String> headerParams, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable){
		jsonPostHttpRequest(url, null, headerParams, 0, AsyncHttpConst.HEADER_CONTENT_TYPE_JSON, requestInfo, responseClass, callable);
	}
	
	/**
	 * 发送post请求，请求内容为json对象，返回结果为json对象
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param contentType 请求内容类型
	 * @param requestInfo 请求内容的json对象
	 * @param responseClass 返回结果对象类型
	 * @param callable 返回结果回调
	 */
	public static <T extends Serializable, M extends BaseResponse<M>> void jsonPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		JsonRequestParams<T> params = new JsonRequestParams<T>();
		JsonResponseHandler<M> handler = new JsonResponseHandler<M>(responseClass, callable);
		
		if (mResponseAdapter != null){
			handler.setResponseAdapter(mResponseAdapter);
		}
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (!TextUtils.isEmpty(contentType)){
			params.setContentType(contentType);
		}
		
		params.setRequestJsonObj(requestInfo);
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(url, params, handler);
	}
	
	/**
	 * 发送form键值参数请求
	 * @param url 请求url地址
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formPostHttpRequest(String url, Map<String, String> formDatas, SimpleRequestCallable callable){
		formPostHttpRequest(url, null, null, 0, null, formDatas, callable);
	}
	
	/**
	 * 发送form键值参数请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param contentType 请求内容类型
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, Map<String, String> formDatas, SimpleRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		FormRequestParams params = new FormRequestParams();
		ResponseHandler handler = new ResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (!TextUtils.isEmpty(contentType)){
			params.setContentType(contentType);
		}
		
		if (formDatas != null){
			params.putFormParam(formDatas);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(url, params, handler);
	}
	
	/**
	 * 发送form键值参数请求,返回结果为json对象
	 * @param url 请求url地址
	 * @param formDatas form键值参数
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void formPostHttpRequest(String url, Map<String, String> formDatas, Class<M> responseClass, JsonRequestCallable<M> callable){
		formPostHttpRequest(url, null, formDatas, responseClass, callable);
	}
	
	/**
	 * 发送form键值参数请求,请求参数以及返回结果都为对象
	 * @param url 请求url地址
	 * @param key 请求参数的key
	 * @param requestInfo 请求参数对象
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <T extends Serializable, M extends BaseResponse<M>> void formPostHttpRequest(String url, String key, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable){
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();//new Gson();
    	String requestData = gson.toJson(requestInfo);
    	
    	Map<String, String> formDatas = new ConcurrentHashMap<String, String>();
    	formDatas.put(key, requestData);
    	formPostHttpRequest(url, formDatas, responseClass, callable);
	}
	
	/**
	 * 发送form键值参数请求,返回结果为json对象
	 * @param url 请求url地址
	 * @param headerParams url中带的参数，会进行url编码
	 * @param formDatas form键值参数
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void formPostHttpRequest(String url, Map<String, String> headerParams, Map<String, String> formDatas, Class<M> responseClass, JsonRequestCallable<M> callable){
		formPostHttpRequest(url, null, headerParams, 0, null, formDatas, responseClass, callable);
	}
	
	/**
	 * 发送form键值参数请求,返回结果为json对象
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param contentType 请求内容类型
	 * @param formDatas form键值参数
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void formPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, Map<String, String> formDatas, Class<M> responseClass, JsonRequestCallable<M> callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		FormRequestParams params = new FormRequestParams();
		JsonResponseHandler<M> handler = new JsonResponseHandler<M>(responseClass, callable);
		
		if (mResponseAdapter != null){
			handler.setResponseAdapter(mResponseAdapter);
		}
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (!TextUtils.isEmpty(contentType)){
			params.setContentType(contentType);
		}
		
		if (formDatas != null){
			params.putFormParam(formDatas);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(url, params, handler);
	}
	
	/**
	 * 发送模拟form-data表单请求
	 * @param url 请求url地址
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formDataPostHttpRequest(String url, Map<String, String> formDatas, SimpleRequestCallable callable){
		formDataPostHttpRequest(url, null, null, 0, formDatas, callable);
	}
	
	/**
	 * 发送模拟form-data表单请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formDataPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, Map<String, String> formDatas, SimpleRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		FormDataRequestParams params = new FormDataRequestParams();
		ResponseHandler handler = new ResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (formDatas != null){
			params.putFormParam(formDatas);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(url, params, handler);
	}
	
	/**
	 * 普通下载
	 * @param url 下载url
	 * @param fileDir  本地保存目录
	 * @param fileName 本地保存文件名
	 * @param callable 下载回调方法
	 */
	public static void download(String url, String fileDir, String fileName, DownloadRequestCallable callable){
		download(url, fileDir, fileName, 0, callable);
	}
	
	/**
	 * 断点下载
	 * @param url 下载url
	 * @param fileDir 本地保存目录
	 * @param fileName 本地保存文件名
	 * @param startPos 断点下载开始位置
	 * @param callable 下载回调方法
	 */
	public static void download(String url, String fileDir, String fileName, long startPos, DownloadRequestCallable callable){
		download(url, null, null, 0, fileDir, fileName, startPos, 0, callable);
	}
	
	/**
	 * 下载文件，支持断点续传
	 * @param url 下载url
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param fileDir 本地保存目录
	 * @param fileName 本地保存文件名
	 * @param startPos 下载开始位置
	 * @param endPos 下载结束位置
	 * @param callable 下载回调方法
	 */
	public static void download(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String fileDir, String fileName, long startPos, long endPos, DownloadRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		DownloadRequestParams params = new DownloadRequestParams();
		DownloadResponseHandler handler = new DownloadResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		params.setStartPos(startPos);
		params.setEndPos(endPos);
		params.setFileDir(fileDir);
		params.setFileName(fileName);
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_FILE_HTTP);
		httpRequest.post(url, params, handler);
	}
	
	/**
	 * 单文件普通上传
	 * @param url 上传地址
	 * @param formDatas 表单参数
	 * @param name 文件属性名
	 * @param file 要上产的文件
	 * @param callable 上传回调
	 */
	public static void upload(String url, Map<String, String> formDatas, String name, File file, UploadRequestCallable callable){
		FileWrapper fileWrapper = new FileWrapper();
		fileWrapper.setFile(file);
		upload(url, formDatas, name, fileWrapper, callable);;
	}
	
	/**
	 * 单文件断点上传
	 * @param url 上传地址
	 * @param formDatas 表单参数
	 * @param name 文件属性名
	 * @param fileWrapper 要上传的文件信息
	 * @param callable 上传回调
	 */
	public static void upload(String url, Map<String, String> formDatas, String name, FileWrapper fileWrapper, UploadRequestCallable callable){
		Map<String, FileWrapper> fileWrappers = new ConcurrentHashMap<String, FileWrapper>();
		fileWrappers.put(name, fileWrapper);
		upload(url, null, null, 0, formDatas, fileWrappers, callable);
	}
	
	/**
	 * 文件上传，支持多文件断点续传
	 * @param url 上传地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param formDatas 表单参数
	 * @param fileWrappers 上传的文件列表
	 * @param callable 上传回调
	 */
	public static void upload(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, Map<String, String> formDatas, Map<String, FileWrapper> fileWrappers, UploadRequestCallable callable){
		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		UploadRequestParams params = new UploadRequestParams();
		UploadResponseHandler handler = new UploadResponseHandler(callable);
		
		if (headerParams != null) {
			params.putHeaderParam(headerParams);
		}
		
		if (urlParams != null){
			params.put(urlParams);
		}
		
		if (timeout > 0){
			params.setTimeout(timeout);
		}
		
		if (formDatas != null){
			params.putFormParam(formDatas);
		}
		
		if (fileWrappers != null){
			params.putFileParam(fileWrappers);
		}
		
		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_FILE_HTTP);
		httpRequest.post(url, params, handler);
	}
}
