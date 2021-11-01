package com.expertek.tradehouse.tradehouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.expertek.tradehouse.MainSettings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// How to open "USB Tethering" setting (modem On/Off)
// https://stackoverflow.com/questions/11171721/how-to-call-the-usb-tethering-intent-in-android-4-0-and-3-0
// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
// ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Service.CONNECTIVITY_SERVICE);
// Log.i("", connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).toString());
public class USBConnectReceiver extends BroadcastReceiver {
    private static final String tetherPackage = "com.android.settings";
    private static final String tetherClassName = "com.android.settings.TetherSettings";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            onUSBConnected(context);
        } else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            onUSBDisconnected(context);
        }
    }

    protected void onUSBConnected(Context context) {
        if (MainSettings.Tethering) {
            Intent tetherSettings = new Intent();
            tetherSettings.setClassName(tetherPackage, tetherClassName);
            context.startActivity(tetherSettings);
        }
    }

    protected void onUSBDisconnected(Context context) {
        System.out.println("USB cable disconnected");
    }

    public static String getConnectedIp() {
        try {
            final BufferedReader arpfile = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = arpfile.readLine()) != null) {
                final String[] split = line.split(" +");
                if (split.length < 6) continue;

                final String iface = split[5];
                final String mac = split[3];
                final String ip = split[0];

                if (iface.equals("rndis0") && mac.matches("..:..:..:..:..:..")) {
                    return ip;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}