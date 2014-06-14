package com.gyh.crm.app;

import android.content.Context;
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

import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.DBAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    private DBAdapter db = new DBAdapter(this);
    private ListView listView;
    private List<Base> baseList = new ArrayList<Base>();
    private DBListAdapter dbListAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.open();
        listView=(ListView)findViewById(R.id.listview);
        dbListAdapter=new DBListAdapter(this);
        listView.setAdapter(dbListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("userbase",baseList.get(i));
                intent.setClass(MainActivity.this,UserRecordActivity.class);
                startActivity(intent);
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
