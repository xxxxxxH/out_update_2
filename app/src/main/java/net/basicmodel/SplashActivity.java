package net.basicmodel;

import android.content.Intent;

import com.xxxxxxh.update.BaseActivity;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void startMainActivity() {
        startActivity(new Intent(this,MainActivity.class));
    }
}
