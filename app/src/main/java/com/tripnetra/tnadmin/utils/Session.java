package com.tripnetra.tnadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public Session(Context ctx){
        prefs = ctx.getSharedPreferences("TripAdminPanel", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.apply();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public boolean loggedin(){ return prefs.getBoolean("loggedInmode", false); }

    public void setUserDet(String id,String logid,String utype,String uname,String UTId){
        editor.putString("userid",id);
        editor.putString("userlogid",logid);
        editor.putString("usertype",utype);
        editor.putString("username",uname);
        editor.putString("usertype_id",UTId);
        editor.commit();
    }

    public String getUId(){return prefs.getString("userid","");}

    public String getULogid(){return prefs.getString("userlogid","");}

    public String getUType(){return prefs.getString("usertype","");}//"Admin" : "Editor";

    public String getUTypeId(){return prefs.getString("usertype_id","");}

    public String getUName(){return prefs.getString("username","");}

    public void ClearAll(){
        editor.clear();
        editor.apply();
    }

}