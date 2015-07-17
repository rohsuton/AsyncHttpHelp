/**
 * Title: UploadResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013 luoxudong.com
 * Company: 个人
 * Author: 罗旭东
 * Date: 2015年7月17日 下午2:05:55
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.os.Message;

import com.luoxudong.app.asynchttp.callable.UploadRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.model.FileWrapper;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

/** 
 * ClassName: UploadResponseHandler
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 
 * Date: 2015年7月17日 下午2:05:55
 */
public class UploadResponseHandler extends ResponseHandler {
	private static final String TAG = DownloadResponseHandler.class.getSimpleName();
	
	/** 开始传输 */
    protected static final int FILE_TRANSFER_START = 100;
    
    /** 传输中 */
    protected static final int FILE_TRANSFERING = 101;
    
    /** 上传成功 */
    protected static final int FILE_TRANSFER_SUC = 102;
    
    /** 传输缓存大小 */
	protected static final int BUFFER_SIZE = 10 * 1024;
    
	/** 文件传输回调 */
	private UploadRequestCallable mFileCallable = null;
	
	public UploadResponseHandler(UploadRequestCallable callable) {
		super(callable);
		mFileCallable = callable;
	}
	
	/**
	 * 开始上传
	 */
	protected void sendStartTransferMessage() {
        sendMessage(obtainMessage(FILE_TRANSFER_START, null));
    }
	
	/**
	 * 正在上传
	 * @param name
	 * @param totalLength
	 * @param startPost
	 */
	protected void sendTransferingMessage(String name, long totalLength, long startPost) {
        sendMessage(obtainMessage(FILE_TRANSFERING, new Object[]{name, totalLength, startPost}));
    }
	
	/**
	 * 传输成功
	 * @param name
	 */
	protected void sendTransferSucMessage(String name) {
        sendMessage(obtainMessage(FILE_TRANSFER_SUC, name));
    }
	
	@Override
	protected void handlerMessageCustom(Message msg) {
		switch (msg.what) {
		case FILE_TRANSFER_START:
			if (mFileCallable != null) {
				mFileCallable.onStartTransfer();
			}
			break;
		case FILE_TRANSFERING:
			if (mFileCallable != null) {
				Object[] param = (Object[])msg.obj;
				mFileCallable.onTransfering((String)param[0], (Long)param[1], (Long)param[2]);
			}
			break;
		case FILE_TRANSFER_SUC:
			if (mFileCallable != null) {
				mFileCallable.onTransferSuc((String)msg.obj);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 上传文件
	 * @param out 输出流
	 * @param attrName 属性名
	 * @param fileWrapper 文件属性
	 */
	public void writeTo(OutputStream out, String attrName, FileWrapper fileWrapper, long contentLength){
		byte[] buffer = new byte[BUFFER_SIZE];
		RandomAccessFile randomAccessFile = null;//随机读取本地文件
		long timeStamp = System.currentTimeMillis();
		long writedLen = 0;//当前块已经上传的大小
		long length = 0;
		long writeLen = 0;//本次上传大小
		
		try {
			randomAccessFile = new RandomAccessFile(fileWrapper.getFile(), "r");
			randomAccessFile.seek(fileWrapper.getStartPos());
		} catch (FileNotFoundException e) {
			onFailure(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e1) {
				}
			}
			return;
		} catch (IOException e) {
			onFailure(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e1) {
				}
			}
			return;
		}
		
		AsyncHttpLog.d(TAG, "从" + fileWrapper.getStartPos() + "位置开始上传...");
		
		sendStartTransferMessage();
		
		long totalLength = fileWrapper.getFile().length();
		while (writedLen < contentLength) {
			try {
				//从文件中读内容
				length = randomAccessFile.read(buffer);
			} catch (IOException e) {
				onFailure(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
				if (randomAccessFile != null) {
					try {
						randomAccessFile.close();
					} catch (IOException e1) {
					}
				}
				return;
			}
			
			if (contentLength - writedLen > buffer.length) {
				writeLen = buffer.length;
			} else {
				writeLen = contentLength - writedLen;
			}
			
			if (length < writeLen) {
				writeLen = length;
				AsyncHttpLog.w(TAG, "块大小错误!");
			}
			
			try {
				out.write(buffer, 0, (int)writeLen);
			} catch (IOException e) {
				onFailure(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
				if (randomAccessFile != null) {
					try {
						randomAccessFile.close();
					} catch (IOException e1) {
					}
				}
				return;
			}
			
			writedLen += writeLen;
			
			if (mFileCallable != null && (System.currentTimeMillis() - timeStamp) >= AsyncHttpConst.TRANSFER_REFRESH_TIME_INTERVAL) {
				AsyncHttpLog.d(TAG, "已上传大小【已传大小:" + (fileWrapper.getStartPos() + writedLen) + "==块大小:" + contentLength + "==总大小" + totalLength + "】");
				sendTransferingMessage(attrName, totalLength, fileWrapper.getStartPos() + writedLen);
				timeStamp = System.currentTimeMillis();//每一秒调用一次
			}
			
			try {
				out.flush();
			} catch (IOException e) {
			}
		}
		
		AsyncHttpLog.d(TAG, "已上传大小writedLen:" + writedLen);
		
		if (writedLen == contentLength){
			sendTransferSucMessage(attrName);
		}
		
		try {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		} catch (IOException e) {
		}
	}
	
}
