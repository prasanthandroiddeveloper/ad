package com.tripnetra.tnadmin.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.DashBoardActivity;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.adapters.DataAdapter;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Config;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.HOTEL_DET_URL;

public class Hotel_Invtry_Details_Act extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView HnameTv;
    String HName,HnameID;
    CustomLoading cloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invtry_hotel_detils);

        recyclerView = findViewById(R.id.hotelrecycler);
        HnameTv = findViewById(R.id.HNameTV);

        assert getIntent().getExtras()!=null;
        HName = getIntent().getExtras().getString("hname");
        HnameID = getIntent().getExtras().getString("hnameid");

        HnameTv.setText(HName);

        String mob = getIntent().getExtras().getString("mobile");
        if (mob != null) { mob = mob.replaceAll(",","\n"); }
        ((TextView)findViewById(R.id.mobileTv)).setText(mob);

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        assert cloading.getWindow()!=null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cloading.show();

        gethoteldetails();
    }

    private void gethoteldetails() {

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", HnameID);

        new VolleyRequester(this).ParamsRequest(1, HOTEL_DET_URL, cloading, params, true, response -> {
            cloading.dismiss();
            if (response.equals("No Result")) {
                Utils.setSingleBtnAlert(Hotel_Invtry_Details_Act.this, "No Rooms Found", "Ok", true);
            } else {
                List<DataAdapter> list = new ArrayList<>();

                try {
                    JSONArray jarr = new JSONArray(response);

                    for (int i = 0; i < jarr.length(); i++) {
                        DataAdapter dataAdapter = new DataAdapter();

                        JSONObject jobj = jarr.getJSONObject(i);

                        dataAdapter.setRName(jobj.getString("room_type_name"));
                        dataAdapter.setMobile(jobj.getString("hotel_room_details_id"));
                        dataAdapter.setType(jobj.getString("hotel_room_type_id"));
                        dataAdapter.setImage(jobj.getString("room_images"));
                        dataAdapter.setInDate(jobj.getString("max_pax"));
                        dataAdapter.setStatus(jobj.getString("sgl_price"));
                        dataAdapter.setOutDate(jobj.getString("no_of_room_available"));
                        dataAdapter.setHname(HName);
                        dataAdapter.setHid(HnameID);

                        list.add(dataAdapter);

                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(Hotel_Invtry_Details_Act.this));
                    recyclerView.setAdapter(new Recycler_Adapter(list));

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(Hotel_Invtry_Details_Act.this, "SomeThing Went Wrong\nPlease Try Again", "Ok", true);
                }
            }
        });
    }

    class Recycler_Adapter extends RecyclerView.Adapter<Recycler_Adapter.ViewHolder> {

        private List<DataAdapter> listadapter;
        private Context context;

        Recycler_Adapter(List<DataAdapter> listAdapter) {
            super();
            this.listadapter = listAdapter;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_search_recycler, parent, false));
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            TextView NameTv,RDetTv;
            ImageView RoomIv;
            Button ManageBtn;

            ViewHolder(View itemView) {

                super(itemView);

                NameTv = itemView.findViewById(R.id.RNameTV);
                RDetTv = itemView.findViewById(R.id.RoomdetTv);
                RoomIv = itemView.findViewById(R.id.RoomIV);
                ManageBtn = itemView.findViewById(R.id.managebtn);

                context = itemView.getContext();
                ManageBtn.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                if(v==ManageBtn) {
                    Intent intent =  new Intent(context, Hotel_Manage_Invtry_Act.class);
                    intent.putExtra("hotelname", listadapter.get(getAdapterPosition()).getHname());
                    intent.putExtra("roomname", listadapter.get(getAdapterPosition()).getRName());
                    intent.putExtra("hotelid", listadapter.get(getAdapterPosition()).getHid());
                    intent.putExtra("roomdetid", listadapter.get(getAdapterPosition()).getMobile());
                    intent.putExtra("roomtypeid", listadapter.get(getAdapterPosition()).getType());
                    intent.putExtra("roomprice", listadapter.get(getAdapterPosition()).getStatus());
                    intent.putExtra("roomtotal", listadapter.get(getAdapterPosition()).getOutDate());
                    intent.putExtra("roomavail", listadapter.get(getAdapterPosition()).getInDate());
                    context.startActivity(intent);
                }

            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {

            DataAdapter dataAdap = listadapter.get(pos);

            vh.NameTv.setText(dataAdap.getRName());
            vh.RDetTv.setText("Adults : "+dataAdap.getInDate()+"  Price : "+dataAdap.getStatus());

            if(!dataAdap.getImage().equals( "")) {
                String ss = Config.BASEURL;
                ss = ss.replaceAll("androidphpfiles/","cpanel_admin/uploads/hotel_images/");
                Glide.with(context).load(ss+dataAdap.getImage())
                        .apply(new RequestOptions()
                                .centerInside()
                                .placeholder(R.drawable.load1)
                                .transform(new CircleCrop()))
                        .into(vh.RoomIv);
            }
        }

        @Override
        public int getItemCount() { return listadapter.size(); }

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}