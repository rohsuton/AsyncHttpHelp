/**
 * <pre>
 * Title: DownloadFileResponseHandler.java
 * Description: 下载请求结果回调
 * Copyright: Copyright (c) 2014-2016 gjfax.com
 * Company: 广金所
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 11:58
 * Version: 1.0
 * </pre>
 */
package com.luoxudong.app.asynchttp.handler;

import android.os.Message;

import com.luoxudong.app.asynchttp.AsyncHttpConst;
import com.luoxudong.app.asynchttp.callable.DownloadRequestCallable;
import com.luoxudong.app.asynchttp.callable.RequestCallable;
import com.luoxudong.app.asynchttp.exception.AsyncHttpException;
import com.luoxudong.app.asynchttp.exception.AsyncHttpExceptionCode;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * <pre>
 * Class: DownloadFileResponseHandler
 * Description: 下载请求结果回调
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 11:58
 * Version: 1.0
 * </pre>
 */
public class DownloadFileResponseHandler extends ResponseHandler {
    private final String TAG = DownloadFileResponseHandler.class.getSimpleName();

    /** 开始传输 */
    protected static final int FILE_TRANSFER_START = 100;
    /** 传输中 */
    protected static final int FILE_TRANSFERING = 101;
    /** 传输缓存大小 */
    protected static final int BUFFER_SIZE = 200 * 1024;
    /** 断点下载起始位置 */
    private long mOffset = 0;
    /** 下载文件保存路径 */
    private String mFileDir = null;
    /** 下载文件名 */
    private String mFileName = null;
    /** 每次下载bffer大小 */
    private byte[] mBuffer = new byte[BUFFER_SIZE];

    public DownloadFileResponseHandler(String fileDir, String fileName, long offset, RequestCallable callable) {
        super(callable);
        mFileDir = fileDir;
        mFileName = fileName;
        mOffset = offset;
    }

    @Override
    protected void parseResponse(Response response) {
        boolean isChunked = "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"));

        sendStartTransferMessage();//开始下载

        if (isChunked) {//chunked编码，不支持断点下载
            normalDownload(response);
        } else {//支持断点下载
            breakpointDownload(response);
        }
    }

    @Override
    protected void onSuccess(byte[] buffer) {
        onSuccess();
    }

    public void onSuccess() {
        if (mCallable != null && mCallable instanceof DownloadRequestCallable) {
            ((DownloadRequestCallable)mCallable).onSuccess();
        }
    }

    /**
     * 开始下载
     */
    protected void sendStartTransferMessage() {
        sendMessage(obtainMessage(FILE_TRANSFER_START, null));
    }

    /**
     * 正在传输
     */
    protected void sendTransferingMessage(long totalLength, long transferedLength) {
        sendMessage(obtainMessage(FILE_TRANSFERING, new long[]{totalLength, transferedLength}));
    }

    @Override
    protected void handlerMessageCustom(Message msg) {
        switch (msg.what) {
            case FILE_TRANSFER_START:
                if (mCallable != null && mCallable instanceof DownloadRequestCallable) {
                    ((DownloadRequestCallable)mCallable).onStartTransfer();
                }

                break;
            case FILE_TRANSFERING:
                if (mCallable != null && mCallable instanceof DownloadRequestCallable) {
                    long[] param = (long[])msg.obj;
                    ((DownloadRequestCallable)mCallable).onTransfering(param[0], param[1]);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 普通下载
     * @param response
     */
    private void normalDownload(Response response) {
        File localFile = new File(mFileDir, mFileName);
        int length = 0;
        long offset = 0;

        if (!localFile.getParentFile().exists()) {//文件夹不存在则创建文件夹
            localFile.getParentFile().mkdirs();
        } else {//不支持断点下载，删除原来的文件
            localFile.delete();
        }

        InputStream is = response.body().byteStream();
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(localFile);

            long timeStamp = System.currentTimeMillis();
            while ((length = is.read(mBuffer)) != -1) {
                out.write(mBuffer, 0, length);
                offset += length;

                if ((System.currentTimeMillis() - timeStamp) >= AsyncHttpConst.TRANSFER_REFRESH_TIME_INTERVAL) {
                    AsyncHttpLog.d(TAG, "下载进度:" + offset);
                    sendTransferingMessage(offset + 1, offset);
                    timeStamp = System.currentTimeMillis();// 每一秒调用一次
                }
            }

            sendTransferingMessage(offset, offset);
            sendSuccessMessage(response.headers(), null);

        } catch (IOException e) {
            sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), e);
                }
            }
        }
    }

    /**
     * 断点下载
     * @param response
     */
    private void breakpointDownload(Response response) {
        RandomAccessFile randomAccessFile = null;
        long totalLength = response.body().contentLength();//下载文件总长度
        InputStream is = response.body().byteStream();

        File localFile = new File(mFileDir, mFileName);

        if (!localFile.getParentFile().exists()) {//文件夹不存在则创建文件夹
            localFile.getParentFile().mkdirs();
        }

        if (localFile.exists() && localFile.length() != totalLength){//文件存在但大小不一致，则删除
            localFile.delete();
        }

        try {
            boolean isNewFile = !localFile.exists();
            randomAccessFile = new RandomAccessFile(localFile, "rw");
            if (isNewFile) {//如果是新文件则指定文件大小
                randomAccessFile.setLength(totalLength);
            }

            downloading(response, randomAccessFile);
        } catch (FileNotFoundException e1) {
            sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("文件不存在!"));
            return;
        } catch (IOException e) {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("IO异常!"));
            return;
        }finally{
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * 开始下载
     * @param response
     * @param randomAccessFile
     * @throws IOException
     */
    private void downloading(Response response, RandomAccessFile randomAccessFile) throws IOException {
        long offset = 0;
        int length = 0;
        InputStream is = response.body().byteStream();
        long totalLength = response.body().contentLength();//下载文件总长度

        //支持断点下载
        if (response.code() == 206) {
            offset = mOffset;
        }

        randomAccessFile.seek(offset);

        long timeStamp = System.currentTimeMillis();
        while (offset < totalLength && (length = is.read(mBuffer)) != -1) {
            offset += length;
            randomAccessFile.write(mBuffer, 0, length);

            if ((System.currentTimeMillis() - timeStamp) >= AsyncHttpConst.TRANSFER_REFRESH_TIME_INTERVAL || offset == totalLength) {
                AsyncHttpLog.d(TAG, "下载进度:" + offset + "/" + totalLength);
                sendTransferingMessage(totalLength, offset);
                timeStamp = System.currentTimeMillis();// 每一秒调用一次
            }
        }

        if (offset == totalLength) {//下载完成
            sendTransferingMessage(totalLength, totalLength);
            sendSuccessMessage(response.headers(), null);
        } else if (offset > totalLength) {
            sendFailureMessage(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode(), new AsyncHttpException("本地文件长度超过总长度!"));
        }
    }

}
