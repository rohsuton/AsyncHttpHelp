/**
 * Title: AsyncHttpTask.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午5:01:39
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;

import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.interfaces.IHttpRequestCancelListener;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;
import com.luoxudong.app.threadpool.manager.ThreadTaskObject;

/** 
 * ClassName: AsyncHttpTask
 * Description:发送http请求线程
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午5:01:39
 */
public class AsyncHttpTask extends ThreadTaskObject {
	private static final String TAG = AsyncHttpTask.class.getSimpleName();
	
	/** httpclient对象 */
	private AbstractHttpClient mHttpClient = null;
	
	/** http请求上下文 */
    private HttpContext mHttpContext = null;
    
    /** http请求 */
    private HttpUriRequest mHttpRequest = null;
    
    /** 请求类型 */
    private String mRequestMethodName = null;
    
    /** 请求地址 */
    private String mUrl = null;
    
    /** 结果回调 */
    private ResponseHandler mResponseHandler = null;
    
    /** 请求参数 */
    private RequestParams mRequestParams = null;
    
    /**
     * 异常出现次数
     */
    private int executionCount = 0;
    
    /**
     * 任务是否暂停
     */
    private boolean isTaskCanceled = false;
    
    private IHttpRequestCancelListener httpRequestCancelListener = null;
    
	public AsyncHttpTask(int threadPoolType, AbstractHttpClient httpClient, HttpContext httpContext, String url, String requestMethodName, RequestParams requestParams, ResponseHandler responseHandler) {
		super(threadPoolType, null);
		mHttpClient = httpClient;
		mHttpContext = httpContext;
		mRequestMethodName = requestMethodName;
		mResponseHandler = responseHandler;
		mRequestParams = requestParams;
		mUrl = url;
	}
	
	@Override
	public void run() {
		AsyncHttpLog.i(TAG, "启动HTTP任务...");

		if (isTaskCanceled) {
			if (httpRequestCancelListener != null) {
				httpRequestCancelListener.onCanceled();
			}

			if (mResponseHandler != null) {
				mResponseHandler.sendCancelMessage();
				mResponseHandler.sendFinishMessage();
			}

			return;
		}
		
		if (mRequestParams != null) {
			if (mRequestParams instanceof UploadRequestParams) {//上传
				uploadExecute();
				return;
			} else if (mRequestParams instanceof DownloadRequestParams) {//下载
				downloadExecute();
				return;
			}
		}
		
		execute();
	}
	
	@Override
	public void cancel() {
		isTaskCanceled = true;

		abortRequest();

		super.cancel();

		if (mResponseHandler != null) {
			mResponseHandler.sendCancelMessage();
		}
		
		if (httpRequestCancelListener != null) {
			httpRequestCancelListener.onCanceled();
		}
	}
	
	/**
	 * 中断请求
	 */
	private void abortRequest() {
		if (mHttpRequest != null && !mHttpRequest.isAborted()) {
			mHttpRequest.abort();
		}
	}
	
	private void makeRequest() throws IOException {
        if(!Thread.currentThread().isInterrupted()) {
        	try {
        		HttpResponse response = mHttpClient.execute(mHttpRequest, mHttpContext);
    			
        		if(!Thread.currentThread().isInterrupted()) {
        			if(mResponseHandler != null) {
        				mResponseHandler.sendResponseMessage(mHttpClient.getCookieStore(), response);
        			}
        		} else{
        		}
        	} catch (IOException e) {
        		if(!Thread.currentThread().isInterrupted()) {
        			throw e;
        		}
        	}
        }
    }

