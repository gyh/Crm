package frame.system.seven.common.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import frame.system.seven.R;
import frame.system.seven.common.base.BaseActivity;
import frame.system.seven.common.bean.Base;

/**
 * Created by guoyuehua on 14-5-30.
 */
public class Utils {

    /**
     * 强制隐藏软键盘
     * */
    public static void hidesoftInput(View view,BaseActivity baseActivity){
        InputMethodManager imm = (InputMethodManager) baseActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
    * 用JAVA自带的函数
    * */
    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * 用正则表达式判断是否全是数字
     * */
    public static boolean isAllNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


    /**
     * 判断手机号码是否输入正确
     * */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18|14)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    /**
     * 选择通话类型
     * */
    public static void alertPhoneType(final BaseActivity baseActivity, final EditText phoneedit){
        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
        builder.setTitle(R.string.action_phone)
                .setItems(R.array.phonetypearr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            getPhonenumber(baseActivity,1,phoneedit);
                        }else if(which==1){
                            getPhonenumber(baseActivity,2,phoneedit);
                        }else if(which==2){
                            getPhonenumber(baseActivity,3,phoneedit);
                        }
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 选择通话类型
     * */
    public static void getPhonenumber(BaseActivity baseActivity,int Type, final EditText phoneedit){
        int i=0;
        ContentResolver cr = baseActivity.getContentResolver();
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE,CallLog.Calls.DURATION},
                null, null,CallLog.Calls.DEFAULT_SORT_ORDER);
        String strtemp="";
        while (cursor.moveToNext()){
            if(Type == cursor.getInt(2)){
                strtemp+=cursor.getString(0)+"_";
                i++;
            }

        }
        final String[] strphone=strtemp.split("_");
        cursor.close();
        if(strphone.length>0){
            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
            builder.setTitle(R.string.action_phone)
                    .setItems(strphone, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            phoneedit.setText(strphone[which]);
                        }
                    });
            Dialog dialog = builder.create();
            dialog.show();
        }else {
            Toast.makeText(baseActivity,"没有记录",Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * 获取日期
     * */
    public static String getDate(){
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
    /**
     * 获取时间
     * */
    public static String getTime(){

        SimpleDateFormat formatter1=new SimpleDateFormat("HH:mm");
        Date curDate1 = new Date(System.currentTimeMillis());//获取当前时间
        return formatter1.format(curDate1);
    }

    /**
     * 选择提示框
     * */
    public static void alertYesOrNo(final BaseActivity baseActivity,String title,String msg,String firstbtntitle,String secondbtntitle
                              ,DialogInterface.OnClickListener firstlis,DialogInterface.OnClickListener secondlis){
        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
        builder.setTitle(title).setMessage(msg).
                setPositiveButton(firstbtntitle,firstlis).setNegativeButton(secondbtntitle,secondlis);
        Dialog dialog = builder.create();
        dialog.show();
    }
    /**
     * 获取更改后的时间
     * */
    public static void alertTime(final BaseActivity baseActivity, final TextView timeview, final TextView dateview){
        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
        builder.setTitle(R.string.action_time)
                .setItems(R.array.timearr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            DialogFragment newFragment = new DatePickerFragment(baseActivity,new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    int month=monthOfYear+1;
                                    if(month>9){
                                        if(dayOfMonth>9){
                                            dateview.setText(""+year+"年"+month+"月"+dayOfMonth+"日");
                                        }else {
                                            dateview.setText(""+year+"年"+month+"月0"+dayOfMonth+"日");
                                        }
                                    }else{
                                        if(dayOfMonth>9){
                                            dateview.setText(""+year+"年0"+month+"月"+dayOfMonth+"日");
                                        }else {
                                            dateview.setText(""+year+"年0"+month+"月0"+dayOfMonth+"日");
                                        }
                                    }
                                }
                            });
                            newFragment.show(baseActivity.getSupportFragmentManager(),"timePicker");
                        }else if(which==1){
                            DialogFragment newFragment = new TimePickerFragment(baseActivity,new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    if(hourOfDay>9){
                                        if(minute>9){
                                            timeview.setText("" + hourOfDay + ":" + minute);
                                        }else{
                                            timeview.setText("" + hourOfDay + ":0" + minute);
                                        }
                                    }else{
                                        if(minute>9){
                                            timeview.setText("0" + hourOfDay + ":" + minute);
                                        }else{
                                            timeview.setText("0" + hourOfDay + ":0" + minute);
                                        }
                                    }

                                }
                            });
                            newFragment.show(baseActivity.getSupportFragmentManager(),"timePicker");
                        }
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 时间弹出窗口
     * */
    public static class TimePickerFragment extends DialogFragment{

        private  BaseActivity baseActivity;
        private TimePickerDialog.OnTimeSetListener onTimeSetListener;
        public TimePickerFragment(BaseActivity baseActivit, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
            this.baseActivity = baseActivit;
            this.onTimeSetListener = onTimeSetListener;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(baseActivity,onTimeSetListener, hour, minute,
                    DateFormat.is24HourFormat(baseActivity));
        }
    }

    public static class FireMissilesDialogFragment extends DialogFragment {
        /**
         * 创建Fragment对话框实例
         *
         * @param title：指定对话框的标题。
         * @return：Fragment对话框实例。
         */
        public static FireMissilesDialogFragment newInstance(String title) {
            FireMissilesDialogFragment frag = new FireMissilesDialogFragment();
            Bundle args = new Bundle();
            // 自定义的标题
            args.putString("title", title);
            frag.setArguments(args);
            return frag;
        }

        /*
         * 覆写Fragment类的onCreateDialog方法，在DialogFragment的show方法执行之后， 系统会调用这个回调方法。
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // 获取对象实例化时传入的窗口标题。
            String title = getArguments().getString("title");
            // 用builder创建对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(title);
            builder.setPositiveButton("fire", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // FIRE ZE MISSILES!
                }
            });
            builder.setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // 创建一个dialog对象并返回
            return builder.create();
        }
    }

    /**
     * 将用户数据转换成字符串
     * */
    public static String setBaseToFileString(Base base){
        String strfile = "";
        strfile+=base.getUsertime()+"_"
                +base.getUsername()+"_"
                +base.getPhonenumber()+"_"
                +base.getUserlevel()+"_"
                +base.getUserev()+"_"
                +base.getUserrecord()+"#";
        return strfile;
    }

    /**
     * 将用户信息转换成字符串
     * */
    public static String setInfoToFileString(Base base){
        String strfile = "";
        strfile+=base.getUsertime()+"_"
                +base.getPhonenumber()+"_"
                +base.getUserrecord()+"@";
        return strfile;
    }

    /**
     * 日期弹出窗口
     * */
    public static class DatePickerFragment extends DialogFragment{
        private BaseActivity baseActivity;
        private  DatePickerDialog.OnDateSetListener onDateSetListener;
        public DatePickerFragment(BaseActivity baseActivit, DatePickerDialog.OnDateSetListener onDateSetListener) {
            this.baseActivity = baseActivit;
            this.onDateSetListener = onDateSetListener;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(baseActivity,onDateSetListener, year, month, day);
        }
    }
}
