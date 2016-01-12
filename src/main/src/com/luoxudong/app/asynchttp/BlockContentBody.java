/**
 * Title: BlockContentBody.java
 * Description: 
 * Copyright: Copyright (c) 2013 luoxudong.com
 * Company: 个人
 * Author: 罗旭东
 * Date: 2015年7月17日 下午4:38:38
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.OutputStream;

import com.luoxudong.app.asynchttp.mime.content.AbstractContentBody;
import com.luoxudong.app.asynchttp.model.FileWrapper;

/** 
 * ClassName: BlockContentBody
 * Description:重写ContentBody，使其支持分块上传
 * Create by: 
 * Date: 2015年7月17日 下午4:38:38
 */
public class BlockContentBody extends AbstractContentBody {
	/** 上传文件回调 */
	private UploadResponseHandler mUploadResponseHandler = null;
	
	/** 属性名称 */
	private String mAttrName = null;
	
	/** 上传文件参数 */
	private  FileWrapper mFileWrapper = null;
	
	public BlockContentBody(String mimeType) {
		super(mimeType);
	}
	
	public BlockContentBody(UploadResponseHandler uploadResponseHandler, String attrName, FileWrapper fileWrapper)
	{
		this("application/octet-stream");
		mUploadResponseHandler = uploadResponseHandler;
		mAttrName = attrName;
		mFileWrapper = fileWrapper;
	}

	@Override
	public String getFilename() {
		return mFileWrapper.getFile().getName();
	}

	@Override
	public void writeTo(OutputStream out) {
		//开始上传
		if (mUploadResponseHandler != null) {
			mUploadResponseHandler.writeTo(out, mAttrName, mFileWrapper, getContentLength());
		}
	}

	@Override
	public String getCharset() {
		return AsyncHttpConst.HTTP_ENCODING;
	}

	@Override
	public String getTransferEncoding() {
		return "binary";
	}
	
	@Override
	public long getContentLength() {
		if (mFileWrapper.getBlockSize() <= 0) {//未指定上传大小时默认上传剩余多有
			return mFileWrapper.getFile().length() - mFileWrapper.getStartPos();
		}
		
		if (mFileWrapper.getBlockSize() > mFileWrapper.getFile().length()){//块大小大于文件大小，则只上传剩余的
			return mFileWrapper.getFile().length() - mFileWrapper.getStartPos();
		}
		
		if (mFileWrapper.getStartPos() + mFileWrapper.getBlockSize() > mFileWrapper.getFile().length()){//块大小大于剩余的大小则上传剩余的
			return mFileWrapper.getFile().length() - mFileWrapper.getStartPos();
		}
		
		return mFileWrapper.getBlockSize();
	}
}
