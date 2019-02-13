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
import java.util.Objects;

import static com.tripnetra.tnadmin.utils.Config.CANCEL_URL;
import static com.tripnetra.tnadmin.utils.Config.PAYMT_URL;
import static com.tripnetra.tnadmin.utils.Config.RESEND_URL;
import static com.tripnetra.tnadmin.utils.Config.TOUR_VOCH_URL;

@SuppressLint("SetTextI18n")
public class TourBookVoucherAct extends AppCompatActivity {

    TextView PnrstatTv,TraDateTv,TourTv,TourTvd,CancelTv,RejectTv;
    Session session;
    String PnrNo,PnrId,Type,UserType,PayType;
    CustomLoading cloading;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_book_voucher);

        TourTv = findViewById(R.id.TourTv);
        TourTvd = findViewById(R.id.TourTvd);
        CancelTv = findViewById(R.id.cancelTv);
        RejectTv = findViewById(R.id.rejectTv);

        assert getIntent().getExtras() != null;
        PnrNo = getIntent().getExtras().getString("pnrnumber");
        Type = getIntent().getExtras().getString("type");//darshan  tour

        session = new Session(this);

        UserType = session.getUType();

        if (UserType.equals("Admin")) {
            TourTv.setVisibility(View.VISIBLE);
            TourTvd.setVisibility(View.VISIBLE);
        } else {
            TourTv.setVisibility(View.INVISIBLE);
            TourTvd.setVisibility(View.INVISIBLE);
        }

        if(Type.equals("tour")){
            findViewById(R.id.linearid1).setVisibility(View.GONE);
            findViewById(R.id.countLayt).setVisibility(View.GONE);
        }else{
            findViewById(R.id.linearid1).setVisibility(View.VISIBLE);
            findViewById(R.id.balLayt).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.partnametv)).setText("Total Price");
        }

        PnrstatTv = findViewById(R.id.PnrStatTv);
        TraDateTv = findViewById(R.id.tdateTv);

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        Objects.requireNonNull(cloading.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cloading.show();

        ((TextView)findViewById(R.id.PnrTv)).setText(PnrNo);
        getpnrdatamthd();

    }

    public void custresendmthd(View v){showreturnalert(1,"Do You Want To\nResend Voucher To Customer");}

    private void getpnrdatamthd() {

        Map<String, String> params = new HashMap<>();
        params.put("pnrno", PnrNo);

        new VolleyRequester(this).ParamsRequest(1, TOUR_VOCH_URL, cloading, params, true, response -> {
            if(cloading.isShowing()){cloading.dismiss();}
            try {
                JSONObject jobj = new JSONObject(response);

                PnrId = jobj.getString("sightseen_booking_id");

                ((TextView)findViewById(R.id.BookDatetv)).setText(jobj.getString("created_on"));
                ((TextView)findViewById(R.id.tnameTv)).setText(jobj.getString("sightseen_name"));
                ((TextView)findViewById(R.id.nameTv)).setText(jobj.getString("firstname")+" "+jobj.getString("lastname"));
                ((TextView)findViewById(R.id.mobileTv)).setText(jobj.getString("phone"));
                ((TextView)findViewById(R.id.emailTv)).setText(jobj.getString("email_id"));
                ((TextView)findViewById(R.id.cityTv)).setText(jobj.getString("search_city"));
                ((TextView)findViewById(R.id.countTv)).setText(jobj.getString("adults")+" Adults & "+jobj.getString("childs")+" Childs");
                ((TextView)findViewById(R.id.partpriceTv)).setText("₹ "+jobj.getString("amount"));
                ((TextView)findViewById(R.id.checkin)).setText(jobj.getString("checkin_date"));
                ((TextView)findViewById(R.id.expchin)).setText(jobj.getString("expected_checkin"));
                ((TextView)findViewById(R.id.TourTv)).setText(jobj.getString("supported_by"));

                PayType = jobj.getString("payment_type");
                ((TextView)findViewById(R.id.payTv)).setText(PayType);

                float pamt=0,totamt=0;
                if(!jobj.getString("amount").equals("")) {
                    pamt = Float.parseFloat(jobj.getString("amount"));
                }
                if(!jobj.getString("sightseen_total_price").equals("")) {
                    totamt = Float.parseFloat(jobj.getString("sightseen_total_price"));
                }
                ((TextView)findViewById(R.id.balpriceTv)).setText("₹ "+(totamt-pamt));

                Date pickupdate,currentdate;
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                currentdate = cal.getTime();

                TraDateTv.setText(Utils.ChangeDateFormat(jobj.getString("travel_date"),1));
                pickupdate = Utils.StrtoDate(jobj.getString("travel_date"));

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
                    findViewById(R.id.custReTv).setVisibility(View.VISIBLE);
                }else if(ss.contains("CANCEL")) {
                    PnrstatTv.setText("CANCELLED");
                    PnrstatTv.setTextColor(Color.parseColor("#CB0909"));
                }else {
                    PnrstatTv.setText("PROCESS");
                    PnrstatTv.setTextColor(Color.GRAY);
                }

                getpaydata();

            } catch (JSONException|NumberFormatException e) {
                //e.printStackTrace();
                Utils.setSingleBtnAlert(TourBookVoucherAct.this,"Something Went Wrong Try Again","Ok",true);
            }
        });

    }

    private void getpaydata() {
        Map<String, String> params = new HashMap<>();
        params.put("type", PayType);
        params.put("id", PnrNo);

        new VolleyRequester(this).ParamsRequest(1, PAYMT_URL, null, params, true, response ->
                ((TextView)findViewById(R.id.paymsgTv)).setText(response));
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
                    if(typ == 1){resendvouchermthd();
                    }else if(typ == 3){postcancelmthd("cancel");
                    }else if(typ == 4){postcancelmthd("reject");}
                }).setNegativeButton("No", (dialog, id) -> {}).setCancelable(true).create().show();
    }

    private void resendvouchermthd() {

        Map<String, String> params = new HashMap<>();
        params.put("pnr_id", PnrNo);
        params.put("bookingtype", "tour");

        new VolleyRequester(this).ParamsRequest(1, RESEND_URL, cloading, params, false, response -> {
            if(cloading.isShowing()){cloading.dismiss();}

            String ss = response.contains("success") ? "Booking Voucher Sent" : "Voucher Sending Failed";
            Utils.setSingleBtnAlert(TourBookVoucherAct.this,ss,"Ok",false);
        });
    }

    public void postcancelmthd(final String canceltype){

        Map<String, String> params = new HashMap<>();
        params.put("booktype", "tour");
        params.put("canceltype", canceltype);
        params.put("pnr_no", PnrNo);
        params.put("pnr_id", PnrId);
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

                Utils.setSingleBtnAlert(TourBookVoucherAct.this, (UserType.equals("Admin")) ? "Booking Cancelled" : "Booking Cancellation Raised", "Ok", false);
            }else if(response.toLowerCase().contains("success") && canceltype.equals("reject")){
                RejectTv.setVisibility(View.GONE);
                Utils.setSingleBtnAlert(TourBookVoucherAct.this, "Booking Cancellation Rejected", "Ok", false);
            }else {
                Utils.setSingleBtnAlert(TourBookVoucherAct.this, "Booking Not Cancelled", "Ok", false);
            }
        });

    }

}