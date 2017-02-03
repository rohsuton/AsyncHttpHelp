/**
 * Title: UploadFileRequest.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年11月23日 下午5:07:54
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.request;

import com.luoxudong.app.asynchttp.AsyncHttpConst;
import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.ContentType;
import com.luoxudong.app.asynchttp.builder.UploadFileBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;
import com.luoxudong.app.asynchttp.handler.UploadFileResponseHandler;
import com.luoxudong.app.asynchttp.model.FileWrapper;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

/** 
 * <pre>
 * ClassName: UploadFileRequest
 * Description:上传文件
 * Create by: 罗旭东
 * Date: 2016年11月23日 下午5:07:54
 * </pre>
 */
public class UploadFileRequest extends AsyncHttpRequest {
	private final String TAG = UploadFileRequest.class.getSimpleName();

	private Map<String, FileWrapper> mFileMap = null;
	protected Map<String, String> mFormMap = null;
	private UploadFileResponseHandler mHandler = null;

	public UploadFileRequest(UploadFileBuilder builder) {
		super(builder);
		mFormMap = builder.getFormMap();
		mFileMap = builder.getFileMap();
	}
	
	@Override
	public Request buildRequest(RequestCallable callable) {
		MultipartBody.Builder builder = new MultipartBody.Builder()
        .setType(MultipartBody.FORM);
		addParams(builder);
		addFiles(builder);
		
		
		return mBuilder.post(builder.build()).build();
	}
	
	@Override
	public AsyncHttpTask build() {
		initRequest();
		return new AsyncHttpTask(this);
	}

	@Override
	public ResponseHandler buildResponseHandler(RequestCallable callable) {
		mHandler = new UploadFileResponseHandler(callable);
		return mHandler;
	}
	
	private void addParams(MultipartBody.Builder builder) {
		if (mFormMap != null && !mFormMap.isEmpty()) {
			for (String key : mFormMap.keySet()) {
				builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""), RequestBody.create(null, mFormMap.get(key)));
			}
		}
	}
	
	private void addFiles(MultipartBody.Builder builder) {
		if (mFileMap != null) {
			for (String key : mFileMap.keySet()) {
				RequestBody fileBody = null;
				final FileWrapper fileWrapper = mFileMap.get(key);
				
				fileBody = new UploadFileRequestBody(fileWrapper);
				builder.addFormDataPart(key, fileWrapper.getFile().getName(), fileBody);
			}
		}
	}

	class UploadFileRequestBody extends RequestBody {
		private FileWrapper mFileWrapper = null;
		private long mOffset = 0;
		private long mByteCount = 0;
		
		public UploadFileRequestBody(FileWrapper fileWrapper) {
			mFileWrapper = fileWrapper;
			mOffset = mFileWrapper.getStartPos();
			mByteCount = mFileWrapper.getBlockSize();
			
			if (mOffset == 0 && mByteCount == 0) {//完整上传
				mByteCount = mFileWrapper.getFile().length();
			}
		}
		
		@Override
		public MediaType contentType() {
			return MediaType.parse(ContentType.octetStream.getValue());
		}
		
		@Override
		public long contentLength() throws IOException {
			return mByteCount;
		}
		
		@Override
		public void writeTo(BufferedSink sink) throws IOException {
			mHandler.sendStartTransferMessage();
			if (mOffset < 0 || (mOffset > mFileWrapper.getFile().length() - 1)) {
				mHandler.sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("startPos错误!"));
				return;
			}
			
			if (mOffset + mByteCount > mFileWrapper.getFile().length()) {
				mHandler.sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("blockSize错误!"));
				return;
			}
			
			RandomAccessFile rf = new RandomAccessFile(mFileWrapper.getFile(), "r");  
        	rf.seek(mOffset);
        	
        	byte[] buffer = new byte[20 * 1024];
        	long length = 0;
        	try {
				long timeStamp = System.currentTimeMillis();
        		for (long count = 0; count < mByteCount;) {
	        		length = rf.read(buffer);
	        		if (length < 0){
	        			break;
	        		}
	        		if (count + length > mByteCount) {
	        			length = mByteCount - count;
	        		}
	        		
	        		sink.write(buffer, 0, (int)length);
	        		count += length;

					if ((System.currentTimeMillis() - timeStamp) >= AsyncHttpConst.TRANSFER_REFRESH_TIME_INTERVAL) {
						AsyncHttpLog.d(TAG, "上传进度:" + count + "/" + mByteCount);
						mHandler.sendTransferingMessage(mFileWrapper.getFile().getName(), mByteCount, count);
						timeStamp = System.currentTimeMillis();// 每一秒调用一次
					}
	        	}

				mHandler.sendTransferSucMessage(mFileWrapper.getFile().getName());
			} catch (Exception e) {
				mHandler.sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
			}
        	
        	sink.flush();
        	rf.close();
		}
		
	}
}
