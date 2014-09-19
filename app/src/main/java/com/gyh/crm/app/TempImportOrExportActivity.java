package com.gyh.crm.app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.DBAdapter;
import com.gyh.crm.app.common.FileService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/9/18.
 */
public class TempImportOrExportActivity extends BaseActivity {

    private final static String FILENAME = "strcustomerlist.txt";
    private List<Base> baseList = new ArrayList<Base>();
    private DBAdapter db = new DBAdapter(this);
    private FileService fileService;
    private Button importuser;
    private Button importinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempimportorexport);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fileService= new FileService(FILENAME);
        db.open();
        getListDate();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    /**
     * 获取数据
     * */
    private void getListDate(){
        baseList.clear();
        Cursor cursor =db.getUserList();
        while (cursor.moveToNext()){
            Base base = new Base();
            base.setUsertime(cursor.getString(0));
            base.setUsername(cursor.getString(1));
            base.setPhonenumber(cursor.getString(2));
            base.setUserlevel(cursor.getString(3));
            base.setUserev(cursor.getString(4));
            base.setUserrecord(cursor.getString(5));
            Cursor cursor1 = db.getRecordList(base.getPhonenumber());
            base.setNum(cursor1.getCount()+"");
            cursor1.close();
            baseList.add(base);
        }
        cursor.close();
    }

    private void initView(){
        importuser = (Button) findViewById(R.id.btnimportuser);
        importinfo = (Button) findViewById(R.id.btnimportinfo);
        importuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFileStrtoList();
            }
        });
    }

    private void setFileStrtoList(){
        String readstr=fileService.getFromSDCard();
    }
}
