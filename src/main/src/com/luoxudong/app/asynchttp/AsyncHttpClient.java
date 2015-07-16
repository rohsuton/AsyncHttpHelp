/**
 * Title: AsyncHttpClient.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午3:49:02
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;
import com.luoxudong.app.threadpool.manager.ThreadTaskObject;

/** 
 * ClassName: AsyncHttpClient
 * Description:创建httpclient单例对象
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午3:49:02
 */
public class AsyncHttpClient {
	private static final String TAG = AsyncHttpClient.class.getSimpleName();
	
	private static DefaultHttpClient httpClient = null;
	
	/**
	 * 
	 * @description:创建单例httpClient对象
	 * @return
	 * @return HttpClient
	 * @throws
	 */
	public static synchronized DefaultHttpClient getAsyncHttpClient()
	{
		if (httpClient == null) {
			AsyncHttpLog.i(TAG, "正在创建HttpClient对象");
			BasicHttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
			HttpConnectionParams.setSoTimeout(httpParams, AsyncHttpConst.DEFAULT_SOCKET_TIMEOUT);
			HttpConnectionParams.setConnectionTimeout(httpParams, AsyncHttpConst.DEFAULT_SOCKET_TIMEOUT);
			HttpConnectionParams.setTcpNoDelay(httpParams, true);
			HttpConnectionParams.setSocketBufferSize(httpParams, AsyncHttpConst.DEFAULT_SOCKET_BUFFER_SIZE);

			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setUserAgent(httpParams, AsyncHttpConst.sUserAgent);
			HttpProtocolParams.setContentCharset(httpParams, AsyncHttpConst.HTTP_ENCODING);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
	        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
	        
			httpClient = new DefaultHttpClient(cm, httpParams);

			// HTTP协议请求拦截器
			httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
				@Override
				public void process(HttpRequest request, HttpContext context) {
					if (!request.containsHeader(AsyncHttpConst.HEADER_ACCEPT_ENCODING)) {
						request.addHeader(AsyncHttpConst.HEADER_ACCEPT_ENCODING, AsyncHttpConst.ENCODING_GZIP);
					}
				}
			});

			//HTTP协议应答拦截器
			httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
				@Override
				public void process(HttpResponse response, HttpContext context) {
					final HttpEntity entity = response.getEntity();
					
					if (entity == null) {
						return;
					}
					
					AsyncHttpLog.i(TAG, "HTTP请求返回码[" + response.getStatusLine().getStatusCode() + "]");
					
					final Header encoding = entity.getContentEncoding();
					if (encoding != null) {
						for (HeaderElement element : encoding.getElements()) {
							if (element.getName().equalsIgnoreCase(AsyncHttpConst.ENCODING_GZIP)) {
								response.setEntity(new InflatingEntity(response
										.getEntity()));
								break;
							}
						}
					}
				}
			});

			httpClient.setHttpRequestRetryHandler(new RetryHandler(AsyncHttpConst.DEFAULT_MAX_RETRIES));
		}
		
		return httpClient;
	}
	
	/**
	 * 
	 * @description:关闭httpClient连接
	 * @return void
	 * @throws
	 */
	public static synchronized void closeAsyHttpClient()
	{
		if (httpClient != null)
		{
			AsyncHttpLog.i(TAG, "正在销毁HttpClient对象");
			new ThreadTaskObject(){
				public void run() {
					synchronized (httpClient) {
						httpClient.getConnectionManager().shutdown();
						httpClient = null;
					}
				};
			}.start();
		}
	}
	
	/**
	 * 
	 * @description:设置ssl传输
	 * @param sslSocketFactory
	 * @return void
	 * @throws
	 */
	public static void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		if (sslSocketFactory != null){
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", sslSocketFactory, 443));
		}
	}
	
	public static void setUserAgent(String userAgent){
		AsyncHttpConst.sUserAgent = userAgent;
		
		if (httpClient != null){
			HttpProtocolParams.setUserAgent(httpClient.getParams(), userAgent);
		}
	}
	
	private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
