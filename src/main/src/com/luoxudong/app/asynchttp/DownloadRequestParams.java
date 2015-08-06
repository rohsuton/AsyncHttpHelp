/**
 * Title: DownloadRequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月14日 下午12:00:25
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

/** 
 * ClassName: DownloadRequestParams
 * Description:下载文件请求参数
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午12:00:25
 */
public class DownloadRequestParams extends RequestParams {
	@SuppressWarnings("unused")
	private static final String TAG = DownloadRequestParams.class.getSimpleName();
	
	/** 本地保存的文件目录 */
	private String mFileDir = null;
	
	/** 本地保存的文件名 */
	private String mFileName = null;
	
	/** 断点下载起始位置，从0开始计算 */
	private long mStartPos = 0;
	
	/** 断点下载结束位置，小于等于起始位置时，默认下载完 */
	private long mEndPos = 0;

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

	/**
	 * 设置下载起始位置，从0开始计算
	 * @param startPos 起始位置，比如：从第一个字节开始传，则startPos为0
	 */
	public void setStartPos(long startPos) {
		mStartPos = startPos;
	}

	public long getStartPos() {
		return mStartPos;
	}
	
	/**
	 * 设置下载结束位置，小于等于起始位置时，默认下载完
	 * @param endPos 结束位置，如：传输前100个字节，则startPos=0，endPos=99
	 */
	public void setEndPos(long endPos) {
		mEndPos = endPos;
	}
	
	public long getEndPos() {
		return mEndPos;
	}
	
}
