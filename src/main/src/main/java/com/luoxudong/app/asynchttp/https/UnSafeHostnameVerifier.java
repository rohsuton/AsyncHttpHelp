/**
 * <pre>
 * Title: UnSafeHostnameVerifier.java
 * Description:
 * Copyright: Copyright (c) 2014-2016 gjfax.com
 * Company: 广金所
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:04
 * Version: 1.0
 * </pre>
 */
package com.luoxudong.app.asynchttp.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * <pre>
 * Class: UnSafeHostnameVerifier
 * Description:
 * Author: 罗旭东 (hi@luoxudong.com)
 * Date: 2017/1/24 12:05
 * Version: 1.0
 * </pre>
 */
public class UnSafeHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
