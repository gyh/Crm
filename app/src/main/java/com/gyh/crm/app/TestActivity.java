package com.gyh.crm.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import frame.system.seven.common.base.BaseActivity;

/**
 * Created by GYH on 2015/1/30.
 */
public class TestActivity extends BaseActivity{

    private View helloword;
    private YoYo.YoYoString rope;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        helloword = findViewById(R.id.hello_world);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rope = YoYo.with(Techniques.Wobble).duration(1200).playOn(helloword);// after start,just click mTarget view, rope is not init
            }
        }, 2000);
    }
}
