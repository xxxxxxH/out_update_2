package com.xxxxxxh.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.lijunhuayc.downloader.downloader.DownloadProgressListener;
import com.lijunhuayc.downloader.downloader.DownloaderConfig;
import com.tencent.mmkv.MMKV;
import com.xxxxxxh.http.RequestService;
import com.xxxxxxh.http.RetrofitUtils;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Copyright (C) 2021,2021/9/7, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class UpdateManager {
    private Dialog dialog = null;
    private ProgressBar progressBar;
    private static UpdateManager instance = null;

    private UpdateManager() {
    }

    public static UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }

    public void update(String data, ResponseListener listener) {
        Retrofit retrofit = RetrofitUtils.getInstance().retrofit();
        retrofit.create(RequestService.class).getResult(data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                listener.onResponse(response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("xxxxxxH","onFailure");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Dialog permissionDlg(Context context, Activity activity, String imgUrl, String content) {
        Dialog dialog = null;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_1, null);
        dialog = new AlertDialog.Builder(context).setView(view).create();
        ImageView imageView = view.findViewById(R.id.img);
        Glide.with(context).load(imgUrl).into(imageView);
        TextView tv = view.findViewById(R.id.desc);
        tv.setText(content);
        Dialog finalDialog = dialog;
        view.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowThirdInstall(context, activity);
                finalDialog.dismiss();
            }
        });
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void allowThirdInstall(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT > 24) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                Uri uri = Uri.parse("package:" + context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(intent, 1);
            }
        }
    }

    public Dialog updateDlg(Context context, String text, String path) {
        Dialog dialog = null;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_3, null);
        dialog = new AlertDialog.Builder(context).setView(view).create();
        TextView textView = view.findViewById(R.id.update);
        textView.setText(text);
        Dialog finalDialog = dialog;
        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(context, path);
                finalDialog.dismiss();
            }
        });
        return dialog;
    }

    public Dialog downloadDlg(Context context) {
        Dialog dialog = null;
        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_2, null);
        dialog = new AlertDialog.Builder(context).setView(v).create();
        progressBar = v.findViewById(R.id.progress_bar);
        return dialog;
    }

    private void download(Context context, String path) {
        new DownloaderConfig()
                .setThreadNum(1)
                .setDownloadUrl(path)
                .setSaveDir(Environment.getExternalStorageDirectory())
                .setDownloadListener(new DownloadProgressListener() {
                    @Override
                    public void onDownloadTotalSize(int totalSize) {

                    }

                    @Override
                    public void updateDownloadProgress(int size, float percent, float speed) {
                        if (dialog == null) {
                            dialog = downloadDlg(context);
                        }
                        dialog.show();
                        progressBar.setProgress((int) percent);
                    }

                    @Override
                    public void onDownloadSuccess(String apkPath) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        installApk(context);
                    }

                    @Override
                    public void onDownloadFailed() {

                    }

                    @Override
                    public void onPauseDownload() {

                    }

                    @Override
                    public void onStopDownload() {

                    }
                }).buildWolf(context).startDownload();
    }

    private void installApk(Context context) {
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "a.apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > 24) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    public BroadcastReceiver addReceiver(Context context) {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                    String data = intent.getDataString();
                    if (!TextUtils.isEmpty(data)) {
                        if (data.contains(context.getPackageName())) {
                            MMKV.defaultMMKV().encode("state", true);
                        }
                    }
                }
            }
        };
        return receiver;
    }
}
