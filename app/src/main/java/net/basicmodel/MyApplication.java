package net.basicmodel;

import android.os.Environment;

import com.xxxxxxh.update.BaseApplication;

import java.io.File;
import java.util.UUID;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class MyApplication extends BaseApplication {
    @Override
    protected String getAppId() {
        return "361";
    }

    @Override
    protected String getAppName() {
        return "net.basicmodel";
    }

    @Override
    protected String getUrl() {
        return "http://smallfun.xyz/worldweather361/";
    }

    @Override
    protected String getAesPassword() {
        return "VPWaTtwYVPS1PeQP";
    }

    @Override
    protected String getAesHex() {
        return "jQ4GbGckQ9G7ACZv";
    }

    @Override
    protected String getToken() {
        String token = "";
        if (!new File(Environment.getExternalStorageDirectory().toString() + File.separator + "a.testupdate.txt").exists()) {
            token = UUID.randomUUID().toString();
            FileUtils.saveFile(token);
        } else {
            token =
                    FileUtils.readrFile(Environment.getExternalStorageDirectory().toString() + File.separator + "a.testupdate.txt");
        }
        return token;
    }
}
