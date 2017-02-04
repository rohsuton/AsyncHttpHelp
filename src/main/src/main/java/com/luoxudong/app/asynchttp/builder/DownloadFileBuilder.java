/**
 * Title: DownloadFileBuilder.java
 * Description: 下载文件的构建类
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年12月29日 上午10:58:18
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.request.DownloadFileRequest;
import com.luoxudong.app.asynchttp.request.PostBytesRequest;

/** 
 * <pre>
 * ClassName: DownloadFileBuilder
 * Description:下载文件的构建类,支持文件断点下载
 * Create by: 罗旭东
 * Date: 2016年12月29日 上午10:58:18
 * </pre>
 */
public class DownloadFileBuilder extends RequestBuilder<DownloadFileBuilder> {
	/** 断点下载起始位置 */
	private long mOffset = 0;
	/** 下载文件大小 */
	private long mLength = 0;
	/** 下载文件保存路径 */
	private String mFileDir = null;
	/** 下载文件名 */
	private String mFileName = null;

	@Override
	public AsyncHttpTask build() {
		DownloadFileRequest request = new DownloadFileRequest(this);
		request.setFileDir(mFileDir);
		request.setFileName(mFileName);
		request.setLength(mLength);
		request.setOffset(mOffset);

		initRequest(request);//初始化request
		return request.build();
	}

	/**
	 * 设置下载起始位置，默认从文件开始位置下载
	 * @param offset 起始位置
	 * @return
     */
	public DownloadFileBuilder offset(long offset) {
		mOffset = offset;
		return this;
	}

	/**
	 * 设置本次下载数据大小，默认下载整个文件
	 * @param length 下载数据大小
	 * @return
     */
	public DownloadFileBuilder length(long length) {
		mLength = length;
		return this;
	}

	/**
	 * 设置下载文件保存的目录
	 * @param fileDir 下载文件保存的目录
	 * @return
     */
	public DownloadFileBuilder fileDir(String fileDir) {
		mFileDir = fileDir;
		return this;
	}

	/**
	 * 设置下载文件本地名称
	 * @param fileName 文件名
	 * @return
     */
	public DownloadFileBuilder fileName(String fileName) {
		mFileName = fileName;
		return this;
	}
}
