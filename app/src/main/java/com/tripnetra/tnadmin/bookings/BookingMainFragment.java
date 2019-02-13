package com.tripnetra.tnadmin.bookings;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.tripnetra.tnadmin.adapters.Booking_Recycler_Adapter;
import com.tripnetra.tnadmin.adapters.DataAdapter;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Config;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BookingMainFragment extends Fragment {

    public BookingMainFragment() {}

    TextView AllTV,UpcomingTV,CancelTv,NopeTv;
    String BookType="CONFIRM",date="today",ReportType,DataURL;
    int slimit=0,elimit=200,first_use=0;
    FloatingActionButton fabbtn;
    RecyclerView recyclerView;
    Booking_Recycler_Adapter recycleAdapter;
    boolean loading = true;
    int pastVisiblesItems=0,visibleItemCount=0,totalItemCount=0;
    CustomLoading cloading;
    List<DataAdapter> list;
    Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        activity = getActivity();

        setHasOptionsMenu(true);

        AllTV = view.findViewById(R.id.allTv);
        UpcomingTV = view.findViewById(R.id.UpcomeTv);
        CancelTv = view.findViewById(R.id.cancelTv);
        NopeTv = view.findViewById(R.id.NopeTv);
        fabbtn = view.findViewById(R.id.fabBtn);
        list = new ArrayList<>();

        cloading = new CustomLoading(activity);
        cloading.setCancelable(false);
        assert cloading.getWindow() != null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        String ss = Objects.requireNonNull(Objects.requireNonNull(((AppCompatActivity) activity).getSupportActionBar()).getTitle()).toString();

        switch (ss) {
            case "Hotel Bookings":ReportType = "hotel";break;
            case "Car Bookings": ReportType = "car";break;
            case "Tour Bookings": ReportType = "tour";break;
            default: ReportType = "darshan";break;
        }

        DataURL = Config.ADMINURL+ReportType+"bookingreports.php";

        recyclerView = view.findViewById(R.id.recyclerView);
        final LinearLayoutManager recyclerViewlayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        AllTV.setOnClickListener(v -> {
            AllTV.setBackgroundResource(R.drawable.line_bottom_blue);
            UpcomingTV.setBackgroundColor(Color.TRANSPARENT);
            CancelTv.setBackgroundColor(Color.TRANSPARENT);
            BookType="PROCESS";first_use=0;
            date="0000-00-00";
            mclear();
        });

        UpcomingTV.setOnClickListener(v -> {
            UpcomingTV.setBackgroundResource(R.drawable.line_bottom_blue);
            AllTV.setBackgroundColor(Color.TRANSPARENT);
            CancelTv.setBackgroundColor(Color.TRANSPARENT);
            BookType="CONFIRM";first_use=0;
            date="today";
            mclear();
        });

        CancelTv.setOnClickListener(v -> {
            CancelTv.setBackgroundResource(R.drawable.line_bottom_blue);
            UpcomingTV.setBackgroundColor(Color.TRANSPARENT);
            AllTV.setBackgroundColor(Color.TRANSPARENT);
            BookType="CANCELLED";first_use=0;
            date="0000-00-00";
            mclear();
        });

        fabbtn.setOnClickListener(v -> {
            final Calendar cal = Calendar.getInstance();

            final Calendar ncal = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (v1, year, month, day) -> {
                ncal.set(year,month,day);

                date = Utils.DatetoStr(ncal.getTime(),0);

                mclear();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
            long now = System.currentTimeMillis() - 1000;
            datePickerDialog.getDatePicker().setMinDate(now-31536000000L);
            datePickerDialog.getDatePicker().setMaxDate(now+31536000000L);
        });

        getbookreps();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) {
                    visibleItemCount = recyclerViewlayoutManager.getChildCount();
                    totalItemCount = recyclerViewlayoutManager.getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();

                    if (loading && ((visibleItemCount + pastVisiblesItems) >= totalItemCount)) {
                        //Toast.makeText(activity,"totalItemCount is "+totalItemCount+"\n"+(visibleItemCount + pastVisiblesItems), Toast.LENGTH_SHORT).show();
                        loading = false;
                        getbookreps();
                    }
                }
            }
        });

        return view;
    }

    private void getbookreps() {

        cloading.show();

        Map<String, String> params = new HashMap<>();
        params.put("reporttype", BookType);
        params.put("dbdate", date);
        params.put("slimit", String.valueOf(slimit));
        params.put("elimit", String.valueOf(elimit));

        new VolleyRequester(activity).ParamsRequest(1, DataURL, cloading, params, false, response -> {

            if(cloading.isShowing()){cloading.dismiss();}

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
                first_use=1;
                try {
                    JSONArray jarr = new JSONArray(response);

                    for (int i = 0; i < jarr.length(); i++) {

                        DataAdapter dataAdapter = new DataAdapter();

                        JSONObject jobj = jarr.getJSONObject(i);

                        switch (ReportType) {
                            case "hotel":
                                dataAdapter.setHname(jobj.getString("hotel_name"));
                                dataAdapter.setGname(jobj.getString("contact_fname") + " " + jobj.getString("contact_lname"));
                                dataAdapter.setRName(jobj.getString("no_of_rooms") + " - " + jobj.getString("booking_room_type"));
                                dataAdapter.setHid(jobj.getString("pnr_no"));
                                dataAdapter.setStatus(jobj.getString("booking_status"));
                                dataAdapter.setMobile(jobj.getString("contact_mobile_number"));
                                dataAdapter.setInDate(jobj.getString("check_in_date"));
                                dataAdapter.setOutDate(jobj.getString("check_out_date"));

                                break;
                            case "car":
                                dataAdapter.setHname(jobj.getString("car_name"));
                                dataAdapter.setGname(jobj.getString("first_name") + " " + jobj.getString("last_name"));
                                dataAdapter.setRName(jobj.getString("from_city"));
                                dataAdapter.setHid(jobj.getString("pnr_no"));
                                dataAdapter.setStatus(jobj.getString("booking_status"));
                                dataAdapter.setMobile(jobj.getString("phone_no"));
                                dataAdapter.setInDate(jobj.getString("pickup_date"));
                                dataAdapter.setOutDate(jobj.getString("pickup_date"));
                                break;

                            default://case "tour": case "darshan":
                                dataAdapter.setHname(jobj.getString("sightseen_name"));
                                dataAdapter.setGname(jobj.getString("firstname") + " " + jobj.getString("lastname"));
                                dataAdapter.setRName(jobj.getString("search_city"));
                                dataAdapter.setHid(jobj.getString("pnr_no"));
                                dataAdapter.setStatus(jobj.getString("booking_status"));
                                dataAdapter.setMobile(jobj.getString("phone"));
                                dataAdapter.setInDate(jobj.getString("checkin_date"));
                                dataAdapter.setOutDate(jobj.getString("travel_date"));
                                break;
                        }

                        dataAdapter.setType(ReportType);
                        list.add(dataAdapter);
                    }

                    if (slimit == 0) {
                        recycleAdapter = new Booking_Recycler_Adapter(list);
                        recyclerView.setAdapter(recycleAdapter);
                    } else {
                        recycleAdapter.notifyItemInserted(totalItemCount);
                    }

                    slimit += 200;elimit += 200;
                    loading = true;
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(activity, "SomeThing Went Wrong\nPlease Try Again", "Ok", false);
                }
            }
        });
    }

    public void mclear(){
        list.clear();
        if (recycleAdapter != null) { recycleAdapter.notifyDataSetChanged(); }
        slimit=0;elimit=200;
        getbookreps();
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
    public boolean onOptionsItemSelected(MenuItem item) {return super.onOptionsItemSelected(item);}

    @Override
    public void onResume() {
        super.onResume();
        UpcomingTV.setBackgroundResource(R.drawable.line_bottom_blue);
        AllTV.setBackgroundColor(Color.TRANSPARENT);
        CancelTv.setBackgroundColor(Color.TRANSPARENT);
        BookType="CONFIRM";first_use=0;
        date="today";
        mclear();
    }

}