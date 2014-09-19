package com.gyh.crm.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.DBAdapter;
import com.gyh.crm.app.common.FileService;
import com.gyh.crm.app.common.Utils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private final static String FILENAME = "crmuser.txt";

    private DBAdapter db = new DBAdapter(this);
    private ListView listView;
    private List<Base> baseList = new ArrayList<Base>();
    private DBListAdapter dbListAdapter ;
    private FileService fileService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.open();
        listView=(ListView)findViewById(R.id.listview);
        dbListAdapter=new DBListAdapter(this);
        listView.setAdapter(dbListAdapter);
        fileService= new FileService(FILENAME);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("userbase",baseList.get(i));
                intent.setClass(MainActivity.this,UserRecordActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteUser(baseList.get(i).getPhonenumber());
                return true;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent().setClass(MainActivity.this,AddUserActivity.class));
            return true;
        }else if(id==R.id.action_search){
            startActivity(new Intent().setClass(MainActivity.this,SearchActivity.class));
            return true;
        }else if(id==R.id.action_export){
            if(fileService.ishasFile()){
                Utils.alertYesOrNo(MainActivity.this,"提示","文件已存在，是否覆盖？","覆盖","点错了",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fileService.saveToSDCard(getUserDBtoString());
                        Toast.makeText(MainActivity.this,"导出成功",Toast.LENGTH_SHORT).show();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
            }else {
                fileService.saveToSDCard(getUserDBtoString());
                Toast.makeText(MainActivity.this,"导出成功",Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(id==R.id.action_import){
            setStringToUser(fileService.getFromSDCard());
        }else if(id==R.id.action_temp){
            startActivity(new Intent().setClass(MainActivity.this,TempImportOrExportActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListDate();
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
        dbListAdapter.notifyDataSetChanged();
    }

    /**
     * 删除用户
     * */
    private  void deleteUser(final String phonenumber){
        Utils.alertYesOrNo(this,"删除","是否删除此用户以及相关记录","是的","点错了",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.deleteUser(phonenumber);
                getListDate();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }


    /**
     * 将获取的数据解析成实体数据
     * */
    private void setStringToUser(String readstr){
        List<Base> tempList = new ArrayList<Base>();
//        String[] userlist = readstr.split("#");
//        for(int i=0;i<userlist.length;i++){
//            Base base = new Base();
//            base.setUsername(userlist[0]);
//            base.setPhonenumber(userlist[1]);
//            base.setUserrecord(userlist[2]);
//            base.setUserlevel(userlist[3]);
//            base.setUserev(userlist[4]);
//            base.setUsertime(userlist[5]);
//            tempList.add(base);
//        }
//
//        for(int i=0;i<tempList.size();i++){
//            for()
//        }
        if(!"".equals(readstr)&& readstr!=null){
            String [] userlist= readstr.split("$");
            for(int i=0;i<userlist.length;i++){
                String[] userinfo=userlist[i].split("#");
                String[] userinfos=userinfo[0].split("_");
                Base base = new Base();
                base.setUsertime(userinfos[0]);
                base.setUsername(userinfos[1]);
                base.setPhonenumber(userinfos[2]);
                base.setUserlevel(userinfos[3]);
                base.setUserev(userinfos[4]);
                base.setUserrecord(userinfos[5]);
                Toast.makeText(this,base.getUsername(),Toast.LENGTH_LONG).show();
                String[] userinforecord=userinfo[1].split("@");
            }
        }
    }

    /**
     * 将数据转换成字符串
     * $ -- 隔开每个客户信息
     * # -- 隔开客户信息和记录
     * @ -- 隔开每条记录
     * _ -- 隔开每个字段
     * */
    private String getUserDBtoString(){
        String userstr="";
        Cursor cursor =db.getUserList();
        while (cursor.moveToNext()){
            Base base = new Base();
            base.setUsertime(cursor.getString(0));
            base.setUsername(cursor.getString(1));
            base.setPhonenumber(cursor.getString(2));
            base.setUserlevel(cursor.getString(3));
            base.setUserev(cursor.getString(4));
            base.setUserrecord(cursor.getString(5));
            userstr+=base.getUsertime()+"_"
                    +base.getUsername()+"_"
                    +base.getPhonenumber()+"_"
                    +base.getUserlevel()+"_"
                    +base.getUserev()+"_"
                    +base.getUserrecord()+"#";
            Cursor cursor1 = db.getRecordList(base.getPhonenumber());
            while (cursor1.moveToNext()){
                Base base1 = new Base();
                base1.setUsertime(cursor1.getString(0));
                base1.setPhonenumber(cursor1.getString(1));
                base1.setUserrecord(cursor1.getString(2));
                userstr+=base1.getUsertime()+"_"
                        +base1.getPhonenumber()+"_"
                        +base1.getUserrecord()+"@";
            }
            cursor1.close();
            userstr+="$";
        }
        cursor.close();
        return userstr;
    }

    class DBListAdapter extends BaseAdapter{
        private LayoutInflater mInflater;
        public DBListAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return baseList.size();
        }

        @Override
        public Object getItem(int i) {
            return baseList.get(i);
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
                view = mInflater.inflate(R.layout.item_userlist, null);
                holder.username=(TextView)view.findViewById(R.id.username);
                holder.usertime=(TextView)view.findViewById(R.id.usertime);
                holder.userphone=(TextView)view.findViewById(R.id.userphone);
                holder.usernum=(TextView)view.findViewById(R.id.usernum);
                view.setTag(holder);
            }else {
                holder = (ViewHolder)view.getTag();
            }
            holder.username.setText(baseList.get(i).getUsername());
            holder.usertime.setText(baseList.get(i).getUsertime());
            holder.userphone.setText(baseList.get(i).getPhonenumber());
            holder.usernum.setText(baseList.get(i).getNum());
            return view;
        }
    }

    public class ViewHolder{
        public TextView username;
        public TextView usertime;
        public TextView userphone;
        public TextView usernum;
    }

}
