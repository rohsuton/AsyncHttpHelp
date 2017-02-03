# AsyncHttpHelp #

由于Android 6.0系统不在自带HttpClient，AsyncHttpHelp也跟着升级了，AsyncHttpHelp 3.0开始基于OkHttp封装，sdk已经集成OkHttp 3.4.1源码。

## 优点 ##
1. 功能齐全，提供常用的http网络访问接口。
2. 轻量级，无任何第三方库依赖，库大小为90K左右。
3. 定制化，自定义json解析库，支持请求参数，返回内容预处理。
4. 易用性，简单易用，只需几行代码即可完成请求，可随意设置cookie、http头部等信息。

## 功能 ##
1. 普通get请求
2. 普通post请求
3. Form表单提交数据
4. byte数组格式数据传输
5. json格式内容传输（json字符串自动转java对象，java对象自动转json字符串，支持自定义json解析库）
6. 普通文件上传/下载
7. 断点上传/下载
8. 自定义cookie、http头部信息等
9. 自定义https证书
10. 支持取消请求
11. 请求内容，返回内容预处理
12. 设置请求结果是否在UI线程执行
13. 更多。。。

## 使用说明 ##

> Get请求
    
    long id = AsyncHttpUtil.get()
    .url(url)
    .addHeaderParams(heads)
    .addCookie("sign", String.valueOf(System.currentTimeMillis()))
    .userAgent(userAgent)
    .tag("tag")
    .mainThread(true)
    .build().request(new StringRequestCallable() {

    @Override
    public void onFailed(int errorCode, String errorMsg) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求失败" + errorMsg, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onSuccess(String responseInfo) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求成功" + responseInfo, Toast.LENGTH_SHORT).show();;
    }
    });

> Post请求

    long id = AsyncHttpUtil.post()
    .url(url)
    .addHeaderParams(heads)
    .addCookie("sign", String.valueOf(System.currentTimeMillis()))
    .userAgent(userAgent)
    .body("body内容")
    .tag("tag")
    .mainThread(true)
    .build().request(new StringRequestCallable() {
    
    @Override
    public void onFailed(int errorCode, String errorMsg) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求失败" + errorMsg, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onSuccess(String responseInfo) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求成功" + responseInfo, Toast.LENGTH_SHORT).show();;
    }
    });

> Post表单

    long id = AsyncHttpUtil.postForm()
    .url(url)
    .addHeaderParams(heads)
    .addCookie("sign", String.valueOf(System.currentTimeMillis()))
    .userAgent(userAgent)
    .addFormParam("key1", "value1")
    .addFormParam("key2", "value2")
    .tag("tag")
    .mainThread(true)
    .build().request(new StringRequestCallable() {

    @Override
    public void onFailed(int errorCode, String errorMsg) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求失败" + errorMsg, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onSuccess(String responseInfo) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求成功" + responseInfo, Toast.LENGTH_SHORT).show();;
    }
    });

> Post字节数组

    long id = AsyncHttpUtil.postBytes()
    .url(url)
    .addHeaderParams(heads)
    .addCookie("sign", String.valueOf(System.currentTimeMillis()))
    .userAgent(userAgent)
    .buffer(new byte[]{1,2,3,4,5})
    .tag("tag")
    .mainThread(true)
    .build().request(new RequestCallable() {

    @Override
    public void onFailed(int errorCode, String errorMsg) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求失败" + errorMsg, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onSuccess(byte[] buffer) {
    Toast.makeText(GetRequestActivity.this, getId() + "请求成功" + buffer, Toast.LENGTH_SHORT).show();;
    }
    });


正在整理中。。。

**如有疑问可联系作者：hi@luoxudong.com或者rohsuton@gmail.com**