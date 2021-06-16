package com.expertek.tradehouse;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.ArraySet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public class MainSettings {
    /** Application preferences */
    private static final SharedPreferences mSettings = loadPreferences();
    private static final Set<String> mStringSet = new ArraySet<String>(0);

    public static String Documents_db = "documents.s3db";
    public static String Dictionaries_db = "dictionaries.s3db";

    public static String TradeHouseAddress = mSettings.getString("TradeHouseAddress", "172.23.16.87"); // "Rubanda.expertek.local" // "localhost"
    public static int TradeHousePort = mSettings.getInt("TradeHousePort", 8080);

    public static String TradeHouseObject = mSettings.getString("TradeHouseObject", "маг1");
    public static final String SerialNumber = getSerialNumber();
    public static Set<String> BarcodePrefixes = mSettings.getStringSet("BarcodePrefixes", mStringSet);

    public static boolean WorkOffline = mSettings.getBoolean("WorkOffline", false);
    public static boolean LoadInvents = mSettings.getBoolean("LoadInvents", true);
    public static boolean CheckMarks = mSettings.getBoolean("CheckMarks", false);
    public static int LinesPerPage = mSettings.getInt("LinesPerPage", 20);

    public static int ConnectionTimeout = mSettings.getInt("ConnectionTimeout", 2000);
    public static int CheckTimeout = mSettings.getInt("CheckTimeout", 5000);
    public static int LoadTimeout = mSettings.getInt("LoadTimeout", 60000);
    public static int SendTimeout = mSettings.getInt("SendTimeout", 180000);

    /**
     * Save Application settings and preferences.
     */
    public static boolean savePreferences() {
        final SharedPreferences.Editor preferences = mSettings.edit();
        final Field[] fields = MainSettings.class.getDeclaredFields();

        for (Field field : fields) {
            final String name = field.getName();
            final Class<?> datatype = field.getType();

            if (!Modifier.isFinal(field.getModifiers())) try {
                if (datatype.equals(String.class)) {
                    preferences.putString(name, (String) field.get(null));
                } else if (datatype.equals(Integer.class)) {
                    preferences.putInt(name, field.getInt(null));
                } else if (datatype.equals(Boolean.class)) {
                    preferences.putBoolean(name, field.getBoolean(null));
                } else if (datatype.equals(Long.class)) {
                    preferences.putLong(name, field.getLong(null));
                } else if (datatype.equals(Float.class)) {
                    preferences.putFloat(name, field.getFloat(null));
                } else if (datatype.equals(Set.class)) {
                    preferences.putStringSet(name, toStringSet((Set<?>) field.get(null)));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return preferences.commit();
    }

    @SuppressWarnings("unchecked")
    private static Set<String> toStringSet(Set<?> value) {
        try {
            return (Set<String>) value;
        } catch (ClassCastException e) {
            return mStringSet;
        }
    }

    /**
     * Return Application settings and preferences.
     */
    private static SharedPreferences loadPreferences() {
        final Application app = MainApplication.app();
        return app.getSharedPreferences(app.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Retrieve Serial number of physical mobile device.
     * You need permissions in AndroidManifest.xml file:
     *     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     *     <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
     */
    @SuppressWarnings("deprecation")
    private static String getSerialNumber() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return android.os.Build.getSerial();
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
