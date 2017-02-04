/**
 * Title: UploadFileBuilder.java
 * Description: 上传文件构建类
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2016年11月23日 下午5:13:21
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.builder;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.model.FileWrapper;
import com.luoxudong.app.asynchttp.request.UploadFileRequest;

/** 
 * <pre>
 * ClassName: UploadFileBuilder
 * Description:上传文件构建类，支持多文件断点上传下载
 * Create by: 罗旭东
 * Date: 2016年11月23日 下午5:13:21
 * </pre>
 */
public class UploadFileBuilder extends RequestBuilder<UploadFileBuilder> {
	/** form表单内容 */
	protected Map<String, String> mFormMap = null;
	/** 上传文件参数 */
	private Map<String, FileWrapper> mFileMap = null;
	
	public UploadFileBuilder() {
		init();
	}
	
	@Override
	protected void init() {
		super.init();
		mFormMap = new ConcurrentHashMap<String, String>();
		mFileMap = new ConcurrentHashMap<String, FileWrapper>();
	}
	
	@Override
	public AsyncHttpTask build() {
		UploadFileRequest request = new UploadFileRequest(this);
		request.setFormMap(mFormMap);
		request.setFileMap(mFileMap);

		initRequest(request);//初始化request
		return request.build();
	}
	
	public UploadFileBuilder addFormParam(String key, String value) {
		mFormMap.put(key, value);
		return this;
	}
	
	public UploadFileBuilder addFormParams(Map<String, String> formParams) {
		for(Map.Entry<String, String> entry : formParams.entrySet()) {
			addFormParam(entry.getKey(), entry.getValue());
        }
		return this;
	}
	
	public UploadFileBuilder addFile(String key, File file) {
		FileWrapper fileWrapper = new FileWrapper();
		fileWrapper.setFile(file);
		fileWrapper.setBlockSize(file.length());
		addFile(key, fileWrapper);
		return this;
	}
	
	public UploadFileBuilder addFile(String key, FileWrapper value) {
		mFileMap.put(key, value);
		return this;
	}
	
	public UploadFileBuilder addFiles(Map<String, FileWrapper> fileParams) {
		for(Map.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
			addFile(entry.getKey(), entry.getValue());
        }
		return this;
	}
}
