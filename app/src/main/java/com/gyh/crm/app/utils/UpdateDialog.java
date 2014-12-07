package com.gyh.crm.app.utils;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import com.baidu.kirin.KirinConfig;
import com.baidu.kirin.PostChoiceListener;
import com.baidu.kirin.StatUpdateAgent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GYH on 2014/11/22.
 */
public class UpdateDialog {

    private Context mContext = null;
    private String mAppName = null;
    private final PostChoiceListener mmPostChoiceListener;

    public UpdateDialog(Context context, String appName,
                        PostChoiceListener _mPostUpdateChoiceListener) {
        mContext = context;
        this.mAppName = appName;
        mmPostChoiceListener = _mPostUpdateChoiceListener;

    }

    public void doUpdate(String downloadUrl, String content) {
        showNewerVersionFoundDialog(downloadUrl, content);
    }

    private void showNewerVersionFoundDialog(final String downloadUrl,String content) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("应用:"+mAppName);
        builder.setMessage(content);

        builder.setPositiveButton("现在升级", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //将用户选择反馈给服务器
                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.put("isdown", "1");
                    jsonobject.put("downloadUrl", downloadUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mmPostChoiceListener.PostUpdateChoiceResponse(jsonobject);
                StatUpdateAgent.postUserChoice(mContext, KirinConfig.CONFIRM_UPDATE, mmPostChoiceListener);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("暂不升级", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //将用户选择反馈给服务器
                JSONObject jsonobject = new JSONObject();
                try {
                    jsonobject.put("isdown", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mmPostChoiceListener.PostUpdateChoiceResponse(jsonobject);
                StatUpdateAgent.postUserChoice(mContext, KirinConfig.LATER_UPDATE, mmPostChoiceListener);
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }
}
