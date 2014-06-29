package com.gyh.crm.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.DBAdapter;
import com.gyh.crm.app.common.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guoyuehua on 14-5-30.
 */
public class AddUserActivity extends BaseActivity{

    String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    Pattern p = Pattern.compile(regEx);

    private TextView userdate;
    private TextView usertime;
    private EditText username;
    private EditText userphone;
    private EditText userrecord;
    private RatingBar ratingBarlevel;
    private RatingBar ratingBarev;
    private DBAdapter db = new DBAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adduser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        db.open();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    /**
     * 初始化布局
     * */
    private void initView(){
        userdate=(TextView)findViewById(R.id.userdate);
        usertime=(TextView)findViewById(R.id.usertime);
        username=(EditText)findViewById(R.id.username);
        userphone=(EditText)findViewById(R.id.userphone);
        userrecord=(EditText)findViewById(R.id.userrecord);
        ratingBarlevel=(RatingBar)findViewById(R.id.ratingbarlevel);
        ratingBarev=(RatingBar)findViewById(R.id.ratingbarev);
        usertime.setText(Utils.getTime());
        userdate.setText(Utils.getDate());
    }
    /**
     * 保存数据,usertime,username,userphone,userlevel,userev,userrecord
     * */
    private void save(){
        if(checked()){
            boolean saveok=db.insertUser(userdate.getText().toString()+" "+usertime.getText().toString()
                    ,username.getText().toString()
                    ,userphone.getText().toString()
                    ,ratingBarlevel.getRating()+""
                    ,ratingBarev.getRating()+""
                    ,userrecord.getText().toString());
            if(saveok){
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this,"手机号已经存在",Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 检查数据是否正确
     * */
    private boolean checked(){

        boolean isok=true;
        if(("").equals(username.getText().toString())){
            isok=false;
            Toast.makeText(this,"客户姓名不能为空！",Toast.LENGTH_SHORT).show();
        }else if(("").equals(userphone.getText().toString())){
            isok=false;
            Toast.makeText(this,"客户手机号不能为空！",Toast.LENGTH_SHORT).show();
        }else if(("").equals(userrecord.getText().toString())){
            isok=false;
            Toast.makeText(this,"客户记录不能为空！",Toast.LENGTH_SHORT).show();
        }else if(ratingBarev.getRating()==0.0){
            isok=false;
            Toast.makeText(this,"客户评论不能为空！",Toast.LENGTH_SHORT).show();
        }else if(ratingBarlevel.getRating()==0.0){
            isok=false;
            Toast.makeText(this,"客户等级不能为空！",Toast.LENGTH_SHORT).show();
        }else if(!Utils.isPhoneNumberValid(userphone.getText().toString())){
            isok=false;
            Toast.makeText(this,"客户手机号输入错误！",Toast.LENGTH_SHORT).show();
        }else {
            Matcher usernamem = p.matcher(username.getText().toString());
            Matcher userrecordm = p.matcher(userrecord.getText().toString());
            if(usernamem.find()){
                isok=false;
                Toast.makeText(this,"不能包含特殊字符！",Toast.LENGTH_SHORT).show();
            }else if(userrecordm.find()){
                isok=false;
                Toast.makeText(this,"不能包含特殊字符！",Toast.LENGTH_SHORT).show();
            }
        }


        return isok;
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adduser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_phone){
            Utils.alertPhoneType(AddUserActivity.this,userphone);
            return true;
        }else if(id==R.id.action_time){
            Utils.alertTime(AddUserActivity.this,usertime,userdate);
            return true;
        }else if(id==R.id.action_save){
            save();
            return true;
        }else if(id==android.R.id.home){
            Utils.alertYesOrNo(AddUserActivity.this,"提示信息","是否放弃保存","是的","不是",new DialogInterface.OnClickListener() {
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
