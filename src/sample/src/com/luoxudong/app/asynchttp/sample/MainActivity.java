package com.luoxudong.app.asynchttp.sample;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luoxudong.app.asynchttp.AsyncHttpRequest;
import com.luoxudong.app.asynchttp.AsyncHttpUtil;
import com.luoxudong.app.asynchttp.callable.BinaryRequestCallable;
import com.luoxudong.app.asynchttp.callable.DownloadRequestCallable;
import com.luoxudong.app.asynchttp.callable.JsonRequestCallable;
import com.luoxudong.app.asynchttp.callable.SimpleRequestCallable;
import com.luoxudong.app.asynchttp.callable.UploadRequestCallable;
import com.luoxudong.app.asynchttp.interceptor.JsonRequestInterceptor;
import com.luoxudong.app.asynchttp.interceptor.JsonResponseInterceptor;
import com.luoxudong.app.asynchttp.sample.model.Request;
import com.luoxudong.app.asynchttp.sample.model.Response;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;
import com.luoxudong.app.asynchttp.utils.ByteUtil;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		AsyncHttpLog.enableLog();
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btn_1:
			//getRequest();
			//getRequestForJsonObj();
			//postRequest();
			//postFormRequest();
			//postBinRequest();
			//postRequestForJsonObj();
			//simpleDownload();
			//brokenDownload();
			//upload();
			brokenUpload();
			break;
		default:
			break;
		}
	}
	
	private void getRequest(){
		new AsyncHttpUtil.Builder()
		//.mainThread(true)//返回结果是否在主线程中执行，默认为true
		.url("http://www.luoxudong.com/")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.setCallable(new SimpleRequestCallable() {
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				Toast.makeText(MainActivity.this, "请求失败:" + errorMsg, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onSuccess(String responseInfo) {
				Toast.makeText(MainActivity.this, "请求成功：" + responseInfo, Toast.LENGTH_LONG).show();
			}
		})
		.build().get();
	}
	
	private void getRequestForJsonObj(){
		new AsyncHttpUtil.Builder()
		//.mainThread(true)//返回结果是否在主线程中执行，默认为true
		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.setResponseClass(Response.class)//返回的json对象
		.setJsonResponseInterceptor(new JsonResponseInterceptor<Response>() {//返回结果拦截器，方便自定义json解析器以及对返回结果作简单预处理。

			@Override
			public Response convertJsonToObj(String responseStr, Class<Response> mResponseClass) {
				return new Gson().fromJson(responseStr, mResponseClass);
			}

			@Override
			public boolean checkResponse(Response response) {
				if (response.getServerResult().getResultCode() == 0){
					return true;
				}
				setErrorMsg(response.getServerResult().getResultMessage());
				return false;
			}
			
		})
		.setCallable(new JsonRequestCallable<Response>() {//回调

			@Override
			public void onFailed(int errorCode, String errorMsg) {
				Toast.makeText(MainActivity.this, "请求失败:" + errorMsg, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onSuccess(Response responseInfo) {
				Toast.makeText(MainActivity.this, "请求成功：" + responseInfo.getServerResult().getResultMessage(), Toast.LENGTH_LONG).show();
			}
		})
		.build().get();
	}
	
	private void postRequest(){
		new AsyncHttpUtil.Builder()
		//.mainThread(true)//返回结果是否在主线程中执行，默认为true
		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		//.setContentType(AsyncHttpConst.HEADER_CONTENT_TYPE_TEXT)//Content-Type
		.setStrBody("body内容")//post内容
		.setCallable(new SimpleRequestCallable() {
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				Toast.makeText(MainActivity.this, "请求失败:" + errorMsg, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onSuccess(String responseInfo) {
				Toast.makeText(MainActivity.this, "请求成功：" + responseInfo, Toast.LENGTH_LONG).show();
			}
		})
		.build().post();
	}
	
	private void postFormRequest(){
		new AsyncHttpUtil.Builder()
		//.mainThread(true)//返回结果是否在主线程中执行，默认为true
		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.addFormData("a", "1")//设置form表单数据，也可以调用setFormDatas方法
		.addFormData("b", "百纯")
		.setCallable(new SimpleRequestCallable() {
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				Toast.makeText(MainActivity.this, "请求失败:" + errorMsg, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onSuccess(String responseInfo) {
				Toast.makeText(MainActivity.this, "请求成功：" + responseInfo, Toast.LENGTH_LONG).show();
			}
		})
		.build().post();
	}
	
	private void postBinRequest(){
		new AsyncHttpUtil.Builder()
		//.mainThread(true)//返回结果是否在主线程中执行，默认为true
		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.setBinaryBody(new byte[]{1,2,3,4,5})
		.setCallable(new BinaryRequestCallable() {
			
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				Toast.makeText(MainActivity.this, "请求失败:" + errorMsg, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onSuccess(byte[] buffer) {
				Toast.makeText(MainActivity.this, "请求成功：" + ByteUtil.getHexStr(buffer), Toast.LENGTH_LONG).show();
			}
		})
		.build().post();
	}
	
	private void postRequestForJsonObj(){
		List<String> list = new ArrayList<String>();
		list.add("item1");
		list.add("item2");
		Request req = new Request();
		req.setKey1("value1");
		req.setKey2(1);
		req.setKey3(true);
		req.setKey4(list);
		
		new AsyncHttpUtil.Builder()
		//.mainThread(true)//返回结果是否在主线程中执行，默认为true
		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.setRequestObj(req)//json格式请求内容
		.setJsonRequestInterceptor(new JsonRequestInterceptor() {//请求数据拦截器，自定义json解析器
			
			@Override
			public String convertJsonToObj(Object requestObj) {
				return new Gson().toJson(requestObj);
			}
		})
		.setResponseClass(Response.class)
		.setJsonResponseInterceptor(new JsonResponseInterceptor<Response>() {//返回结果拦截器，方便自定义json解析器以及对返回结果作简单预处理。

			@Override
			public Response convertJsonToObj(String responseStr, Class<Response> mResponseClass) {
				return new Gson().fromJson(responseStr, mResponseClass);
			}

			@Override
			public boolean checkResponse(Response response) {
				if (response.getServerResult().getResultCode() == 0){
					return true;
				}
				setErrorMsg(response.getServerResult().getResultMessage());
				return false;
			}
			
		})
		.setCallable(new JsonRequestCallable<Response>() {
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				if (Looper.myLooper() == Looper.getMainLooper()) {
					Log.e(">>>", "当前在主线程中执行");
					Toast.makeText(MainActivity.this, "请求失败:" + errorMsg, Toast.LENGTH_LONG).show();
				}else{
					Log.e(">>>", "当前不在主线程中执行");
				}
			}
			
			@Override
			public void onSuccess(Response responseInfo) {
				Toast.makeText(MainActivity.this, "请求成功：" + responseInfo.getServerResult().getResultMessage(), Toast.LENGTH_LONG).show();
			}
		})
		.build().post();
	}
	
	private void simpleDownload(){
		new AsyncHttpUtil.Builder()
		.url("http://down.sandai.net/thunderspeed/ThunderSpeed1.0.33.358.exe")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.setDownloadFileDir("/sdcard")//文件本地保存路径
		.setDownloadfileName("a.exe")//文件名称
		.setCallable(new DownloadRequestCallable() {
			
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				
			}
			
			@Override
			public void onTransfering(long totalLength, long transferedLength) {
				// TODO Auto-generated method stub
				super.onTransfering(totalLength, transferedLength);
			}
			
			@Override
			public void onSuccess(String responseInfo) {
				// TODO Auto-generated method stub
				super.onSuccess(responseInfo);
			}
		})
		.build().download();
	}
	
	private void brokenDownload(){
		AsyncHttpRequest request = new AsyncHttpUtil.Builder()
		.url("http://down.sandai.net/thunderspeed/ThunderSpeed1.0.33.358.exe")
		//.setUserAgent("custom user-agent")//设置User-Agent
		//.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		//.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		//.addUrlParam("p2", "2")
		//.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		//.addHeaderParam("param2", "bbb")
		//.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		//.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.setDownloadFileDir("/sdcard")//文件本地保存路径
		.setDownloadfileName("a.exe")//文件名称
		.setFileStartPos(1000)//设置续下载开始位置
		.setCallable(new DownloadRequestCallable() {
			
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				
			}
			
			@Override
			public void onTransfering(long totalLength, long transferedLength) {
				// TODO Auto-generated method stub
				super.onTransfering(totalLength, transferedLength);
			}
			
			@Override
			public void onSuccess(String responseInfo) {
				// TODO Auto-generated method stub
				super.onSuccess(responseInfo);
			}
		})
		.build().download();
	}
	
	private void upload(){
		AsyncHttpRequest request = new AsyncHttpUtil.Builder()
		.url("http://192.168.100.62:8080/MyHost/fund/upload.do")
		.setUserAgent("custom user-agent")//设置User-Agent
		.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		.addUrlParam("p2", "2")
		.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		.addHeaderParam("param2", "bbb")
		.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.addUploadFile("file", new File("/sdcard/Fund.apk"))//添加文件，也可以调用setFileWrappers方法，同时添加多个文件
		.addUploadFile("file1", new File("/sdcard/wifi_config.log"))
		.addFormData("md5", "aadfsdf")//添加form参数
		.setCallable(new UploadRequestCallable() {
			
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				Log.i(">>>>>>>", "上传失败");
			}
			
			@Override
			public void onTransfering(String name, long totalLength, long transferedLength) {
				Log.i(">>>>>>>", "上传进度：" + name + ">>>" + totalLength + ">>>" + transferedLength);
			}
			
			@Override
			public void onTransferSuc(String name) {
				Log.i(">>>>>>>", "文件" + name + "上传完成");
			}
			
			@Override
			public void onSuccess(String responseInfo) {
				Log.i(">>>>>>>", "上传成功！");
			}
			
			@Override
			public void onCancel() {
				Log.i(">>>>>>>", "上传取消");
			}
		})
		.build().upload();
	}
	
	private void brokenUpload(){
		AsyncHttpRequest request = new AsyncHttpUtil.Builder()
		.url("http://192.168.100.62:8080/MyHost/fund/upload.do")
		.setUserAgent("custom user-agent")//设置User-Agent
		.addCookie("sid", "00fcb944b86443d580139ef7ab766655")//添加cookie
		.addUrlParam("p1", "1")//添加url参数，或者使用setUrlParams(urlParams)设置url参数
		.addUrlParam("p2", "2")
		.addHeaderParam("param1", "aaa")//设置http头部信息，或者使用setHeaderParams(headerParams)设置http头部信息
		.addHeaderParam("param2", "bbb")
		.setConnectTimeout(15 * 1000)//设置连接服务器超时时间，默认是30秒
		.setReadTimeout(15 * 1000)//设置读数据超时时间，默认是30秒
		.addUploadFile("file", new File("/sdcard/Fund.apk"))//添加文件，也可以调用setFileWrappers方法，同时添加多个文件
		.addUploadFile("file1", new File("/sdcard/wifi_config.log"))
		.addFormData("md5", "aadfsdf")//添加form参数
		.setCallable(new UploadRequestCallable() {
			
			@Override
			public void onFailed(int errorCode, String errorMsg) {
				Log.i(">>>>>>>", "上传失败");
			}
			
			@Override
			public void onTransfering(String name, long totalLength, long transferedLength) {
				Log.i(">>>>>>>", "上传进度：" + name + ">>>" + totalLength + ">>>" + transferedLength);
			}
			
			@Override
			public void onTransferSuc(String name) {
				Log.i(">>>>>>>", "文件" + name + "上传完成");
			}
			
			@Override
			public void onSuccess(String responseInfo) {
				Log.i(">>>>>>>", "上传成功！");
			}
			
			@Override
			public void onCancel() {
				Log.i(">>>>>>>", "上传取消");
			}
		})
		.build().upload();
	}
}
