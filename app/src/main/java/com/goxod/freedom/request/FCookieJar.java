package com.goxod.freedom.request;

import com.goxod.freedom.okhttp.cookie.store.CookieStore;
import com.goxod.freedom.okhttp.cookie.store.HasCookieStore;
import com.goxod.freedom.okhttp.utils.Exceptions;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by Levey on 2016/4/3.
 */
public class FCookieJar implements CookieJar, HasCookieStore
{
    private CookieStore cookieStore;

    public FCookieJar(CookieStore cookieStore)
    {
        if (cookieStore == null) Exceptions.illegalArgument("cookieStore can not be null.");
        this.cookieStore = cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        cookieStore.add(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url)
    {
        return cookieStore.get(url);
    }

    @Override
    public CookieStore getCookieStore()
    {
        return cookieStore;
    }
}
