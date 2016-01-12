/**
 * Title: BinaryEntity.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年1月12日 下午3:17:16
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

/** 
 * <pre>
 * ClassName: BinaryEntity
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 罗旭东
 * Date: 2016年1月12日 下午3:17:16
 * </pre>
 */
public class BinaryEntity implements HttpEntity {
	private byte[] mBuffer = null;
	
	public BinaryEntity(byte[] buffer) {
		mBuffer = buffer;
	}
	
	@Override
	public void consumeContent() throws IOException {
		
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return new ByteArrayInputStream(mBuffer);
	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public long getContentLength() {
		if (mBuffer != null){
			return mBuffer.length;
		}
		
		return 0;
	}

	@Override
	public Header getContentType() {
		return new BasicHeader(HTTP.CONTENT_TYPE, "application/octet-stream");//text/plain
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		outstream.write(mBuffer);
	}

}
