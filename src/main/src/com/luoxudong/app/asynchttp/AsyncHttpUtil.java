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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

import com.luoxudong.app.asynchttp.callable.BinaryRequestCallable;
import com.luoxudong.app.asynchttp.callable.DownloadRequestCallable;
import com.luoxudong.app.asynchttp.callable.JsonRequestCallable;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.callable.SimpleRequestCallable;
import com.luoxudong.app.asynchttp.callable.UploadRequestCallable;
import com.luoxudong.app.asynchttp.interceptor.JsonRequestInterceptor;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;
import com.luoxudong.app.asynchttp.model.FileWrapper;
import com.luoxudong.app.asynchttp.threadpool.constant.ThreadPoolConst;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * <pre>
 * ClassName: AsyncHttpUtil
 * Description:http请求工具类 
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午5:00:05
 * </pre>
 */
public class AsyncHttpUtil {
	private static final String TAG = "AsyncHttpUtil";
	
	/** 返回结果是否在主线程中执行 */
	private boolean mMainThread = true;
	
	/** 请求的URL地址 */
	private String mUrl = null;

	/** 链接超时时间 */
	private int mConnectTimeout = 0;

	/** 读取数据超时时间 */
	private int mReadTimeout = 0;

	/** ssl证书文件输入流 */
	private InputStream mCertificatesInputSream = null;

	/** url带的参数 */
	private Map<String, String> mUrlParams = null;

	/** http头部信息，cookie等参数可以通过这个方法设置 */
	private Map<String, String> mHeaderParams = null;

	/** Cookie信息 */
	private Map<String, String> mCookies = null;
	
	/** Content-Type */
	private String mContentType = null;

	/** 请求body内容 */
	private String mStrBody = null;

	/** 二进制数据内容 */
	private byte[] mBinaryBody = null;

	/** 发送json请求时,返回的json对象类型 */
	private Class mResponseClass = null;

	/** 发送json请求时，请求参数对应的json对象 */
	private Object mRequestObj = null;

	/** form表单方式请求时表单内容 */
	private Map<String, String> mFormDatas = null;

	/** 断点下载，断点上传的起始位置 */
	private long mFileStartPos = 0;

	/** 下载文件存放本地的目录 */
	private String mDownloadFileDir = null;

	/** 下载文件的文件名 */
	private String mDownloadfileName = null;

	/** 批量文件上传时，文件参数配置，可以自定义断点上传数据块 */
	private Map<String, FileWrapper> mFileWrappers = null;

	/** 普通http请求的回调 */
	private SimpleRequestCallable mSimpleCallable = null;

	/** json请求的回调 */
	private JsonRequestCallable mJsonCallable = null;
	
	/** 二进制请求的回调 */
	private BinaryRequestCallable mBinaryCallable = null;

	/** 下载文件回调 */
	private DownloadRequestCallable mDownloadCallable = null;

	/** 上传文件回调 */
	private UploadRequestCallable mUploadCallable = null;

	/** json请求拦截器 */
	private JsonRequestInterceptor mJsonRequestInterceptor = null;

	/** json回调来节气 */
	private JsonResponseInterceptor mJsonResponseInterceptor = null;

	private AsyncHttpUtil(Builder builder) {
		mMainThread = builder.mMainThread;
		mUrl = builder.mUrl;
		mConnectTimeout = builder.mConnectTimeout;
	 	mReadTimeout = builder.mReadTimeout;
		mCertificatesInputSream = builder.mCertificatesInputSream;
		mUrlParams = builder.mUrlParams;
		mHeaderParams = builder.mHeaderParams;
		mCookies = builder.mCookies;
		mContentType = builder.mContentType;
		mStrBody = builder.mStrBody;
		mBinaryBody = builder.mBinaryBody;
		mResponseClass = builder.mResponseClass;
		mRequestObj = builder.mRequestObj;
		mFormDatas = builder.mFormDatas;
		mFileStartPos = builder.mFileStartPos;
		mDownloadFileDir = builder.mDownloadFileDir;
		mDownloadfileName = builder.mDownloadfileName;
		mFileWrappers = builder.mFileWrappers;
		mJsonRequestInterceptor = builder.mJsonRequestInterceptor;
		mJsonResponseInterceptor = builder.mJsonResponseInterceptor;
		
		if (builder.mCallable instanceof JsonRequestCallable){
			mJsonCallable = (JsonRequestCallable)builder.mCallable;
		}else if (builder.mCallable instanceof DownloadRequestCallable) {
			mDownloadCallable = (DownloadRequestCallable)builder.mCallable;
		}else if (builder.mCallable instanceof UploadRequestCallable) {
			mUploadCallable = (UploadRequestCallable)builder.mCallable;
		}else if (builder.mCallable instanceof BinaryRequestCallable) {
			mBinaryCallable = (BinaryRequestCallable)builder.mCallable;
		}else if (builder.mCallable instanceof SimpleRequestCallable) {
			mSimpleCallable = (SimpleRequestCallable)builder.mCallable;
		}
	}
	
