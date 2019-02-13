package com.tripnetra.tnadmin;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;

import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.Session;
import com.tripnetra.tnadmin.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.tripnetra.tnadmin.utils.Config.LOGIN_URL;

public class LoginMainActivity extends AppCompatActivity {

    TextInputLayout UserTil,PassTil;
    EditText UserET,PassET;
    Session session;
    CustomLoading cloading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        UserTil = findViewById(R.id.useridTIL);
        PassTil = findViewById(R.id.passwdTIL);
        UserET = findViewById(R.id.userET);
        PassET = findViewById(R.id.passET);

        cloading = new CustomLoading(this);
        cloading.setCancelable(false);
        Objects.requireNonNull(cloading.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        session = new Session(this);

        if(session.loggedin() && session.getUTypeId().length()>0){
            startActivity(new Intent(this,DashBoardActivity.class));
            finish();
        }

    }

    public void verifydata(View v){

        final String DUserid = UserET.getText().toString(),DPassword = PassET.getText().toString();

        UserTil.setErrorEnabled(false);PassTil.setErrorEnabled(false);

        if(DUserid.equals("")){
            UserTil.setError("Enter Userid");
        }else if(DPassword.equals("")){
            PassTil.setError("Enter Password");
        }else {
            cloading.show();

            Map<String, String> params = new HashMap<>();
            params.put("userid", DUserid);
            params.put("password", DPassword);

            new VolleyRequester(this).ParamsRequest(1, LOGIN_URL, cloading, params, false, response -> {
                cloading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);

                    if (!jObj.getBoolean("error")) {
                        String id = jObj.getJSONObject("userdata").getString("role_details_id");
                        String typ = (id.equals( "2")) ? "Admin" : "Editor";

                        session.setUserDet(jObj.getString("uid"),DUserid,typ,jObj.getJSONObject("userdata").getString("admin_name"),id);
                        session.setLoggedin(true);

                        startActivity(new Intent(LoginMainActivity.this,DashBoardActivity.class));
                    } else {
                        new AlertDialog.Builder(LoginMainActivity.this)
                                .setMessage(Html.fromHtml("<font color='#FF0000'>Invalid User-ID or Password</font>"))
                                .setCancelable(true).create().show();
                    }
                }catch (JSONException e) {
                    Utils.setSingleBtnAlert(LoginMainActivity.this,"Something Went Wrong Try Again","Ok",false);
                    //e.printStackTrace();
                }
            });
        }
    }

}