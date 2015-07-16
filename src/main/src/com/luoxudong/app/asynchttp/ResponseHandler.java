/**
 * Title: ResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午5:22:32
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.luoxudong.app.asynchttp.callable.SimpleRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: ResponseHandler
 * Description:http请求回调
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午5:22:32
 */
public class ResponseHandler {
	private static final String TAG = ResponseHandler.class.getSimpleName();
	
	/** 请求成功 */
	protected static final int SUCCESS_MESSAGE = 0;
	
	/** 请求失败 */
    protected static final int FAILURE_MESSAGE = 1;
    
    /** 开始请求 */
    protected static final int START_MESSAGE = 2;
    
    /** 请求完成 */
    protected static final int FINISH_MESSAGE = 3;
    
    /** 终端请求 */
    protected static final int CANCEL_MESSAGE = 4;

    protected SimpleRequestCallable mCallable = null;
    
    private Handler mMainHandler = null;
    
	public ResponseHandler(SimpleRequestCallable callable) {
		mCallable = callable;
		// 创建一个handler，把消息传送到主线程
		if (Looper.myLooper() != null) {
			mMainHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ResponseHandler.this.handleMessage(msg);
				}
			};

		}
	}
    
    /**
     * 请求返回结果统一入口
     * @param cookies
     * @param response
     */
    protected void sendResponseMessage(CookieStore cookieStore, HttpResponse response) {
         StatusLine status = response.getStatusLine();
         String responseBody = null;
         try {
             HttpEntity entity = null;
             HttpEntity temp = response.getEntity();
             if(temp != null) {
                 entity = new BufferedHttpEntity(temp);
                 responseBody = EntityUtils.toString(entity, AsyncHttpConst.HTTP_ENCODING);
                 AsyncHttpLog.i(TAG, responseBody);
             }
         } catch(IOException e) {
         	sendFailureMessage(AsyncHttpExceptionCode.httpResponseException.getErrorCode(), e);
         }

         if(status.getStatusCode() >= 300) {
             sendFailureMessage(AsyncHttpExceptionCode.httpResponseException.getErrorCode(), new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
         } else {
             sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), cookieStore, responseBody);
         }
     }
    
    protected void sendSuccessMessage(int statusCode, Header[] headers, CookieStore cookieStore, String responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, headers, cookieStore, responseBody}));
    }

    protected void sendFailureMessage(int errorCode, Throwable e) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{errorCode, e}));
    }

    protected void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    protected void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }
    
    public void sendCancelMessage(){
    	sendMessage(obtainMessage(CANCEL_MESSAGE, null));
    }
    
    protected void handleMessage(Message msg) {
        Object[] response;

        switch(msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[])msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue(), (Header[]) response[1], (CookieStore) response[2], (String) response[3]);
                break;
            case FAILURE_MESSAGE:
                response = (Object[])msg.obj;
                handleFailureMessage((Integer)response[0], (Throwable)response[1]);
                break;
            case START_MESSAGE:
                onStart();
                break;
            case FINISH_MESSAGE:
                onFinish();
                break;
            case CANCEL_MESSAGE:
            	onCancel();
            	break;
            default:
            	handlerMessageCustom(msg);
            	break;
        }
    }
    
    protected void handleSuccessMessage(int statusCode, Header[] headers, CookieStore cookieStore, String responseBody) {
		if (mCallable != null) {
			List<Cookie> cookies = null;
    		
			if (cookieStore != null){
				cookies = cookieStore.getCookies();
			}
			
			mCallable.onSuccess(headers, cookies);
		}
		
    	onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(int errorCode, Throwable e) {
        onFailure(errorCode, e);
    }
    
	protected void handlerMessageCustom(Message msg) {

	}
    
    public void onSuccess(int statusCode, String content) {
    	if (mCallable != null){
    		mCallable.onSuccess(content);
    	}
    }

    public void onFailure(int errorCode, Throwable e) {
    	String errorMsg = e.getMessage();
    	AsyncHttpLog.e(TAG, "错误信息:" + errorMsg + "[" + errorCode + "]");
    	
		if (mCallable != null) {
			mCallable.onFailed(errorCode, errorMsg);
		}
    }
    
    public void onStart() {
    	if (mCallable != null){
    		mCallable.onStart();
    	}
    }

    public void onFinish() {
    	if (mCallable != null){
    		mCallable.onFinish();
    	}
    }
    
    public void onCancel() {
    	if (mCallable != null){
    		mCallable.onCancel();
    	}
    }

    protected void sendMessage(Message msg) {
        if(mMainHandler != null){
        	mMainHandler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if(mMainHandler != null){
            msg = mMainHandler.obtainMessage(responseMessage, response);
        }else{
            msg = Message.obtain();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }
}
