package com.gyh.crm.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.kirin.CheckUpdateListener;
import com.baidu.kirin.PostChoiceListener;
import com.baidu.kirin.StatUpdateAgent;
import com.baidu.kirin.objects.KirinCheckState;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.gyh.crm.app.common.Base;
import com.gyh.crm.app.common.BaseActivity;
import com.gyh.crm.app.common.Constant;
import com.gyh.crm.app.common.DBService;
import com.gyh.crm.app.common.FileService;
import com.gyh.crm.app.common.Utils;
import com.gyh.crm.app.listener.DBServiceListener;
import com.gyh.crm.app.utils.DownloadCompleteReceiver;
import com.gyh.crm.app.utils.UpdateDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity implements CheckUpdateListener,PostChoiceListener {

    private final static String FILENAME = "crmuser.txt";
    private ListView listView;
    private List<Base> baseList = new ArrayList<Base>();
    private DBListAdapter dbListAdapter;
    private FileService fileService;
    private UpdateDialog utestUpdate;
    private CheckUpdateListener mCheckUpdateResponse;
    private PostChoiceListener mPostUpdateChoiceListener;
    private DownloadCompleteReceiver receiver;//接受下载完成的广播
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dbListAdapter.notifyDataSetChanged();
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiver = new DownloadCompleteReceiver();//创建下载完毕接收器
        listView = (ListView) findViewById(R.id.listview);
        dbListAdapter = new DBListAdapter(this);
        listView.setAdapter(dbListAdapter);
        fileService = new FileService(FILENAME);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("userbase", baseList.get(i));
                intent.setClass(MainActivity.this, UserRecordActivity.class);
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
        setBaiduTongji();
    }

    /**
     * 设置百度移动统计
     * */
    private void setBaiduTongji(){
        StatService.setSessionTimeOut(30);
        StatService.setLogSenderDelayed(30);
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);
        StatService.setAppChannel(this,	"Baidu Market",	true);
        // 小流量发布相关---------------------start------------------------------------------------------
        // 这些设置以及检查更新的代码需要在StatService的系列设置调用之后才行（如果使用了setAppChannel来设置渠道
        // ，起码必须在setAppChannel之后）
        mCheckUpdateResponse = this;
        mPostUpdateChoiceListener = this;
        utestUpdate = new UpdateDialog(this, getResources().getString(R.string.app_name),mPostUpdateChoiceListener);
        StatUpdateAgent.setTestMode(); // 打开小流量调试模式，在该模式下，不受更新频率设置的影响。如果不设置测试模式，那么请求间隔默认每天会请求一次

        // 小流量检查是否有更新，该调用必须在setAppChannel之后调用才可以。启动调用的时候，第二个参数设置true，此时每天启动只提示一次
