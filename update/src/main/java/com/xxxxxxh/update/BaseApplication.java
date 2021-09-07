package com.xxxxxxh.update;

import android.app.Application;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public abstract class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

   protected abstract String getAppId();

    protected abstract String getAppName();

    protected abstract String getUrl();

    protected abstract String getAesPassword();

    protected abstract String getAesHex();

    protected abstract String getToken();

}
