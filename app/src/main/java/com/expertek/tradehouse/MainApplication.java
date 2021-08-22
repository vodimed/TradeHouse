package com.expertek.tradehouse;

import android.app.Application;

import androidx.annotation.NonNull;

import com.common.extensions.database.DBaseRoom;
import com.common.extensions.exchange.ServiceActivity;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.Dictionaries_v1Room;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.Documents_v1Room;

public class MainApplication extends Application {
    private static Application app;
    public static final DBaseRoom<DbDictionaries> dictionaries = new DBaseRoom<DbDictionaries>(Dictionaries_v1Room.class);
    public static final DBaseRoom<DBDocuments> documents = new DBaseRoom<DBDocuments>(Documents_v1Room.class);

    // Return Application instance on static method manner
    public static Application app() {
        return app;
    }

    public MainApplication() {
        super();
        app = this;
        Thread.setDefaultUncaughtExceptionHandler(allerrors);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dictionaries.open(this, MainSettings.Dictionaries_db);
        documents.open(this, MainSettings.Documents_db);

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
    public static <E extends DbDictionaries> boolean replace_dictionaries_db_file(@NonNull String name, @NonNull Class<E> version) {
        return dictionaries.replace(name, version);
    }

    // Replace DBDocuments database file with new one (as a whole)
    public static <E extends DBDocuments> boolean replace_documents_db_file(@NonNull String name, @NonNull Class<E> version) {
        return documents.replace(name, version);
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
