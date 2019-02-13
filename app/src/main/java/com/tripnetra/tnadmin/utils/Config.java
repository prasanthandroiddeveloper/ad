package com.tripnetra.tnadmin.utils;

import com.tripnetra.tnadmin.BuildConfig;

public class Config {

    public static String BASEURL = BuildConfig.Baserl;
    //public static String BASEURL = BuildConfig.Baserl+"test/";

    public static String ADMINURL = BASEURL+BuildConfig.adminn;

    public static String PAYMENT_VOU_URL = ADMINURL+"paymentvoucher.php",
                            HOTEL_DET_URL = ADMINURL+"hoteldetails.php",
                            HOTEL_NAMES_URL = ADMINURL+"hotelnames.php",
                            LOG_DETAILS_URL = ADMINURL+"getlogdetails.php",
                            ROOM_DET_URL = ADMINURL+"roomdetails.php",
                            PAY_REP_URL = ADMINURL+"paymentreports.php",
                            CHECK_IP_URL = BASEURL+"checkip.php",
                            LOGIN_URL = ADMINURL+"adminlogin.php",
                            TOUR_VOCH_URL = ADMINURL+"tourbookingvoucher.php",
                            CANCEL_URL = ADMINURL+"105/cancelbooking.php",
                            CANCEL_CURL = ADMINURL+"105/cancel_reports.php",
                            BOOK_VOU_URL  = ADMINURL+"hotelbookingvoucher.php",
                            RESEND_URL = ADMINURL+"105/hotelvouherresend.php",
                            CAR_BOOK_URL = ADMINURL+"carbookingvoucher.php",
                            CAR_DETAILS_URL = ADMINURL+"car_details.php";

    public static String PAYMT_URL = "https://tripnetra.com/cpanel_admin/booking/get_payment_status/6865446727eae9cbd513";
}
