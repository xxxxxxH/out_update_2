package com.xxxxxxh.update;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.example.weeboos.permissionlib.PermissionRequest;
import com.facebook.applinks.AppLinkData;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String appLink;
    protected String installReferrer;
    int msgCount = 0;
    private final String[] permission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.i("xxxxxxH", "msgCount = " + msgCount);
                Log.i("xxxxxxH", "applink = " + appLink);
                Log.i("xxxxxxH", "installReferrer = " + installReferrer);
                msgCount++;
                if (msgCount == 10) {
                    startMainActivity();
                } else {
                    if (!TextUtils.isEmpty(appLink) && !TextUtils.isEmpty(installReferrer)) {
                        startMainActivity();
                    } else {
                        Message msg1 = new Message();
                        msg1.what = 1;
                        sendMessageDelayed(msg1, 1000);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        MMKV.initialize(this);
        requestPermission();
    }

    private void requestPermission() {
        PermissionRequest.getInstance().build(this)
                .requestPermission(new PermissionRequest.PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString("facebook"))) {
                            getFacebookInfo();
                        } else {
                            appLink = MMKV.defaultMMKV().decodeString("facebook");
                        }

                        if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString("google"))) {
                            getGoogleInfo();
                        } else {
                            installReferrer = MMKV.defaultMMKV().decodeString("google");
                        }

                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessageDelayed(msg, 1000);
                    }

                    @Override
                    public void permissionDenied(ArrayList<String> permissions) {

                    }

                    @Override
                    public void permissionNeverAsk(ArrayList<String> permissions) {

                    }
                }, permission);
    }

    private void getFacebookInfo() {
        AppLinkData.fetchDeferredAppLinkData(this, new AppLinkData.CompletionHandler() {
            @Override
            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                if (appLinkData != null){
                    appLink = appLinkData.getTargetUri().toString();
                }

                if (TextUtils.isEmpty(appLink)) {
                    appLink = "Applink is empty";
                } else {
                    MMKV.defaultMMKV().encode("facebook", appLink);
                }
            }
        });
    }

    private void getGoogleInfo() {
        InstallReferrerClient client = InstallReferrerClient.newBuilder(this).build();
        client.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                    try {
                        installReferrer = client.getInstallReferrer().getInstallReferrer();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    installReferrer = "Referrer is empty";
                }

                if (!TextUtils.equals(installReferrer, "Referrer is empty")) {
                    MMKV.defaultMMKV().encode("google", installReferrer);
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                installReferrer = "Referrer is empty";
            }
        });
    }

    protected  abstract int getLayout();

    protected abstract void startMainActivity();
}
