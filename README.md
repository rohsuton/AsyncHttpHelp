# AsyncHttpHelp #

AsyncHttpHelp是一个android平台下基于httpclient开发的HTTP网络请求工具。

## 优点 ##
1. 功能齐全，提供常用的http网络访问接口。
2. 轻量级，无任何第三方库依赖，库大小为90K左右。
3. 定制化，自定义json解析库，支持请求参数，返回内容预处理。
4. 易用性，简单易用，只需几行代码即可完成请求，可随意设置cookie、http头部等信息

## 功能 ##
1. 普通get请求
2. 普通post请求
3. Form表单提交数据
4. 二进制数据传输
5. json格式内容传输（json字符串自动转java对象，java对象自动转json字符串）
6. 普通文件上传/下载
7. 断点上传/下载
8. 分块上传文件
9. session保持
10. 自定义cookie、http头部信息等
11. 取消请求
12. 自定义json解析器
13. 请求内容，返回内容预处理
14. 设置请求结果是否在UI线程执行
15. 更多。。。

## 测试示例 ##
![效果](http://img.blog.csdn.net/20160114121246548)

## 用法示例 ##

> **GET请求**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://www.luoxudong.com/")
    		.setCallable(new SimpleRequestCallable() {
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//请求失败
    			}
    			
    			@Override
    			public void onSuccess(String responseInfo) {
    				//请求成功
    			}
    		})
    		.build().get();


> **GET请求（返回结果为JSON，自动转换成JAVA对象）**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
    		.setResponseClass(Response.class)//返回的json对象类型，自动转换改类的对象
    		.setJsonResponseInterceptor(new JsonResponseInterceptor<Response>() {//返回结果拦截器，方便自定义json解析器以及对返回结果作简单预处理。
    			@Override
    			public Response convertJsonToObj(String responseStr, Class<Response> mResponseClass) {
    				return new Gson().fromJson(responseStr, mResponseClass);
    			}
    
    			@Override
    			public boolean checkResponse(Response response) {//可以根据返回的结果判定该请求是否成功，如果返回true，则在callable中调用onSuccess回调方法，为false时调用onFailed回调方法
    				if (response.getServerResult().getResultCode() == 0){
    					return true;
    				}
					setErrorCode(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode());
    				setErrorMsg(response.getServerResult().getResultMessage());
    				return false;
    			}
    			
    		})
    		.setCallable(new JsonRequestCallable<Response>() {//回调
    
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//请求失败
    			}
    			
    			@Override
    			public void onSuccess(Response responseInfo) {
    				//请求成功
    			}
    		})
    		.build().get();

> **POST请求**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
    		.setStrBody("body内容")//post内容
    		.setCallable(new SimpleRequestCallable() {
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//请求失败
    			}

    			@Override
    			public void onSuccess(String responseInfo) {
    				//请求成功
    			}
    		})
    		.build().post();

> **POST请求（模拟Form表单）**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
    		.addFormData("a", "1")//设置form表单数据，也可以调用setFormDatas方法
    		.addFormData("b", "luoxudong")
    		.setCallable(new SimpleRequestCallable() {
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//请求失败
    			}
    			
    			@Override
    			public void onSuccess(String responseInfo) {
    				//请求成功
    			}
    		})
    		.build().post();
 

> **POST请求（内容为二进制数据）**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
    		.setBinaryBody(new byte[]{1,2,3,4,5})
    		.setCallable(new BinaryRequestCallable() {
    			
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//请求失败
    			}
    			
    			@Override
    			public void onSuccess(byte[] buffer) {
    				//请求成功
    			}
    		})
    		.build().post();


> **POST请求（JSON字符串自动转换对象）**

    List<String> list = new ArrayList<String>();
    		list.add("item1");
    		list.add("item2");
    		Request req = new Request();
    		req.setKey1("value1");
    		req.setKey2(1);
    		req.setKey3(true);
    		req.setKey4(list);
    		
    		AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://www.bchun.com/fund/service.do?func=getProvinces")
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
    				setErrorCode(AsyncHttpExceptionCode.defaultExceptionCode.getErrorCode());
    				setErrorMsg(response.getServerResult().getResultMessage());
    				return false;
    			}
    			
    		})
    		.setCallable(new JsonRequestCallable<Response>() {
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//请求失败
    			}
    			
    			@Override
    			public void onSuccess(Response responseInfo) {
    				//请求成功
    			}
    		})
    		.build().post();

