package com.tripnetra.tnadmin.bookings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Session;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.CANCEL_URL;
import static com.tripnetra.tnadmin.utils.Config.CAR_BOOK_URL;
import static com.tripnetra.tnadmin.utils.Config.PAYMT_URL;

@SuppressLint("SetTextI18n")
public class CarBookVoucherAct extends AppCompatActivity {

    TextView PnrstatTv,CancelTv,RejectTv;
    String PnrNo,Pnr_id,UserType,PayType;
    CustomLoading cloading;
    Session session;
    TextView suppTv,suppTvd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_book_voucher);

        assert getIntent().getExtras() != null;
        PnrNo = getIntent().getExtras().getString("pnrnumber");
        PnrstatTv = findViewById(R.id.PnrStatTv);
        suppTv= findViewById(R.id.suppTv);
        suppTvd= findViewById(R.id.suppTvd);
        CancelTv = findViewById(R.id.cancelTv);
        RejectTv = findViewById(R.id.rejectTv);

        session = new Session(this);

        UserType = session.getUType();

        if (UserType.equals("Admin")) {
            suppTv.setVisibility(View.VISIBLE);
            suppTvd.setVisibility(View.VISIBLE);
        } else {
            suppTv.setVisibility(View.INVISIBLE);
            suppTvd.setVisibility(View.INVISIBLE);
        }

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        if (cloading.getWindow()!=null) {
            cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        cloading.show();

        ((TextView)findViewById(R.id.PnrTv)).setText(PnrNo);

        getpnrdatamthd();

    }

    private void getpnrdatamthd() {
        Map<String, String> params = new HashMap<>();
        params.put("pnrno", PnrNo);

        new VolleyRequester(this).ParamsRequest(1,CAR_BOOK_URL , cloading, params, false, response -> {
            if(cloading.isShowing()){cloading.dismiss();}
            try {
                JSONObject jobj = new JSONObject(response);

                Pnr_id = jobj.getString("booking_car_id");

                ((TextView)findViewById(R.id.cnametv)).setText(jobj.getString("car_name"));
                ((TextView)findViewById(R.id.ctypetv)).setText(jobj.getString("car_type"));
                ((TextView)findViewById(R.id.ccomptv)).setText(jobj.getString("car_company_name"));
                ((TextView)findViewById(R.id.nameTv)).setText(jobj.getString("first_name")+" "+jobj.getString("last_name"));
                ((TextView)findViewById(R.id.mobileTv)).setText(jobj.getString("phone_no"));
                ((TextView)findViewById(R.id.emailTv)).setText(jobj.getString("email_id"));
                ((TextView)findViewById(R.id.pickplaceTv)).setText(jobj.getString("from_city"));
                ((TextView)findViewById(R.id.dropplaceTv)).setText(jobj.getString("to_city"));
                ((TextView)findViewById(R.id.picktimeTv)).setText(jobj.getString("pickup_time"));
                ((TextView)findViewById(R.id.totPriceTv)).setText("₹ "+jobj.getString("total_amount"));
                ((TextView)findViewById(R.id.BookDatetv)).setText(jobj.getString("created_on"));
                ((TextView)findViewById(R.id.suppTv)).setText(jobj.getString("supported_by"));

                PayType = jobj.getString("payment_type");
                ((TextView)findViewById(R.id.payTv)).setText(PayType);

                Date pickupdate ,currentdate;
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                currentdate = cal.getTime();

                ((TextView)findViewById(R.id.pickTv)).setText(Utils.ChangeDateFormat(jobj.getString("pickup_date"),1));

                pickupdate = Utils.StrtoDate(jobj.getString("pickup_date"));

                String ss = jobj.getString("booking_status");

                if(ss.contains("CONFIRM")) {

                    CancelTv.setVisibility(View.GONE);RejectTv.setVisibility(View.GONE);
                    if(pickupdate!=null && pickupdate.compareTo(currentdate)>0){
                        if(!jobj.getString("cancel_request").equals("true")){
                            CancelTv.setVisibility(View.VISIBLE);
                        }
                        if(jobj.getString("cancel_request").equals("true") && UserType.equals("Admin")){
                            RejectTv.setVisibility(View.VISIBLE);
                        }
                    }

                    PnrstatTv.setText("CONFIRMED");
                    PnrstatTv.setTextColor(Color.parseColor("#049C72"));
                }else if(ss.contains("CANCEL")) {
                    PnrstatTv.setText("CANCELLED");
                    PnrstatTv.setTextColor(Color.parseColor("#CB0909"));
                }else {
                    PnrstatTv.setText("PROCESS");
                    PnrstatTv.setTextColor(Color.GRAY);
                }

                getpaydata();

            } catch (JSONException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(CarBookVoucherAct.this,"Something Went Wrong Try Again","Ok",true);
            }
        });
    }

    private void getpaydata() {
        Map<String, String> params = new HashMap<>();
        params.put("type", PayType);
        params.put("id", PnrNo);

        new VolleyRequester(this).ParamsRequest(1, PAYMT_URL, null, params, true, response -> (
                (TextView)findViewById(R.id.paymsgTv)).setText(response));
    }

    public void cancelbookmthd(View v){
        if (v.getId() == R.id.rejectTv) {
            showreturnalert(4, "Do You Want To Reject Booking");
        } else {
            showreturnalert(3, "Do You Want To Cancel Booking");
        }

    }

    public void showreturnalert(final int typ, String msg){
        new AlertDialog.Builder(this).setMessage(msg)
                .setPositiveButton("Yes", (dialog, id) -> {
                    cloading.show();
                    if(typ == 3){postcancelmthd("cancel");
                    }else if(typ == 4){postcancelmthd("reject");}

                }).setNegativeButton("No", (dialog, id) -> {}).setCancelable(true).create().show();
    }

    public void postcancelmthd(final String canceltype) {

        Map<String, String> params = new HashMap<>();
        params.put("booktype", "car");
        params.put("canceltype", canceltype);
        params.put("pnr_no", PnrNo);
        params.put("pnr_id", Pnr_id);
        params.put("usertype", (UserType.equals("Admin")) ? "ADMIN" : "EXECUTIVE");
        params.put("roleid", session.getUTypeId());
        params.put("username", session.getUName().replaceAll(" ","_"));

        new VolleyRequester(this).ParamsRequest(1, CANCEL_URL, cloading, params, false, response -> {
            if(cloading.isShowing()){cloading.dismiss();}

            if(response.toLowerCase().contains("success") && canceltype.equals("cancel")) {
                CancelTv.setVisibility(View.GONE);
                if (UserType.equals("Admin")) {
                    PnrstatTv.setText("CANCELLED");
                    PnrstatTv.setTextColor(Color.parseColor("#CB0909"));
                }
                Utils.setSingleBtnAlert(CarBookVoucherAct.this, (UserType.equals("Admin")) ? "Booking Cancelled" : "Booking Cancellation Raised", "Ok", false);
            }else if(response.toLowerCase().contains("success") && canceltype.equals("reject")){
                RejectTv.setVisibility(View.GONE);
                Utils.setSingleBtnAlert(CarBookVoucherAct.this, "Booking Cancellation Rejected", "Ok", false);
            }else {
                Utils.setSingleBtnAlert(CarBookVoucherAct.this, "Booking Not Cancelled", "Ok", false);
            }
        });

    }

}