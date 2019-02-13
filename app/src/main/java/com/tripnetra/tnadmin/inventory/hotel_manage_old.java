/*
package com.tripnetra.tnadmin.inventory;



import android.app.DatePickerDialog;
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
import com.tripnetra.tnadmin.utils.G46567;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.ROOM_DET_URL;

public class hotel_manage_old extends AppCompatActivity {

    String HnameID,RTypeID,Fromdate,Todate, OldAvail, OldPrice;
    EditText AvlRoomEt,RoomPriceEt;
    Switch BlockSwitch;
    TextView FromTv,ToTv,TotalRoomTV;
    int cur=0;long ftdate;
    Boolean BlockHotel=false;
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

        ((TextView)findViewById(R.id.RNameTV)).setText(getIntent().getExtras().getString("roomname"));
        TotalRoomTV.setText(getIntent().getExtras().getString("roomtotal"));

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Fromdate = Utils.DatetoStr(cal.getTime(),0);
        FromTv.setText(Utils.DatetoStr(cal.getTime(),1));
        cal.add(Calendar.DATE, 1);
        Todate = Utils.DatetoStr(cal.getTime(),0);
        ToTv.setText(Utils.DatetoStr(cal.getTime(),1));

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
        cur = 1;
        ftdate = System.currentTimeMillis() - 1000;
        datedialog();
    }

    public void todatemthd(View v){
        if(cur!=0) {
            cur = 2;
            datedialog();
        }else{
            ftdate = System.currentTimeMillis() - 1000+86400000L;
            cur = 2;
            datedialog();
        }
    }

    private void datedialog() {

        final Calendar cal = Calendar.getInstance();

        final Calendar c1 = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, day) -> {
            c1.set(year,month,day);

            if(cur == 1) {
                Fromdate = Utils.DatetoStr(c1.getTime(),0);
                ftdate = (c1.getTimeInMillis() - 1000)+86400000L;

                Calendar ncal = Calendar.getInstance();
                ncal.setTime(c1.getTime());
                ncal.add(Calendar.DATE, 1);
                Todate = Utils.DatetoStr(ncal.getTime(),0);
                FromTv.setText(Utils.DatetoStr(c1.getTime(),1));
                ToTv.setText(Utils.DatetoStr(ncal.getTime(),1));

                todatemthd(view);

            }else if (cur == 2){
                Todate = Utils.DatetoStr(c1.getTime(),0);
                ToTv.setText(Utils.DatetoStr(c1.getTime(),1));
                cloading.show();
                getroomdetails();
            }

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(ftdate);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000+31536000000L);
    }

    private void getroomdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("hotelid", HnameID);
        params.put("roomtypeid", RTypeID);
        params.put("fromdate", Fromdate);
        params.put("type", "get");

        new VolleyRequester(this).ParamsRequest(1, ROOM_DET_URL, cloading, params, true, response -> {
            cloading.dismiss();
            try {
                JSONObject jobj = new JSONObject(response);

                OldAvail = jobj.getString("no_of_room_available");
                OldPrice = jobj.getString("sgl_price");

                TotalRoomTV.setText(jobj.getString("no_of_room"));
                AvlRoomEt.setText(OldAvail);
                RoomPriceEt.setText(OldPrice);

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(hotel_manage_old.this,"SomeThing Went Wrong\nPlease Try Again","Ok",true);
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
            params.put("blockhotel", (BlockHotel) ? "true" : "false");


            new VolleyRequester(this).ParamsRequest(1, ROOM_DET_URL, cloading, params, true, response -> {
                cloading.dismiss();
                Utils.setSingleBtnAlert(hotel_manage_old.this, response, "Ok", true);
            });
        }
    }

}

*/
