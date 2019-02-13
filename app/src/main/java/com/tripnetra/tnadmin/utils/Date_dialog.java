package com.tripnetra.tnadmin.utils;

import android.app.Activity;
import android.app.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

public class Date_dialog {

    private Activity activity;
    private long mindate,maxdate;

    public Date_dialog(Activity act, long min, long max){
        activity = act;
        mindate = min;
        maxdate = max;
    }

    public void DateDialog(final date_interface intrfce){
        final Calendar cal = Calendar.getInstance();

        DatePickerDialog pickerDialog = new DatePickerDialog(activity, (view, year, month, day) -> {
            Calendar calndr = Calendar.getInstance();
            calndr.set(year,month,day);

            intrfce.getResponse(calndr.getTime());

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        pickerDialog.show();
        pickerDialog.getDatePicker().setMinDate(mindate);
        pickerDialog.getDatePicker().setMaxDate(maxdate);

    }

    public interface date_interface {
        void getResponse(Date date);
    }

}