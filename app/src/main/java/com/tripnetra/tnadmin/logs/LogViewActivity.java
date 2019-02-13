package com.tripnetra.tnadmin.logs;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.adapters.DataAdapter;
import com.tripnetra.tnadmin.adapters.LogRecycler_Adapter;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.LOG_DETAILS_URL;

public class LogViewActivity extends AppCompatActivity {

    LogRecycler_Adapter recycleAdapter;
    RecyclerView recyclerView;
    String HName, HNameId, FromDate, ToDate,Log_Type;
    CustomLoading cloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        assert getIntent().getExtras()!=null;
        HName = getIntent().getExtras().getString("hotelname");
        HNameId = getIntent().getExtras().getString("hotelid");
        FromDate = getIntent().getExtras().getString("fromdate");
        ToDate = getIntent().getExtras().getString("todate");
        Log_Type = getIntent().getExtras().getString("type");//HOTEL   DARSHAN

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        assert cloading.getWindow()!=null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        getlogdetails();

    }

    private void getlogdetails() {

        cloading.show();

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", HNameId);
        params.put("fromdate", FromDate);
        params.put("todate", ToDate);
        params.put("type", Log_Type);

        new VolleyRequester(this).ParamsRequest(1, LOG_DETAILS_URL, cloading, params, true, response -> {
            cloading.dismiss();

            if (response.equals("No Result")) {
                findViewById(R.id.NopeTv).setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                findViewById(R.id.NopeTv).setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                List<DataAdapter> listAdapter = new ArrayList<>();

                try {
                    JSONArray jarr = new JSONArray(response);
                    if(Log_Type.equals("HOTEL")) {
                        for (int i = 0; i < jarr.length(); i++) {
                            DataAdapter dataAdapter = new DataAdapter();

                            JSONObject jobj = jarr.getJSONObject(i);

                            dataAdapter.setGname(jobj.getString("admin_name"));
                            dataAdapter.setInDate(jobj.getString("from_date"));
                            dataAdapter.setOutDate(jobj.getString("to_date"));
                            dataAdapter.setMobile(jobj.getString("creation_date"));
                            dataAdapter.setType(jobj.getString("old_availability"));
                            dataAdapter.setStatus(jobj.getString("new_availability"));
                            dataAdapter.setHname(jobj.getString("old_price"));
                            dataAdapter.setRName(jobj.getString("new_price"));

                            dataAdapter.setNewType(Log_Type);
                            listAdapter.add(dataAdapter);
                        }
                    }else{
                        for (int i = 0; i < jarr.length(); i++) {
                            DataAdapter dataAdapter = new DataAdapter();

                            JSONObject jobj = jarr.getJSONObject(i);

                            dataAdapter.setGname(jobj.getString("admin_name"));
                            dataAdapter.setInDate(FromDate);
                            dataAdapter.setOutDate(ToDate);
                            dataAdapter.setMobile(jobj.getString("creation_date"));
                            dataAdapter.setType(jobj.getString("old_adult_price"));
                            dataAdapter.setStatus(jobj.getString("new_adult_price"));
                            dataAdapter.setHname(jobj.getString("old_child_price"));
                            dataAdapter.setRName(jobj.getString("new_child_price"));

                            dataAdapter.setNewName(jobj.getString("sightseen_name"));
                            dataAdapter.setCarName(jobj.getString("car_name"));
                            dataAdapter.setCap1(jobj.getString("old_capacity"));
                            dataAdapter.setCap2(jobj.getString("new_capacity"));

                            dataAdapter.setNewType(Log_Type);

                            listAdapter.add(dataAdapter);
                        }
                    }
                    recycleAdapter = new LogRecycler_Adapter(listAdapter);
                    recyclerView.setAdapter(recycleAdapter);
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(LogViewActivity.this, "SomeThing Went Wrong\nPlease Try Again", "Ok", true);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (recycleAdapter != null) {
                    recycleAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        return true;
    }

}