	public <M> void get() {
		if (TextUtils.isEmpty(mUrl)){
			Log.e(TAG, "URL不能为空！");
			return;
		}

		if (mResponseClass != null && mJsonResponseInterceptor == null){
			Log.e(TAG, "setResponseClass和setJsonResponseInterceptor必须同时使用");
			return;
		}

		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		RequestParams params = new RequestParams();
		ResponseHandler handler = new ResponseHandler(mSimpleCallable);//默认为普通get请求，返回字符串

		if (mJsonCallable != null){//以json对象的方式返回
			handler = new JsonResponseHandler<M>(mResponseClass, mJsonCallable);
			((JsonResponseHandler<M>)handler).setJsonResponseInterceptor(mJsonResponseInterceptor);
		}

		handler.setMainThread(mMainThread);
		
		if (mUrlParams != null){
			params.put(mUrlParams);
		}

		if (mHeaderParams != null) {
			params.putHeaderParam(mHeaderParams);
		}

		if (mCookies != null){
			params.putCookies(mCookies);
		}
		
		if (mConnectTimeout > 0){
			params.setConnectTimeout(mConnectTimeout);
		}

		if (mReadTimeout > 0){
			params.setReadTimeout(mReadTimeout);
		}

		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.get(mUrl, params, handler);
	}

