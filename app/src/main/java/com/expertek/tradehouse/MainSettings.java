package com.expertek.tradehouse;

import android.content.SharedPreferences;

public class MainSettings {
    /** Application preferences */
    private static final SharedPreferences mSettings = MainApplication.getPreferences(MainApplication.MODE_PRIVATE);

    public static String DocumentsDB = "documents.s3db";
    public static String DictionariesDB = "dictionaries.s3db";

    public static int ConnectionTimeout = mSettings.getInt("ConnectionTimeout", 2000);
    public static String ThreadHouseAddress = mSettings.getString("ThreadHouseAddress", "Rubanda.expertek.local" /*"localhost"*/);
    public static int ThreadHousePort = mSettings.getInt("ThreadHousePort", 8080);
}
