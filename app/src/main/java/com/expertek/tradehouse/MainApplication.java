package com.expertek.tradehouse;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.StringRes;

public class MainApplication extends Application {
    private static Application app;

    public MainApplication() {
        super();
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        //app.getApplicationContext().openOrCreateDatabase();
        //ActivityManager.killBackgroundProcesses(Service)
        //ActivityManager.AppTask
    }

    /**
     * Return Application instance on static method manner.
     */
    public static Application inst() {
        return app;
    }

    /**
     * Return Application settings and preferences.
     */
    public static SharedPreferences getPreferences(int mode) {
        return app.getSharedPreferences(app.getPackageName(), mode);
    }

    /**
     * Before you can deliver the notification on Android 8.0 and higher, you
     * must register your app's notification channel with the system by passing
     * an instance of NotificationChannel to createNotificationChannel().
     */
    private static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            final NotificationManager systemtray =
                    (NotificationManager)app.getSystemService(NOTIFICATION_SERVICE);
            final NotificationChannel channel = new NotificationChannel(
                    app.getString(R.string.CHANNEL_ID), app.getText(R.string.service_tradehouse),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);

            systemtray.createNotificationChannel(channel);
        }
    }

    public static Notification createNotification(
            Class<? extends Activity> actiity, @StringRes int title, @StringRes int text)
    {
        final Notification.Builder builder;

        // Set the info for the views that show in the notification panel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(app, app.getString(R.string.CHANNEL_ID));
        } else {
            builder = DeprecatedNotificationBuilder(app);
        }

        // The PendingIntent to launch our activity if the user selects this notification
        final PendingIntent contentIntent = PendingIntent.getActivity(app, 0,
                new Intent(app, actiity), PendingIntent.FLAG_UPDATE_CURRENT);

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        return builder.setContentIntent(contentIntent) // The intent to send when the entry is clicked
                .setSmallIcon(R.drawable.ic_launcher)  // the status icon
                .setWhen(System.currentTimeMillis())   // the time stamp
                .setContentTitle(app.getText(title))  // the label of the entry
                .setContentText(app.getText(text))  // the contents of the entry
                .build();
    }

    @Deprecated
    private static Notification.Builder DeprecatedNotificationBuilder(Context context) {
        return new Notification.Builder(context).setSound(null);
    }
}
