package com.tripnetra.tnadmin.logs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.CAR_DETAILS_URL;
import static com.tripnetra.tnadmin.utils.Config.HOTEL_NAMES_URL;

public class LogDetailsFragment extends Fragment {

    AutoCompleteTextView HotelATV;
    Button CinBtn,CoutBtn,SearchBtn;
    String FromDate,ToDate,HName,HNameId,LogType = "HOTEL";
    long mindate;int dateflag=0;
    List<String> HnamesList, HidsList,DnamesList, DidsList;
    Boolean h_data = false,d_data = false;
    CustomLoading cloading;
    Activity activity;

    public LogDetailsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_details, container, false);

        activity = getActivity();

        HotelATV = view.findViewById(R.id.hotelAtv);
        CinBtn = view.findViewById(R.id.cinBtn);
        CoutBtn = view.findViewById(R.id.coutBtn);
        SearchBtn = view.findViewById(R.id.searchBtn);
        HnamesList = new ArrayList<>();HidsList = new ArrayList<>();
        DnamesList = new ArrayList<>();DidsList = new ArrayList<>();

        cloading = new CustomLoading(activity);
        cloading.setCancelable(false);
        assert cloading.getWindow() != null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        long fdate = System.currentTimeMillis(),tdate = System.currentTimeMillis()+86400000L;

        FromDate = Utils.DatetoStr(fdate,0);ToDate = Utils.DatetoStr(tdate,0);
        CinBtn.setText(Utils.DatetoStr(fdate,1));CoutBtn.setText(Utils.DatetoStr(tdate,1));

        CinBtn.setOnClickListener(v -> {
            mindate = System.currentTimeMillis() - 7776000000L;
            dateflag = 1;
            datedialog();
        });

        CoutBtn.setOnClickListener(v -> {
                mindate = fdate;
                dateflag = 2;
                datedialog();
        });

        SearchBtn.setOnClickListener(v -> {
            HName = HotelATV.getText().toString();

            if(TextUtils.isEmpty(HName)) {
                HotelATV.requestFocus();
                HotelATV.setError("Enter Name");
            } else if(LogType.equals("HOTEL") && !HnamesList.contains(HName)) {
                HotelATV.requestFocus();
                HotelATV.setError("Enter Valid Hotel");
            } else if(LogType.equals("DARSHAN") && !DnamesList.contains(HName)) {
                HotelATV.requestFocus();
                HotelATV.setError("Enter Valid Package");
            }else{
                Bundle b = new Bundle();
                b.putString("hotelname",HName);
                b.putString("hotelid",HNameId);
                b.putString("fromdate",FromDate);
                b.putString("todate",ToDate);
                b.putString("type",LogType);

                Intent intent = new Intent(activity,LogViewActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        final TextView HotelTv = view.findViewById(R.id.HotelTv);
        final TextView DarshanTv = view.findViewById(R.id.darshanTv);

        HotelTv.setOnClickListener(v -> {
            HotelTv.setBackgroundResource(R.drawable.border_white);
            DarshanTv.setBackgroundColor(Color.TRANSPARENT);
            LogType = "HOTEL";HotelATV.setHint("Hotel Name");
            HotelATV.clearFocus();HotelATV.setText("");
            if(h_data){
                set_atvdata(HnamesList,HidsList);
            }else{
                gethotels();
            }
        });

        DarshanTv.setOnClickListener(v -> {
            DarshanTv.setBackgroundResource(R.drawable.border_white);
            HotelTv.setBackgroundColor(Color.TRANSPARENT);
            LogType = "DARSHAN";HotelATV.setHint("Package Name");
            HotelATV.clearFocus();HotelATV.setText("");
            if(d_data){
                set_atvdata(DnamesList,DidsList);
            }else{
                getpackages();
            }
        });

        gethotels();
        return view;
    }

    private void gethotels() {

        cloading.show();

        new VolleyRequester(activity).ParamsRequest(1, HOTEL_NAMES_URL, cloading, null, false, response -> {

            if(cloading.isShowing()){ cloading.dismiss(); }

            try {
                JSONArray jarr = new JSONArray(response);

                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);
                    HnamesList.add(json.getString("hotel_name"));
                    HidsList.add(json.getString("hotel_details_id"));
                }
                h_data = true;
                set_atvdata(HnamesList,HidsList);
                getpackages();
            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity, "SomeThing Went Wrong\nPlease Try Again", "Ok", false);
            }
        });

    }

    private void getpackages() {

        cloading.show();

        Map<String, String> params = new HashMap<>();
        params.put("type", "all");

        new VolleyRequester(activity).ParamsRequest(1, CAR_DETAILS_URL, cloading, params, false, response -> {

            if(cloading.isShowing()){ cloading.dismiss(); }

            try {

                JSONArray jarr = new JSONArray(response);
                DnamesList = new ArrayList<>();
                DidsList = new ArrayList<>();

                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);
                    DnamesList.add(json.getString("sightseen_name"));
                    DidsList.add(json.getString("sightseen_id"));
                }

                if(d_data){set_atvdata(DnamesList,DidsList);}
                d_data = true;


            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity, "SomeThing Went Wromg\nPlease Try Again", "Ok", false);
            }
        });

    }

    private void set_atvdata(final List<String> list,final List<String> id_list){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, R.layout.textview_layout, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        HotelATV.setThreshold(1);
        HotelATV.setAdapter(dataAdapter);

        HotelATV.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            HName = String.valueOf(arg0.getItemAtPosition(arg2));
            HNameId = id_list.get(list.indexOf(HName));
            HotelATV.clearFocus();
            clearfocus();
        });

    }

    public void datedialog(){

        final Calendar cal = Calendar.getInstance();

        DatePickerDialog pickerDialog = new DatePickerDialog(activity, (view, year, month, day) -> {
            Calendar calndr = Calendar.getInstance();
            calndr.set(year,month,day);

            if (dateflag == 1) {

                Calendar ncal = Calendar.getInstance();
                ncal.setTime(calndr.getTime());
                ncal.add(Calendar.DATE, 1);

                FromDate = Utils.DatetoStr(calndr.getTime(),0);ToDate = Utils.DatetoStr(ncal.getTime(),0);
                CinBtn.setText(Utils.DatetoStr(calndr.getTime(),1));CoutBtn.setText(Utils.DatetoStr(ncal.getTime(),1));

                mindate = calndr.getTimeInMillis();
                dateflag = 2;
                datedialog();

            } else if (dateflag == 2) {
                ToDate = Utils.DatetoStr(calndr.getTime(),0);
                CoutBtn.setText(Utils.DatetoStr(calndr.getTime(),1));
            }

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        pickerDialog.show();
        pickerDialog.getDatePicker().setMinDate(mindate);
        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000 + 7776000000L);
    }

    private void clearfocus() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}