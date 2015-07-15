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
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
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
			cancel();// 取消任务
			return;
		}

		if (mRequestParams != null) {
			if (mRequestParams instanceof UploadRequestParams) {//上传
				uploadExecute();
			} else if (mRequestParams instanceof DownloadRequestParams) {//下载
				downloadExecute();
			} else {
				execute();
			}
		} else {
			execute();
		}
	}
	
	@Override
	public void cancel() {
		isTaskCanceled = true;

		abortRequest();

		super.cancel();

		if (mResponseHandler != null) {
			mResponseHandler.sendCancelMessage();
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
            } catch (UnknownHostException e) {
		        if(mResponseHandler != null) {
		            mResponseHandler.sendFailureMessage(AsyncHttpExceptionCode.unknownHostException.getErrorCode(), e);
		        }
	        	return;
            }catch (SocketException e){
                // Added to detect host unreachable
                if(mResponseHandler != null) {
                	mResponseHandler.sendFailureMessage(AsyncHttpExceptionCode.httpSocketException.getErrorCode(), e);
                }
                return;
            }catch (SocketTimeoutException e){
                if(mResponseHandler != null) {
                	mResponseHandler.sendFailureMessage(AsyncHttpExceptionCode.socketTimeoutException.getErrorCode(), e);
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
            } catch (Exception e) {
            	//e.printStackTrace();
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
	private void execute() {
		if (mResponseHandler != null) {
			mResponseHandler.sendStartMessage();// 开始请求
		}
		
		if (isTaskCanceled) {
			if (httpRequestCancelListener != null) {
				httpRequestCancelListener.onCanceled();
			}

			if (mResponseHandler != null) {
				mResponseHandler.sendCancelMessage();
			}

			return;
		}

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
			

			makeRequestWithRetries();
		} catch (IOException e) {
			if (e.getCause() instanceof SSLPeerUnverifiedException) {
				errorCode = AsyncHttpExceptionCode.sslPeerUnverifiedException
						.getErrorCode();
				errorMsg = "不支持ssl加密传输，切换到普通模式。";
			} else {
				errorCode = AsyncHttpExceptionCode.defaultExceptionCode
						.getErrorCode();
				errorMsg = e.toString();
			}

			if (httpRequestCancelListener != null) {
				httpRequestCancelListener.onCanceled();
			}

			if (mResponseHandler != null) {
				mResponseHandler.sendFailureMessage(errorCode,
						new AsyncHttpException(errorCode, errorMsg));
			}
		} catch (Exception e) {
			String msg = null;
			if (e instanceof IllegalStateException
					&& e.toString() != null
					&& e.toString().indexOf(" Target host must not be null") >= 0) {
				errorCode = AsyncHttpExceptionCode.serviceAddrError
						.getErrorCode();
				msg = "服务器地址格式错误";
			} else {
				errorCode = AsyncHttpExceptionCode.defaultExceptionCode
						.getErrorCode();
				msg = e.toString();
			}

			if (mResponseHandler != null) {
				mResponseHandler.sendFailureMessage(errorCode,
						new AsyncHttpException(errorCode, msg));
			}
			e.printStackTrace();
		}finally{
			if (mResponseHandler != null) {
				mResponseHandler.sendFinishMessage();// 请求结束
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
    	if (isTaskCanceled)
		{
			if (httpRequestCancelListener != null){
    			httpRequestCancelListener.onCanceled();
    		}
    		
    		if(mResponseHandler != null) {
    			mResponseHandler.sendCancelMessage();
            }
    		
			return;
		}
    	
    	/*UploadRequestParams uploadRequestParams = (UploadRequestParams)mRequestParams;
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
*/    }
    
    /**
     * 
     * @description:执行下载请求
     * @return void
     * @throws
     */
    private void downloadExecute()
    {
    	/*if (isTaskCanceled)
		{
    		if (httpRequestCancelListener != null){
    			httpRequestCancelListener.onCanceled();
    		}
    		
    		if(mResponseHandler != null) {
    			mResponseHandler.sendCancelMessage();
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
		
		execute();*/
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
		String contentType = mRequestParams.getContentType();
		if (headers != null && headers.size() > 0) {
			for (String name : headers.keySet()) {
				mHttpRequest.addHeader(name, headers.get(name));
			}
		}
		
		/*if (!TextUtils.isEmpty(contentType)){
			mHttpRequest.addHeader(AsyncHttpConst.HEADER_CONTENT_TYPE, contentType);
		}*/

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
    
	public void setHttpRequestCancelListener(
			IHttpRequestCancelListener httpRequestCancelListener) {
		this.httpRequestCancelListener = httpRequestCancelListener;
	}
}
