package com.gyh.crm.app.common;

import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.gyh.crm.app.listener.DBServiceListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/9/20.
 */
public class DBService {

    private DBAdapter db;
    private Handler handler;

    public DBService(DBAdapter db,Handler handler){
        this.db = db;
        this.handler=handler;
    }

    public void getUserList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Base> bases = new ArrayList<Base>();
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
                    bases.add(base);
                }
                cursor.close();
                Message message = new Message();
                message.getData().putSerializable(Constant.IntentValueType.BASETYPE,(Serializable)bases);
                handler.handleMessage(message);
                handler.sendEmptyMessage(1);
            }
        }).start();
    }
}
