package net.basicmodel;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;
import com.xxxxxxh.update.ResponseListener;
import com.xxxxxxh.update.UpdateManager;

import net.entity.RequestBean;
import net.entity.ResultEntity;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class MainActivity extends AppCompatActivity implements ResponseListener {

    UpdateManager manager = null;
    ResultEntity entity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (checkState()) {
            RequestBean bean = getRequestBean();
            manager = UpdateManager.getInstance();
            manager.update(AesEncryptUtil.encrypt(new Gson().toJson(bean)), this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("action_download");
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(manager.addReceiver(this), intentFilter);
//        }
    }

    private boolean checkState() {
        return MMKV.defaultMMKV().decodeBool("state");
    }

    private RequestBean getRequestBean() {
        RequestBean request = new RequestBean();
        request.setAppId(new MyApplication().getAppId());
        request.setAppName(new MyApplication().getAppName());
        request.setApplink(MMKV.defaultMMKV().decodeString("facebook", "AppLink is empty"));
        request.setRef(MMKV.defaultMMKV().decodeString("google", "Referrer is empty"));
        request.setToken(new MyApplication().getToken());
        request.setIstatus(MMKV.defaultMMKV().decodeBool("isFirst", true));
        return request;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResponse(Response<ResponseBody> response) {
        try {
            String result = AesEncryptUtil.decrypt(response.body().string());
            if (!TextUtils.isEmpty(result)) {
                Type typeToken = new TypeToken<ResultEntity>() {
                }.getType();
                entity = new Gson().fromJson(result, typeToken);
                if (Build.VERSION.SDK_INT > 24) {
                    if (!this.getPackageManager().canRequestPackageInstalls()) {
                        manager.permissionDlg(this, this, entity.getUkey(), entity.getPkey()).show();
                    } else {
                        manager.updateDlg(this, entity.getIkey(), entity.getPath()).show();
                    }
                } else {
                    manager.updateDlg(this, entity.getIkey(), entity.getPath()).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            manager.permissionDlg(this, this, entity.getUkey(), entity.getPkey()).show();
        } else {
            manager.updateDlg(this, entity.getIkey(), entity.getPath()).show();
        }
    }
}
