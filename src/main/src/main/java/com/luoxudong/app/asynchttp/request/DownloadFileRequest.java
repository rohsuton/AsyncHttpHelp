/**
 * <pre>
 * Title: DownloadFileRequest.java
 * Description: 下载文件请求类
 * Copyright: Copyright (c) 2014-2016 gjfax.com
 * Company: 广金所
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:12
 * Version: 1.0
 * </pre>
 */
package com.luoxudong.app.asynchttp.request;

import com.luoxudong.app.asynchttp.AsyncHttpTask;
import com.luoxudong.app.asynchttp.builder.DownloadFileBuilder;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.handler.DownloadFileResponseHandler;
import com.luoxudong.app.asynchttp.handler.ResponseHandler;

import okhttp3.Request;

/**
 * <pre>
 * Class: DownloadFileRequest
 * Description: 下载文件请求类
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:12
 * Version: 1.0
 * </pre>
 */
public class DownloadFileRequest extends AsyncHttpRequest {
    /** 断点下载起始位置 */
    private long mOffset = 0;
    /** 下载文件大小 */
    private long mLength = 0;
    /** 下载文件保存路径 */
    private String mFileDir = null;
    /** 下载文件名 */
    private String mFileName = null;

    public DownloadFileRequest(DownloadFileBuilder builder) {
        super(builder);
        mOffset = builder.getOffset();
        mLength = builder.getLength();
        mFileDir = builder.getFileDir();
        mFileName = builder.getFileName();

        //断点下载
        if (mOffset > 0 || mLength > 0) {
            String rangeValue = "bytes=" + mOffset + "-";

            if (mLength > 0) {
                rangeValue += (mOffset + mLength);
            }

            builder.getHeaderParams().put("range", rangeValue);
        }
    }

    @Override
    public AsyncHttpTask build() {
        initRequest();
        return new AsyncHttpTask(this);
    }

    @Override
    public Request buildRequest(RequestCallable callable) {
        return mBuilder.get().build();
    }

    @Override
    public ResponseHandler buildResponseHandler(RequestCallable callable) {
        return new DownloadFileResponseHandler(mFileDir, mFileName, mOffset, callable);
    }
}
