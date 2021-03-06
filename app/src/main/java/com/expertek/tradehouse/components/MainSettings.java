package com.expertek.tradehouse.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.ArraySet;

import com.expertek.tradehouse.Application;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public class MainSettings {
    /** Application preferences */
    private static final SharedPreferences mSettings = loadPreferences();
    private static final Set<String> mStringSet = new ArraySet<String>(0);

    public static String Documents_db = "documents.s3db";
    public static String Dictionaries_db = "dictionaries.s3db";

    public static String TradeHouseAddress = mSettings.getString("TradeHouseAddress", "exp-wks-045.expertek.local"); // "Rubanda.expertek.local" // "localhost"
    public static int TradeHousePort = mSettings.getInt("TradeHousePort", 8080);

    public static String TradeHouseUserName = mSettings.getString("TradeHouseUserName", "адм");
    public static String TradeHouseUserId = mSettings.getString("TradeHouseUserId", "");
    public static String TradeHouseObjType = mSettings.getString("TradeHouseObjType", "маг");
    public static int TradeHouseObjCode = mSettings.getInt("TradeHouseObjCode", 0);
    public static Set<String> BarcodePrefixes = mSettings.getStringSet("BarcodePrefixes", mStringSet);
    public static boolean Tethering = mSettings.getBoolean("Tethering", true);
    public static final String SerialNumber = getSerialNumber();

    public static boolean WorkOffline = mSettings.getBoolean("WorkOffline", false);
    public static boolean LoadInvents = mSettings.getBoolean("LoadInvents", true);
    public static boolean CheckMarks = mSettings.getBoolean("CheckMarks", false);
    public static int LinesPerPage = mSettings.getInt("LinesPerPage", 20);

    public static int ConnectionTimeout = mSettings.getInt("ConnectionTimeout", 2000);
    public static int CheckTimeout = mSettings.getInt("CheckTimeout", 5000);
    public static int LoadTimeout = mSettings.getInt("LoadTimeout", 60000);
    public static int SendTimeout = mSettings.getInt("SendTimeout", 180000);

    // Reload cross-process values from disk (for separated process Service)
    public static void reloadPreferences() {
        final SharedPreferences preferences = loadPreferences();
        final Field[] fields = MainSettings.class.getDeclaredFields();

        for (Field field : fields) {
            final String name = field.getName();
            final Class<?> datatype = field.getType();

            if (!Modifier.isFinal(field.getModifiers())) try {
                if (datatype.equals(String.class)) {
                    field.set(null, preferences.getString(name, (String) field.get(null)));
                } else if (datatype.equals(Integer.class) || datatype.equals(Integer.TYPE)) {
                    field.setInt(null, preferences.getInt(name, field.getInt(null)));
                } else if (datatype.equals(Boolean.class) || datatype.equals(Boolean.TYPE)) {
                    field.setBoolean(null, preferences.getBoolean(name, field.getBoolean(null)));
                } else if (datatype.equals(Long.class) || datatype.equals(Long.TYPE)) {
                    field.setLong(null, preferences.getLong(name, field.getLong(null)));
                } else if (datatype.equals(Float.class) || datatype.equals(Float.TYPE)) {
                    field.setFloat(null, preferences.getFloat(name, field.getFloat(null)));
                } else if (datatype.equals(Set.class)) {
                    field.set(null, preferences.getStringSet(name, toStringSet((Set<?>) field.get(null))));
                }
            } catch (IllegalAccessException e) {
                Logger.e(e);
            }
        }
    }

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
                } else if (datatype.equals(Integer.class) || datatype.equals(Integer.TYPE)) {
                    preferences.putInt(name, field.getInt(null));
                } else if (datatype.equals(Boolean.class) || datatype.equals(Boolean.TYPE)) {
                    preferences.putBoolean(name, field.getBoolean(null));
                } else if (datatype.equals(Long.class) || datatype.equals(Long.TYPE)) {
                    preferences.putLong(name, field.getLong(null));
                } else if (datatype.equals(Float.class) || datatype.equals(Float.TYPE)) {
                    preferences.putFloat(name, field.getFloat(null));
                } else if (datatype.equals(Set.class)) {
                    preferences.putStringSet(name, toStringSet((Set<?>) field.get(null)));
                }
            } catch (IllegalAccessException e) {
                Logger.e(e);
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
    @SuppressWarnings("deprecation")
    private static SharedPreferences loadPreferences() {
        final android.app.Application app = Application.app();
        return app.getSharedPreferences(app.getPackageName(), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
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
            Logger.e(e);
            return null;
        }
    }
}