> **普通下载**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://shouji.360tpcdn.com/151125/1259d7ddba8a048c2b9e778c9b5a9d04/com.bchun.fundqa_5.apk")
    		.setDownloadFileDir("/sdcard")//文件本地保存路径
    		.setDownloadfileName("fund.apk")//文件名称
    		.setCallable(new DownloadRequestCallable() {
    			
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//下载失败
    			}
    			
    			@Override
    			public void onTransfering(long totalLength, long transferedLength) {
    				//下载进度
    			}
    			
    			@Override
    			public void onSuccess(String responseInfo) {
    				//下载完成
    			}
    		})
    		.build().download();

> **断点下载**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://shouji.360tpcdn.com/151125/1259d7ddba8a048c2b9e778c9b5a9d04/com.bchun.fundqa_5.apk")
    		.setDownloadFileDir("/sdcard")//文件本地保存路径
    		.setDownloadfileName("fund.apk.temp")//文件名称
    		.setFileStartPos(1000)//设置续下载开始位置
    		.setCallable(new DownloadRequestCallable() {
    			
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//下载失败
    			}
    			
    			@Override
    			public void onTransfering(long totalLength, long transferedLength) {
    				//下载进度
    			}
    			
    			@Override
    			public void onSuccess(String responseInfo) {
    				//下载完成
    			}
    		})
    		.build().download();

> **普通上传**

    AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://192.168.100.62:8080/MyHost/fund/upload.do")
    		.addUploadFile("file", new File("/sdcard/Fund.apk"))//添加文件，也可以调用setFileWrappers方法，同时添加多个文件
    		.addUploadFile("file1", new File("/sdcard/wifi_config.log"))
    		.addFormData("md5", "aadfsdf")//添加form参数
    		.setCallable(new UploadRequestCallable() {
    			
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//上传失败
    			}
    			
    			@Override
    			public void onTransfering(String name, long totalLength, long transferedLength) {
    				//上传进度
    			}
    			
    			@Override
    			public void onTransferSuc(String name) {
    				//文件name上传完成
    			}
    			
    			@Override
    			public void onSuccess(String responseInfo) {
    				//全部上传成功！
    			}
    			
    			@Override
    			public void onCancel() {
    				//上传取消
    			}
    		})
    		.build().upload();


> **断点上传**

    FileWrapper fileWrapper = new FileWrapper();
    		fileWrapper.setFile(new File("/sdcard/Fund.apk"));
    		fileWrapper.setBlockSize(100000);//上传100000字节数据，默认为0，上传至文件末尾
    		fileWrapper.setStartPos(1000);//从1000字节开始上传
    		AsyncHttpRequest request = new AsyncHttpUtil.Builder()
    		.url("http://192.168.100.62:8080/MyHost/fund/upload.do")
    		.addFileWrapper("file", fileWrapper)//自定义上传文件，支持断点续传，支持上传指定数据大小
    		.addFormData("md5", "aadfsdf")//添加form参数
    		.setCallable(new UploadRequestCallable() {
    			
    			@Override
    			public void onFailed(int errorCode, String errorMsg) {
    				//上传失败
    			}
    			
    			@Override
    			public void onTransfering(String name, long totalLength, long transferedLength) {
    				//上传进度
    			}
    			
    			@Override
    			public void onTransferSuc(String name) {
    				//文件name上传完成
    			}
    			
    			@Override
    			public void onSuccess(String responseInfo) {
    				//上传成功！
    			}
    			
    			@Override
    			public void onCancel() {
    				//上传取消
    			}
    		})
    		.build().upload();

> **中断请求**

    request.cancel();


> **打开/关闭日志输出**

    AsyncHttpLog.enableLog();

    AsyncHttpLog.disableLog();

**详细使用方法请查看源码中附带的demo**

**如有疑问可联系作者：hi@luoxudong.com或者rohsuton@gmail.com**