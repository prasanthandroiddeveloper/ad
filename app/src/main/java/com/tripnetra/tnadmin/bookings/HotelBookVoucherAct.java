package com.tripnetra.tnadmin.bookings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Session;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.BOOK_VOU_URL;
import static com.tripnetra.tnadmin.utils.Config.CANCEL_URL;
import static com.tripnetra.tnadmin.utils.Config.PAYMT_URL;
import static com.tripnetra.tnadmin.utils.Config.RESEND_URL;

@SuppressLint("SetTextI18n")
public class HotelBookVoucherAct extends AppCompatActivity {

    TextView PNRStatTv,NoroomTv,NonightTv,TripPaybTv,supportTv,suptext,CancelTv,RejectTv;
    String Pnrno,Hotelid,Compamount="null",UserType,pnr_Id,PayType;
    CustomLoading cloading;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_book_voucher);

        assert getIntent().getExtras()!=null;
        Pnrno = getIntent().getExtras().getString("pnrnumber");

        session = new Session(this);
        PNRStatTv = findViewById(R.id.PnrStatTv);
        supportTv = findViewById(R.id.supportTv);
        suptext = findViewById(R.id.suptxt);
        CancelTv = findViewById(R.id.cancelTv);
        RejectTv = findViewById(R.id.rejectTv);

        UserType = session.getUType();

        if (UserType.equals("Admin")) {
            supportTv.setVisibility(View.VISIBLE);
            suptext.setVisibility(View.VISIBLE);
            findViewById(R.id.linearid).setVisibility(View.VISIBLE);
        } else {
            supportTv.setVisibility(View.INVISIBLE);
            suptext.setVisibility(View.INVISIBLE);
            findViewById(R.id.linearid).setVisibility(View.INVISIBLE);

        }

        PNRStatTv = findViewById(R.id.PnrStatTv);
        NoroomTv = findViewById(R.id.NoRoomsTv);
        NonightTv = findViewById(R.id.NoNightsTv);
        TripPaybTv = findViewById(R.id.trippayblTV);

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        if (cloading.getWindow()!=null) {
            cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        cloading.show();

        getpnrdetails();

    }

    private void getpnrdetails() {

        Map<String, String> params = new HashMap<>();
        params.put("pnrno", Pnrno);

        new VolleyRequester(this).ParamsRequest(1, BOOK_VOU_URL, cloading, params, true, response -> {
            if(cloading.isShowing()){cloading.dismiss();}
            if(response.equals("No Result")){
                Utils.setSingleBtnAlert(HotelBookVoucherAct.this,"No Data Found","Ok",true);
            }else{
                try {
                    JSONObject jobj = new JSONObject(response);

                    Date cindate,currentdate;
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    currentdate = cal.getTime();
                    pnr_Id = jobj.getString("id");
                    ((TextView)findViewById(R.id.BookDatetv)).setText(jobj.getString("book_date"));
                    ((TextView)findViewById(R.id.mobileTv)).setText(jobj.getString("contact_mobile_number"));
                    ((TextView)findViewById(R.id.emailTv)).setText(jobj.getString("contact_email"));
                    ((TextView)findViewById(R.id.supportTv)).setText(jobj.getString("supported_by"));
                    ((TextView)findViewById(R.id.PnrTv)).setText(Pnrno);
                    ((TextView)findViewById(R.id.HNameTV)).setText(jobj.getString("hotel_name"));
                    ((TextView)findViewById(R.id.HMobTv)).setText(jobj.getString("phone_number"));
                    ((TextView)findViewById(R.id.GnameTv)).setText(jobj.getString("contact_fname")+" "+jobj.getString("contact_lname"));
                    ((TextView)findViewById(R.id.RoomTypeTv)).setText(jobj.getString("booking_room_type"));
                    ((TextView)findViewById(R.id.StdCinTv)).setText(jobj.getString("exp_checkin_time"));
                    ((TextView)findViewById(R.id.NoguestTv)).setText(jobj.getString("guest_count"));
                    ((TextView)findViewById(R.id.EbedTv)).setText(jobj.getString("total_extra_beds"));
                    PayType = jobj.getString("payment_type");
                    ((TextView)findViewById(R.id.payTv)).setText(PayType);

                    NoroomTv.setText(jobj.getString("no_of_rooms"));
                    NonightTv.setText(jobj.getString("no_of_nights"));
                    Hotelid = jobj.getString("hotel_details_id");

                    ((TextView)findViewById(R.id.CInTv)).setText(Utils.ChangeDateFormat(jobj.getString("check_in_date"),1));
                    ((TextView)findViewById(R.id.COutTv)).setText(Utils.ChangeDateFormat(jobj.getString("check_out_date"),1));

                    cindate = Utils.StrtoDate(jobj.getString("check_in_date"));

                    if(jobj.getString("booking_status").contains("CONFIRM")){

                        CancelTv.setVisibility(View.GONE);RejectTv.setVisibility(View.GONE);
                        if(cindate!=null && cindate.compareTo(currentdate)>0){
                            if(!jobj.getString("cancel_request").equals("true") ){
                                CancelTv.setVisibility(View.VISIBLE);
                            }
                            if(jobj.getString("cancel_request").equals("true") && UserType.equals("Admin")){
                                RejectTv.setVisibility(View.VISIBLE);
                            }
                        }

                        findViewById(R.id.suppReTv).setVisibility(View.VISIBLE);
                        findViewById(R.id.custReTv).setVisibility(View.VISIBLE);
                        PNRStatTv.setTextColor(Color.parseColor("#049C72"));
                        PNRStatTv.setText("CONFIRMED");
                    }else if(jobj.getString("booking_status").contains("CANCEL")){
                        PNRStatTv.setTextColor(Color.parseColor("#CB0909"));
                        PNRStatTv.setText("CANCELLED");
                    }else if(jobj.getString("booking_status").contains("PROCESS")){
                        PNRStatTv.setTextColor(Color.GRAY);
                        PNRStatTv.setText("PROCESSING");
                    }

                    LinearLayout PriceNogstLayt = findViewById(R.id.pricelayt), PriceGstLayT = findViewById(R.id.pricegstlayt);

                    if(jobj.getString("total_gst").equals("")){
                        PriceGstLayT.setVisibility(View.GONE);
                        PriceNogstLayt.setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.priceTV)).setText(jobj.getString("total_sgl_price")+"/-");
                        TripPaybTv.setText(jobj.getString("total_sgl_price")+"/-");
                    }else {
                        PriceNogstLayt.setVisibility(View.GONE);
                        PriceGstLayT.setVisibility(View.VISIBLE);
                        DecimalFormat df = new DecimalFormat("0.0");
                        float price = Float.parseFloat(jobj.getString("total_sgl_price")),
                                commision = Float.parseFloat(jobj.getString("commission"));
                        float Hcomm = price * commision / 100;
                        float HcPrice = 0, HGst = 0;
                        ((TextView)findViewById(R.id.totalchargeTV)).setText(df.format(price) + " /-");

                        int dumm = Integer.parseInt(jobj.getString("no_of_nights")) * Integer.parseInt(jobj.getString("no_of_rooms"));
                        if (jobj.getString("bh_gstin").equals("NotAvailable") && !jobj.getString("hotel_type_id").equals("4") && (price/dumm  >= 1000)) {
                            findViewById(R.id.totpricegstifTv).setVisibility(View.VISIBLE);
                        }

                        if (commision != 0) {
                            findViewById(R.id.TcommLayt).setVisibility(View.VISIBLE);
                            ((TextView)findViewById(R.id.tripcomsnTV)).setText(df.format(Hcomm) + " /-");
                            if (!jobj.getString("bh_gstin").equals("NotAvailable")) {
                                findViewById(R.id.TgstLayt).setVisibility(View.VISIBLE);
                                HcPrice = Hcomm * 18 / 100;
                                ((TextView)findViewById(R.id.tripgstTV)).setText(df.format(HcPrice) + " /-");
                            }
                        }

                        if (!jobj.getString("bh_gstin").equals("NotAvailable")) {
                            findViewById(R.id.HgstLayt).setVisibility(View.VISIBLE);
                            HGst = Float.parseFloat(jobj.getString("total_gst"));
                            ((TextView)findViewById(R.id.hotgstTV)).setText(df.format(HGst) + " /-");
                        }

                        float dummTotal = price - Hcomm - HcPrice + HGst;
                        TripPaybTv.setText(df.format(dummTotal) + " /-");

                        if(!jobj.getString("complimentary_amount").equals("") &&!jobj.getString("complimentary_amount").equals("0")){
                            LinearLayout HCompLayt1 = findViewById(R.id.HCompLayt1),HCompLayt2 = findViewById(R.id.HCompLayt2);
                            HCompLayt1.setVisibility(View.VISIBLE);HCompLayt2.setVisibility(View.VISIBLE);
                            TextView totalTv = findViewById(R.id.CompTotTV),CompChargeTv = findViewById(R.id.CompRChargeTV);
                            float dcomp = Float.parseFloat(jobj.getString("complimentary_amount"));
                            totalTv.setText(df.format(dummTotal) + " /-");
                            CompChargeTv.setText(df.format(dcomp) + " /-");
                            Compamount = String.valueOf(dcomp);
                            TripPaybTv.setText(df.format(dummTotal-dcomp) + " /-");
                        }
                    }

                    if(jobj.getString("hotel_type_id").equals("4")){
                        ((TextView)findViewById(R.id.totpriceNameTv)).setText("Total Donation");
                        ((TextView)findViewById(R.id.PaybrkNameTv)).setText("Supplier Donation");
                    }else{
                        ((TextView)findViewById(R.id.totpriceNameTv)).setText("Total Price");
                        ((TextView)findViewById(R.id.PaybrkNameTv)).setText("Supplier Payment Breakup");
                    }
                    ((TextView)findViewById(R.id.totPriceTv)).setText("Rs : "+jobj.getString("TotalPrice"));

                    getpaydata();

                } catch (JSONException e) {
                    //e.printStackTrace();
                    Utils.setSingleBtnAlert(HotelBookVoucherAct.this,"SomeThing Went Wrong Please Try Again","Ok",true);
                }
            }
        });
    }

    private void getpaydata() {
        Map<String, String> params = new HashMap<>();
        params.put("type", PayType);
        params.put("id", Pnrno);

        new VolleyRequester(this).ParamsRequest(1, PAYMT_URL, null, params, true, response ->
                ((TextView)findViewById(R.id.paymsgTv)).setText(response));
    }

    public void custresendmthd(View v){showreturnalert(1,"Do You Want To \n\nResend Voucher To Customer");}

    public void suppresendmthd(View v){showreturnalert(2,"Do You Want To \n\nResend Voucher To Supplier");}

    public void cancelbookmthd(View v) {
        if (v.getId() == R.id.rejectTv) {
            showreturnalert(4, "Do You Want To Reject Booking");
        } else {
            showreturnalert(3, "Do You Want To Cancel Booking");
        }
    }

    private void resendvouchermthd(final String vouchertype) {

        Map<String, String> params = new HashMap<>();
        params.put("pnr_id", pnr_Id);
        params.put("type", vouchertype);
        params.put("bookingtype", "hotel");

        new VolleyRequester(this).ParamsRequest(1, RESEND_URL, cloading, params, false, response -> {
            if (cloading.isShowing()) {
                cloading.dismiss();
            }
            String ss = response.contains("success") ? "Booking Voucher Sent" : "Voucher Sending Failed";
            Utils.setSingleBtnAlert(HotelBookVoucherAct.this, ss, "Ok", false);
        });
    }

    public void postcancelmthd(final String canceltype){

        Map<String, String> params = new HashMap<>();
        params.put("booktype", "hotel");
        params.put("canceltype", canceltype);
        params.put("pnr_no", Pnrno);
        params.put("pnr_id", pnr_Id);
        params.put("hotel_id", Hotelid);
        params.put("no_of_rooms", NoroomTv.getText().toString());
        params.put("no_of_nights", NonightTv.getText().toString());
        params.put("amount", Compamount);

        params.put("usertype", (UserType.equals("Admin")) ? "ADMIN" : "EXECUTIVE");
        params.put("roleid", session.getUTypeId());
        params.put("username", session.getUName().replaceAll(" ","_"));

        new VolleyRequester(this).ParamsRequest(1, CANCEL_URL, cloading, params, false, response -> {
            if(cloading.isShowing()){cloading.dismiss();}

            if(response.toLowerCase().contains("success") && canceltype.equals("cancel")) {
                CancelTv.setVisibility(View.GONE);
                if (UserType.equals("Admin")) {
                    PNRStatTv.setText("CANCELLED");
                    PNRStatTv.setTextColor(Color.parseColor("#CB0909"));
                }
                Utils.setSingleBtnAlert(HotelBookVoucherAct.this, (UserType.equals("Admin")) ? "Booking Cancelled" : "Booking Cancellation Raised", "Ok", false);
            }else if(response.toLowerCase().contains("success") && canceltype.equals("reject")){
                RejectTv.setVisibility(View.GONE);
                Utils.setSingleBtnAlert(HotelBookVoucherAct.this, "Booking Cancellation Rejected", "Ok", false);
            }else {
                Utils.setSingleBtnAlert(HotelBookVoucherAct.this, "Booking Not Cancelled", "Ok", false);
            }
        });
    }

    public void showreturnalert(final int typ, String msg){
         new AlertDialog.Builder(this).setMessage(msg)
                .setPositiveButton("Yes", (dialog, id) -> {
                    cloading.show();
                    if(typ == 1){resendvouchermthd("customer");
                    }else if(typ == 2){resendvouchermthd("supplier");
                    }else if(typ == 3){postcancelmthd("cancel");
                    }else if(typ == 4){postcancelmthd("reject");}

                }).setNegativeButton("No", (dialog, id) -> {})
                 .setCancelable(true).create().show();
    }

}
