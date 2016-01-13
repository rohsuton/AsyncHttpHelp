/**
 * Title: UploadRequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月14日 下午12:01:02
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;

import com.luoxudong.app.asynchttp.mime.HttpMultipartMode;
import com.luoxudong.app.asynchttp.mime.MultipartEntity;
import com.luoxudong.app.asynchttp.mime.content.StringBody;
import com.luoxudong.app.asynchttp.model.FileWrapper;

/** 
 * <pre>
 * ClassName: UploadRequestParams
 * Description:上传请求参数，支持同时上传多个文件
 * Create by: 罗旭东
 * Date: 2015年7月14日 下午12:01:02
 * </pre>
 */
public class UploadRequestParams extends FormRequestParams {
	/** 上传文件参数 */
	private Map<String, FileWrapper> mFileParams = null;
	
	/** 上传文件回调方法 */
	private UploadResponseHandler mUploadResponseHandler = null;
	
	public UploadRequestParams() {
        init();
    }
	
	@Override
	protected void init() {
		super.init();
		mFileParams = new ConcurrentHashMap<String, FileWrapper>();
	}
	
	/** 移除文件 */
	public void removeFileParam(String key) {
		mFileParams.remove(key);
	}
	
	/**
     * 添加文件参数
     * @param source
     */
    public void putFileParam(Map<String, FileWrapper> source) {
        for(Map.Entry<String, FileWrapper> entry : source.entrySet()) {
        	putFileParam(entry.getKey(), entry.getValue());
        }
    }
    
	/**
     * 添加文件参数
     * @param key 参数key
     * @param value 参数值
     */
    public void putFileParam(String key, FileWrapper value){
		if (key != null && value != null) {
			mFileParams.put(key, value);
		}
    }
    
    @Override
    public HttpEntity getEntity() {
    	MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		
    	if (mFormParams != null){
    		for(HashMap.Entry<String, String> entry : mFormParams.entrySet()) {
        		try {
        			StringBody stringBody = new StringBody(entry.getValue(), Charset.forName(AsyncHttpConst.HTTP_ENCODING));
    				entity.addPart(entry.getKey(), stringBody);
    			} catch (UnsupportedEncodingException e) {
    			}
            }
    	}
    	
    	if (mFileParams != null){
    		for(HashMap.Entry<String, FileWrapper> entry : mFileParams.entrySet()) {
    			BlockContentBody blockStreamBody = new BlockContentBody(getUploadResponseHandler(), entry.getKey(), entry.getValue());
    			entity.addPart(entry.getKey(), blockStreamBody);
            }
    	}
		
		return entity;
    }

	public UploadResponseHandler getUploadResponseHandler() {
		return mUploadResponseHandler;
	}

	public void setUploadResponseHandler(UploadResponseHandler uploadResponseHandler) {
		mUploadResponseHandler = uploadResponseHandler;
	}
    
}
