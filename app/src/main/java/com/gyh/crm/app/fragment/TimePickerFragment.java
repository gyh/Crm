package com.gyh.crm.app.fragment;

/**
 * Created by GYH on 2014/11/24.
 */

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.gyh.crm.app.common.BaseActivity;

import java.util.Calendar;

/**
 * 时间弹出窗口
 * */
public class TimePickerFragment extends DialogFragment {

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