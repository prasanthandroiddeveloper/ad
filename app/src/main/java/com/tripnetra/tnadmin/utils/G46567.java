package com.tripnetra.tnadmin.utils;

import android.app.Application;

public class G46567 extends Application {

    String userid,username,usertype;

    public String getUserId(){return userid;}
    public void setUserId(String string){this.userid = string;}

    public String getUserName(){return username;}
    public void setUserName(String string){this.username = string;}

    public String getUserType(){return usertype;}//"Admin" "Editor"
    public void setUserType(String string){this.usertype = string;}

}