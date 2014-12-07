package com.gyh.crm.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
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

import com.gyh.crm.app.R;
import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GYH on 2014/6/28.
 */
public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private android.app.ActionBar actionBar;
    private ListView listView;
    private List<Base> baseList = new ArrayList<Base>();
    private DBListAdapter dbListAdapter ;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView=(ListView)findViewById(R.id.listview);
        dbListAdapter=new DBListAdapter(this);
        listView.setAdapter(dbListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("userbase",baseList.get(i));
                intent.setClass(SearchActivity.this,UserRecordActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                deleteUser(baseList.get(i).getPhonenumber());
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        searchView = new SearchView(actionBar.getThemedContext());
        searchView.setQueryHint("搜索客户");
        searchView.setOnQueryTextListener(this);
        searchView.onActionViewExpanded();
        menu.add("Search")
                .setIcon(R.drawable.ic_action_search)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        doSearch(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
    /**
     * 执行搜索
     * */
    private void doSearch(String str){
        if(Utils.isAllNumber(str)){
            doSearchPhonenumber(str);
        }
    }
    /**
     * 搜索电话号码
     * */
    private void doSearchPhonenumber(String number){
        baseList.clear();
        Cursor cursor = db.getUserLikephone(number);
        if(cursor.getCount()>0){
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
            Utils.hidesoftInput(searchView,this);
        }
    }


    class DBListAdapter extends BaseAdapter {
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
