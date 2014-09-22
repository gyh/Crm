package com.gyh.crm.app.common;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.gyh.crm.app.MainActivity;

/**
 * Created by guoyuehua on 14-5-30.
 */
public class BaseActivity extends ActionBarActivity{
    protected DBAdapter db = new DBAdapter(this);
    //带内容的转圈
    private ProgressDialog msgProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db.open();
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    /**
     * 带内容的弹出提示框
     * @param dialogMsgId 弹出框转圈提示
     */
    public void showMsgProgress(int dialogMsgId){
        String dialogMsg = getResources().getString(dialogMsgId);
        if (this.msgProgressDialog == null) {
            this.msgProgressDialog = new ProgressDialog(this);
            this.msgProgressDialog.setCancelable(true);
        }
        this.msgProgressDialog.setMessage(dialogMsg);
        this.msgProgressDialog.show();
    }

    public void dismissProgressDialog() {
        try{
            if(this.msgProgressDialog != null)
                this.msgProgressDialog.cancel();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
