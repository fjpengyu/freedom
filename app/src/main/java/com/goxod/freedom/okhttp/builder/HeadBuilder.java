package com.goxod.freedom.okhttp.builder;

import com.goxod.freedom.okhttp.OkHttpUtils;
import com.goxod.freedom.okhttp.request.OtherRequest;
import com.goxod.freedom.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers).build();
    }
}