    private void makeRequestWithRetries() throws ConnectException {
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = mHttpClient.getHttpRequestRetryHandler();
        executionCount = 0;
        while (retry) {
            try {
                makeRequest();
                return;
            }catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount, mHttpContext);
            } catch (Exception e) {
                cause = new IOException("HttpClient空指针异常:" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount, mHttpContext);
            }
        }

        // no retries left, crap out with exception
        ConnectException ex = new ConnectException();
        ex.initCause(cause);
        throw ex;
    }
    
    /**
     * 
     * @description:执行请求
     * @return void
     * @throws
     */
	private void execute() {
		int errorCode = AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode();
		String errorMsg = null;
		if (!buildHttpRequest()){
			if (mResponseHandler != null) {
				errorCode = AsyncHttpExceptionCode.buildRequestError.getErrorCode();
				errorMsg = "构建http请求错误";
				mResponseHandler.sendFailureMessage(errorCode, new AsyncHttpException(errorCode, errorMsg));
			}
			
			return;
		}

		// 设置http头部信息
		buildHttpHeader();

		AsyncHttpLog.i(TAG, "URL:" + mUrl);
		try {
			if (mResponseHandler != null) {
				mResponseHandler.sendStartMessage();// 开始请求
			}
			makeRequestWithRetries();
		} catch (IOException e) {
			if (e.getCause() instanceof SSLPeerUnverifiedException) {
				errorCode = AsyncHttpExceptionCode.sslPeerUnverifiedException.getErrorCode();
				errorMsg = "不支持ssl加密传输，切换到普通模式。";
			} else if (e.getCause() instanceof UnknownHostException) {
				errorCode = AsyncHttpExceptionCode.unknownHostException.getErrorCode();
				errorMsg = e.getCause().getMessage();
	        	return;
            } else if (e.getCause() instanceof SocketException) {
				errorCode = AsyncHttpExceptionCode.httpSocketException.getErrorCode();
				errorMsg = e.getCause().getMessage();
	        	return;
            } else if (e.getCause() instanceof SocketTimeoutException) {
				errorCode = AsyncHttpExceptionCode.socketTimeoutException.getErrorCode();
				errorMsg = e.getCause().getMessage();
	        	return;
            } else if (e.getCause() instanceof ConnectTimeoutException) {
				errorCode = AsyncHttpExceptionCode.connectTimeoutException.getErrorCode();
				errorMsg = e.getCause().getMessage();
	        	return;
            } else if (e.getCause() instanceof IllegalStateException && e.getCause().getMessage() != null && e.getCause().getMessage().indexOf(" Target host must not be null") >= 0) {
				errorCode = AsyncHttpExceptionCode.serviceAddrError.getErrorCode();
				errorMsg = "服务器地址格式错误";
	        	return;
            } else {
				errorCode = AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode();
				errorMsg = e.getCause().getMessage();
			}
			
			if (mResponseHandler != null) {
				mResponseHandler.sendFailureMessage(errorCode, new AsyncHttpException(errorCode, errorMsg));
			}
			
			e.getCause().printStackTrace();
		}finally{
			if (mResponseHandler != null) {
				mResponseHandler.sendFinishMessage();//请求结束
			}
		}
	}
    
    /**
     * 
     * @description:执行上传请求
     * @return void
     * @throws
     */
    protected void uploadExecute()
    {
    	UploadRequestParams uploadRequestParams = (UploadRequestParams)mRequestParams;
		uploadRequestParams.setUploadResponseHandler((UploadResponseHandler)mResponseHandler);
		execute();//上传文件数据
    }
    
    /**
     * 
     * @description:执行下载请求，支持断点下载 
     * @return void
     * @throws
     */
    private void downloadExecute()
    {
    	DownloadRequestParams downloadRequestParams = (DownloadRequestParams)mRequestParams;
    	DownloadResponseHandler downloadHttpResponseHandler = (DownloadResponseHandler)mResponseHandler;
    	
    	//设置断点下载参数
    	String rangeValue = "bytes=" + downloadRequestParams.getStartPos() + "-";
    	
    	if (downloadRequestParams.getEndPos() > downloadRequestParams.getStartPos()){
    		rangeValue += downloadRequestParams.getEndPos();
    	}
    	
    	mRequestParams.putHeaderParam(AsyncHttpConst.HEADER_RANGE, rangeValue);
		
    	downloadHttpResponseHandler.setFileName(downloadRequestParams.getFileName());
    	downloadHttpResponseHandler.setFileDir(downloadRequestParams.getFileDir());
    	downloadHttpResponseHandler.setStartPos(downloadRequestParams.getStartPos());
		execute();
    }
    
    
    /**
     * 构建HttpRequest对象
     * @return
     */
    private boolean buildHttpRequest()
    {
    	mUrl = getUrlWithQueryString(mUrl, mRequestParams);
    	
    	if (mRequestMethodName == null || mUrl == null)
    	{
    		AsyncHttpLog.e(TAG, "url为空!");
    		return false;
    	}
    	
    	if (mRequestMethodName.equalsIgnoreCase(HttpGet.METHOD_NAME)){
    		mHttpRequest = new HttpGet(mUrl);
    	}else if (mRequestMethodName.equalsIgnoreCase(HttpPost.METHOD_NAME)){
    		HttpPost postRequest = new HttpPost(mUrl);
			if (mRequestParams != null) {
				postRequest.setEntity(paramsToEntity(mRequestParams));
			}
    		mHttpRequest = postRequest;
    	}else if (mRequestMethodName.equalsIgnoreCase(HttpPut.METHOD_NAME)){
    		HttpPut putRequest = new HttpPut();
			if (mRequestParams != null) {
				putRequest.setEntity(paramsToEntity(mRequestParams));
			}
    		mHttpRequest = putRequest;
    	}else if (mRequestMethodName.equalsIgnoreCase(HttpDelete.METHOD_NAME)){
    		mHttpRequest = new HttpDelete(mUrl);
    	}else if (mRequestMethodName.equalsIgnoreCase(HttpOptions.METHOD_NAME)){
    		mHttpRequest = new HttpOptions(mUrl);
    	}
    	
    	//设置连接超时
    	if (mRequestParams.getTimeout() > 0){
    		mHttpRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, mRequestParams.getTimeout());
    	}
    	return true;
    }
    
    /**
     * 构建http头
     * @return
     */
	private boolean buildHttpHeader() {
		Map<String, String> headers = mRequestParams.getHeaderParams();
		if (headers != null && headers.size() > 0) {
			for (String name : headers.keySet()) {
				mHttpRequest.addHeader(name, headers.get(name));
			}
		}

		return true;
	}

    /**
     * 获取完成url
     * @param url
     * @param params
     * @return
     */
    private String getUrlWithQueryString(String url, RequestParams params) {
        if(params != null) {
            String param = params.getParamString();
            if (param != null && !"".equals(param))
            {
            	if (url.indexOf("?") == -1) {
                    url += "?" + param;
                } else {
                    url += "&" + param;
                }
            }
        }

        return url;
    }

    /**
     * 获取http请求体
     * @param params
     * @return
     */
    private HttpEntity paramsToEntity(RequestParams params) {
        HttpEntity entity = null;

        if(params != null) {
            entity = params.getEntity();
        }

        return entity;
    }
    
	public void setHttpRequestCancelListener(IHttpRequestCancelListener httpRequestCancelListener) {
		this.httpRequestCancelListener = httpRequestCancelListener;
	}
}
