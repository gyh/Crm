package com.gyh.crm.app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import frame.system.seven.common.bean.Base;
import frame.system.seven.common.base.BaseActivity;
import frame.system.seven.common.utils.Constant;
import frame.system.seven.common.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guoyuehua on 14-5-30.
 */
public class AddUserActivity extends BaseActivity{


    //特殊字符集
    Pattern p = Pattern.compile(Constant.regEx);

    private TextView userdate;
    private TextView usertime;
    private EditText username;
    private EditText userphone;
    private EditText userrecord;
    private RatingBar ratingBarlevel;
    private RatingBar ratingBarev;
    private String OPERATIONTYPE = "";
    //动画效果实体
    private YoYo.YoYoString rope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adduser);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        OPERATIONTYPE = getIntent().getStringExtra(Constant.ExtraKeyName.OPERATIONTYPE);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    /**
     * 初始化布局
     * */
    @SuppressLint("WrongViewCast")
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
        if(OPERATIONTYPE.equals(Constant.ExtraKeyValue.OPERATIONTYPE_UPDATE)){
            Base base = (Base)getIntent().getSerializableExtra(Constant.IntentValueType.BASETYPE);
            username.setText(base.getUsername());
            userphone.setText(base.getPhonenumber());
            userphone.setEnabled(false);
            userrecord.setText(base.getUserrecord());
            String[] tempstr = base.getUsertime().split(" ");
            userdate.setText(tempstr[0]);
            usertime.setText(tempstr[1]);
            ratingBarlevel.setRating(Float.valueOf(base.getUserlevel()));
            ratingBarev.setRating(Float.valueOf(base.getUserev()));
        }
    }
    /**
     * 保存数据,usertime,username,userphone,userlevel,userev,userrecord
     * */
    private void save(){
        if(checked()){
            boolean saveok = false;
            if(OPERATIONTYPE.equals(Constant.ExtraKeyValue.OPERATIONTYPE_ADD)){
                saveok=db.insertUser(userdate.getText().toString()+" "+usertime.getText().toString()
                        ,username.getText().toString()
                        ,userphone.getText().toString()
                        ,ratingBarlevel.getRating()+""
                        ,ratingBarev.getRating()+""
                        ,userrecord.getText().toString());
            }else if (OPERATIONTYPE.equals(Constant.ExtraKeyValue.OPERATIONTYPE_UPDATE)){
                saveok=db.updateUser(userdate.getText().toString()+" "+usertime.getText().toString()
                        ,username.getText().toString()
                        ,userphone.getText().toString()
                        ,ratingBarlevel.getRating()+""
                        ,ratingBarev.getRating()+""
                        ,userrecord.getText().toString());
            }
            if(saveok){
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                Base base = new Base();
                base.setUsername(username.getText().toString());
                base.setUserlevel(ratingBarlevel.getRating()+"");
                base.setUserev(ratingBarev.getRating()+"");
                base.setUsertime(userdate.getText().toString()+" "+usertime.getText().toString());
                base.setPhonenumber(userphone.getText().toString());
                base.setUserrecord(userrecord.getText().toString());
                Intent intent = new Intent();
                intent.putExtra(Constant.IntentValueType.BASETYPE,base);
                setResult(RESULT_OK,intent);
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
            setYoYoAnim(username,"客户姓名不能为空！");
        }else if(("").equals(userphone.getText().toString())){
            isok=false;
            setYoYoAnim(userphone,"客户手机号不能为空！");
        }else if(("").equals(userrecord.getText().toString())){
            isok=false;
            setYoYoAnim(userrecord,"客户记录不能为空！");
        }else if(ratingBarev.getRating()==0.0){
            isok=false;
            setYoYoAnim(ratingBarev,"客户评论不能为空！");
        }else if(ratingBarlevel.getRating()==0.0){
            isok=false;
            setYoYoAnim(ratingBarlevel,"客户等级不能为空！");
        }else if(!Utils.isPhoneNumberValid(userphone.getText().toString())){
            isok=false;
            setYoYoAnim(userphone,"客户手机号输入错误！");
        }else {
            //查找用户和记录有没有特殊字符
            Matcher usernamem = p.matcher(username.getText().toString());
            Matcher userrecordm = p.matcher(userrecord.getText().toString());
            if(usernamem.find()){
                isok=false;
                setYoYoAnim(username,"不能包含特殊字符！");
            }else if(userrecordm.find()){
                isok=false;
                setYoYoAnim(userrecord,"不能包含特殊字符！");
            }
        }
        return isok;
    }


    /**
     * 界面效果
     * */
    private void setYoYoAnim(View view,String str){
        if(view != null){
            rope = YoYo.with(Techniques.Shake).duration(1000).playOn(view);
        }
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
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
