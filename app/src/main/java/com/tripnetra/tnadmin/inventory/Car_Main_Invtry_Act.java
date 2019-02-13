package com.tripnetra.tnadmin.inventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.CAR_DETAILS_URL;


public class Car_Main_Invtry_Act extends Fragment {

    AutoCompleteTextView HnameTv;
    Button SearchBtn;
    String PName, PNameId;
    List<String> namesList, idsList;
    Activity activity;

    public Car_Main_Invtry_Act() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory_main, container, false);

        activity = getActivity();

        HnameTv = view.findViewById(R.id.autocompTv);
        SearchBtn = view.findViewById(R.id.search_button);
        HnameTv.setHint("Package Name");

        gethnamesdet();

        return view;
    }

    private void gethnamesdet() {

        Map<String, String> params = new HashMap<>();
        params.put("type", "all");

        new VolleyRequester(activity).ParamsRequest(1, CAR_DETAILS_URL, null, params, false, response -> {
            try {

                JSONArray jarr = new JSONArray(response);
                namesList = new ArrayList<>();
                idsList = new ArrayList<>();

                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);
                    namesList.add(json.getString("city_name")+" - "+json.getString("sightseen_name"));
                    idsList.add(json.getString("sightseen_id"));
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, R.layout.textview_layout, namesList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                HnameTv.setThreshold(1);
                HnameTv.setAdapter(dataAdapter);

                HnameTv.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    PName = String.valueOf(arg0.getItemAtPosition(arg2));
                    int ii = namesList.indexOf(PName);
                    PNameId = idsList.get(ii);

                    HnameTv.clearFocus();
                    View view = activity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });

                SearchBtn.setOnClickListener(view -> {
                    if (PName != null) {

                        Intent intent = new Intent(activity, Car_Invtry_Details_Act.class);
                        intent.putExtra("hname", PName);
                        intent.putExtra("hnameid", PNameId);

                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(activity, "SomeThing Went Wromg\nPlease Try Again", "Ok", false);
            }
        });

    }

}