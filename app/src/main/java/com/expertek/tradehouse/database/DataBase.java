package com.expertek.tradehouse.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Connector to your database. Usage example:
 *     private static DataBase<DbDictionaries> dictionaries;
 *     private static DataBase<DBDocuments> documents;
 *     ...
 *     dictionaries.create(DbDictionaries_v1.class, "dictionaries");
 *     documents.create(DBDocuments_v1.class, "documents");
 * @param <DbInterface>
 */
public class DataBase<DbInterface extends BaseFace> {
    private final WeakReference<Context> context;
    private final Migration[] migrations;
    private DbInterface instance = null;
    private String filename = null;

    public DataBase(Context context, @NonNull Migration... migrations) {
        this.context = new WeakReference<Context>(context);
        this.migrations = migrations;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void close() {
        if (instance != null) ((RoomDatabase)instance).close();
    }

    @SuppressWarnings("unchecked") // "<E extends RoomDatabase & DbInterface>" does not allowed in java
    public boolean create(@NonNull Class<? extends DbInterface> version, @NonNull String name) {
        try {
            close();
            instance = (DbInterface)androidx.room.Room.databaseBuilder(
                    context.get(), version.asSubclass(RoomDatabase.class), name)
                    .allowMainThreadQueries().enableMultiInstanceInvalidation()
                    .addMigrations(migrations)
                    .build();
            filename = name;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean replace(@NonNull Class<? extends DbInterface> version, @NonNull File source) {
        final Context local = context.get();
        final File current = local.getDatabasePath(filename);
        final File obsolete = local.getDatabasePath(filename + ".bak");

        if (obsolete.exists()) try {
            if (!obsolete.delete()) throw new SecurityException();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }

        boolean result = true;
        if (source.exists()) try {
            close();
            checkoper(current.renameTo(obsolete));
            checkoper(source.renameTo(current));
        } catch (SecurityException e) {
            e.printStackTrace();
            if (obsolete.exists()) try {
                checkoper(obsolete.renameTo(current));
            } catch (SecurityException g) {
                g.printStackTrace();
                return false;
            }
        } finally {
            result = create(version, filename);
        }
        return result;
    }

    private void checkoper(boolean result) throws SecurityException {
        if (!result) throw new SecurityException();
    }

    public DbInterface db() {
        return instance;
    }
}
