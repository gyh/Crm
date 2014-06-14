package com.gyh.crm.app;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;

/**
 * Created by GYH on 2014/6/8.
 */
public class UserRecordActivity extends BaseActivity{

    private Base base;
    private TextView username;
    private TextView usertime;
    private TextView userphone;
    private TextView userrecord;
    private RatingBar ratingBarlevel;
    private RatingBar ratingBarev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userrecord);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        base=(Base)getIntent().getSerializableExtra("userbase");
        initView();

    }
    /**
     * 初始化布局
     * **/
    private void initView(){
        username=(TextView)findViewById(R.id.username);
        usertime=(TextView)findViewById(R.id.usertime);
        userphone=(TextView)findViewById(R.id.userphone);
        userrecord=(TextView)findViewById(R.id.userrecord);
        ratingBarlevel=(RatingBar)findViewById(R.id.ratingbarlevel);
        ratingBarev=(RatingBar)findViewById(R.id.ratingbarev);
        username.setText(base.getUsername());
        userphone.setText(base.getPhonenumber());
        usertime.setText(base.getUsertime());
        userrecord.setText(base.getUserrecord());
        ratingBarev.setRating(Float.valueOf(base.getUserev()));
        ratingBarlevel.setRating(Float.valueOf(base.getUserlevel()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDate();
    }

    /**
     * 初始化数据
     * */
    private void initDate(){

    }
}
