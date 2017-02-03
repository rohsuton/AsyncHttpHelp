/**
 * Title: UploadRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013 luoxudong.com
 * Company: 个人
 * Author: 罗旭东
 * Date: 2015年7月17日 下午6:24:58
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.callable;

/** 
 * ClassName: UploadRequestCallable
 * Description:TODO(这里用一句话描述这个类的作用)
 * Create by: 
 * Date: 2015年7月17日 下午6:24:58
 */
public abstract class UploadRequestCallable extends StringRequestCallable {
	/**
	 * 开始上传
	 */
	public void onStartTransfer(){};
	
	/**
	 * 上传中
	 * @param fileName 当前传输的文件名
	 * @param totalLength 当前上传文件总大小
	 * @param transferedLength 当前文件已上传大小
	 */
	public void onTransfering(String fileName, long totalLength, long transferedLength){};
	
	/**
	 * 上传成功
	 * @param fileName
	 */
	public void onTransferSuc(String fileName){};

}
