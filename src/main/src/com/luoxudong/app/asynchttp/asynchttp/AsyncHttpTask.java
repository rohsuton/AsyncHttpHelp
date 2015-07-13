/**
 * Title: AsyncHttpTask.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午5:01:39
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import com.luoxudong.app.asynchttp.asynchttp.interfaces.IHttpRequestCancelListener;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
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
	private AbstractHttpClient httpClient = null;
    private HttpContext httpContext = null;
    private HttpUriRequest httpRequest = null;
    private String requestMethodName = null;//请求类型
    private String url = null;
    private AsyncHttpResponseHandler responseHandler = null;
    private RequestParams requestParams = null;
    private Map<String, String> headers = null;
    private int timeout = -1;
    
    /**
     * 异常出现次数
     */
    private int executionCount = 0;
    
    /**
     * 任务是否暂停
     */
    private boolean isTaskCanceled = false;
    
    private IHttpRequestCancelListener httpRequestCancelListener = null;
    
	public AsyncHttpTask(int threadPoolType, AbstractHttpClient httpClient, HttpContext httpContext, String url, String requestMethodName, RequestParams requestParams, AsyncHttpResponseHandler responseHandler) {
		super(threadPoolType, null);
		this.httpClient = httpClient;
		this.httpContext = httpContext;
		this.requestMethodName = requestMethodName;
		this.responseHandler = responseHandler;
		this.requestParams = requestParams;
		this.url = url;
	}
	
	@Override
	public void run() {
		AsyncHttpLog.i(TAG, "启动HTTP任务...");
		if (isTaskCanceled)
		{
			cancel();//取消任务
			return;
		}
		
		if (requestParams != null)
    	{
    		if (requestParams instanceof UploadRequestParams)//上传请求
    		{
    			uploadExecute();
    		}
    		else if (requestParams instanceof DownloadRequestParams)//下载请求
    		{
    			downloadExecute();
    		}
    		else {
    			execute();
			}
    	}else {
    		execute();
		}
	}
	
	@Override
	public void cancel() {
		isTaskCanceled = true;
		
		abortRequest();
		
		super.cancel();
		
		if(responseHandler != null) {
            responseHandler.sendCancelMessage();
        }
	}
	
	private void abortRequest()
	{
		if (httpRequest != null && !httpRequest.isAborted())
		{
			httpRequest.abort();
		}
	}
	
	private void makeRequest() throws IOException {
        if(!Thread.currentThread().isInterrupted()) {
        	try {
        		HttpResponse response = httpClient.execute(httpRequest, httpContext);
        		
        		CookieStore mCookieStore = httpClient.getCookieStore();
        		List<Cookie> cookies = null;
        		
    			if (mCookieStore != null){
    				cookies = mCookieStore.getCookies();
    			}
    			
        		if(!Thread.currentThread().isInterrupted()) {
        			if(responseHandler != null) {
        				responseHandler.sendResponseMessage(cookies, response);
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
        HttpRequestRetryHandler retryHandler = httpClient.getHttpRequestRetryHandler();
        executionCount = 0;
        while (retry) {
            try {
                makeRequest();
                return;
            } catch (UnknownHostException e) {
		        if(responseHandler != null) {
		            responseHandler.sendFailureMessage(AsyncHttpExceptionCode.unknownHostException.getErrorCode(), e);
		        }
	        	return;
            }catch (SocketException e){
                // Added to detect host unreachable
                if(responseHandler != null) {
                    responseHandler.sendFailureMessage(AsyncHttpExceptionCode.httpSocketException.getErrorCode(), e);
                }
                return;
            }catch (SocketTimeoutException e){
                if(responseHandler != null) {
                    responseHandler.sendFailureMessage(AsyncHttpExceptionCode.socketTimeoutException.getErrorCode(), e);
                }
                return;
            } catch (AsyncHttpException e) {
            	/*if(responseHandler != null) {
                    responseHandler.sendFailureMessage(AsyncHttpExceptionCode.socketTimeoutException.getErrorCode(), e);
                }
                return;*/
			} catch (IOException e) {
                cause = e;
                e.printStackTrace();
                return;
                //retry = retryHandler.retryRequest(cause, ++executionCount, httpContext);
            } catch (NullPointerException e) {
                // there's a bug in HttpClient 4.0.x that on some occasions causes
                // DefaultRequestExecutor to throw an NPE, see
                // http://code.google.com/p/android/issues/detail?id=5255
                cause = new IOException("HttpClient空指针异常:" + e.getMessage());
                //retry = retryHandler.retryRequest(cause, ++executionCount, httpContext);
                return;
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
    private void execute()
    {
    	if (isTaskCanceled)
		{
    		if (httpRequestCancelListener != null){
    			httpRequestCancelListener.onCanceled();
    		}
    		
    		if(responseHandler != null) {
                responseHandler.sendCancelMessage();
            }
    		
			return;
		}
    	
    	int errorCode = AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode();
    	String errorMsg = null;
    	buildHttpRequest();
    	
    	//设置http头部信息
    	buildHttpHeader();

    	AsyncHttpLog.i(TAG, "URL:" + url);
		try {
			if (responseHandler != null) {
				responseHandler.sendStartMessage();// 开始请求
			}

			makeRequestWithRetries();

			if (responseHandler != null) {
				responseHandler.sendFinishMessage();// 请求结束
			}
		} catch (IOException e) {
			if (e.getCause() instanceof SSLPeerUnverifiedException)
			{
				errorCode = AsyncHttpExceptionCode.sslPeerUnverifiedException.getErrorCode();
				errorMsg = "不支持ssl加密传输，切换到普通模式。";
			}
			else
			{
				errorCode = AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode();
				errorMsg = e.toString();
			}
			
			if (httpRequestCancelListener != null){
				httpRequestCancelListener.onCanceled();
			}
			
			if (responseHandler != null) {
				responseHandler.sendFinishMessage();
				responseHandler.sendFailureMessage(errorCode, new AsyncHttpException(errorCode, errorMsg));
			}
		} catch (Exception e) {
			String msg = null;
			if (e instanceof IllegalStateException && e.toString() != null && e.toString().indexOf(" Target host must not be null") >= 0){
				errorCode = AsyncHttpExceptionCode.serviceAddrError.getErrorCode();
				msg = "服务器地址格式错误";
			}else {
				errorCode = AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode();
				msg = e.toString();
			}
			
			if (responseHandler != null) {
				responseHandler.sendFinishMessage();
				responseHandler.sendFailureMessage(errorCode, new AsyncHttpException(errorCode, msg));
			}
			e.printStackTrace();
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
    	if (isTaskCanceled)
		{
			if (httpRequestCancelListener != null){
    			httpRequestCancelListener.onCanceled();
    		}
    		
    		if(responseHandler != null) {
                responseHandler.sendCancelMessage();
            }
    		
			return;
		}
    	
    	UploadRequestParams uploadRequestParams = (UploadRequestParams)requestParams;
    	UploadHttpResponseHandler uploadHttpResponseHandler = (UploadHttpResponseHandler)responseHandler;
    	
    	if(uploadHttpResponseHandler != null) {//开始上传
    		uploadHttpResponseHandler.sendStartTransferMessage();
        }
    	
    	Map<String, String> paramsMap = new HashMap<String, String>();
    	
    	UploadData uploadData = new UploadData();
    	
    	long fileOffset = 0;
    	long blockSize = -1;
    	if (uploadRequestParams.isBreakpointsTransfer())
    	{
    		fileOffset = uploadRequestParams.getFileOffset();
    		blockSize = uploadRequestParams.getFileSize() - fileOffset;
    	}
    	uploadData.setStartOffset(fileOffset);
		uploadData.setBlockSize(blockSize);
		uploadData.setUploadStringParams(paramsMap);
		
		abortRequest();//关闭上一次连接
		uploadRequestParams.setUploadData(uploadData);
		execute();//上传文件数据
    }
    
    /**
     * 
     * @description:执行下载请求
     * @return void
     * @throws
     */
    private void downloadExecute()
    {
    	if (isTaskCanceled)
		{
    		if (httpRequestCancelListener != null){
    			httpRequestCancelListener.onCanceled();
    		}
    		
    		if(responseHandler != null) {
                responseHandler.sendCancelMessage();
            }
    		
			return;
		}
    	
    	DownloadRequestParams downloadRequestParams = (DownloadRequestParams)requestParams;
    	DownloadHttpResponseHandler downloadHttpResponseHandler = (DownloadHttpResponseHandler)responseHandler;
    	
    	if(downloadHttpResponseHandler != null) {//开始下载
    		downloadHttpResponseHandler.sendStartTransferMessage();
        }
    	
		abortRequest();//关闭上一次连接
		if (downloadRequestParams.isBreakpointsTransfer())//支持断点续传
		{
			downloadHttpResponseHandler.addDownloadRange(headers);//断点下载
		}
		
		execute();
    }
    
    
    /**
     * 
     * @description:构建HttpRequest对象
     * @return
     * @return HttpUriRequest
     * @throws
     */
    private void buildHttpRequest()
    {
    	url = getUrlWithQueryString(url, requestParams);
    	
    	if (requestMethodName == null || url == null)
    	{
    		AsyncHttpLog.e(TAG, "url为空!");
    		return;
    	}
    	
    	if (requestMethodName.equalsIgnoreCase(HttpGet.METHOD_NAME)){
    		httpRequest = new HttpGet(url);
    	}else if (requestMethodName.equalsIgnoreCase(HttpPost.METHOD_NAME)){
    		HttpEntityEnclosingRequestBase postRequest = new HttpPost(url);
    		if(requestParams != null)
            {
            	postRequest.setEntity(paramsToEntity(requestParams));
            }
            httpRequest = postRequest;
    	}else if (requestMethodName.equalsIgnoreCase(HttpPut.METHOD_NAME)){
    		HttpEntityEnclosingRequestBase putRequest = new HttpPut();
    		if(requestParams != null)
            {
    			putRequest.setEntity(paramsToEntity(requestParams));
            }
    		httpRequest = putRequest;
    	}else if (requestMethodName.equalsIgnoreCase(HttpDelete.METHOD_NAME)){
    		httpRequest = new HttpDelete(url);
    	}
    	
    }
    
    private void buildHttpHeader()
    {
    	if (httpRequest != null && headers != null && headers.size() > 0)
    	{
    		for (String name : headers.keySet()) {
    			httpRequest.addHeader(name, headers.get(name));
            }
    	}
    }

    private <T> String getUrlWithQueryString(String url, RequestParams params) {
        if(params != null) {
            String paramString = params.getParamString();
            if (paramString != null && !"".equals(paramString))
            {
            	if (url.indexOf("?") == -1) {
                    url += "?" + paramString;
                } else {
                    url += "&" + paramString;
                }
            }
        }

        return url;
    }

    private <T> HttpEntity paramsToEntity(RequestParams params) {
        HttpEntity entity = null;

        if(params != null) {
            entity = params.getEntity();
        }

        return entity;
    }
    
	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setHttpRequestCancelListener(
			IHttpRequestCancelListener httpRequestCancelListener) {
		this.httpRequestCancelListener = httpRequestCancelListener;
	}
}