//        StatUpdateAgent.postUserChoice(MainActivity.this,4,mPostUpdateChoiceListener);
        StatUpdateAgent.checkUpdate(MainActivity.this, true,mCheckUpdateResponse);

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
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AddUserActivity.class);
            intent.putExtra(Constant.ExtraKeyName.OPERATIONTYPE, Constant.ExtraKeyValue.OPERATIONTYPE_ADD);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search) {
            startActivity(new Intent().setClass(MainActivity.this, SearchActivity.class));
            return true;
        } else if (id == R.id.action_export) {
            if (fileService.ishasFile()) {
                Utils.alertYesOrNo(MainActivity.this, "提示", "文件已存在，是否覆盖？", "覆盖", "点错了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fileService.saveToSDCard(getUserDBtoString());
                        Toast.makeText(MainActivity.this, "导出成功", Toast.LENGTH_SHORT).show();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
            } else {
                fileService.saveToSDCard(getUserDBtoString());
                Toast.makeText(MainActivity.this, "导出成功", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_import) {
            setStringToUser(fileService.getFromSDCard());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getListDate();
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 获取数据
     */
    private void getListDate() {
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
        Collections.reverse(baseList);
        dbListAdapter.notifyDataSetChanged();
    }

    /**
     * 删除用户
     */
    private void deleteUser(final String phonenumber) {
        Utils.alertYesOrNo(this, "删除", "是否删除此用户以及相关记录", "是的", "点错了", new DialogInterface.OnClickListener() {
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
     */
    private void setStringToUser(String readstr) {
        if (!"".equals(readstr) && readstr != null) {
            String[] userlist = readstr.split("￥￥");//分割用户信息
            for (int i = 0; i < userlist.length; i++) {
                String[] userinfo = userlist[i].split("#");
                String[] userinfos = userinfo[0].split("_");
                Base base = new Base();
                base.setUsertime(userinfos[0]);
                base.setUsername(userinfos[1]);
                base.setPhonenumber(userinfos[2]);
                base.setUserlevel(userinfos[3]);
                base.setUserev(userinfos[4]);
                base.setUserrecord(userinfos[5]);
                boolean ishas = false;//判断此用户在数据库中是否存在
                for (int j = 0; j < baseList.size(); j++) {
                    if (base.getPhonenumber().equals(baseList.get(j).getPhonenumber())) {
                        ishas = true;
                        break;
                    }
                }
                //若不存在保存数据库
                if (!ishas) {
                    saveUserToDB(base);
                    if (userinfo.length > 1 && userinfo[1] != null && !"".equals(userinfo[1])) {
                        String[] userinforecordlist = userinfo[1].split("@");
                        for (int n = 0; n < userinforecordlist.length; n++) {
                            String[] userinforecord = userinforecordlist[n].split("_");
                            Base base1 = new Base();
                            base1.setUsertime(userinforecord[0]);
                            base1.setPhonenumber(userinforecord[1]);
                            base1.setUserrecord(userinforecord[2]);
                            saveUserInfoToDB(base1);
                        }
                    }
                }
            }
            //刷新列表
            getListDate();
        } else {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将数据转换成字符串
     * ￥￥ -- 隔开每个客户信息
     * # -- 隔开客户信息和记录
     *
     * @ -- 隔开每条记录
     * _ -- 隔开每个字段
     */
    private String getUserDBtoString() {
        String userstr = "";
        Cursor cursor = db.getUserList();
        while (cursor.moveToNext()) {
            Base base = new Base();
            base.setUsertime(cursor.getString(0));
            base.setUsername(cursor.getString(1));
            base.setPhonenumber(cursor.getString(2));
            base.setUserlevel(cursor.getString(3));
            base.setUserev(cursor.getString(4));
            base.setUserrecord(cursor.getString(5));
            userstr += Utils.setBaseToFileString(base);
            Cursor cursor1 = db.getRecordList(base.getPhonenumber());
            while (cursor1.moveToNext()) {
                Base base1 = new Base();
                base1.setUsertime(cursor1.getString(0));
                base1.setPhonenumber(cursor1.getString(1));
                base1.setUserrecord(cursor1.getString(2));
                userstr += Utils.setInfoToFileString(base1);
            }
            cursor1.close();
            userstr += "￥￥";
        }
        cursor.close();
        return userstr;
    }

    /**
     * 保存用户到数据库
     */
    private void saveUserToDB(Base tempitem) {
        boolean saveok = db.insertUser(tempitem.getUsertime()
                , tempitem.getUsername()
                , tempitem.getPhonenumber()
                , tempitem.getUserlevel() + ""
                , tempitem.getUserev() + ""
                , tempitem.getUserrecord());
    }

    /**
     * 添加用户记录信息到数据库
     */
    private void saveUserInfoToDB(Base tempitem) {
        db.insertRecord(tempitem.getUsertime()
                , tempitem.getPhonenumber()
                , tempitem.getUserrecord());
    }

    @Override
    public void checkUpdateResponse(KirinCheckState state,HashMap<String, String> dataContainer) {
        // TODO Auto-generated method stub
        if (state == KirinCheckState.ALREADY_UP_TO_DATE) {
            Log.d("demodemo", "stat == KirinCheckState.ALREADY_UP_TO_DATE");
            // KirinAgent.postUserChoice(getApplicationContext(),
            // choice);//choice 几种升级类型：0-未更新，1-不更新，2-稍后更新，3-手动更新，4-强制更新
        } else if (state == KirinCheckState.ERROR_CHECK_VERSION) {
            Log.d("demodemo", "KirinCheckState.ERROR_CHECK_VERSION");
        } else if (state == KirinCheckState.NEWER_VERSION_FOUND) {
            Log.d("demodemo", "KirinCheckState.NEWER_VERSION_FOUND"
                    + dataContainer.toString());

            String isForce = dataContainer.get("updatetype");
            String noteInfo = dataContainer.get("note");
            String publicTime = dataContainer.get("time");
            String appUrl = dataContainer.get("appurl");
            String appName = dataContainer.get("appname");
            String newVersionName = dataContainer.get("version");
            String newVersionCode = dataContainer.get("buildid");
            String attachInfo = dataContainer.get("attach");

            // 这些信息都是在mtj.baidu.com上您选择的小流量定制信息
            utestUpdate.doUpdate(appUrl, noteInfo);
        }
    }

    @Override
    public void PostUpdateChoiceResponse(JSONObject jsonObject) {
        try {
            if("1".equals(jsonObject.getString("isdown"))){
                DownloadManager manager =(DownloadManager)this.getSystemService(DOWNLOAD_SERVICE); //初始化下载管理器
                String url = jsonObject.getString("downloadUrl");
                Uri resource = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(resource);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(false);
                //设置文件类型
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
                request.setMimeType(mimeString);
                //在通知栏中显示
                request.setShowRunningNotification(true);
                request.setVisibleInDownloadsUi(true);
                //sdcard的目录下的download文件夹
                request.setDestinationInExternalPublicDir("/download/", "crm.apk");
                request.setTitle("crm.apk");
                manager.enqueue(request);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class DBListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public DBListAdapter(Context context) {
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
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_userlist, null);
                holder.username = (TextView) view.findViewById(R.id.username);
                holder.usertime = (TextView) view.findViewById(R.id.usertime);
                holder.userphone = (TextView) view.findViewById(R.id.userphone);
                holder.usernum = (TextView) view.findViewById(R.id.usernum);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.username.setText(baseList.get(i).getUsername());
            holder.usertime.setText(baseList.get(i).getUsertime());
            holder.userphone.setText(baseList.get(i).getPhonenumber());
            holder.usernum.setText(baseList.get(i).getNum());
            return view;
        }
    }

    public class ViewHolder {
        public TextView username;
        public TextView usertime;
        public TextView userphone;
        public TextView usernum;
    }

}
