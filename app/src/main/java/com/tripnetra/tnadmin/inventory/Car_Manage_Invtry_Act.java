package com.tripnetra.tnadmin.inventory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
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

import static com.tripnetra.tnadmin.utils.Config.CAR_DETAILS_URL;

public class Car_Manage_Invtry_Act extends AppCompatActivity {

    String PnameID,PName,Car_id,O_A_price,O_C_Price,O_Capacity,Fromdate,Todate,Avalbty;
    EditText CnameEt, CActEt,CcapET, CPartET,AvailET;
    TextView FromTv,ToTv;
    CustomLoading cloading;
    int cur=0;long ftdate = System.currentTimeMillis() - 1000;
    G46567 g46567;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_mnage_invtry);

        assert getIntent().getExtras() != null;
        PnameID = getIntent().getExtras().getString("sightseen_id");
        PName = getIntent().getExtras().getString("sightseen_name");
        Car_id = getIntent().getExtras().getString("car_id");
        O_A_price = getIntent().getExtras().getString("actual_price");
        O_C_Price = getIntent().getExtras().getString("car_price");
        O_Capacity = getIntent().getExtras().getString("max_capacity");

        g46567 = (G46567) getApplicationContext();

        CnameEt = findViewById(R.id.NameEt);
        CActEt = findViewById(R.id.ActEt);
        CcapET = findViewById(R.id.CapactiyET);
        CPartET = findViewById(R.id.PartEt);
        ((TextView) findViewById(R.id.packtv)).setText(PName);
        FromTv = findViewById(R.id.fromTV);
        ToTv = findViewById(R.id.toTV);
        AvailET = findViewById(R.id.availEt);

        CnameEt.setText(getIntent().getExtras().getString("car_name"));
        CActEt.setText(O_A_price);
        CPartET.setText(O_C_Price);
        CcapET.setText(O_Capacity);

        if(!g46567.getUserType().toLowerCase().contains("admin")){
            CnameEt.setEnabled(false);CcapET.setEnabled(false);AvailET.setEnabled(false);
            findViewById(R.id.rgrp).setVisibility(View.GONE);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Fromdate = Utils.DatetoStr(cal.getTime(),0);
        FromTv.setText(Utils.DatetoStr(cal.getTime(),1));
        cal.add(Calendar.DATE, 1);
        Todate = Utils.DatetoStr(cal.getTime(),0);
        ToTv.setText(Utils.DatetoStr(cal.getTime(),1));

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        assert cloading.getWindow()!=null;
        cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getinvtry_details();
    }

    public void Submit(View view) {

        String carname = CnameEt.getText().toString();
        String new_act_p = CActEt.getText().toString();
        String new_cap = CcapET.getText().toString();
        String new_part_p = CPartET.getText().toString();

        if(carname.equals("")){
            Toast.makeText(this,"Enter Car Name",Toast.LENGTH_SHORT).show();
        }else if(new_act_p.equals("")){
            Toast.makeText(this,"Enter Adult Price",Toast.LENGTH_SHORT).show();
        }else if(new_cap.equals("")){
            Toast.makeText(this,"Enter Capacity",Toast.LENGTH_SHORT).show();
        }else if(new_part_p.equals("")){
            Toast.makeText(this,"Enter Child Price",Toast.LENGTH_SHORT).show();
        }else {
            cloading.show();

            Map<String, String> params = new HashMap<>();

            Avalbty = AvailET.getText().toString();

            params.put("sightseen_id", PnameID);
            params.put("car_id", Car_id);
            params.put("car_name", carname);
            params.put("old_a_price", O_A_price);
            params.put("new_a_price", new_act_p);
            params.put("old_c_price", O_C_Price);
            params.put("new_c_price", new_part_p);
            params.put("old_capacity", O_Capacity);
            params.put("new_capacity", new_cap);
            params.put("availability", Avalbty);
            params.put("fromdate", Fromdate);
            params.put("todate", Todate);
            params.put("admin_id", g46567.getUserId());
            params.put("admin_name", g46567.getUserName());
            params.put("type", "newupdate");

            RadioGroup rgrp = findViewById(R.id.rgrp);
            if(rgrp.getCheckedRadioButtonId() == R.id.insert){
                params.put("category", "insert");
            }else{
                params.put("category", "update");
            }

            new VolleyRequester(this).ParamsRequest(1, CAR_DETAILS_URL, cloading, params, false, response -> {
                cloading.dismiss();
                new AlertDialog.Builder(Car_Manage_Invtry_Act.this)
                        .setMessage(response)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            Intent intent = new Intent(Car_Manage_Invtry_Act.this, Car_Invtry_Details_Act.class);
                            intent.putExtra("hname", PName);
                            intent.putExtra("hnameid", PnameID);

                            startActivity(intent);
                            dialog.dismiss();
                        }).setCancelable(false).show();
            });
        }
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
                ftdate = c1.getTimeInMillis() - 1000;

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
                getinvtry_details();
            }

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(ftdate);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000+31536000000L);
    }

    private void getinvtry_details() {

        cloading.show();

        Map<String, String> params = new HashMap<>();
        params.put("sightseen_id", PnameID);
        params.put("car_id", Car_id);
        params.put("from_date", Fromdate);
        params.put("type", "get");

        new VolleyRequester(this).ParamsRequest(1, CAR_DETAILS_URL, cloading, params, false, response -> {
            cloading.dismiss();

            if(response.equals("") || response.equals("null")){
                Toast.makeText(Car_Manage_Invtry_Act.this,"Avalability Not Avaialble",Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jobj = new JSONObject(response);

                O_A_price = jobj.getString("actual_price");
                O_C_Price = jobj.getString("car_price");
                O_Capacity = jobj.getString("max_capacity");
                Avalbty = jobj.getString("da_count");

                CActEt.setText(O_A_price);
                CPartET.setText(O_C_Price);
                CcapET.setText(O_Capacity);
                AvailET.setText(Avalbty);

            } catch (JSONException e) {
                //e.printStackTrace();
                Toast.makeText(Car_Manage_Invtry_Act.this,"Technical error",Toast.LENGTH_SHORT).show();
            }
        });

    }

}