package com.expertek.tradehouse;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.common.extensions.database.SQLiteSchema;
import com.common.extensions.exchange.ServiceActivity;
import com.common.extensions.exchange.ServiceConnector;
import com.common.extensions.exchange.ServiceInterface;
import com.expertek.tradehouse.components.Logger;
import com.expertek.tradehouse.components.MainSettings;
import com.expertek.tradehouse.dictionaries.DbDictionaries;
import com.expertek.tradehouse.dictionaries.Dictionaries_v1Sqlite;
import com.expertek.tradehouse.documents.DBDocuments;
import com.expertek.tradehouse.documents.Documents_v1Sqlite;
import com.expertek.tradehouse.tradehouse.TradeHouseService;
import com.expertek.tradehouse.tradehouse.TradeHouseTask;
import com.expertek.tradehouse.tradehouse.Документы;
import com.expertek.tradehouse.tradehouse.Словари;

import java.io.File;

public class Application extends android.app.Application {
    private static android.app.Application app;
    public static final SQLiteSchema<DbDictionaries> dictionaries = new SQLiteSchema<DbDictionaries>(Dictionaries_v1Sqlite.class, new Dictionaries_v1Sqlite.M_0_1());
    public static final SQLiteSchema<DBDocuments> documents = new SQLiteSchema<DBDocuments>(Documents_v1Sqlite.class, new Documents_v1Sqlite.M_0_1());
    //public static final RoomSchema<DbDictionaries> dictionaries = new RoomSchema<DbDictionaries>(Dictionaries_v1Room.class);
    //public static final RoomSchema<DBDocuments> documents = new RoomSchema<DBDocuments>(Documents_v1Room.class);

    // Return Application instance on static method manner
    public static android.app.Application app() {
        return app;
    }

    public Application() {
        super();
        app = this;
        Logger.setApplicationContext(this);
        Thread.setDefaultUncaughtExceptionHandler(allerrors);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!dictionaries.open(this, MainSettings.Dictionaries_db)) {
            tradehouse.enqueue(new ServiceInterface.JobInfo(1, Словари.class, tradehouse.receiver()), null);
        }

        if (!documents.open(this, MainSettings.Documents_db)) {
            tradehouse.enqueue(new ServiceInterface.JobInfo(2, Документы.class, tradehouse.receiver()), null);
        }

        ServiceActivity.createNotificationChannel(app,
                R.string.CHANNEL_ID,
                R.string.service_tradehouse);

        //AccessibilityManager -- logging
    }

    // Replace DbDictionaries database file with new one (as a whole)
    public static <E extends DbDictionaries> boolean replace_dictionaries_db_file(@NonNull String name, @NonNull Class<E> version) {
        return dictionaries.replace(name, version);
    }

    // Replace DBDocuments database file with new one (as a whole)
    public static <E extends DBDocuments> boolean replace_documents_db_file(@NonNull String name, @NonNull Class<E> version) {
        return documents.replace(name, version);
    }

    public static String getVersion() {
        final PackageManager manager = app.getPackageManager();
        try {
            final PackageInfo info = manager.getPackageInfo(app.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }
    }

    private final ServiceConnector tradehouse = new ServiceConnector(this, TradeHouseService.class) {
        @Override
        @SuppressWarnings("unchecked")
        public void onJobResult(@NonNull ServiceInterface.JobInfo work, Bundle result) {
            switch (work.getJobId()) {
                case 1:
                    final File dictionaries = Application.app().getDatabasePath(MainSettings.Dictionaries_db);
                    Application.replace_dictionaries_db_file(TradeHouseTask.temporary(dictionaries).getName(),
                            (Class<? extends DbDictionaries>) result.getSerializable(dictionaries.getName()));
                    break;
                case 2:
                    final File documents = Application.app().getDatabasePath(MainSettings.Documents_db);
                    Application.replace_documents_db_file(TradeHouseTask.temporary(documents).getName(),
                            (Class<? extends DBDocuments>) result.getSerializable(documents.getName()));
                    break;
            }
        }

        @Override
        public void onJobException(@NonNull ServiceInterface.JobInfo work, @NonNull Throwable e) {
            Logger.e(e);
            //System.exit(-1);
        }
    };

    /**
     * Handle all uncaught Exceptions and Errors of the project
     */
    private final Thread.UncaughtExceptionHandler allerrors = new Thread.UncaughtExceptionHandler() {
        private final Thread.UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            Logger.e(e);
            original.uncaughtException(t, e);
        }
    };
}
