/**
 * Title: RequestParams.java
 * Description: 
 * Copyright: Copyright (c) 2013-2015 luoxudong.com
 * Company: 个人
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2015年7月13日 下午5:15:28
 * Version: 1.0
 */
package com.luoxudong.app.asynchttp.asynchttp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/** 
 * ClassName: RequestParams
 * Description:Http请求参数
 * Create by: 罗旭东
 * Date: 2015年7月13日 下午5:15:28
 */
public class RequestParams {
	protected AsyncHttpResponseHandler responseHandler = null;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;
    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, ArrayList<String>> urlParamsWithArray;
    protected ConcurrentHashMap<String, String> formParams;
    protected ConcurrentHashMap<String, ArrayList<String>> formParamsWithArray;

    /**
     * Constructs a new empty <code>RequestParams</code> instance.
     */
    public RequestParams() {
        init();
    }

    /**
     * Constructs a new RequestParams instance containing the key/value
     * string params from the specified map.
     * @param source the source key/value string map to add.
     */
    public RequestParams(Map<String, String> source) {
        init();

        for(Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Constructs a new RequestParams instance and populate it with a single
     * initial key/value string param.
     * @param key the key name for the intial param.
     * @param value the value string for the initial param.
     */
    public RequestParams(String key, String value) {
        init();

        put(key, value);
    }

    /**
     * Constructs a new RequestParams instance and populate it with multiple
     * initial key/value string param.
     * @param keysAndValues a sequence of keys and values. Objects are
     * automatically converted to Strings (including the value {@code null}).
     * @throws IllegalArgumentException if the number of arguments isn't even.
     */
    public RequestParams(Object... keysAndValues) {
      init();
      int len = keysAndValues.length;
      if (len % 2 != 0)
        throw new IllegalArgumentException("Supplied arguments must be even");
      for (int i = 0; i < len; i += 2) {
        String key = String.valueOf(keysAndValues[i]);
        String val = String.valueOf(keysAndValues[i + 1]);
        put(key, val);
      }
    }

    /**
     * Adds a key/value string pair to the request.
     * @param key the key name for the new param.
     * @param value the value string for the new param.
     */
    public void put(String key, String value){
        if(key != null && value != null) {
            urlParams.put(key, value);
        }
    }
    
    /**
     * 添加form表单参数
     * @param key 参数key
     * @param value 参数值
     */
    public void putFormParam(String key, String value){
    	if (key != null && value != null)
    	{
    		formParams.put(key, value);
    	}
    }

    /**
     * Adds a file to the request.
     * @param key the key name for the new param.
     * @param file the file to add.
     */
    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    /**
     * Adds param with more than one value.
     * @param key the key name for the new param.
     * @param values is the ArrayList with values for the param.
     */
    public void put(String key, ArrayList<String> values)  {
        if(key != null && values != null) {
            urlParamsWithArray.put(key, values);
        }
    }
    
    /**
     * 增加form表单有多个值得参数
     * @param key 属性
     * @param values 值
     */
    public void putFormParam(String key, ArrayList<String> values)  {
        if(key != null && values != null) {
            formParamsWithArray.put(key, values);
        }
    }
    
    /**
     * Adds an input stream to the request.
     * @param key the key name for the new param.
     * @param stream the input stream to add.
     */
    public void put(String key, InputStream stream) {
        put(key, stream, null);
    }

    /**
     * Adds an input stream to the request.
     * @param key the key name for the new param.
     * @param stream the input stream to add.
     * @param fileName the name of the file.
     */
    public void put(String key, InputStream stream, String fileName) {
        put(key, stream, fileName, null);
    }

    /**
     * Adds an input stream to the request.
     * @param key the key name for the new param.
     * @param stream the input stream to add.
     * @param fileName the name of the file.
     * @param contentType the content type of the file, eg. application/json
     */
    public void put(String key, InputStream stream, String fileName, String contentType) {
        if(key != null && stream != null) {
            fileParams.put(key, new FileWrapper(stream, fileName, contentType));
        }
    }

    /**
     * Removes a parameter from the request.
     * @param key the key name for the parameter to remove.
     */
    public void remove(String key){
        urlParams.remove(key);
        fileParams.remove(key);
        urlParamsWithArray.remove(key);
        formParams.remove(key);
        formParamsWithArray.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            ArrayList<String> values = entry.getValue();
            for (int i = 0; i < values.size(); i++) {
                if (i != 0)
                    result.append("&");
                result.append(entry.getKey());
                result.append("=");
                result.append(values.get(i));
            }
        }
        
        for(ConcurrentHashMap.Entry<String, String> entry : formParams.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        
        for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : formParamsWithArray.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            ArrayList<String> values = entry.getValue();
            for (int i = 0; i < values.size(); i++) {
                if (i != 0)
                    result.append("&");
                result.append(entry.getKey());
                result.append("=");
                result.append(values.get(i));
            }
        }

        return result.toString();
    }

   /**
     * Returns an HttpEntity containing all request parameters
     */
    public HttpEntity getEntity() {
        HttpEntity entity = null;

        if(!fileParams.isEmpty()) {//普通文件上传
            SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();
            //MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            // Add string params
            for(ConcurrentHashMap.Entry<String, String> entry : formParams.entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            // Add dupe params
            for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : formParamsWithArray.entrySet()) {
                ArrayList<String> values = entry.getValue();
                for (String value : values) {
                    multipartEntity.addPart(entry.getKey(), value);
                }
            }

            // Add file params
            int currentIndex = 0;
            int lastIndex = fileParams.entrySet().size() - 1;
            for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
                FileWrapper file = entry.getValue();
                if(file.inputStream != null) {
                    boolean isLast = currentIndex == lastIndex;
                    if(file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, file.contentType, isLast);
                    } else {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }

            entity = multipartEntity;
        }
        else
        {
        	try {
				entity = new UrlEncodedFormEntity(getFormParamsList(), AsyncHttpConst.HTTP_ENCODING);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return entity;
    }

    private void init(){
        urlParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
        urlParamsWithArray = new ConcurrentHashMap<String, ArrayList<String>>();
        formParams = new ConcurrentHashMap<String, String>();
        formParamsWithArray = new ConcurrentHashMap<String, ArrayList<String>>();
    }

    /**
     * 获取url参数
     * @return
     */
    protected List<BasicNameValuePair> getUrlParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (String value : values) {
                lparams.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }

        return lparams;
    }
    
    /**
     * 获取form表单参数
     * @return
     */
    protected List<BasicNameValuePair> getFormParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for(ConcurrentHashMap.Entry<String, String> entry : formParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : formParamsWithArray.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (String value : values) {
                lparams.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }

        return lparams;
    }

    protected String getParamString() {
        return URLEncodedUtils.format(getUrlParamsList(), AsyncHttpConst.HTTP_ENCODING);
    }

    private static class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName, String contentType) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public String getFileName() {
            if(fileName != null) {
                return fileName;
            } else {
                return "nofilename";
            }
        }
    }
	public AsyncHttpResponseHandler getResponseHandler() {
		return responseHandler;
	}

	public void setResponseHandler(AsyncHttpResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
	}
}
