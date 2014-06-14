package com.gyh.crm.app.common;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gyh.crm.app.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guoyuehua on 14-5-30.
 */
public class Utils {


    /**
     * 判断手机号码是否输入正确
     * */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
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

        private BaseActivity baseActivity;
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
