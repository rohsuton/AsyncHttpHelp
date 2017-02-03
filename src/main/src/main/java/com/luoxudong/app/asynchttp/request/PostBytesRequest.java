/**
 * Title: PostBytesRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年12月29日 下午2:31:03
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.request;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.ContentType;
import com.luoxudong.app.asynchttp.builder.PostBytesBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/** 
 * <pre>
 * ClassName: PostBytesRequest
 * Description:提交byte数组请求类
 * Create by: 罗旭东
 * Date: 2016年12月29日 下午2:31:03
 * </pre>
 */
public class PostBytesRequest extends AsyncHttpRequest {
	private byte[] mBuffer = null;
	
	public PostBytesRequest(PostBytesBuilder builder) {
		super(builder);
		mBuffer = builder.getBuffer();
	}
	
	@Override
	public AsyncHttpTask build() {
		initRequest();
		return new AsyncHttpTask(this);
	}

	@Override
	public Request buildRequest(RequestCallable callable) {
		if (getBuffer() == null) {
			mBuffer = new byte[0];
		}
		return mBuilder.post(RequestBody.create(MediaType.parse(ContentType.octetStream.getValue()), getBuffer())).build();
	}

	@Override
	public ResponseHandler buildResponseHandler(RequestCallable callable) {
		return new ResponseHandler(callable);
	}

	public byte[] getBuffer() {
		return mBuffer;
	}

}
