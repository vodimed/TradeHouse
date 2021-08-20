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
    public static final DataBase<DbDictionaries> dictionaries = new DataBase<DbDictionaries>(DbDictionaries_v1.class);
    public static final DataBase<DBDocuments> documents = new DataBase<DBDocuments>(DBDocuments_v1.class);

    // Return Application instance on static method manner
    public static Application app() {
        return app;
    }

    public MainApplication() {
        super();
        app = this;
        DataBase.setContext(this);
        Thread.setDefaultUncaughtExceptionHandler(allerrors);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dictionaries.create(MainSettings.Dictionaries_db);
        documents.create(MainSettings.Documents_db);

        //TODO: database
        try {
            com.expertek.tradehouse.dictionaries.entity.client cl1 = new com.expertek.tradehouse.dictionaries.entity.client();
            cl1.cli_code = 1;
            cl1.cli_type = 1;
            cl1.Name = "aaa";
            com.expertek.tradehouse.dictionaries.entity.client cl2 = new com.expertek.tradehouse.dictionaries.entity.client();
            cl2.cli_code = 2;
            cl2.cli_type = 2;
            cl2.Name = "BBB";
            MainApplication.dictionaries.db().clients().insertAll(cl1, cl2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceActivity.createNotificationChannel(app,
                R.string.CHANNEL_ID,
                R.string.service_tradehouse);

        //AccessibilityManager -- logging
        //ActivityManager.killBackgroundProcesses(Service); ActivityManager.AppTask
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
     * Handle all uncaught Exceptions and Errors of the project
     */
    private final Thread.UncaughtExceptionHandler allerrors = new Thread.UncaughtExceptionHandler() {
        private final Thread.UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            e.printStackTrace();
            original.uncaughtException(t, e);
        }
    };
}
