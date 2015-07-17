/**
 * Title: DownloadRequestCallable.java
 * Description: 
 * Copyright: Copyright (c) 2013 luoxudong.com
 * Company: 个人
 * Author: 罗旭东
 * Date: 2015年7月17日 下午2:27:30
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.callable;

/** 
 * ClassName: DownloadRequestCallable
 * Description:下载文件文件回调
 * Create by: 
 * Date: 2015年7月17日 下午2:27:30
 */
public abstract class DownloadRequestCallable extends SimpleRequestCallable {
	/**
	 * 开始下载
	 */
	public void onStartTransfer(){};
	
	/**
	 * 下载中
	 * @param totalLength
	 * @param transferedLength
	 */
	public void onTransfering(long totalLength, long transferedLength){};
	
	
}
