/**
 * Title: AsyncHttpResponseHandler.java
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
import org.apache.http.client.HttpResponseException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;

import com.luoxudong.app.asynchttp.callable.SimpleHttpRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: AsyncHttpResponseHandler
 * Description:http请求回调
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午5:22:32
 */
public class AsyncHttpResponseHandler {
	private static final String TAG = AsyncHttpResponseHandler.class.getSimpleName();
	protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int CANCEL_MESSAGE = 4;//任务取消中断

    private SimpleHttpRequestCallable callable = null;
    
    private Handler handler = null;
    
    public AsyncHttpResponseHandler(SimpleHttpRequestCallable callable)
    {
    	this.callable = callable;
    }
    
    /**
     * Creates a new AsyncHttpResponseHandler
     */
    public AsyncHttpResponseHandler() {
        // Set up a handler to post events back to the correct thread if possible
        /*if(Looper.myLooper() != null) {
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    AsyncHttpResponseHandler.this.handleMessage(msg);
                }
            };
            
        }*/
    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when the request is started, override to handle in your own code
     */
    public void onStart() {}

    /**
     * Fired in all cases when the request is finished, after both success and failure, override to handle in your own code
     */
    public void onFinish() {}
    
    public void onCancel() {}

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param content the body of the HTTP response from the server
     */
    public void onSuccess(String content) {
    	
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param statusCode the status code of the response
     * @param headers the headers of the HTTP response
     * @param content the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, Header[] headers, String content) {
    	if (callable != null)
    	{
    		callable.onSuccess(headers, content);
    		callable.onSuccess(content);
    	}
    	
        onSuccess(statusCode, content);
    }

    /**
     * Fired when a request returns successfully, override to handle in your own code
     * @param statusCode the status code of the response
     * @param content the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, String content)
    {
        onSuccess(content);
    }
    
    /**
     * Fired when a request fails to complete, override to handle in your own code
     * @param error the underlying cause of the failure
     * @param content the response body, if any
     */
    public void onFailure(int errorCode, Throwable e) {
    	String errorMsg = e.getMessage();
    	AsyncHttpLog.e(TAG, "错误信息:" + errorMsg + "[" + errorCode + "]");
    	onFailure(errorCode, errorMsg, e);
    }
    
    public void onFailure(int errorCode, String errorMsg, Throwable e)
    {
    	if (callable != null)
    	{
    		callable.onFailed(errorCode, errorMsg);
    	}
    }

    protected void sendSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[]{statusCode, headers, responseBody}));
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


    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(int statusCode, Header[] headers, String responseBody) {
        onSuccess(statusCode, headers, responseBody);
    }

    protected void handleFailureMessage(int errorCode, Throwable e) {
        onFailure(errorCode, e);
    }



    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object[] response;

        switch(msg.what) {
            case SUCCESS_MESSAGE:
                response = (Object[])msg.obj;
                handleSuccessMessage(((Integer) response[0]).intValue(), (Header[]) response[1], (String) response[2]);
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
    
    protected void handlerMessageCustom(Message msg)
    {
    	
    }

    protected void sendMessage(Message msg) {
        if(handler != null){
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if(handler != null){
            msg = this.handler.obtainMessage(responseMessage, response);
        }else{
            msg = Message.obtain();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }

    // Interface to AsyncHttpRequest
   protected void sendResponseMessage(List<Cookie> cookies, HttpResponse response) {
        StatusLine status = response.getStatusLine();
        String responseBody = null;
        try {
            HttpEntity entity = null;
            HttpEntity temp = response.getEntity();
            if(temp != null) {
                entity = new BufferedHttpEntity(temp);
                responseBody = EntityUtils.toString(entity, AsyncHttpConst.HTTP_ENCODING);
                AsyncHttpLog.i("HttpResponse", responseBody);
            }
        } catch(IOException e) {
        	sendFailureMessage(AsyncHttpExceptionCode.jsonResponseException.getErrorCode(), e);
        }

        if(status.getStatusCode() >= 300) {
            sendFailureMessage(AsyncHttpExceptionCode.httpResponseException.getErrorCode(), new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
        } else {
            sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
        }
    }
   
   public void setHandler(Handler handler) {
	   this.handler = handler;
   }
}
