package com.expertek.tradehouse;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.common.extensions.database.DataBase;
import com.common.extensions.database.DataEngine;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.DbDictionaries_v1;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.DBDocuments_v1;

import java.io.File;

public class MainApplication extends Application {
    private static Application app;
    private static DataBase<DbDictionaries> dictionaries;
    private static DataBase<DBDocuments> documents;

    // Return Application instance on static method manner
    public static Application app() {
        return app;
    }

    public MainApplication() {
        super();
        app = this;
        dictionaries = new DataBase<DbDictionaries>(this);
        documents = new DataBase<DBDocuments>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        dictionaries.create(DbDictionaries_v1.class, MainSettings.Dictionaries_db);
        documents.create(DBDocuments_v1.class, MainSettings.Documents_db);

        //ActivityManager.killBackgroundProcesses(Service); ActivityManager.AppTask
    }

    // Return DbDictionaries instance
    public static DbDictionaries dbc() {
        return dictionaries.db();
    }

    // Return DBDocuments instance
    public static DBDocuments dbd() {
        return documents.db();
    }

    // Replace DbDictionaries database file with new one (as a whole)
    public static <E extends DataEngine & DbDictionaries> boolean replace_dictionaries_db_file(
            @NonNull Class<E> version, @NonNull File source)
    {
        return dictionaries.replace(version, source);
    }

    // Replace DBDocuments database file with new one (as a whole)
    public static <E extends DataEngine & DBDocuments> boolean replace_documents_db_file(
            @NonNull Class<E> version, @NonNull File source)
    {
        return documents.replace(version, source);
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
