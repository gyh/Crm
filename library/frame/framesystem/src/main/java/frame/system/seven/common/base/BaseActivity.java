package frame.system.seven.common.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.baidu.mobstat.StatService;

import frame.system.seven.db.DBAdapter;

/**
 * Created by guoyuehua on 14-5-30.
 */
public class BaseActivity extends ActionBarActivity {
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

    public void onResume() {
        super.onResume();

        /**
         * 页面起始（每个Activity中都需要添加，如果有继承的父Activity中已经添加了该调用，那么子Activity中务必不能添加）
         * 不能与StatService.onPageStart一级onPageEnd函数交叉使用
         */
        StatService.onResume(this);
    }

    public void onPause() {
        super.onPause();

        /**
         * 页面结束（每个Activity中都需要添加，如果有继承的父Activity中已经添加了该调用，那么子Activity中务必不能添加）
         * 不能与StatService.onPageStart一级onPageEnd函数交叉使用
         */
        StatService.onPause(this);
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
