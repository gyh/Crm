package com.gyh.crm.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.Constant;
import com.gyh.crm.app.common.Utils;

import java.util.ArrayList;
import java.util.List;

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
    private ListView listview;
    private DBListAdapter dbListAdapter;
    private List<Base> bases= new ArrayList<Base>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userrecord);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        base=(Base)getIntent().getSerializableExtra("userbase");
        initView();

    }
    /**
     * 初始化布局
     * **/
    private void initView(){
        dbListAdapter=new DBListAdapter(this);
        listview=(ListView)findViewById(R.id.listview);
        listview.setAdapter(dbListAdapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteUserRecord(bases.get(i).getPhonenumber(),"'"+bases.get(i).getUsertime()+"'");
                return false;
            }
        });

        username=(TextView)findViewById(R.id.username);
        usertime=(TextView)findViewById(R.id.usertime);
        userphone=(TextView)findViewById(R.id.userphone);
        userrecord=(TextView)findViewById(R.id.userrecord);
        ratingBarlevel=(RatingBar)findViewById(R.id.ratingbarlevel);
        ratingBarev=(RatingBar)findViewById(R.id.ratingbarev);
        setViewData();
    }

    /**
     * 设置View的数据
     * */
    private void setViewData(){
        username.setText(base.getUsername());
        userphone.setText(base.getPhonenumber());
        usertime.setText(base.getUsertime());
        userrecord.setText(base.getUserrecord());
        ratingBarev.setRating(Float.valueOf(base.getUserev()));
        ratingBarlevel.setRating(Float.valueOf(base.getUserlevel()));
    }

    @Override
    public void onResume() {
        super.onResume();
        initDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.updateuser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent();
            intent.setClass(UserRecordActivity.this, AddUserRecordActivity.class);
            intent.putExtra("phonenumber",base.getPhonenumber());
            startActivity(intent);
            return true;
        }else if(id == R.id.action_update){
            Intent intent = new Intent();
            intent.setClass(UserRecordActivity.this,AddUserActivity.class);
            intent.putExtra(Constant.ExtraKeyName.OPERATIONTYPE,Constant.ExtraKeyValue.OPERATIONTYPE_UPDATE);
            intent.putExtra(Constant.IntentValueType.BASETYPE,base);
            startActivityForResult(intent,Constant.RequestCode.USERRECORD_TO_ADDUSER);
            return true;
        }else if(id == R.id.action_call){
            Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + base.getPhonenumber()));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constant.RequestCode.USERRECORD_TO_ADDUSER){
            if(resultCode == RESULT_OK){
                base = (Base) data.getSerializableExtra(Constant.IntentValueType.BASETYPE);
                setViewData();
            }
        }
    }

    /**
     * 初始化数据
     * */
    private void initDate(){
        bases.clear();
        Cursor cursor = db.getRecordList(base.getPhonenumber());
        while (cursor.moveToNext()){
            Base base = new Base();
            base.setUsertime(cursor.getString(0));
            base.setPhonenumber(cursor.getString(1));
            base.setUserrecord(cursor.getString(2));
            bases.add(base);
        }
        dbListAdapter.notifyDataSetChanged();
    }

    /**
     * 删除用户
     * */
    private  void deleteUserRecord(final String phonenumber, final String usertime){
        Utils.alertYesOrNo(this, "删除", "是否删除此用户的这条记录", "是的", "点错了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.deleteRecord(phonenumber, usertime);
                initDate();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }
    class DBListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public DBListAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return bases.size();
        }

        @Override
        public Object getItem(int i) {
            return bases.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (view == null) {
                holder=new ViewHolder();
                view = mInflater.inflate(R.layout.item_userrecord, null);
                holder.usertime=(TextView)view.findViewById(R.id.usertime);
                holder.userrecord=(TextView)view.findViewById(R.id.userrecord);
                view.setTag(holder);
            }else {
                holder = (ViewHolder)view.getTag();
            }
            holder.usertime.setText(bases.get(i).getUsertime());
            holder.userrecord.setText(bases.get(i).getUserrecord());
            return view;
        }
    }

    public class ViewHolder{
        public TextView usertime;
        public TextView userrecord;
    }
}
