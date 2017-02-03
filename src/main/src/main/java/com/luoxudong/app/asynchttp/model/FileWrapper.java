/**
 * Title: FileWrapper.java
 * Description: 
 * Copyright: Copyright (c) 2013 luoxudong.com
 * Company: 个人
 * Author: 罗旭东
 * Date: 2015年7月17日 下午5:11:54
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.model;

import java.io.File;

/** 
 * ClassName: FileWrapper
 * Description:上传文件参数封装类
 * Create by: 
 * Date: 2015年7月17日 下午5:11:54
 */
public class FileWrapper {
	/** 上传的文件 */
	private File mFile = null;
	/** 上传的起始地址 */
	private long mStartPos = 0;
	/** 上传的块大小，默认上传全部文件 */
	private long mBlockSize = 0;

	public File getFile() {
		return mFile;
	}

	public void setFile(File file) {
		mFile = file;
	}

	public long getStartPos() {
		return mStartPos;
	}

	public void setStartPos(long startPos) {
		mStartPos = startPos;
	}

	public long getBlockSize() {
		return mBlockSize;
	}

	public void setBlockSize(long blockSize) {
		mBlockSize = blockSize;
	}
	
}
