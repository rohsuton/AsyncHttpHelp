/**
 * <pre>
 * Title: UploadFileResponseHandler.java
 * Description:上传文件回调
 * Copyright: Copyright (c) 2014-2016 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/13 17:27
 * Version: 1.0
 * </pre>
 */
package com.luoxudong.app.asynchttp.handler;

import android.os.Message;

import com.luoxudong.app.asynchttp.AsyncHttpConst;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.callable.UploadRequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

import java.io.UnsupportedEncodingException;

/**
 * <pre>
 * Class: UploadFileResponseHandler
 * Description: 上传文件回调
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:02
 * Version: 1.0
 * </pre>
 */
public class UploadFileResponseHandler extends ResponseHandler {
    private final String TAG = UploadFileResponseHandler.class.getSimpleName();

    /** 开始传输 */
    protected static final int FILE_TRANSFER_START = 100;
    /** 传输中 */
    protected static final int FILE_TRANSFERING = 101;
    /** 单个文件上传成功 */
    protected static final int FILE_TRANSFER_SEC = 102;
    /** 传输缓存大小 */
    protected static final int BUFFER_SIZE = 200 * 1024;

    public UploadFileResponseHandler(RequestCallable callable) {
        super(callable);
    }

    /**
     * 开始下载
     */
    public void sendStartTransferMessage() {
        sendMessage(obtainMessage(FILE_TRANSFER_START, null));
    }

    /**
     * 正在传输
     */
    public void sendTransferingMessage(String fileName, long totalLength, long transferedLength) {
        sendMessage(obtainMessage(FILE_TRANSFERING, new Object[]{fileName, totalLength, transferedLength}));
    }

    public void sendTransferSucMessage(String fileName) {
        sendMessage(obtainMessage(FILE_TRANSFER_SEC, fileName));
    }

    @Override
    protected void handlerMessageCustom(Message msg) {
        switch (msg.what) {
            case FILE_TRANSFER_START:
                if (mCallable != null && mCallable instanceof UploadRequestCallable) {
                    ((UploadRequestCallable)mCallable).onStartTransfer();
                }

                break;
            case FILE_TRANSFERING:
                if (mCallable != null && mCallable instanceof UploadRequestCallable) {
                    Object[] param = (Object[])msg.obj;
                    ((UploadRequestCallable)mCallable).onTransfering((String)param[0], (long)param[1], (long)param[2]);
                }
                break;
            case FILE_TRANSFER_SEC:
                if (mCallable != null && mCallable instanceof UploadRequestCallable) {
                    ((UploadRequestCallable)mCallable).onTransferSuc((String)msg.obj);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onSuccess(byte[] buffer) {
        try {
            onSuccess(buffer == null ? null : new String(buffer, AsyncHttpConst.HTTP_ENCODING));
        } catch (UnsupportedEncodingException e) {
            if (mCallable != null) {
                mCallable.onFailed(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), "不支持该编码!");
            }
        }
    }

    protected void onSuccess(String responseBody) {
        AsyncHttpLog.i(TAG, responseBody);

        if (mCallable != null && mCallable instanceof UploadRequestCallable) {
            ((UploadRequestCallable)mCallable).onSuccess(responseBody);
        } else {
            AsyncHttpLog.w(TAG, "回调类型错误！");
        }
    }
}
