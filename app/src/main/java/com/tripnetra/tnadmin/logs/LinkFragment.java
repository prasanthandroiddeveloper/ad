package com.tripnetra.tnadmin.logs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tripnetra.tnadmin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class LinkFragment extends Fragment implements View.OnClickListener {


    public LinkFragment() {}
    //View MainView;
    Activity activity;
    List<String> namesList, idsList, pkglist, idsList1,ilsit,catlist,desclist,taxlist;
    String SName, SId, SName1,SId1,CID,desc,tax;

    Button submitbtn;
    Spinner packspi;
    AutoCompleteTextView psearchtv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_link, container, false);

        activity = getActivity();

        submitbtn = view.findViewById(R.id.submitbtn);
        packspi = view.findViewById(R.id.packspi);
        psearchtv = view.findViewById(R.id.psearchtv);
        submitbtn.setOnClickListener(this);
        gethnamesdet();
        return view;
    }

    private void gethnamesdet() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://tripnetra.com/tours/get_city_details/app", response -> {

            namesList = new ArrayList<>();
            idsList = new ArrayList<>();

            try {
                JSONArray jarr = new JSONArray(response);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);
                    namesList.add(json.getString("label"));
                    idsList.add(json.getString("id"));

                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.textview_layout, R.id.txt, namesList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                psearchtv.setThreshold(1);
                psearchtv.setAdapter(dataAdapter);
                psearchtv.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    SName = String.valueOf(arg0.getItemAtPosition(arg2));
                    int ii = namesList.indexOf(SName);
                    SId = idsList.get(ii);

                    packspi.setVisibility(View.VISIBLE);
                    gethnamesdet1();
                    psearchtv.clearFocus();
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

            Toast.makeText(getActivity(),"something went wrong Try again",Toast.LENGTH_SHORT).show();
        });

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }
    private void gethnamesdet1() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://tripnetra.com/cpanel_admin/calendar/get_darshan_packages/6865446727eae9cbd513", response -> {

            pkglist = new ArrayList<>();
            idsList1 = new ArrayList<>();
            ilsit = new ArrayList<>();
            catlist=new ArrayList<>();
            desclist=new ArrayList<>();
            taxlist=new ArrayList<>();


            try {
                JSONArray jarr = new JSONArray(response);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);
                    pkglist.add(json.getString("sightseen_name"));
                    ilsit.add(json.getString("sightseen_id"));
                    catlist.add(json.getString("sightseen_category_id"));
                    desclist.add(json.getString("sightseen_description"));
                    taxlist.add(json.getString("service_tax"));

                    idsList1.add(SId);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.textview_layout, R.id.txt, pkglist);
                packspi.setAdapter(dataAdapter);
                packspi.setPrompt("Select package");
                packspi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view, int position, long row_id) {
                        SName1 = String.valueOf(arg0.getItemAtPosition(position));
                        int iii = pkglist.indexOf(SName1);
                        SId1 = ilsit.get(iii);
                        int cid=pkglist.indexOf(SName1);
                        CID=catlist.get(cid);

                        desc=desclist.get(iii);
                        Log.i("s",SId1);
                        Log.i("c",CID);
                        Log.i("de",desc);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getActivity(),"something went wrong Try again",Toast.LENGTH_SHORT).show();
        }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("city_id", SId);
                    return params;

                }
        };

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue.add(stringRequest);
    }


    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        Intent intent = new Intent(getActivity(), Link_InfoActivity.class);
        bundle.putString("sightseen_name", SName1);
        bundle.putString("sightseen_id", SId1);
        bundle.putString("city_name", SName);
        bundle.putString("sightseen_description", desc);
        bundle.putString("service_tax", tax);
        bundle.putInt("sightseen_category_id", Integer.parseInt(CID));
        intent.putExtras(bundle);
        Log.i("bundle", String.valueOf(bundle));
        startActivity(intent);


    }
}
