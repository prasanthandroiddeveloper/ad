package com.tripnetra.tnadmin.rest;

import android.app.Activity;
import android.os.Build;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.TLSSocketFactory;
import com.tripnetra.tnadmin.utils.Utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class VolleyRequester {

    private Activity activity;

    public VolleyRequester(Activity activit) {
        this.activity = activit;
    }

    public void ParamsRequest(int method, String JsonURL, final CustomLoading loaddlg, final Map<String, String> params, final Boolean close, final VolleyCallback callback) {

        StringRequest stringRequest = new StringRequest(method, JsonURL,
                callback::getResponse, error -> {
                    error.printStackTrace();
                    if(loaddlg != null ){if(loaddlg.isShowing())loaddlg.dismiss();}
                    String ss ;
                    if( error instanceof NetworkError) { ss = "Network ";
                    } else if( error instanceof ServerError) { ss = "Server ";
                    } else if( error instanceof TimeoutError) {ss = "TimeOut ";
                    }else {ss = ""; }
                    Utils.setSingleBtnAlert(activity,ss+activity.getResources().getString(R.string.error_occur),"Ok",close);

                }) {
            @Override
            public Map<String, String> getParams() {//getParams()
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            HttpStack stack;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                stack = new HurlStack();
            }
            Volley.newRequestQueue(activity, stack).add(stringRequest);
        } else {
            Volley.newRequestQueue(activity).add(stringRequest);
        }
    }

}