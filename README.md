# AsyncHttpHelp #

AsyncHttpHelp是一个android平台下基于httpclient开发的HTTP网络请求库。

## 优点 ##
1. 功能齐全，提供常用的http网络访问接口。
2. 轻量级，无任何第三方库依赖，库大小为90K左右。
3. 定制化，自定义json解析库，支持请求参数，返回内容预处理。

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
![效果](http://img.blog.csdn.net/20160114120946540?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

## 用法示例 ##

> **GET请求**

    new AsyncHttpUtil.Builder()
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

    new AsyncHttpUtil.Builder()
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

    new AsyncHttpUtil.Builder()
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



 

**详细使用方法请查看源码中附带的demo**