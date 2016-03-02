package za.co.hellogroup.xmppchat;

import android.app.Application;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rofhiwa.sikhauli on 2/10/2016.
 */
public class MyApplication extends Application {
    private final String  SERVER_ADDRESS = "154.0.174.24";
    private final int  SERVER_PORT = 5222;
    private final String  SERVER_NAME = "bua.co.za";
    private final String  USER_NAME = "admin";
    private final String  USER_PASSWORD = "rofhiwa";

    public String getServerAddress() {
        return SERVER_ADDRESS;
    }

    public int getServerPort() {
        return SERVER_PORT;
    }

    public String getServerName() {
        return SERVER_NAME;
    }

    public String getUserName() {
        return USER_NAME;
    }

    public String getUserPassword() {
        return USER_PASSWORD;
    }

    String getTime() {
        Date date = new Date( );
        SimpleDateFormat ft =  new SimpleDateFormat ("hh:mma", Locale.ENGLISH);
        return ft.format(date);
    }
}