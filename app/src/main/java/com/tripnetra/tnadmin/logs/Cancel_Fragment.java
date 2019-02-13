package com.tripnetra.tnadmin.logs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.adapters.Booking_Recycler_Adapter;
import com.tripnetra.tnadmin.adapters.DataAdapter;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.CANCEL_CURL;

public class Cancel_Fragment extends Fragment {

    String BookType;
    CustomLoading cloading;
    RecyclerView recyclerView;
    View MainView;
    TextView HotelTv,DarshanTv,TourTv,CarTv,NopeTV;
    Activity activity;

    public Cancel_Fragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainView = inflater.inflate(R.layout.fragment_cancellations, container, false);

        activity = getActivity();
        BookType = "hotel";
        recyclerView = MainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        cloading = new CustomLoading(activity);
        cloading.setCancelable(false);
        assert cloading.getWindow() != null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        HotelTv = MainView.findViewById(R.id.hotelTv);
        DarshanTv = MainView.findViewById(R.id.darshanTv);
        TourTv = MainView.findViewById(R.id.TourTv);
        CarTv = MainView.findViewById(R.id.carTv);
        NopeTV = MainView.findViewById(R.id.NopeTv);

        getcanceldata();

        HotelTv.setOnClickListener(v -> {
            seback(HotelTv);
            BookType = "hotel";
            getcanceldata();
        });

        DarshanTv.setOnClickListener(v -> {
            seback(DarshanTv);
            BookType = "darshan";
            getcanceldata();
        });

        TourTv.setOnClickListener(v -> {
            seback(TourTv);
            BookType = "tour";
            getcanceldata();
        });

        CarTv.setOnClickListener(v -> {
            seback(CarTv);
            BookType = "car";
            getcanceldata();
        });


        return MainView;
    }

    public void getcanceldata(){

        cloading.show();

        Map<String, String> params = new HashMap<>();
        params.put("booktype", BookType);

        new VolleyRequester(activity).ParamsRequest(1, CANCEL_CURL, cloading, params, false, response -> {

            if(cloading.isShowing()){cloading.dismiss();}

            if(response.equals("[]")){
                recyclerView.setVisibility(View.GONE);
                NopeTV.setVisibility(View.VISIBLE);
            }else{

                try {
                    JSONObject jobbj = new JSONObject(response);

                    if(jobbj.getInt("h_count")!=0 ){((TextView) MainView.findViewById(R.id.hcTv)).setText(String.valueOf(jobbj.getInt("h_count")));}
                    if(jobbj.getInt("d_count")!=0 ){((TextView) MainView.findViewById(R.id.dcTv)).setText(String.valueOf(jobbj.getInt("d_count")));}
                    if(jobbj.getInt("t_count")!=0 ){((TextView) MainView.findViewById(R.id.tcTv)).setText(String.valueOf(jobbj.getInt("t_count")));}
                    if(jobbj.getInt("c_count")!=0 ){((TextView) MainView.findViewById(R.id.ccTv)).setText(String.valueOf(jobbj.getInt("c_count")));}

                    JSONArray jarr = jobbj.getJSONArray("v_data");

                    if(jarr.length()==0){
                        recyclerView.setVisibility(View.GONE);
                        NopeTV.setVisibility(View.VISIBLE);
                    }else {
                        recyclerView.setVisibility(View.VISIBLE);
                        NopeTV.setVisibility(View.GONE);

                        List<DataAdapter> list = new ArrayList<>();

                        for (int i = 0; i < jarr.length(); i++) {

                            DataAdapter dataAdapter = new DataAdapter();

                            JSONObject jobj = jarr.getJSONObject(i);

                            switch (BookType) {
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

                            dataAdapter.setType(BookType);
                            list.add(dataAdapter);
                        }

                        recyclerView.setAdapter(new Booking_Recycler_Adapter(list));
                    }

                } catch (JSONException e) {
                    Utils.setSingleBtnAlert(activity, "SomeThing Went Wrong\nPlease Try Again", "Ok", false);
                }

            }
        });

    }

    public void seback(TextView texttv){
        HotelTv.setBackgroundColor(Color.TRANSPARENT);
        DarshanTv.setBackgroundColor(Color.TRANSPARENT);
        TourTv.setBackgroundColor(Color.TRANSPARENT);
        CarTv.setBackgroundColor(Color.TRANSPARENT);

        texttv.setBackgroundResource(R.drawable.line_bottom_blue);
    }
}
