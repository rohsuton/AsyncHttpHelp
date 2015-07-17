/**
 * Title: DownloadResponseHandler.java
 * Description: 
 * Copyright: Copyright (c) 2013 luoxudong.com
 * Company: 个人
 * Author: 罗旭东
 * Date: 2015年7月17日 下午2:05:39
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;

import android.os.Message;

import com.luoxudong.app.asynchttp.callable.DownloadRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;


/** 
 * ClassName: DownloadResponseHandler
 * Description:文件下载回调
 * Create by: 
 * Date: 2015年7月17日 下午2:05:39
 */
public class DownloadResponseHandler extends ResponseHandler {
	private static final String TAG = DownloadResponseHandler.class.getSimpleName();
	
	/** 开始传输 */
    protected static final int FILE_TRANSFER_START = 100;
    
    /** 传输中 */
    protected static final int FILE_TRANSFERING = 101;
    
    /** 传输缓存大小 */
	protected static final int BUFFER_SIZE = 10 * 1024;
    
	/** 文件传输回调 */
	private DownloadRequestCallable mFileCallable = null;
	
	/** 本地保存的文件目录 */
	private String mFileDir = null;
	
	/** 本地保存的文件名 */
	private String mFileName = null;
	
	private long mStartPos = 0;
	
	
	public DownloadResponseHandler(DownloadRequestCallable callable) {
		super(callable);
		mFileCallable = callable;
	}

	@Override
	protected void sendResponseMessage(CookieStore cookieStore, HttpResponse response) {
		long totalLength = 0;//更新下载文件总长度
		HttpEntity httpEntity = response.getEntity();
		
		if (httpEntity.isChunked()) {//Chunked解码
			//这里有一个bug，Chunked解码的结果，无法通过这种方法获取实际长度
			totalLength = httpEntity.getContentLength();
		} else {
			totalLength = httpEntity.getContentLength();
			AsyncHttpLog.w(TAG, "文件大小:" + totalLength);
		}
		
		if (totalLength <= 0) {
			sendFailureMessage(AsyncHttpExceptionCode.invalidDownloadLength.getErrorCode(), new AsyncHttpException("下载文件长度无效"));
			return;
		}
		
		RandomAccessFile randomAccessFile = null;

		File localFile = new File(getFileDir(), getFileName());

		if (!localFile.getParentFile().exists()) {//文件夹不存在则创建文件夹
			localFile.getParentFile().mkdirs();
		}
		
		if (localFile.exists() && localFile.length() != totalLength){//文件存在但大小不一致，则删除
			localFile.delete();
		}
		
		try {
			boolean isNewFile = !localFile.exists();
			randomAccessFile = new RandomAccessFile(localFile, "rw");
			if (isNewFile) {//如果是新文件则指定文件大小 
				randomAccessFile.setLength(totalLength);
			}
		} catch (FileNotFoundException e1) {
			sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("文件不存在!"));
			return;
		} catch (IOException e) {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("IO异常!"));
			return;
		}finally{
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
		
		long startPost = 0;
		
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT)
		{
			AsyncHttpLog.w(TAG, "支持断点下载!");
			startPost = getStartPos();
		}
		
		sendStartTransferMessage();
		downloading(response, cookieStore, totalLength, startPost, localFile);
	}
	
	/**
	 * 开始下载
	 */
	protected void sendStartTransferMessage() {
        sendMessage(obtainMessage(FILE_TRANSFER_START, null));
    }
	
	/**
	 * 正在传输
	 */
	protected void sendTransferingMessage(long totalLength, long startPost) {
        sendMessage(obtainMessage(FILE_TRANSFERING, new long[]{totalLength, startPost}));
    }
	
	
	/**
	 * 开始下载
	 * @param response
	 * @param cookieStore
	 * @param totalLength
	 * @param startPost
	 * @param file
	 */
	private void downloading(HttpResponse response, CookieStore cookieStore, long totalLength, long startPost, File file){
		byte[] buffer = new byte[BUFFER_SIZE];
		InputStream instream = null;
		RandomAccessFile randomAccessFile = null;
		long timeStamp = System.currentTimeMillis();
		int length = 0;
		try {
			instream = response.getEntity().getContent();
			
			randomAccessFile = new RandomAccessFile(file, "rw");
			AsyncHttpLog.d(TAG, "从" + startPost + "位置开始下载");
			randomAccessFile.seek(startPost);
			
			while (startPost != totalLength && length != -1) {
				length = instream.read(buffer);
				
				if (length == -1) {
					if (startPost != totalLength) {
						// 矫正文件大小
						// sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(),
						// new AsyncHttpException("文件大小不一致!"));
					}
					break;
				}
				
				startPost += length;
				randomAccessFile.write(buffer, 0, length);
				
				if (mFileCallable != null && (System.currentTimeMillis() - timeStamp) >= AsyncHttpConst.TRANSFER_REFRESH_TIME_INTERVAL) {
					AsyncHttpLog.d(TAG, "下载进度:" + startPost + "/" + totalLength);
					sendTransferingMessage(totalLength, startPost);
					timeStamp = System.currentTimeMillis();// 每一秒调用一次
				}
			}
			
			if (startPost == totalLength) {//下载完成
				StatusLine status = response.getStatusLine();
				sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), cookieStore, null);
			} else if (startPost > totalLength) {
				sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("本地文件长度超过总长度!"));
				return;
			}
			
		} catch (IllegalStateException e) {
			sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
			e.printStackTrace();
		} catch (SocketException e) {
			sendFailureMessage(AsyncHttpExceptionCode.httpSocketException.getErrorCode(), e);
		}
		catch (IOException e) {
			sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
			e.printStackTrace();
		} finally {
			if (randomAccessFile != null) {
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
				long[] param = (long[])msg.obj;
				mFileCallable.onTransfering(param[0], param[1]);
			}
			break;
		default:
			break;
		}
	}
	
	public String getFileDir() {
		return mFileDir;
	}

	public void setFileDir(String fileDir) {
		mFileDir = fileDir;
	}

	public String getFileName() {
		return mFileName;
	}

	public void setFileName(String fileName) {
		mFileName = fileName;
	}

	public long getStartPos() {
		return mStartPos;
	}

	public void setStartPos(long startPos) {
		mStartPos = startPos;
	}
	
}
