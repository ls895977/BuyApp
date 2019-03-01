package com.vedeng.httpclient.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**********************************************************
 * @文件名称：CookiesManager.java
 * @文件作者：聂中泽
 * @创建时间：2016/12/27 11:14
 * @文件描述：Cookie管理器
 * @修改历史：2016/12/27 创建初始版本
 **********************************************************/
public class CookiesManager implements CookieJar {
    private volatile static CookiesManager cookiesManager;
    private final PersistentCookieStore cookieStore = new PersistentCookieStore();

    private CookiesManager() {

    }

    public static CookiesManager getInstance() {
        if (cookiesManager == null) {
            synchronized (CookiesManager.class) {
                if (cookiesManager == null) {
                    cookiesManager = new CookiesManager();
                }
            }
        }
        return cookiesManager;
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(httpUrl, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl);
        return cookies;
    }

    public void clearCookies() {
        cookieStore.removeAll();
    }

    public List<Cookie> getCookies(HttpUrl httpUrl) {
        return cookieStore.get(httpUrl);
    }

    public List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }
}
