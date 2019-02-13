package com.tripnetra.tnadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tripnetra.tnadmin.bookings.BookingMainFragment;
import com.tripnetra.tnadmin.inventory.Car_Main_Invtry_Act;
import com.tripnetra.tnadmin.inventory.Hotel_Main_Invtry_Act;
import com.tripnetra.tnadmin.logs.Cancel_Fragment;
import com.tripnetra.tnadmin.logs.LinkFragment;
import com.tripnetra.tnadmin.logs.LogDetailsFragment;
import com.tripnetra.tnadmin.payments.PaymentRepMainFragment;
import com.tripnetra.tnadmin.rest.VolleyRequester;
import com.tripnetra.tnadmin.utils.G46567;
import com.tripnetra.tnadmin.utils.Session;

import static com.tripnetra.tnadmin.utils.Config.CHECK_IP_URL;

public class DashBoardActivity extends AppCompatActivity {

    Session session;
    NavigationView navigationView;
    DrawerLayout drawerLayt;
    Toolbar toolbar;
    static int navItemIndex = 0,Titleindex=0;
    static String CURRENT_TAG = "Inventory";
    String LoginID,UserType,UserName,UserId;
    String[] activityTitles;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        session = new Session(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoginID = session.getULogid();
        String Userid = session.getUId();
        UserType = session.getUType();
        UserName = session.getUName();
        UserId  = session.getUId();

        if (!UserName.equals("")) {

            G46567 g665 = (G46567) getApplicationContext();
            g665.setUserId(Userid);
            g665.setUserName(UserName);
            g665.setUserType(UserType);

            drawerLayt = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);

            navigationView.setItemIconTintList(null);
            View navHeader = navigationView.getHeaderView(0);

            activityTitles = new String[10];
            activityTitles[0] = "Inventory";
            activityTitles[1] = "Car Inventory";
            activityTitles[2] = "Payment Reports";
            activityTitles[3] = "Hotel Bookings";
            activityTitles[4] = "Car Bookings";
            activityTitles[5] = "Tour Bookings";
            activityTitles[6] = "Darshan Bookings";
            activityTitles[8] = "Log Details";
            activityTitles[7] = "Cancellations";

            activityTitles[9] = "Link";

            ((TextView) navHeader.findViewById(R.id.nameTv)).setText(UserName);
            int ii = 0;
            if (LoginID.contains("_")) {
                ii = LoginID.indexOf("_") + 1;
            } else if (LoginID.contains(" ")) {
                ii = LoginID.indexOf(" ") + 1;
            }
            if (ii != 0) {
                ((TextView) navHeader.findViewById(R.id.nameshortTv)).setText(String.valueOf(LoginID.charAt(0)) + String.valueOf(LoginID.charAt(ii)));
            } else {
                ((TextView) navHeader.findViewById(R.id.nameshortTv)).setText(String.valueOf(LoginID.charAt(0)));
            }

            setUpNavigationView();

            if (savedInstanceState == null) {
                navItemIndex = 0;
                Titleindex = 0;
                CURRENT_TAG = activityTitles[0];
                loadHomeFragment();
            }
           /* if(!(UserId.equals("12")||UserId.equals("16")|| UserType.equals("Admin"))){
                checkip = false;
                checkipmethd();
            }

*/

            else{force_logout();}

        }
    }

    private void setUpNavigationView() {

        if(!UserType.equals("Admin")) {
            navigationView.getMenu().findItem(R.id.pricereps).setVisible(false);//Hide Payment Reports
            navigationView.getMenu().findItem(R.id.logdata).setVisible(false);
            navigationView.getMenu().findItem(R.id.cancels).setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()) {

                case R.id.nav_inventory:
                    navItemIndex = 0;
                    Titleindex=0;
                    CURRENT_TAG = activityTitles[0];
                    break;
                case R.id.Carinv:
                    navItemIndex = 1;
                    Titleindex=1;
                    CURRENT_TAG = activityTitles[1];
                    break;
                case R.id.pricereps:
                    navItemIndex = 2;
                    Titleindex=2;
                    CURRENT_TAG = activityTitles[2];
                    break;

                case R.id.hotelbookings:
                    navItemIndex = 3;
                    Titleindex=3;
                    CURRENT_TAG = activityTitles[3];
                    break;
                case R.id.carbookings:
                    navItemIndex = 4;
                    Titleindex=4;
                    CURRENT_TAG = activityTitles[4];
                    break;
                case R.id.tourbookings:
                    navItemIndex = 5;
                    Titleindex=5;
                    CURRENT_TAG = activityTitles[5];
                    break;
                case R.id.darbookings:
                    navItemIndex = 6;
                    Titleindex=6;
                    CURRENT_TAG = activityTitles[6];
                    break;
                case R.id.cancels:
                    navItemIndex = 7;
                    Titleindex=7;
                    CURRENT_TAG = activityTitles[7];
                    break;
                case R.id.logdata:
                    navItemIndex = 8;
                    Titleindex=8;
                    CURRENT_TAG = activityTitles[8];
                    break;

                case R.id.link:
                    navItemIndex = 9;
                    Titleindex=9;
                    CURRENT_TAG = activityTitles[9];
                    break;


                default:
                    navItemIndex = 0;
                    Titleindex=0;
                    CURRENT_TAG = activityTitles[0];
                    break;
            }

            menuItem.setChecked(true);

            loadHomeFragment();

            return true;
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayt, toolbar, R.string.app_name, R.string.dummy_content) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayt.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadHomeFragment() {

        if(navItemIndex<3){
            navigationView.getMenu().getItem(navItemIndex).setChecked(true);
        }else if(navItemIndex<8){
            //to get items in group 2 or sub menu
            navigationView.getMenu().getItem(3).getSubMenu().getItem(navItemIndex-3).setChecked(true);
        }else{
            navigationView.getMenu().getItem(4).setChecked(true);
            //navigationView.getMenu().getItem(navItemIndex-3).setChecked(true);
        }

        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle(activityTitles[Titleindex]);

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) { drawerLayt.closeDrawers();return; }

        new Handler().post(() -> {
            Fragment fragment = getHomeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
        });

        drawerLayt.closeDrawers();

        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new Hotel_Main_Invtry_Act();
            case 1:
                return new Car_Main_Invtry_Act();
            case 2:
                return new PaymentRepMainFragment();
            case 3: case 4: case 5: case 6:
                return new BookingMainFragment();
            case 7:
                return new Cancel_Fragment();
            case 8:
                return new LogDetailsFragment();

            case 9:
                return new LinkFragment();

            default:
                return new Hotel_Main_Invtry_Act();
        }
    }

