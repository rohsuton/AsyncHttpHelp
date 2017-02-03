/**
 * <pre>
 * Title: MyTrustManager.java
 * Description: 信任证书管理类
 * Copyright: Copyright (c) 2014-2016 gjfax.com
 * Company: 广金所
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:03
 * Version: 1.0
 * </pre>
 */
package com.luoxudong.app.asynchttp.https;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * <pre>
 * Class: MyTrustManager
 * Description: 信任证书管理类
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:03
 * Version: 1.0
 * </pre>
 */
public class MyTrustManager implements X509TrustManager {
    private X509TrustManager mDefaultTrustManager = null;
    private X509TrustManager mLocalTrustManager = null;

    public MyTrustManager(TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        mDefaultTrustManager = chooseTrustManager(trustManagerFactory.getTrustManagers());
        mLocalTrustManager = chooseTrustManager(trustManagers);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            mDefaultTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            mLocalTrustManager.checkServerTrusted(chain, authType);
        }
    }


    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }
}
