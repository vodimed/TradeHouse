package com.expertek.tradehouse.tradehouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.common.extensions.Logger;
import com.expertek.tradehouse.MainSettings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// How to open "USB Tethering" setting (modem On/Off)
// https://stackoverflow.com/questions/11171721/how-to-call-the-usb-tethering-intent-in-android-4-0-and-3-0
// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
// ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Service.CONNECTIVITY_SERVICE);
// Log.i("", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).toString());
public class ConnectionReceiver extends BroadcastReceiver {
    public enum Status {connected, unavailable, launched}
    private static final String tetherPackage = "com.android.settings";
    private static final String tetherClassName = "com.android.settings.TetherSettings";
    private static final String[] linksSupported = {"eth", "rndis"}; // in preferable order

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            onUSBCableConnected(context);
        } else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            onUSBCableDisconnected(context);
        }
    }

    protected void onUSBCableConnected(Context context) {
        connect(context);
    }

    protected void onUSBCableDisconnected(Context context) {
        System.out.println("USB cable disconnected");
    }

    // https://developer.android.com/training/monitoring-device-state/battery-monitoring.html
    public static boolean isUSBCableConnected(Context context) {
        final IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        final Intent batteryStatus = context.registerReceiver(null, ifilter);
        final int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
    }

    public static Status connect(Context context) {
        if (getConnectedIp() != null) return Status.connected;
        if (!MainSettings.Tethering) return Status.unavailable;

        final Intent tetherSettings = new Intent();
        tetherSettings.setClassName(tetherPackage, tetherClassName);
        context.startActivity(tetherSettings);
        return Status.launched;
    }

    public static String getConnectedIp() {
        int foindId = linksSupported.length;
        String foundIp = null;
        try {
            final BufferedReader arpfile = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = arpfile.readLine()) != null) {
                final String[] split = line.split(" +");
                if (split.length < 6) continue;

                final String iface = split[5];
                final String mac = split[3];
                final String ip = split[0];

                if (mac.matches("..:..:..:..:..:..")) {
                    for (int i = 0; i < foindId; i++) {
                        if (iface.startsWith(linksSupported[i])) {
                            foundIp = ip;
                            foindId = i;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Logger.e(e);
        }
        return foundIp;
    }
}