	public <M> void post() {
		if (TextUtils.isEmpty(mUrl)){
			Log.e(TAG, "URL不能为空！");
			return;
		}

		if (mRequestObj != null && mJsonRequestInterceptor == null){
			Log.e(TAG, "setRequestObj和setJsonRequestInterceptor必须同时使用");
			return;
		}

		if (mResponseClass != null && mJsonResponseInterceptor == null){
			Log.e(TAG, "setResponseClass和setJsonResponseInterceptor必须同时使用");
			return;
		}

		AsyncHttpRequest httpRequest = new AsyncHttpRequest();

		RequestParams params = new RequestParams();
		ResponseHandler handler = new ResponseHandler(mSimpleCallable);//默认为普通get请求，返回字符串

		//请求内容为json对象
		if (mRequestObj != null){
			params = new JsonRequestParams();
			((JsonRequestParams)params).setRequestJsonObj(mRequestObj);
			((JsonRequestParams)params).setJsonRequestInterceptor(mJsonRequestInterceptor);
		}else if (mFormDatas != null){
			params = new FormRequestParams();
			((FormRequestParams)params).putFormParam(mFormDatas);
		}else if (mBinaryBody != null){
			params = new BinaryRequestParams();
			((BinaryRequestParams)params).setBuffer(mBinaryBody);
		}else{
			params.setRequestBody(mStrBody);
		}
		
		//返回结果为json对象
		if (mJsonCallable != null) {
			handler = new JsonResponseHandler<M>(mResponseClass, mJsonCallable);
			((JsonResponseHandler<M>)handler).setJsonResponseInterceptor(mJsonResponseInterceptor);
			
			if (mContentType != null) {
				mContentType = AsyncHttpConst.HEADER_CONTENT_TYPE_JSON;
			}
		} else if (mBinaryCallable != null) {
			handler = new BinaryResponseHandler(mBinaryCallable);
		}
		
		handler.setMainThread(mMainThread);
		
		if (mUrlParams != null){
			params.put(mUrlParams);
		}

		if (mHeaderParams != null) {
			params.putHeaderParam(mHeaderParams);
		}
		
		if (mCookies != null){
			params.putCookies(mCookies);
		}

		if (mConnectTimeout > 0){
			params.setConnectTimeout(mConnectTimeout);
		}

		if (mReadTimeout > 0){
			params.setReadTimeout(mReadTimeout);
		}
		
		if (!TextUtils.isEmpty(mContentType)){
			params.setContentType(mContentType);
		}

		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_SIMPLE_HTTP);
		httpRequest.post(mUrl, params, handler);
	}

	public void download() {
		if (TextUtils.isEmpty(mUrl)){
			Log.e(TAG, "URL不能为空！");
			return;
		}

		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		DownloadRequestParams params = new DownloadRequestParams();
		DownloadResponseHandler handler = new DownloadResponseHandler(mDownloadCallable);

		handler.setMainThread(mMainThread);
		
		if (mUrlParams != null){
			params.put(mUrlParams);
		}

		if (mHeaderParams != null) {
			params.putHeaderParam(mHeaderParams);
		}

		if (mConnectTimeout > 0){
			params.setConnectTimeout(mConnectTimeout);
		}

		if (mReadTimeout > 0){
			params.setReadTimeout(mReadTimeout);
		}

		params.setStartPos(mFileStartPos);
		params.setFileDir(mDownloadFileDir);
		params.setFileName(mDownloadfileName);

		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_FILE_HTTP);
		httpRequest.get(mUrl, params, handler);
	}

	public void upload() {
		if (TextUtils.isEmpty(mUrl)){
			Log.e(TAG, "URL不能为空！");
			return;
		}

		if (mFileWrappers == null || mFileWrappers.size() == 0){
			Log.e(TAG, "没有选中上传的文件！");
			return;
		}

		AsyncHttpRequest httpRequest = new AsyncHttpRequest();
		UploadRequestParams params = new UploadRequestParams();
		UploadResponseHandler handler = new UploadResponseHandler(mUploadCallable);

		handler.setMainThread(mMainThread);
		
		if (mUrlParams != null){
			params.put(mUrlParams);
		}

		if (mHeaderParams != null) {
			params.putHeaderParam(mHeaderParams);
		}

		if (mConnectTimeout > 0){
			params.setConnectTimeout(mConnectTimeout);
		}

		if (mReadTimeout > 0){
			params.setReadTimeout(mReadTimeout);
		}

		if (mFormDatas != null){
			params.putFormParam(mFormDatas);
		}

		httpRequest.setThreadPoolType(ThreadPoolConst.THREAD_TYPE_FILE_HTTP);
		httpRequest.post(mUrl, params, handler);
	}

	public static void enableLog() {
		AsyncHttpLog.enableLog();
	}
	
	public static void disableLog() {
		AsyncHttpLog.disableLog();
	}
	
	public static class Builder {
		/** 返回结果是否在主线程中执行 */
		private boolean mMainThread = true;
		
		/** 请求的URL地址 */
		private String mUrl = null;

		/** 链接超时时间 */
		private int mConnectTimeout = 0;

		/** 读取数据超时时间 */
		private int mReadTimeout = 0;

		/** ssl证书文件输入流 */
		private InputStream mCertificatesInputSream = null;

		/** url带的参数 */
		private Map<String, String> mUrlParams = null;

		/** http头部信息，cookie等参数可以通过这个方法设置 */
		private Map<String, String> mHeaderParams = null;

		/** Cookie信息 */
		private Map<String, String> mCookies = null;
		
		/** User-Agent */
		private String mUserAgent = null;
		
		/** Content-Type */
		private String mContentType = null;

		/** 请求body内容 */
		private String mStrBody = null;

		/** 二进制数据内容 */
		private byte[] mBinaryBody = null;

		/** 发送json请求时,返回的json对象类型 */
		private Class mResponseClass = null;

		/** 发送json请求时，请求参数对应的json对象 */
		private Object mRequestObj = null;

		/** form表单方式请求时表单内容 */
		private Map<String, String> mFormDatas = null;

		/** 断点下载，断点上传的起始位置 */
		private long mFileStartPos = 0;

		/** 下载文件存放本地的目录 */
		private String mDownloadFileDir = null;

		/** 下载文件的文件名 */
		private String mDownloadfileName = null;

		/** 批量文件上传时，文件参数配置，可以自定义断点上传数据块 */
		private Map<String, FileWrapper> mFileWrappers = null;

		/** 普通http请求的回调 */
		private RequestCallable mCallable = null;

		/** json请求拦截器 */
		private JsonRequestInterceptor mJsonRequestInterceptor = null;

		/** json回调来节气 */
		private JsonResponseInterceptor mJsonResponseInterceptor = null;

		public Builder() {
		}

		public Builder mainThread(boolean mainThread) {
			mMainThread = mainThread;
			return this;
		}
		
		public Builder url(String url) {
			mUrl = url;
			return this;
		}

		public Builder setConnectTimeout(int connectTimeout) {
			mConnectTimeout = connectTimeout;
			return this;
		}

		public Builder setReadTimeout(int readTimeout) {
			mReadTimeout = readTimeout;
			return this;
		}

		public Builder setCertificatesInputSream(InputStream certificatesInputSream) {
			mCertificatesInputSream = certificatesInputSream;
			return this;
		}

		public Builder setUrlParams(Map<String, String> urlParams) {
			mUrlParams = urlParams;
			return this;
		}

		public Builder addUrlParam(String key, String value) {
			if (mUrlParams == null){
				mUrlParams = new HashMap<String, String>();
			}

			mUrlParams.put(key, value);
			return this;
		}

		public Builder setHeaderParams(Map<String, String> headerParams) {
			mHeaderParams = headerParams;
			return this;
		}

		public Builder addHeaderParam(String key, String value) {
			if (mHeaderParams == null){
				mHeaderParams = new HashMap<String, String>();
			}

			mHeaderParams.put(key, value);
			return this;
		}
		
		public Builder setCookies(Map<String, String> cookies) {
			mCookies = cookies;
			return this;
		}
		
		public Builder addCookie(String key, String value) {
			if (mCookies == null){
				mCookies = new HashMap<String, String>();
			}
			
			mCookies.put(key, value);
			return this;
		}

		public Builder setUserAgent(String userAgent) {
			addHeaderParam(AsyncHttpConst.HEADER_USER_AGENT, userAgent);
			return this;
		}
		
		public Builder setContentType(String contentType) {
			mContentType = contentType;
			return this;
		}

		public Builder setStrBody(String strBody) {
			mStrBody = strBody;
			return this;
		}

		public Builder setBinaryBody(byte[] binaryBody) {
			mBinaryBody = binaryBody;
			return this;
		}

		public <M> Builder setResponseClass(Class<M> responseClass) {
			mResponseClass = responseClass;
			return this;
		}

		public Builder setRequestObj(Object requestObj) {
			mRequestObj = requestObj;
			return this;
		}

		public Builder setFormDatas(Map<String, String> formDatas) {
			mFormDatas = formDatas;
			return this;
		}

		public Builder addFormData(String key, String value) {
			if (mFormDatas == null){
				mFormDatas = new HashMap<String, String>();
			}

			mFormDatas.put(key, value);

			return this;
		}

		public Builder setFileStartPos(long fileStartPos) {
			mFileStartPos = fileStartPos;
			return this;
		}

		public Builder setDownloadFileDir(String downloadFileDir) {
			mDownloadFileDir = downloadFileDir;
			return this;
		}

		public Builder setDownloadfileName(String downloadfileName) {
			mDownloadfileName = downloadfileName;
			return this;
		}

		public Builder setFileWrappers(Map<String, FileWrapper> fileWrappers) {
			mFileWrappers = fileWrappers;
			return this;
		}

		public Builder addUploadFile(String key, File uploadFile) {
			if (mFileWrappers == null){
				mFileWrappers = new HashMap<String, FileWrapper>();
			}

			FileWrapper fileWrapper = new FileWrapper();
			fileWrapper.setFile(uploadFile);
			mFileWrappers.put(key, fileWrapper);
			return this;
		}

		public Builder addFileWrapper(String key, FileWrapper fileWrapper) {
			if (mFileWrappers == null){
				mFileWrappers = new HashMap<String, FileWrapper>();
			}

			mFileWrappers.put(key, fileWrapper);

			return this;
		}

		public Builder setCallable(RequestCallable callable) {
			mCallable = callable;
			return this;
		}

		public Builder setJsonRequestInterceptor(JsonRequestInterceptor jsonRequestInterceptor) {
			mJsonRequestInterceptor = jsonRequestInterceptor;
			return this;
		}

		public Builder setJsonResponseInterceptor(JsonResponseInterceptor jsonResponseInterceptor) {
			mJsonResponseInterceptor = jsonResponseInterceptor;
			return this;
		}

		public AsyncHttpUtil build() {
			return new AsyncHttpUtil(this);
		}

	}
}
