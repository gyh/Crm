package com.gyh.crm.app.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Config;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
* Created by GYH on 2014/11/22.
*/
public class DownloadCompleteReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            Toast.makeText(context, "下载完成！", Toast.LENGTH_LONG).show();
            //获取安装路径
            String fileName = "";
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);//从下载服务获取下载管理器
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);//设置过滤状态：成功
            Cursor c = downloadManager.query(query);// 查询以前下载过的‘成功文件’
            if (c.moveToFirst()) {// 移动到最新下载的文件
                fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            }
            File f = new File(fileName.replace("file://", ""));// 过滤路径
            //安装应用
            Intent intent2 = new Intent();
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent2.setAction(android.content.Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.fromFile(f),"application/vnd.android.package-archive");
            context.startActivity(intent2);
        }
    }
}
