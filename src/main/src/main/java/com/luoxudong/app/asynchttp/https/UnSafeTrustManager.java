/**
 * <pre>
 * Title: UnSafeTrustManager.java
 * Description:
 * Copyright: Copyright (c) 2014-2016 gjfax.com
 * Company: 广金所
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:05
 * Version: 1.0
 * </pre>
 */
package com.luoxudong.app.asynchttp.https;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * <pre>
 * Class: UnSafeTrustManager
 * Description:
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:05
 * Version: 1.0
 * </pre>
 */
public class UnSafeTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[]{};
    }
}
