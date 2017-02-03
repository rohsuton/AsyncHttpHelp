/**
 * Title: ResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午5:22:32
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.handler;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Response;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: ResponseHandler
 * Description:处理请求返回结果
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
    /** 中断请求 */
    protected static final int CANCEL_MESSAGE = 4;
    /** 返回结果是否在主线程中执行 */
    private boolean mMainThread = false;
    /** 请求回调 */
    protected RequestCallable mCallable = null;
    
    private Handler mMainHandler = null;
    
	public ResponseHandler(RequestCallable callable) {
		mCallable = callable;
		// 检测当前线程是否有绑定looper，如果没有绑定则使用主线程的looper

		if (Looper.myLooper() != null) {
			mMainHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ResponseHandler.this.handleMessage(msg);
				}
			};
		} else {
			mMainHandler = new Handler(Looper.getMainLooper()) {
				@Override
				public void handleMessage(Message msg) {
					ResponseHandler.this.handleMessage(msg);
				}
			};
		}
		
	}
    
    /**
     * 请求返回结果统一入口
     * @param response
     */
    public void onResponseSucess(Response response) {
		if (!response.isSuccessful()) {
			sendFailureMessage(AsyncHttpExceptionCode.httpResponseException.getErrorCode(), new AsyncHttpException(response.message()));
			return;
		}
         
		parseResponse(response);

     }
    
    /**
     * 解析返回数据
     * @param response
     */
    protected void parseResponse(Response response) {
    	try {
			byte[] buffer = response.body().bytes();

			sendSuccessMessage(response.headers(), buffer);
		} catch (IOException e) {
			sendFailureMessage(AsyncHttpExceptionCode.httpResponseException.getErrorCode(), e);
		}
    }
    
    public void sendSuccessMessage(Headers headers, byte[] buffer) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{ headers, buffer}));
    }

    public void sendFailureMessage(int errorCode, Throwable e) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{errorCode, e}));
    }

    public void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    public void sendFinishMessage() {
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
                handleSuccessMessage((Headers) response[0], (byte[])response[1]);
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
    
    protected void handleSuccessMessage(Headers headers, byte[] buffer) {
		if (mCallable != null) {
			mCallable.onSuccess(headers.toMultimap());
		}
		
		onSuccess(buffer);
    }
    
	protected void handlerMessageCustom(Message msg) {

	}
    
	protected void onStart() {
    	if (mCallable != null){
    		mCallable.onStart();
    	}
    }

	protected void onFinish() {
    	if (mCallable != null){
    		mCallable.onFinish();
    	}
    }
    
	protected void onCancel() {
    	if (mCallable != null){
    		mCallable.onCancel();
    	}
    }

	protected void onSuccess(byte[] buffer) {
    	if (mCallable != null) {
			mCallable.onSuccess(buffer);
		}
	}

    protected void handleFailureMessage(int errorCode, Throwable e) {
        onFailure(errorCode, e);
    }
    
    protected void onFailure(int errorCode, Throwable e) {
    	String errorMsg = e.getMessage();
    	AsyncHttpLog.e(TAG, "错误信息:" + errorMsg + "[" + errorCode + "]");
    	
		if (mCallable != null) {
			mCallable.onFailed(errorCode, errorMsg);
		}
    }
    
    protected void sendMessage(Message msg) {
        if(mMainHandler != null && mMainThread){//需要在主线程中执行
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
    
    public void setMainThread(boolean mainThread) {
		mMainThread = mainThread;
	}
    
}
