package com.tripnetra.tnadmin.logs;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.utils.Date_Picker_Dialog;
import com.tripnetra.tnadmin.utils.G46567;
import com.tripnetra.tnadmin.utils.Utils1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;

public class Link_InfoActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    String HttpUrl = "https://tripnetra.com/excursion/pre_booking";
    String  adult_count, child_count, room_price,
            room_type, hotel_type, car_type, max_capacity, carprc,
            total_price, grand_total_price, service_tax,
            city1, desc1,si,itn,cSId4,descrip,acp1,pprc1,html,itenc,vehicle;

    List<String> itlist, bilist, plist,cnamesList,capcList,cplsit,cpricelsit,acplist,tlist;
    String bndl, itneray, price, FromDate, ToDate, DispCin, DispCout, dayscount, test, enc,UserName,cndl,cityname,cSName, cSId,bndlc,cSId2,cSId3,stax,tax;

    long minDate = System.currentTimeMillis();
    Spinner csearchtv;
    Integer CId;
    FrameLayout frameLayout;

    private RichEditor mEditor;
    EditText mPreview;


    EditText adultcunt,childcunt,vname,ssid,city,cartype,capacity,carprice,htltype,roomtype,roomprice,totalp,gtprc,servicetax,desc,addid,
            darshanPrice,it,sik,actual_price,partial_price,vchname;
    TextView responsecheck1,CindateTv,CinmonthTv,CindayTv,nodays,ddateTv,dmonthTv,ddayTv,cdate,ac,pc,Ttv;
    Button Insert,vhtv,shares;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link__info);


        adultcunt = findViewById(R.id.acunt);
        childcunt = findViewById(R.id.ccunt);
        city = findViewById(R.id.city);
        cartype = findViewById(R.id.cartype);
        capacity = findViewById(R.id.capacity);
        carprice = findViewById(R.id.carprice);
        htltype = findViewById(R.id.htltype);
        roomtype = findViewById(R.id.roomtype);
        roomprice = findViewById(R.id.roomprice);
        totalp = findViewById(R.id.totalp);
        gtprc = findViewById(R.id.gtprc);
        servicetax = findViewById(R.id.servicetax);
        desc = findViewById(R.id.desc);
      //  it = findViewById(R.id.it);
        csearchtv = findViewById(R.id.csearchtv);
        actual_price = findViewById(R.id.acp);
        partial_price = findViewById(R.id.pp);
        frameLayout = findViewById(R.id.frmlyt);
        cdate = findViewById(R.id.cdateTV);
        ac=findViewById(R.id.ac1);
        pc=findViewById(R.id.pc1);
        vchname=findViewById(R.id.vchadd);
        vhtv=findViewById(R.id.vtv);
        shares=findViewById(R.id.share);
        actual_price.setEnabled(false);
        partial_price.setEnabled(false);


        responsecheck1 = findViewById(R.id.responsecheck);
          CindateTv = findViewById(R.id.cindatetv);
        CinmonthTv = findViewById(R.id.cinmontv);
        CindayTv = findViewById(R.id.cindaytv);
       /* nodays = findViewById(R.id.noofdays);*/
          ddateTv = findViewById(R.id.darshandatetv);
        dmonthTv = findViewById(R.id.darshanmontv);
        ddayTv = findViewById(R.id.darshandaytv);
        Ttv = findViewById(R.id.tTV);


        Insert = findViewById(R.id.ButtonInsert);

        UserName = ((G46567) getApplicationContext()).getUserName();

        Log.i("UserName", UserName);

        gethnamesdet2();
        gethnamesdet3();
        Bundle bb = getIntent().getExtras();
        assert bb != null;
        bndl = bb.getString("sightseen_id");
        bndlc = bb.getString("sightseen_id");
       // cndl = bb.getString("sightseen_id");
        cityname = bb.getString("city_name");
        descrip = bb.getString("sightseen_description");
        stax = bb.getString("service_tax");

       /* servicetax.setText(stax);*/

        Log.i("b", bndl);
