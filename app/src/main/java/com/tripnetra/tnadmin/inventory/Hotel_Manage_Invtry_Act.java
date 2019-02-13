package com.tripnetra.tnadmin.inventory;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Date_dialog;
import com.tripnetra.tnadmin.utils.G46567;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.ROOM_DET_URL;

public class Hotel_Manage_Invtry_Act extends AppCompatActivity {

    String HnameID, RTypeID, Fromdate, Todate, OldAvail, OldPrice;
    EditText AvlRoomEt,RoomPriceEt,MessageEt;
    Switch BlockSwitch;
    TextView FromTv,ToTv,TotalRoomTV,NoteTv;
    long ftdate = System.currentTimeMillis() - 1000;
    Boolean BlockHotel = false;
    CustomLoading cloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_mnage_invtry);

        assert getIntent().getExtras() != null;
        HnameID = getIntent().getExtras().getString("hotelid");
        RTypeID = getIntent().getExtras().getString("roomtypeid");
        OldAvail = getIntent().getExtras().getString("roomavail");
        OldPrice = getIntent().getExtras().getString("roomprice");

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        assert cloading.getWindow()!=null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cloading.show();

        TotalRoomTV = findViewById(R.id.TotalRoomTv);
        AvlRoomEt = findViewById(R.id.AvlRoomET);
        RoomPriceEt = findViewById(R.id.RoomPriceET);
        BlockSwitch = findViewById(R.id.blockswitch);
        FromTv = findViewById(R.id.fromTV);
        ToTv = findViewById(R.id.toTV);
        MessageEt = findViewById(R.id.msgEt);
        NoteTv = findViewById(R.id.noteTv);

        ((TextView)findViewById(R.id.RNameTV)).setText(getIntent().getExtras().getString("roomname"));
        TotalRoomTV.setText(getIntent().getExtras().getString("roomtotal"));

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Fromdate = Utils.DatetoStr(cal.getTime(),0);
        FromTv.setText(Utils.DatetoStr(cal.getTime(),1));
        cal.add(Calendar.DATE, 1);
        Todate = Utils.DatetoStr(cal.getTime(),0);
        ToTv.setText(Utils.DatetoStr(cal.getTime(),1));

        findViewById(R.id.notevTv).setOnClickListener(v ->{
            if(NoteTv.getVisibility() == View.VISIBLE){
                NoteTv.setVisibility(View.GONE);
            }else{
                NoteTv.setVisibility(View.VISIBLE);
            }
        });

        BlockSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!BlockHotel){
                BlockHotel = true;
                AvlRoomEt.setText("0");
                AvlRoomEt.setEnabled(false);
                RoomPriceEt.setEnabled(false);
            }else{
                BlockHotel = false;
                AvlRoomEt.setEnabled(true);
                RoomPriceEt.setEnabled(true);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getroomdetails();

    }

    public void fromdatemthd(View v){

        new Date_dialog(this,ftdate,System.currentTimeMillis() - 1000+31536000000L).DateDialog(date -> {
            Fromdate = Utils.DatetoStr(date,0);
            ftdate = date.getTime();

            Calendar ncal = Calendar.getInstance();
            ncal.setTime(date);
            ncal.add(Calendar.DATE, 1);
            Todate = Utils.DatetoStr(ncal.getTime(),0);
            FromTv.setText(Utils.DatetoStr(date,1));
            ToTv.setText(Utils.DatetoStr(ncal.getTime(),1));

            todatemthd(v);
        });

    }

    public void todatemthd(View v){

        new Date_dialog(this,ftdate,System.currentTimeMillis() - 1000+31536000000L).DateDialog(date -> {
            Todate = Utils.DatetoStr(date,0);
            ToTv.setText(Utils.DatetoStr(date,1));
            cloading.show();
            getroomdetails();
        });

    }

    @SuppressLint("SetTextI18n")
    private void getroomdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", HnameID);
        params.put("roomtypeid", RTypeID);
        params.put("fromdate", Fromdate);
        params.put("todate", Todate);
        params.put("type", "nget");

        new VolleyRequester(this).ParamsRequest(1, ROOM_DET_URL, cloading, params, true, response -> {

            cloading.dismiss();

            if(response.equals("failure")){
                Toast.makeText(Hotel_Manage_Invtry_Act.this,"Availability Not Inserted",Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                JSONArray jarr = new JSONArray(response);
                JSONObject jobj = jarr.getJSONObject(0);

                OldAvail = jobj.getString("no_of_room_available");
                OldPrice = jobj.getString("sgl_price");

                TotalRoomTV.setText(jobj.getString("no_of_room"));
                AvlRoomEt.setText(OldAvail);
                RoomPriceEt.setText(OldPrice);

                StringBuilder sb = new StringBuilder();
                sb.append("");

                String ss = "";

                for(int i=0;i<jarr.length();i++){

                    JSONObject jbj = jarr.getJSONObject(i);

                    if(!jbj.getString("status").equals("")){
                        sb.append(jbj.getString("from_date")).append(" , ");
                        MessageEt.setText(jbj.getString("status"));
                        ss = jbj.getString("status");
                    }

                }

                if(!ss.equals("")){
                    NoteTv.setText(String.valueOf(sb));
                    Utils.setSingleBtnAlert(Hotel_Manage_Invtry_Act.this,
                            Utils.ChangeDateFormat(Fromdate,3)+ " to "+Utils.ChangeDateFormat(Todate,3)+ "\n"+ss
                            ,"Ok",false);
                }else{
                    NoteTv.setText("");
                    MessageEt.setText("");
                }

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(Hotel_Manage_Invtry_Act.this,"SomeThing Went Wrong\nPlease Try Again","Ok",true);
            }
        });

    }

    public void submitdata(View v){

        String NewPrice = RoomPriceEt.getText().toString();
        String NewAvail = AvlRoomEt.getText().toString();

        if(NewPrice.equals("")){
            Toast.makeText(this,"Enter Price",Toast.LENGTH_SHORT).show();
        }else if(NewAvail.equals("")){
            Toast.makeText(this,"Enter Availability",Toast.LENGTH_SHORT).show();
        }else {

            String msg = MessageEt.getText().toString();

            cloading.show();

            G46567 g5665 = (G46567) getApplicationContext();

            Map<String, String> params = new HashMap<>();
            params.put("hotelid", HnameID);
            params.put("roomtypeid", RTypeID);
            params.put("fromdate", Fromdate);
            params.put("todate", Todate);
            params.put("oldroomprice", OldPrice);
            params.put("oldroomcount", OldAvail);
            params.put("newroomprice", NewPrice);
            params.put("newroomcount", NewAvail);
            params.put("userid", g5665.getUserId());
            params.put("username", g5665.getUserName());
            params.put("type", "update");
            params.put("message", msg.equals("") ? "none" : msg);
            params.put("blockhotel", (BlockHotel) ? "true" : "false");

            new VolleyRequester(this).ParamsRequest(1, ROOM_DET_URL, cloading, params, true, response -> {
                cloading.dismiss();
                Utils.setSingleBtnAlert(Hotel_Manage_Invtry_Act.this, response, "Ok", true);
            });
        }
    }

}