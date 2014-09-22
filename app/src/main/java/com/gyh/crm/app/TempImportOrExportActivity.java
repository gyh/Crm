package com.gyh.crm.app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.FileService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/9/18.
 */
public class TempImportOrExportActivity extends BaseActivity {

    private final static String FILENAME = "strcustomerlist.txt";
    private final static String FILENAME2 = "strcustomerfollowlis.txt";
    private List<Base> baseList = new ArrayList<Base>();
    private FileService fileUserService;
    private FileService fileUserInfoService;
    private Button importuser;
    private Button importinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempimportorexport);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fileUserService = new FileService(FILENAME);
        fileUserInfoService  = new FileService(FILENAME2);
        getUserListDate();
        initView();
    }

    /**
     * 获取用户数据信息列表
     * */
    private void getUserListDate(){
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

    /**
     * 初始化视图
     * */
    private void initView(){
        importuser = (Button) findViewById(R.id.btnimportuser);
        importinfo = (Button) findViewById(R.id.btnimportinfo);
        importuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFileStrtoList();
            }
        });
        importinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFileInfotoList();
            }
        });
    }

    /**
     * 解析从文件中获取的数据转换成用户数据列表
     * */
    private void setFileStrtoList(){
        if(fileUserService.ishasFile()){
            String readstr= fileUserService.getFromSDCard();
            List<Base> oldList = new ArrayList<Base>();
            String[] userlist = readstr.split("#");
            for(int i=0;i<userlist.length;i++){
                Base base = new Base();
                String[] userInfo = userlist[i].split("_");
                base.setUsername(userInfo[0]);
                base.setPhonenumber(userInfo[1]);
                base.setUserrecord(userInfo[2]);
                base.setUserlevel(userInfo[3]);
                base.setUserev(userInfo[4]);
                base.setUsertime(userInfo[5]);
                oldList.add(base);
            }
            List<Base> tempList = new ArrayList<Base>();
            for(int i=0;i<oldList.size();i++){
                boolean ishas = false;
                for(int j=0;j<baseList.size();j++){
                    if(oldList.get(i).getPhonenumber().equals(baseList.get(j).getPhonenumber())){
                        ishas = true;
                        break;
                    }
                }
                if(!ishas){
                    tempList.add(oldList.get(i));
                }
            }
            for(int i =0;i<tempList.size();i++){
                saveUserToDB(tempList.get(i));
            }
        }else {
            Toast.makeText(this,"不存在这个文件",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 保存用户到数据库
     * */
    private void saveUserToDB(Base tempitem){
        String[] tempstrlist =  tempitem.getUsertime().split("-");
        String strtime = tempstrlist[3];
        String strdate = tempstrlist[0]+"年"+tempstrlist[1]+"月"+tempstrlist[2]+"日";
        boolean saveok=db.insertUser(strdate+" "+strtime
                ,tempitem.getUsername()
                ,tempitem.getPhonenumber()
                ,tempitem.getUserlevel()+""
                ,tempitem.getUserev()+""
                ,tempitem.getUserrecord());
    }



    /**
     * 将获取的记录信息放到想要的
     * */
    private void setFileInfotoList(){
        if(fileUserInfoService.ishasFile()){
            String readstr= fileUserInfoService.getFromSDCard();
            List<Base> oldList = new ArrayList<Base>();
            String[] userinfolist = readstr.split("#");
            for(int i=0;i<userinfolist.length;i++){
                Base base = new Base();
                String[] userInfo = userinfolist[i].split("_");
                base.setUsername(userInfo[0]);
                base.setPhonenumber(userInfo[1]);
                base.setUserrecord(userInfo[2]);
                base.setUsertime(userInfo[3]);
                oldList.add(base);
            }

            for(int i =0;i<oldList.size();i++){
                saveUserInfoToDB(oldList.get(i));
            }
        }else {
            Toast.makeText(this,"不存在这个文件",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 添加用户记录信息到数据库
     * */
    private void saveUserInfoToDB(Base tempitem){
        String[] tempstrlist =  tempitem.getUsertime().split("-");
        String strtime = tempstrlist[3];
        String strdate = tempstrlist[0]+"年"+tempstrlist[1]+"月"+tempstrlist[2]+"日";
        db.insertRecord(strdate+" "+strtime
                ,tempitem.getPhonenumber()
                ,tempitem.getUserrecord());
    }
}
