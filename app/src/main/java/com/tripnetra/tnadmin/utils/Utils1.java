package com.tripnetra.tnadmin.utils;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class Utils1 {

    private static AlertDialog myAlertDialog;
    private static AlertDialog.Builder alertDialogBuilder = null;

   /* public static void setSingleBtnAlert(final Activity activity, String msg, String btnName, final boolean finshact) {
        if (myAlertDialog != null && myAlertDialog.isShowing()) {
            myAlertDialog.dismiss();
            myAlertDialog = null;
        }
        alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setCancelable(false).setMessage(msg).setPositiveButton(btnName, (dialog, which) -> {
            dialog.dismiss();
            if (finshact) {
                activity.finish();
            }
        });
        activity.runOnUiThread(() -> {
            if (!activity.isFinishing()) {
                Utils.myAlertDialog = Utils.alertDialogBuilder.create();
                Utils.myAlertDialog.show();
            }
        });
    }*/

    public static boolean isDataConnectionAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static String orderid(){return "TRIP"+System.currentTimeMillis()/1000+new Random().nextInt(8)+5;}



    public static int getscreenwidth(Activity activity){

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //int width=dm.widthPixels;
        return dm.widthPixels;
    }



    public interface Callback{ void getResponse(String response);}

    public static String ChangeDateFormat(String dates, int  format) {

        String ss;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            Date date = sdf.parse(dates);

            switch (format) {
                case 1: //20-SEP-2018
                    sdf.applyPattern("dd-MMM-yyyy");
                    break;
                case 2: //WED, SEP 20, 2018
                    sdf.applyPattern("EEE, MMM dd, yyyy");
                    break;
                case 3: //SEP 20
                    sdf.applyPattern("MMM dd");
                    break;
                case 4: //SEP 20 1994
                    sdf.applyPattern("MMM dd yyyy");
                    break;
                case 5: //20 SEP
                    sdf.applyPattern("dd MMM");
                    break;
                case 6:
                    sdf.applyPattern("dd");
                    break;
                case 7:
                    sdf.applyPattern("MMM");
                    break;
                case 8:
                    sdf.applyPattern("EEE");
                    break;
                default://2018-09-22
                    sdf.applyPattern("yyyy-MM-dd");
                    break;
            }
            ss = sdf.format(date);
        } catch (ParseException e) {
            //e.printStackTrace();
            ss = dates;
        }

        return ss;

    }

    public static Date StrtoDate (int format,String string) {
        SimpleDateFormat sdf ;

        switch (format) {
            case 1:
                sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                break;
            case 2:
                sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault());
                break;
            default: // case 0
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                break;
        }
        try {
            return sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String DatetoStr (Object date,int  format) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String ss ;
        switch (format) {
            case 1: //20-SEP-2018
                sdf.applyPattern("dd-MMM-yyyy");
                break;
            case 2: //WED, SEP 20, 2018
                sdf.applyPattern("EEE, MMM dd, yyyy");
                break;
            case 3: //SEP 20
                sdf.applyPattern("MMM dd");
                break;
            case 4: //10:20 AM
                sdf.applyPattern("hh:mm aa");
                break;
            case 5: //Sep 20,1994
                sdf.applyPattern("MMM dd, yyyy");
                break;
            case 6:
                sdf.applyPattern("dd");
                break;
            case 7:
                sdf.applyPattern("MMM");
                break;
            case 8:
                sdf.applyPattern("EEE");
                break;
            default://2018-09-22
                sdf.applyPattern("yyyy-MM-dd");
                break;
        }
        ss = sdf.format(date);

        return ss;

    }

}