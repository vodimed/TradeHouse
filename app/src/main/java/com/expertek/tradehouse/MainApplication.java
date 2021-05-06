package com.expertek.tradehouse;

import android.app.Application;

import androidx.annotation.NonNull;

import com.common.extensions.database.DataBase;
import com.common.extensions.database.DataEngine;
import com.common.extensions.exchange.ServiceActivity;
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
        dictionaries.create(DbDictionaries_v1.class, MainSettings.Dictionaries_db);
        documents.create(DBDocuments_v1.class, MainSettings.Documents_db);

        ServiceActivity.createNotificationChannel(app,
                R.string.CHANNEL_ID,
                R.string.service_tradehouse);

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
}
