package com.tripnetra.tnadmin.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tripnetra.tnadmin.BuildConfig;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.G46567;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tripnetra.tnadmin.utils.Config.CHECK_IP_URL;
import static com.tripnetra.tnadmin.utils.Config.HOTEL_NAMES_URL;

public class Hotel_Main_Invtry_Act extends Fragment {

    AutoCompleteTextView HnameTv;
    Button SearchBtn;
    String HName,HNameId,HMobile,Usertype,UserId,UserName;
    List<String> namesList,idsList,MobileList;
    Boolean checkip=false;
    Activity activity;

    public Hotel_Main_Invtry_Act() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_inventory_main, container, false);

        activity = getActivity();
        HnameTv = view.findViewById(R.id.autocompTv);
        SearchBtn = view.findViewById(R.id.search_button);
        Usertype = ((G46567) activity.getApplicationContext()).getUserType();
        UserId  = ((G46567) activity.getApplicationContext()).getUserId();
        UserName=((G46567) activity.getApplicationContext()).getUserName();

        Log.i("UserName",UserName);

        if(!Usertype.equals("Admin")){
            checkip = false;
            checkipmethd();
        }else{
            gethnamesdet();
        }

        return view;
    }

     private void checkipmethd() {

        new VolleyRequester(getActivity()).ParamsRequest(1, CHECK_IP_URL, null, null, false, response -> {
            checkip=false;
            if(response.equals( "Success")){
                gethnamesdet();
            }else{
                new AlertDialog.Builder(getActivity())
                        .setMessage("Hello "+UserName+"\nPlease Connect to Company Network")
                        .setPositiveButton("OK", (dialog, id) ->
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS),568))
                        .setCancelable(false).create().show();

            }
        });

    }



    private void gethnamesdet() {

        new VolleyRequester(activity).ParamsRequest(1, HOTEL_NAMES_URL, null, null, false, response -> {
            try {
                JSONArray jarr = new JSONArray(response);
                namesList = new ArrayList<>();idsList = new ArrayList<>();MobileList = new ArrayList<>();

                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);
                    namesList.add(json.getString("hotel_name"));
                    idsList.add(json.getString("hotel_details_id"));
                    MobileList.add(json.getString("phone_number"));
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, R.layout.textview_layout, namesList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                HnameTv.setThreshold(1);
                HnameTv.setAdapter(dataAdapter);

                HnameTv.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    HName = String.valueOf(arg0.getItemAtPosition(arg2));
                    int ii = namesList.indexOf(HName);
                    HNameId = idsList.get(ii);
                    HMobile = MobileList.get(ii);
                    HnameTv.clearFocus();
                    View view = activity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });

                SearchBtn.setOnClickListener(view -> {
                    if(HNameId==null){
                        LinearLayout llayt = activity.findViewById(R.id.mainlayt);
                        Snackbar snackbar = Snackbar.make(llayt, "Please Enter Hotel Name", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        Intent intent = new Intent(activity,Hotel_Invtry_Details_Act.class);
                        intent.putExtra("hname",HName);
                        intent.putExtra("hnameid",HNameId);
                        intent.putExtra("mobile",(HMobile.equals("")) ? "000000" : HMobile);
                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity,"SomeThing Went Wromg\nPlease Try Again","Ok",false);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(checkip && !Usertype.equals("Admin")) {
            checkipmethd();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==568) {checkipmethd();}
    }

}