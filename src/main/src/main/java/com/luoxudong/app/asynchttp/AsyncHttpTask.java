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

import android.text.TextUtils;

import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;
import com.luoxudong.app.asynchttp.interceptor.UserAgentInterceptor;
import com.luoxudong.app.asynchttp.request.AsyncHttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

/** 
 * ClassName: AsyncHttpTask
 * Description:发送http请求
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午5:01:39
 */
public class AsyncHttpTask {
	/** 请求 */
    private AsyncHttpRequest mHttpRequest = null;
    
    /** 返回结果处理类 */
    private ResponseHandler mResponseHandler = null;
    
    /** 回调 */
    private Call mCall = null;
    
    /** 请求 */
    private Request mRequest = null;
    
    public AsyncHttpTask(AsyncHttpRequest request) {
    	mHttpRequest = request;
    }
    
    /**
	 * 异步请求
	 * @param callable
	 */
	public long request(final RequestCallable callable) {
		try {
			initCallable(callable);
			mResponseHandler.sendStartMessage();//开始
			mCall.enqueue(new Callback() {
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					if (call.isCanceled()) {
						mResponseHandler.sendCancelMessage();
						return;
					}

					//请求数据成功
					mResponseHandler.onResponseSucess(response);
				}

				@Override
				public void onFailure(Call call, IOException e) {
					mResponseHandler.sendFailureMessage(AsyncHttpExceptionCode.httpResponseException.getErrorCode(), e);
				}
			});
		} catch (AsyncHttpException e){
			mResponseHandler.sendFailureMessage(e.getExceptionCode(), e);
		}

		return mHttpRequest.getId();
	}
	
	/**
	 * 同步请求
	 * @return
	 */
	public Response request() throws IOException {
		Response response = null;
		initCallable(null);

		mResponseHandler.sendStartMessage();//开始
		response = mCall.execute();
		mResponseHandler.sendFinishMessage();//结束
		return response;
	}
	
	/**
	 * 构建回调
	 * @param callable
	 * @return
	 */
    private Call initCallable(RequestCallable callable) throws AsyncHttpException {
		mResponseHandler = mHttpRequest.buildResponseHandler(callable);

		if (mHttpRequest.getAsyncHttpException() != null) {
			throw mHttpRequest.getAsyncHttpException();
		}

    	mRequest = mHttpRequest.buildRequest(callable);
		mResponseHandler.setMainThread(mHttpRequest.isMainThread());

    	//重设httpclient配置
    	OkHttpClient okHttpClient = AsyncHttpUtil.getInstance().getHttpClient().getOkHttpClient();

		if (callable != null) {
			callable.setId(mHttpRequest.getId());
		}

    	int connTimeout =  mHttpRequest.getConnectTimeout();
    	int readTimeout = mHttpRequest.getReadTimeout();
    	int writeTimeout = mHttpRequest.getWriteTimeout();
    	String userAgent = mHttpRequest.getUserAgent();
		Map<String, String> cookies = mHttpRequest.getCookies();
    	
    	if (connTimeout > 0 || readTimeout > 0 || writeTimeout > 0 || !TextUtils.isEmpty(userAgent) || cookies != null) {
    		//重新配置
    		Builder builder = okHttpClient.newBuilder();
    		
    		if (connTimeout > 0) {
    			builder = builder.connectTimeout(connTimeout, TimeUnit.MILLISECONDS);
    		}
    		
    		if (readTimeout > 0) {
    			builder = builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
    		}
    		
    		if (writeTimeout > 0) {
    			builder = builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
    		}
    		
    		if (!TextUtils.isEmpty(userAgent)) {
    			builder = builder.addInterceptor(new UserAgentInterceptor(userAgent));
    		}

    		okHttpClient = builder.build();
			CookieJar cookieJar = okHttpClient.cookieJar();

			//自定义cookie
			if (cookies != null) {
				List<Cookie> cookieList = new ArrayList<Cookie>();
				for (String key : cookies.keySet()) {
					cookieList.add(Cookie.parse(mRequest.url(), key + "=" + cookies.get(key)));
				}

				cookieJar.saveFromResponse(mRequest.url(), cookieList);
			}
    	}

    	mCall = okHttpClient.newCall(mRequest);
    	return mCall;
    }
}
