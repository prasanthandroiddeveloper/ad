package com.tripnetra.tnadmin.payments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tnadmin.CustomLoading;
import com.tripnetra.tnadmin.R;
import com.tripnetra.tnadmin.rest.VolleyCallback;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.tripnetra.tnadmin.utils.Config.PAYMENT_VOU_URL;

@SuppressLint("SetTextI18n")
public class PaymentVoucherActivity extends AppCompatActivity {

    TextView HnameTV,RoomTypeTV,PnrTv,PnrStatTV,NightsTV,EbedTV,CInTV,PriceTV,RefidTV,TransTypeTV,DateTimeTV,statusTV,RemarkTV;
    EditText RefidET,DateTimeET,statusET,RemarkEt;
    Button EditButton;
    RadioGroup TransRadGrp;
    RadioButton NeftRbtn,ImpsRbtn;
    String PnrNum,PayType="",Refid,Dates,PStat,Remar,PaymentID;
    Boolean editBool = true;
    CustomLoading cloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_voucher);

        HnameTV = findViewById(R.id.HNameTV);
        RoomTypeTV = findViewById(R.id.RoomTypeTv);
        PnrTv = findViewById(R.id.PnrTv);
        PnrStatTV = findViewById(R.id.PnrStatTv);
        NightsTV = findViewById(R.id.NightsTv);
        EbedTV = findViewById(R.id.EbedTv);
        CInTV = findViewById(R.id.CInTv);
        PriceTV = findViewById(R.id.PriceTv);
        RefidTV = findViewById(R.id.RefidTv);
        TransTypeTV = findViewById(R.id.TransTypeTv);
        DateTimeTV = findViewById(R.id.DateTimeTv);
        statusTV = findViewById(R.id.statusTv);
        RemarkTV = findViewById(R.id.RemarkTv);
        RefidET = findViewById(R.id.RefidEt);
        DateTimeET = findViewById(R.id.DatTimeEt);
        statusET = findViewById(R.id.statusET);
        RemarkEt = findViewById(R.id.RemarkEt);
        EditButton = findViewById(R.id.EditButton);
        TransRadGrp = findViewById(R.id.TransactRgroup);
        NeftRbtn = findViewById(R.id.RadioNFTS);
        ImpsRbtn = findViewById(R.id.RadioIMPS);

        if(getIntent().getExtras()!=null){
            PnrNum = getIntent().getExtras().getString("pnrnumber");

            cloading = new CustomLoading(this);
            cloading.setCancelable(false);
            assert cloading.getWindow() != null;
            cloading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            cloading.show();

            getpayment();

        }
    }

    public void getpayment() {

        Map<String, String> params = new HashMap<>();
        params.put("pnrno", PnrNum);
        params.put("type", "get");

        new VolleyRequester(this).ParamsRequest(1, PAYMENT_VOU_URL, cloading, params, true, response -> {
            cloading.dismiss();
            try{
                if(response.equals("No Result")){
                    Utils.setSingleBtnAlert(PaymentVoucherActivity.this,"No Data Found","Ok",true);
                }else{
                    JSONObject jobj = new JSONObject(response);
                    HnameTV.setText(jobj.getString("hotel_name"));
                    RoomTypeTV.setText(jobj.getString("booking_room_type"));
                    PnrTv.setText(jobj.getString("pnr_no"));
                    PnrStatTV.setText(jobj.getString("payment_status"));
                    NightsTV.setText(jobj.getString("no_of_nights"));
                    EbedTV.setText(jobj.getString("extra_bed_count"));
                    CInTV.setText(jobj.getString("check_in_date"));
                    PaymentID = jobj.getString("payment_id");

                    DecimalFormat df = new DecimalFormat("0.0");
                    float price = Float.parseFloat(jobj.getString("total_sgl_price")), commision;
                    if(jobj.getString("commission").equals("")){
                        commision = 0;
                    }else{
                        commision = Float.parseFloat(jobj.getString("commission"));
                    }
                    float Hcomm = price*commision/100;
                    float HcPrice=0,HGst=0;

                    if (!jobj.getString("bh_gstin").equals("NotAvailable")) {
                        if(commision!=0) {
                            HcPrice = Hcomm * 18 / 100;
                        }
                        if(jobj.getString("total_gst").equals("")){
                            HGst = 0;
                        }else{
                            HGst = Float.parseFloat(jobj.getString("total_gst"));
                        }

                    }

                    PriceTV.setText(df.format(price-Hcomm-HcPrice+HGst)+" /-");

                    Refid = jobj.getString("ref_id");
                    if(Refid.equals("null") || Refid.equals("")) {
                        Refid = "";
                        RefidTV.setText("-");
                        TransTypeTV.setText("-");
                        statusTV.setText("-");
                        RemarkTV.setText("-");
                        DateTimeTV.setText("-");
                    }else{
                        PayType = jobj.getString("neft_imps");
                        Dates = jobj.getString("date_and_time");
                        PStat =jobj.getString("status");
                        Remar=jobj.getString("Remarks");

                        RefidTV.setText(Refid);
                        TransTypeTV.setText(PayType);
                        statusTV.setText(PStat);
                        statusTV.setTextColor(Color.GREEN);
                        RemarkTV.setText(Remar);
                        DateTimeTV.setText(Dates);
                    }
                }
            }catch (JSONException e){
                //e.printStackTrace();
                Utils.setSingleBtnAlert(PaymentVoucherActivity.this,"Something Went Wrong Try Again","Ok",true);
            }
        });
    }

    public void saveactions(View v){

        if(editBool){
            editBool= false;
            EditButton.setText("SAVE");
            EditButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            RefidTV.setVisibility(View.GONE);
            TransTypeTV.setVisibility(View.GONE);
            statusTV.setVisibility(View.GONE);
            RemarkTV.setVisibility(View.GONE);
            DateTimeTV.setVisibility(View.GONE);
            RefidET.setVisibility(View.VISIBLE);
            DateTimeET.setVisibility(View.VISIBLE);
            statusET.setVisibility(View.VISIBLE);
            RemarkEt.setVisibility(View.VISIBLE);
            TransRadGrp.setVisibility(View.VISIBLE);

            if(PayType.equals("NEFT")){
                NeftRbtn.setChecked(true);
            }else{
                ImpsRbtn.setChecked(true);
            }

            RefidET.setText(Refid);
            DateTimeET.setText(Dates);
            statusET.setText(PStat);
            RemarkEt.setText(Remar);

            TransRadGrp.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.RadioNFTS) {
                    PayType="NEFT";
                } else if (checkedId == R.id.RadioIMPS) {
                    PayType="IMPS";
                }
            });
        }else {
            final String Reffid=RefidET.getText().toString(),PayStatus = statusET.getText().toString(),PRemarks = RemarkEt.getText().toString(),
                    Datetime=DateTimeET.getText().toString() ;

            if(Reffid.equals("")){
                Toast.makeText(this,"Please Enter Reference Id",Toast.LENGTH_SHORT).show();
            }else if(PayStatus.equals("")){
                Toast.makeText(this,"Please Enter Payment Status",Toast.LENGTH_SHORT).show();
            }else if(Datetime.equals("")){
                Toast.makeText(this,"Please Enter Date And Time",Toast.LENGTH_SHORT).show();
            }else if(PayType.equals("")){
                Toast.makeText(this,"Please Enter Payment Type",Toast.LENGTH_SHORT).show();
            }else {
                editBool = true;
                EditButton.setEnabled(false);
                cloading.show();

                Map<String, String> params = new HashMap<>();
                params.put("pnrno", PnrNum);
                params.put("type", "upload");
                params.put("paymentid", PaymentID);
                params.put("refid", Reffid);
                params.put("paystatus", PayStatus);
                params.put("remarks", PRemarks);
                params.put("date", Datetime);
                params.put("paytype", PayType);

                new VolleyRequester(this).ParamsRequest(1, PAYMENT_VOU_URL, cloading, params, true, new VolleyCallback() {
                    @Override
                    public void getResponse(String response) {
                        cloading.dismiss();
                        Utils.setSingleBtnAlert(PaymentVoucherActivity.this,response,"Ok",false);

                        RefidTV.setVisibility(View.VISIBLE);
                        TransTypeTV.setVisibility(View.VISIBLE);
                        statusTV.setVisibility(View.VISIBLE);
                        RemarkTV.setVisibility(View.VISIBLE);
                        DateTimeTV.setVisibility(View.VISIBLE);
                        RefidET.setVisibility(View.GONE);
                        DateTimeET.setVisibility(View.GONE);
                        statusET.setVisibility(View.GONE);
                        RemarkEt.setVisibility(View.GONE);
                        TransRadGrp.setVisibility(View.GONE);

                        RefidTV.setText(Reffid);
                        TransTypeTV.setText(PayType);
                        statusTV.setText(PayStatus);
                        RemarkTV.setText(PRemarks);
                        DateTimeTV.setText(Datetime);
                        EditButton.setText("EDIT");
                    }
                });
            }
        }
    }

}