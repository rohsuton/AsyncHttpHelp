AsyncHttpHelp
=========================
    只需调用一行代码即可完成http网络传输请求。该源码封装了http网络传输相关的各种接口，包括get请求、
    post请求、文件上传（支持多文件断点上传），文件下载（支持断点下载），另外可以轻松自定义请求参数。
# Usage
    工具类AsyncHttpUtil常用接口如下：
    
    /**
	 * 设置json返回参数解析规则
	 * @param responseAdapter
	 */
	public static void setResponseAdapter(BaseJsonHttpResponseAdapter responseAdapter)
	
	/**
	 * 设置ssl请求
	 * @param sslSocketFactory
	 */
	public static void setSSLSocketFactory(SSLSocketFactory sslSocketFactory)
	
	/**
	 * 发送简单的http get请求
	 * @param url 请求url地址
	 * @param callable 返回结果回调
	 */
	public static void simpleGetHttpRequest(String url, SimpleRequestCallable callable)
	
	/**
	 * 发送简单的http get请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param callable 返回结果回调
	 */
	public static void simpleGetHttpRequest(String url, Map<String, String> urlParams, SimpleRequestCallable callable)
	
	/**
	 * 发送简单的http get请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义链接超时时间
	 * @param callable 返回结果回调
	 */
	public static void simpleGetHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, SimpleRequestCallable callable)
	
	/**
	 * 发送http get请求，返回结果为json对象
	 * @param url 请求url地址
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void jsonGetHttpRequest(String url, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送http get请求，返回结果为json对象
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义链接超时时间
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void jsonGetHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送简单http post请求
	 * @param url 请求url地址
	 * @param callable 返回结果回调
	 */
	public static void simplePostHttpRequest(String url, SimpleRequestCallable callable)
	
	/**
	 * 发送简单http post请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义链接超时时间
	 * @param contentType 请求内容类型
	 * @param requestBody 消息体内容
	 * @param callable 返回结果回调
	 */
	public static void simplePostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, String requestBody, SimpleRequestCallable callable)
	
	/**
	 * 发送post请求，请求内容为json对象，返回结果为json对象
	 * @param url 请求url地址
	 * @param requestInfo 请求内容的json对象
	 * @param responseClass 返回结果对象类型
	 * @param callable 返回结果回调
	 */
	public static <T extends Serializable, M extends BaseResponse<M>> void jsonPostHttpRequest(String url, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送post请求，请求内容为json对象，返回结果为json对象
	 * @param url 请求url地址
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param requestInfo 请求内容的json对象
	 * @param responseClass 返回结果对象类型
	 * @param callable 返回结果回调
	 */
	public static <T extends Serializable, M extends BaseResponse<M>> void jsonPostHttpRequest(String url, Map<String, String> headerParams, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送post请求，请求内容为json对象，返回结果为json对象
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param contentType 请求内容类型
	 * @param requestInfo 请求内容的json对象
	 * @param responseClass 返回结果对象类型
	 * @param callable 返回结果回调
	 */
	public static <T extends Serializable, M extends BaseResponse<M>> void jsonPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, T requestInfo, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送form键值参数请求
	 * @param url 请求url地址
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formPostHttpRequest(String url, Map<String, String> formDatas, SimpleRequestCallable callable)
	
	/**
	 * 发送form键值参数请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param contentType 请求内容类型
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, Map<String, String> formDatas, SimpleRequestCallable callable)
	
	/**
	 * 发送form键值参数请求,返回结果为json对象
	 * @param url 请求url地址
	 * @param formDatas form键值参数
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void formPostHttpRequest(String url, Map<String, String> formDatas, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送form键值参数请求,返回结果为json对象
	 * @param url 请求url地址
	 * @param headerParams url中带的参数，会进行url编码
	 * @param formDatas form键值参数
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void formPostHttpRequest(String url, Map<String, String> headerParams, Map<String, String> formDatas, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送form键值参数请求,返回结果为json对象
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param contentType 请求内容类型
	 * @param formDatas form键值参数
	 * @param responseClass 返回结果类型
	 * @param callable 返回结果回调
	 */
	public static <M extends BaseResponse<M>> void formPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String contentType, Map<String, String> formDatas, Class<M> responseClass, JsonRequestCallable<M> callable)
	
	/**
	 * 发送模拟form-data表单请求
	 * @param url 请求url地址
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formDataPostHttpRequest(String url, Map<String, String> formDatas, SimpleRequestCallable callable)
	
	/**
	 * 发送模拟form-data表单请求
	 * @param url 请求url地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param formDatas form键值参数
	 * @param callable 返回结果回调
	 */
	public static void formDataPostHttpRequest(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, Map<String, String> formDatas, SimpleRequestCallable callable)
	
	/**
	 * 普通下载
	 * @param url 下载url
	 * @param fileDir  本地保存目录
	 * @param fileName 本地保存文件名
	 * @param callable 下载回调方法
	 */
	public static void download(String url, String fileDir, String fileName, DownloadRequestCallable callable)
	
	/**
	 * 断点下载
	 * @param url 下载url
	 * @param fileDir 本地保存目录
	 * @param fileName 本地保存文件名
	 * @param startPos 断点下载开始位置
	 * @param callable 下载回调方法
	 */
	public static void download(String url, String fileDir, String fileName, long startPos, DownloadRequestCallable callable)
	
	/**
	 * 下载文件，支持断点续传
	 * @param url 下载url
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param fileDir 本地保存目录
	 * @param fileName 本地保存文件名
	 * @param startPos 下载开始位置
	 * @param endPos 下载结束位置
	 * @param callable 下载回调方法
	 */
	public static void download(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, String fileDir, String fileName, long startPos, long endPos, DownloadRequestCallable callable)
	
	/**
	 * 单文件普通上传
	 * @param url 上传地址
	 * @param formDatas 表单参数
	 * @param name 文件属性名
	 * @param file 要上产的文件
	 * @param callable 上传回调
	 */
	public static void upload(String url, Map<String, String> formDatas, String name, File file, UploadRequestCallable callable)
	
	/**
	 * 单文件断点上传
	 * @param url 上传地址
	 * @param formDatas 表单参数
	 * @param name 文件属性名
	 * @param fileWrapper 要上传的文件信息
	 * @param callable 上传回调
	 */
	public static void upload(String url, Map<String, String> formDatas, String name, FileWrapper fileWrapper, UploadRequestCallable callable)
	
	/**
	 * 文件上传，支持多文件断点续传
	 * @param url 上传地址
	 * @param urlParams url中带的参数，会进行url编码
	 * @param headerParams 自定义http头部信息，设置cookie可通过该属性设置
	 * @param timeout 自定义连接超时时间
	 * @param formDatas 表单参数
	 * @param fileWrappers 上传的文件列表
	 * @param callable 上传回调
	 */
	public static void upload(String url, Map<String, String> urlParams, Map<String, String> headerParams, int timeout, Map<String, String> formDatas, Map<String, FileWrapper> fileWrappers, UploadRequestCallable callable)
