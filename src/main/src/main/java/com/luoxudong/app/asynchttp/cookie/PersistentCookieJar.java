/*
 * Copyright (C) 2016 Francisco José Montiel Navarro.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luoxudong.app.asynchttp.cookie;

import com.luoxudong.app.asynchttp.cookie.cache.CookieCache;
import com.luoxudong.app.asynchttp.cookie.persistence.CookiePersistor;
import com.luoxudong.app.asynchttp.utils.AsyncHttpLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class PersistentCookieJar implements ClearableCookieJar {

    private CookieCache cache;
    private CookiePersistor persistor;

    public PersistentCookieJar(CookieCache cache, CookiePersistor persistor) {
        this.cache = cache;
        this.persistor = persistor;

        if (persistor != null) {
            this.cache.addAll(persistor.loadAll());
        }
    }

    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> cookiesToRemove = new ArrayList<>();
        if (cookies != null) {//删除重复的
            for (Cookie cookie : cookies) {
                for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
                    Cookie currentCookie = it.next();

                    if (currentCookie.matches(url) && cookie.name().equalsIgnoreCase(currentCookie.name())) {
                        cookiesToRemove.add(currentCookie);
                        it.remove();
                    }
                }
            }
        }

        cache.addAll(cookies);


        if (persistor != null) {
            persistor.removeAll(cookiesToRemove);
            persistor.saveAll(filterPersistentCookies(cookies));
        }
    }

    private static List<Cookie> filterPersistentCookies(List<Cookie> cookies) {
        List<Cookie> persistentCookies = new ArrayList<>();

        for (Cookie cookie : cookies) {
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            }
        }
        return persistentCookies;
    }

    @Override
    synchronized public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookiesToRemove = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                cookiesToRemove.add(currentCookie);
                it.remove();
            } else if (currentCookie.matches(url)) {
                validCookies.add(currentCookie);
            }
        }

        if (persistor != null) {
            persistor.removeAll(cookiesToRemove);
        }

        return validCookies;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override
    synchronized public void clearSession() {
        cache.clear();

        if (persistor != null) {
            cache.addAll(persistor.loadAll());
        }
    }

    @Override
    synchronized public void clear() {
        cache.clear();

        if (persistor != null) {
            persistor.clear();
        }
    }
}
