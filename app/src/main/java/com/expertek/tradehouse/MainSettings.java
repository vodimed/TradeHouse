package com.expertek.tradehouse;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MainSettings {
    /** Application preferences */
    private static final SharedPreferences mSettings = getPreferences();

    public static String Documents_db = "documents.s3db";
    public static String Dictionaries_db = "dictionaries.s3db";

    public static String ThreadHouseAddress = mSettings.getString("ThreadHouseAddress", "172.23.16.87"); // "Rubanda.expertek.local" // "localhost"
    public static int ThreadHousePort = mSettings.getInt("ThreadHousePort", 8080);
    public static int ConnectionTimeout = mSettings.getInt("ConnectionTimeout", 2000);

    /**
     * Return Application settings and preferences.
     */
    private static SharedPreferences getPreferences() {
        final Application app = MainApplication.app();
        return app.getSharedPreferences(app.getPackageName(), Context.MODE_PRIVATE);
    }
}
