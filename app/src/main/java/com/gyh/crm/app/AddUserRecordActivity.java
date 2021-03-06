package com.gyh.crm.app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import frame.system.seven.common.base.BaseActivity;
import frame.system.seven.common.utils.Constant;
import frame.system.seven.common.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GYH on 2014/6/21.
 */
public class AddUserRecordActivity extends BaseActivity{
    Pattern p = Pattern.compile(Constant.regEx);
    private TextView userdate;
    private TextView usertime;
    private EditText userrecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrecord);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }
    /**
     * 初始化布局
     * */
    @SuppressLint("WrongViewCast")
    private void initView(){
        userdate=(TextView)findViewById(R.id.userdate);
        usertime=(TextView)findViewById(R.id.usertime);
        userrecord=(EditText)findViewById(R.id.userrecord);
        usertime.setText(Utils.getTime());
        userdate.setText(Utils.getDate());
    }
    /**
     * 保存数据,String usertime, String phonenumber, String userrecord
     * */
    private void save(){
        if(checked()){
            db.insertRecord(userdate.getText().toString()+" "+usertime.getText().toString()
                    ,getIntent().getStringExtra("phonenumber")
                    ,userrecord.getText().toString());
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    /**
     * 检查数据是否正确
     * */
    private boolean checked(){
        boolean isok=true;
        if(("").equals(userrecord.getText().toString())){
            isok=false;
            Toast.makeText(this,"客户记录不能为空！",Toast.LENGTH_SHORT).show();
        }else {
            Matcher userrecordm = p.matcher(userrecord.getText().toString());
            if(userrecordm.find()){
                isok=false;
                Toast.makeText(this,"不能包含特殊字符！",Toast.LENGTH_SHORT).show();
            }
        }
        return isok;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adduserrecord, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_save){
            save();
            return true;
        }else if(id==R.id.action_time){
            Utils.alertTime(AddUserRecordActivity.this,usertime,userdate);
            return true;
        }else if(id==android.R.id.home){
            Utils.alertYesOrNo(AddUserRecordActivity.this,"提示信息","是否放弃保存","是的","不是",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