//        Log.i("b", cndl);
        FromDate = Utils1.DatetoStr(System.currentTimeMillis(), 0);
        ToDate = Utils1.DatetoStr(System.currentTimeMillis() + 86400000L, 0);
       // setdiffrncs();
        setdate();
        requestQueue = Volley.newRequestQueue(Link_InfoActivity.this);
        enc = Base64.encodeToString(bndl.getBytes(), Base64.DEFAULT);
        Log.i("enc", enc);

        CId = bb.getInt("sightseen_category_id");
        Log.i("ci", String.valueOf(CId));
        if (CId.equals(7) || CId!=7) {
            city.setVisibility(View.GONE);
            desc.setVisibility(View.GONE);
            capacity.setVisibility(View.GONE);
            cartype.setVisibility(View.GONE);
            htltype.setVisibility(View.GONE);
            roomprice.setVisibility(View.GONE);
            roomtype.setVisibility(View.GONE);
            carprice.setVisibility(View.GONE);
            gtprc.setVisibility(View.GONE);
        }

        if (CId!=7){

            frameLayout.setVisibility(View.GONE);
        }

        if(CId.equals(7)||CId.equals(8)){
            totalp.setVisibility(View.VISIBLE);
        }else{
            totalp.setVisibility(View.GONE);

        }

       /* if(CId.equals(7)||CId.equals(8)){
            actual_price.setVisibility(View.GONE);
            partial_price.setVisibility(View.GONE);
            ac.setVisibility(View.GONE);
            pc.setVisibility(View.GONE);
        }*/


       /* if(CId.equals(8)){
            actual_price.setVisibility(View.GONE);
            partial_price.setVisibility(View.GONE);
            ac.setVisibility(View.GONE);
            pc.setVisibility(View.GONE);
        }
*/

        if(CId.equals(4)){
            actual_price.setEnabled(true);
            partial_price.setEnabled(true);
        } else{
            actual_price.setEnabled(false);
            partial_price.setEnabled(false);
        }


        if(CId.equals(7) || CId.equals(8)){

            ac.setText("Adult Price");
            pc.setText("Child Price");
        } else{
            ac.setText("Actual Price");
            pc.setText("Partial Price");
        }




        if(CId!=7){
            cdate.setText("Tour Date");
        }else {
            cdate.setText("Dharshan Date");
        }

        if(CId.equals(4)){

            Ttv.setVisibility(View.GONE);
        }
        vehicle();

        mEditor = findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);

        mPreview = findViewById(R.id.preview);
        mPreview.setVisibility(View.GONE);
        mEditor.setOnTextChangeListener(text ->


                mPreview.setText(text));

        findViewById(R.id.action_undo).setOnClickListener(v -> mEditor.undo());


        findViewById(R.id.action_bold).setOnClickListener(v -> mEditor.setBold());

        findViewById(R.id.action_italic).setOnClickListener(v -> mEditor.setItalic());

        findViewById(R.id.action_subscript).setOnClickListener(v -> mEditor.setSubscript());

        findViewById(R.id.action_superscript).setOnClickListener(v -> mEditor.setSuperscript());

        findViewById(R.id.action_strikethrough).setOnClickListener(v -> mEditor.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(v -> mEditor.setUnderline());

        findViewById(R.id.action_heading1).setOnClickListener(v -> mEditor.setHeading(1));

        findViewById(R.id.action_heading2).setOnClickListener(v -> mEditor.setHeading(2));

        findViewById(R.id.action_heading3).setOnClickListener(v -> mEditor.setHeading(3));

        findViewById(R.id.action_heading4).setOnClickListener(v -> mEditor.setHeading(4));

        findViewById(R.id.action_heading5).setOnClickListener(v -> mEditor.setHeading(5));

        findViewById(R.id.action_heading6).setOnClickListener(v -> mEditor.setHeading(6));

        findViewById(R.id.action_insert_bullets).setOnClickListener(v -> mEditor.setBullets());

        findViewById(R.id.action_insert_numbers).setOnClickListener(v -> mEditor.setNumbers());

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.BLACK);
                isChanged = !isChanged;
            }
        });

    }


    public void stdate(View v) {

        new Date_Picker_Dialog(this, minDate, System.currentTimeMillis() - 1000 + 31536000000L).DateDialog(date -> {

            FromDate = date;

            Calendar newcal = Calendar.getInstance();
            newcal.setTime(Utils1.StrtoDate(0, date));
            newcal.add(Calendar.DATE, 1);

            ToDate = Utils1.DatetoStr(newcal.getTime(), 0);
            minDate = newcal.getTimeInMillis();
            //setdiffrncs();

            etdate(v);

        });
    }

    public void setdate() {

        Date fdate = Utils1.StrtoDate(0, FromDate);
        Date ldate = Utils1.StrtoDate(0, ToDate);

        CindayTv.setText(Utils1.DatetoStr(fdate, 8));
        ddayTv.setText(Utils1.DatetoStr(ldate, 8));
        CinmonthTv.setText(Utils1.DatetoStr(fdate, 7));
        dmonthTv.setText(Utils1.DatetoStr(ldate, 7));
        CindateTv.setText(Utils1.DatetoStr(fdate, 6));
        ddateTv.setText(Utils1.DatetoStr(ldate, 6));

        DispCin = (Utils1.DatetoStr(fdate, 6) + " " + Utils1.DatetoStr(fdate, 7));
        DispCout = (Utils1.DatetoStr(ldate, 6) + " " + Utils1.DatetoStr(ldate, 7));
    }

    public void etdate(View v) {
        new Date_Picker_Dialog(this, minDate, System.currentTimeMillis() - 1000 + 31536000000L).DateDialog(date -> {
            ToDate = date;
            setdate();
            //setdiffrncs();
        });
    }


    private void gethnamesdet2() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://tripnetra.com/cpanel_admin/calendar/get_darshan_packages/6865446727eae9cbd513", response -> {
            itlist = new ArrayList<>();
            bilist = new ArrayList<>();
            plist = new ArrayList<>();
            tlist = new ArrayList<>();

            try {
                JSONArray jarr = new JSONArray(response);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);

                    if (json.getString("sightseen_id").equals(bndl)) {

                        itlist.add(json.getString("sightseen_itinerary"));
                        plist.add(json.getString("sightseen_price"));
                        tlist.add(json.getString("service_tax"));

                        bilist.add(bndl);
                    }
                }
                itneray = String.valueOf(itlist).replace("[","").replace("]","");
               // itneray= Html.fromHtml(String.valueOf(itlist)).toString();
                price = String.valueOf(plist);
                roomprice.setText(price);
                tax = String.valueOf(tlist).replace("[","").replace("]","");
                servicetax.setText(tax);
                mPreview.setText(itneray);

               // it.setText(itneray);
                mEditor.setHtml(itneray);

                Log.i("il", String.valueOf(itlist));
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }, error -> {
            Toast.makeText(getApplicationContext(),"something went wrong Try again",Toast.LENGTH_SHORT).show();
        }) ;

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void gethnamesdet3() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://tripnetra.com/excursion/get_cars",response -> {

            cnamesList = new ArrayList<>();
            capcList = new ArrayList<>();
            cplsit = new ArrayList<>();
            cpricelsit = new ArrayList<>();
            acplist = new ArrayList<>();

            try {
                JSONArray jarr = new JSONArray(response);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject json = jarr.getJSONObject(i);

                    cnamesList.add(json.getString("car_name"));
                    capcList.add(json.getString("max_capacity"));
                    cplsit.add(json.getString("car_id"));
                    cpricelsit.add(json.getString("car_price"));
                    acplist.add(json.getString("actual_price"));



                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Link_InfoActivity.this, R.layout.textview_layout, R.id.txt, cnamesList);
                csearchtv.setAdapter(dataAdapter);
                csearchtv.setPrompt("Select package");
                csearchtv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View view, int position, long row_id) {
                        cSName = String.valueOf(arg0.getItemAtPosition(position));
                        int iii = cnamesList.indexOf(cSName);
                        cSId = capcList.get(iii);
                        cSId2 = cplsit.get(iii);
                        cSId3 = cpricelsit.get(iii);
                        cSId4 = acplist.get(iii);

                        Log.i("carname", String.valueOf(cSName));
                        Log.i("capacity", String.valueOf(cSId));
                        Log.i("id", String.valueOf(cSId2));
                        Log.i("cprc", String.valueOf(cSId3));
                        Log.i("acprc", String.valueOf(cSId4));
                        actual_price.setText(cSId4);
                        partial_price.setText(cSId3);

                        totalp.setText(String.valueOf(Integer.valueOf(cSId3)+(Integer.valueOf(cSId4))));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }, error -> {
           // Toasty.error(this, "something went wrong Try again", 5);
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("sightseen_id", bndlc);
                return params;

            }
        };

        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void GetValueFromEditText() {

        adult_count = adultcunt.getText().toString().trim();
        child_count = childcunt.getText().toString().trim();
        car_type = cartype.getText().toString().trim();
        max_capacity = capacity.getText().toString().trim();
        carprc = carprice.getText().toString().trim();
        hotel_type = htltype.getText().toString().trim();
        room_type = roomtype.getText().toString().trim();
        room_price = roomprice.getText().toString().trim();
        total_price = totalp.getText().toString().trim();
        grand_total_price = gtprc.getText().toString().trim();
        service_tax = servicetax.getText().toString().trim();
        city1 = city.getText().toString().trim();
        desc1 = desc.getText().toString().trim();
        acp1 = actual_price.getText().toString().trim();
        pprc1 = partial_price.getText().toString().trim();
        //itn = it.getText().toString().trim();
        html = mPreview.getText().toString().trim();
        itenc = Base64.encodeToString(html.getBytes(), Base64.DEFAULT);
        vehicle=vchname.getText().toString().trim();



    }

    public void vehicle() {
    vhtv.setOnClickListener(view1 -> {
        vchname.setVisibility(View.VISIBLE);
         });}





    public void insert(View view) {

        GetValueFromEditText();
       //vehicle();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                ServerResponse -> {
                    test = ServerResponse;
                    responsecheck1.setText(test);

                    Toast.makeText(Link_InfoActivity.this, ServerResponse, Toast.LENGTH_LONG).show();
                    Log.i("resp", ServerResponse);

                },
                volleyError -> {
                    Toast.makeText(Link_InfoActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    Log.i("error", volleyError.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                if(CId.equals(7)){

                    params.put("sup_price", String.valueOf(Integer.valueOf(adult_count)*Integer.valueOf(cSId4)));
                    params.put("total_price",String.valueOf(total_price));
                    params.put("grand_total_price",String.valueOf(total_price));
                    params.put("car_price",String.valueOf(total_price));
                    params.put("checkin_date", FromDate);
                    params.put("date", ToDate);

                }
                else if (CId.equals(8)){
                     params.put("sup_price", "");
                     params.put("grand_total_price",String.valueOf(acp1));
                     params.put("total_price",String.valueOf(total_price));
                     params.put("car_price",String.valueOf(pprc1));
                     params.put("date", ToDate);
                     params.put("checkin_date","");
                }

                 else{
                    params.put("sup_price", "");
                    params.put("grand_total_price",String.valueOf(acp1));
                    params.put("total_price",String.valueOf(pprc1));
                    params.put("car_price",String.valueOf(pprc1));
                    params.put("date", ToDate);
                    params.put("checkin_date","");
                }


                if(!vehicle.equals("")){
                    params.put("vehicle_name", vehicle);
                }else {
                    params.put("vehicle_name", cSName);
                }

                /*params.put("checkin_date", FromDate);*/
                params.put("adult_count", String.valueOf(adult_count));
                params.put("child_count", String.valueOf(child_count));
                params.put("room_price", "");
                params.put("room_type", "");
                params.put("hotel_type", "");
                params.put("ssid", String.valueOf(enc));
                params.put("sik","");
                params.put("city", cityname);
                /*params.put("date", ToDate);*/
                params.put("car_type", String.valueOf(cSId2));
               /* params.put("car_price", String.valueOf(total_price));*/
                params.put("capacity", String.valueOf(cSId));
               /* params.put("total_price", String.valueOf(total_price));*/
               /* params.put("grand_total_price", String.valueOf(total_price));*/
                params.put("service_tax", String.valueOf(service_tax));
                params.put("desc", descrip);
                params.put("itinerary", String.valueOf(itenc));
                params.put("supported_by", UserName);
                Log.i("params", String.valueOf(params));



                return params;

            }

        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(Link_InfoActivity.this);
        requestQueue.add(stringRequest);
    }

    public void response(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("urlink", test);
        Objects.requireNonNull(clipboard).setPrimaryClip(clip);
        Toast.makeText(this, "text  Copied", Toast.LENGTH_SHORT).show();

    }


    public void sharesoc(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, test);
        Log.i("soc",test);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));


    }
}