/* private void checkipmethd() {

        new VolleyRequester(this).ParamsRequest(1, CHECK_IP_URL, null, null, false, response -> {
            if(!response.toLowerCase().contains( "success")){
                new AlertDialog.Builder(DashBoardActivity.this)
                        .setMessage("Hello "+UserName+"\nPlease Connect to Company Network")
                        .setPositiveButton("OK", (dialog, id) ->
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS),568))
                        .setCancelable(false).create().show();

            }
        });

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { getMenuInflater().inflate(R.menu.main, menu);return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==568) {checkipmethd();}
    }*/

    private void logoutUser() {
        new AlertDialog.Builder(DashBoardActivity.this).setMessage("Do you Want to Logout")
                .setPositiveButton("Yes", (dialog, id) -> clearSharedprefs())
                .setNegativeButton("No", (dialog, id) -> {})
                .setCancelable(true).create().show();
    }

    private void force_logout() {
        new AlertDialog.Builder(DashBoardActivity.this).setMessage("Something Wrong\n\nPlease Login Again")
                .setPositiveButton("Ok", (dialog, id) -> clearSharedprefs())
                .setCancelable(false).create().show();
    }

    public void clearSharedprefs(){
        session.setLoggedin(false);
        session.ClearAll();

        Intent intent = new Intent(DashBoardActivity.this, LoginMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(() -> exit = false, 3 * 1000);
        }
    }

}