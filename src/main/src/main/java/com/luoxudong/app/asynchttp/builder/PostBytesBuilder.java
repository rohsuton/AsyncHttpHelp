/**
 * Title: PostBytesBuilder.java
 * Description: 传输字节数组的构建类
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年12月29日 下午2:27:57
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.model.FileWrapper;
import com.luoxudong.app.asynchttp.request.PostBytesRequest;

/** 
 * <pre>
 * ClassName: PostBytesBuilder
 * Description:传输字节数组的构建类，请求内容和返回内容都为字节数组
 * Create by: 罗旭东
 * Date: 2016年12月29日 下午2:27:57
 * </pre>
 */
public class PostBytesBuilder extends RequestBuilder<PostBytesBuilder> {
	/** 请求内容字节数组 */
	private byte[] mBuffer = null;
	
	public PostBytesBuilder() {
		init();
	}
	
	@Override
	public AsyncHttpTask build() {
		return new PostBytesRequest(this).build();
	}

	/**
	 * 设置请求byte数组
	 * @param buffer byte数组
	 * @return
     */
	public PostBytesBuilder buffer(byte[] buffer) {
		mBuffer = buffer;
		return this;
	}
	
	public byte[] getBuffer() {
		return mBuffer;
	}
}
