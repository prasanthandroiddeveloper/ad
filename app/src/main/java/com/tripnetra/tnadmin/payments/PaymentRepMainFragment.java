package com.tripnetra.tnadmin.payments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.adapters.DataAdapter;
import com.tripnetra.tnadmin.adapters.PaymentRepRecycleAdapter;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.tripnetra.tnadmin.utils.Config.PAY_REP_URL;

public class PaymentRepMainFragment extends Fragment {

    public PaymentRepMainFragment() {}

    TextView SelectAllTv, SelectUpcomeTv,NopeTv;
    RecyclerView recyclerView;
    List<DataAdapter> listAdapter;
    PaymentRepRecycleAdapter recycleAdapter;
    boolean loading = true;
    int pastVisiblesItems=0, visibleItemCount=0, totalItemCount=0;
    int slimit = 0,elimit = 100,first_use=0;
    String PayRepType = "UPCOMING", LastcoutDate;
    CustomLoading cloading;
    Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_rep_main, container, false);
        setHasOptionsMenu(true);

        activity = getActivity();

        SelectAllTv = view.findViewById(R.id.AllTv);
        SelectUpcomeTv = view.findViewById(R.id.UpcomeTv);
        NopeTv = view.findViewById(R.id.NopeTv);

        listAdapter = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        final LinearLayoutManager recyclerViewlayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        cloading = new CustomLoading(activity);
        cloading.setCancelable(false);
        assert cloading.getWindow() != null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        SelectAllTv.setOnClickListener(view1 -> {
            SelectAllTv.setBackgroundResource(R.drawable.red_bottom_line);
            SelectUpcomeTv.setBackgroundColor(Color.parseColor("#B60000"));
            PayRepType = "ALL";
            slimit = 0;elimit = 100;
            listAdapter.clear();
            if (recycleAdapter != null) {
                recycleAdapter.notifyDataSetChanged();
            }
            getpaymentreps();
        });

        SelectUpcomeTv.setOnClickListener(view12 -> {
            SelectUpcomeTv.setBackgroundResource(R.drawable.red_bottom_line);
            SelectAllTv.setBackgroundColor(Color.parseColor("#B60000"));
            PayRepType = "UPCOMING";
            slimit = 0;elimit = 100;
            listAdapter.clear();
            if (recycleAdapter != null) {
                recycleAdapter.notifyDataSetChanged();
            }
            getpaymentreps();
        });

        getpaymentreps();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) {

                    visibleItemCount = recyclerViewlayoutManager.getChildCount();
                    totalItemCount = recyclerViewlayoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager()))
                            .findFirstVisibleItemPosition();

                    if (loading && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        //Toast.makeText(activity,"totalItemCount is "+totalItemCount+"\n"+(visibleItemCount + pastVisiblesItems), Toast.LENGTH_SHORT).show();
                        loading = false;

                        getpaymentreps();
                    }
                }
            }
        });
        return view;
    }

    private void getpaymentreps() {

        cloading.show();

        Map<String, String> params = new HashMap<>();
        params.put("status", PayRepType);
        params.put("slimit", String.valueOf(slimit));
        params.put("elimit", String.valueOf(elimit));

        new VolleyRequester(activity).ParamsRequest(1, PAY_REP_URL, cloading, params, false, response -> {
            cloading.dismiss();

            recyclerView.setVisibility(View.VISIBLE);NopeTv.setVisibility(View.GONE);

            if (response.equals("No Result")) {
                if(first_use==0){
                    NopeTv.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    LinearLayout llaout = activity.findViewById(R.id.mainlayt);
                    Snackbar snackbar = Snackbar.make(llaout, "You Reached The End", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } else {
                try {
                    JSONArray jarr = new JSONArray(response);
                    for (int i = 0; i < jarr.length(); i++) {

                        DataAdapter dataAdapter = new DataAdapter();

                        JSONObject jobj = jarr.getJSONObject(i);

                        dataAdapter.setHname(jobj.getString("hotel_name"));
                        dataAdapter.setHid(jobj.getString("pnr_no"));
                        dataAdapter.setStatus(jobj.getString("booking_status"));
                        dataAdapter.setInDate(jobj.getString("check_in_date"));
                        dataAdapter.setType(jobj.getString("booking_room_type"));

                        DecimalFormat df = new DecimalFormat("0.0");
                        float price = 0,commision = 0,HcPrice=0,HGst=0;
                        if(!jobj.getString("total_sgl_price").equals("")&& !jobj.getString("total_sgl_price").equals("null")){
                            price = Float.parseFloat(jobj.getString("total_sgl_price"));
                        }
                        if(!jobj.getString("commission").equals("")&& !jobj.getString("commission").equals("null")){
                            commision = Float.parseFloat(jobj.getString("commission"));
                        }
                        float Hcomm = price*commision/100;

                        if (!jobj.getString("bh_gstin").equals("NotAvailable")) {
                            if(commision!=0) {
                                HcPrice = Hcomm * 18 / 100;
                            }
                            if(!jobj.getString("total_gst").equals("")&& !jobj.getString("total_gst").equals("null")){
                                HGst = Float.parseFloat(jobj.getString("total_gst"));
                            }

                        }
                        dataAdapter.setGname(df.format(price-Hcomm-HcPrice+HGst)+" /-");
                        dataAdapter.setMobile(jobj.getString("status"));

                        LastcoutDate = jobj.getString("check_out_date");
                        listAdapter.add(dataAdapter);

                    }

                    if(slimit==0) {
                        recycleAdapter = new PaymentRepRecycleAdapter(listAdapter);
                        recyclerView.setAdapter(recycleAdapter);
                    }else{
                        recycleAdapter.notifyItemInserted(totalItemCount);
                    }

                    slimit += 100;elimit += 100;
                    loading = true;
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(activity,"Something Went Wrong Try Again","Ok",false);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                if(recycleAdapter!=null) {
                    recycleAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { return super.onOptionsItemSelected(item); }

}

