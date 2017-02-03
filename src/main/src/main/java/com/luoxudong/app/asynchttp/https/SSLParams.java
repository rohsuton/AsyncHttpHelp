/**
 * <pre>
 * Title: SSLParams.java
 * Description: 设置ssl链接参数类
 * Copyright: Copyright (c) 2014-2016 gjfax.com
 * Company: 广金所
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:04
 * Version: 1.0
 * </pre>
 */
package com.luoxudong.app.asynchttp.https;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

/**
 * <pre>
 * Class: SSLParams
 * Description: 设置ssl链接参数类
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:04
 * Version: 1.0
 * </pre>
 */
public class SSLParams {
    private SSLSocketFactory mSSLSocketFactory = null;

    private X509TrustManager mTrustManager = null;

    public SSLSocketFactory getSSLSocketFactory() {
        return mSSLSocketFactory;
    }

    public void setSSLSocketFactory(SSLSocketFactory sSLSocketFactory) {
        mSSLSocketFactory = sSLSocketFactory;
    }

    public X509TrustManager getTrustManager() {
        return mTrustManager;
    }

    public void setTrustManager(X509TrustManager trustManager) {
        mTrustManager = trustManager;
    }
}
