/**
 * Title: BinaryResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月12日 下午4:27:59
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;

import com.luoxudong.app.asynchttp.callable.BinaryRequestCallable;
import com.luoxudong.app.asynchttp.callable.SimpleRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;
import com.luoxudong.app.asynchttp.utils.ByteUtil;

/** 
 * <pre>
 * ClassName: BinaryResponseHandler
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2016年1月12日 下午4:27:59
 * </pre>
 */
public class BinaryResponseHandler extends ResponseHandler {
	private static final String TAG = BinaryResponseHandler.class.getSimpleName();
	
	public BinaryResponseHandler(SimpleRequestCallable callable) {
		super(callable);
	}
	
	@Override
	protected void sendResponseMessage(CookieStore cookieStore, HttpResponse response) {
		 StatusLine status = response.getStatusLine();
         InputStream instream = null;
         
         if(status.getStatusCode() >= 300) {
             sendFailureMessage(AsyncHttpExceptionCode.httpResponseException.getErrorCode(), new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
             return;
 		}

		int length = (int)response.getEntity().getContentLength();
		try {
			instream = response.getEntity().getContent();
			byte[] buffer = new byte[length];
			length = instream.read(buffer);
			AsyncHttpLog.i(TAG, "返回结果:" + ByteUtil.getHexStr(buffer));
			sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), cookieStore, buffer);
		} catch (IllegalStateException e) {
			sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
			e.printStackTrace();
		} catch (IOException e) {
			sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
			e.printStackTrace();
		} finally {
			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
				}
			}
		}
	}
	
	@Override
	public void onSuccess(int statusCode, Object content) {
		if (mCallable != null){
    		((BinaryRequestCallable)mCallable).onSuccess((byte[])content);
    	}
	}
}
