package com.common.extensions.database;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Connector to your database. Usage example:
 *     private static DataBase<DbDictionaries> dictionaries;
 *     private static DataBase<DBDocuments> documents;
 *     ...
 *     dictionaries.create(DbDictionaries_v1.class, "dictionaries");
 *     documents.create(DBDocuments_v1.class, "documents");
 *
 * Create interface to your room-database class,
 * describing DAO methods, based on this interface:
 *     public interface DBDocuments {
 *         // Describe your DAO methods here
 *     }
 *     public abstract class DBDocuments_v1 extends
 *         DataEngine[RoomDatabase] implements DBDocuments
 *     {
 *         // No DAO descriptions here anymore
 *     }
 */
public class DataBase<DbInterface> {
    private static WeakReference<Context> context = null;
    private final DataEngine.DataMigration[] migrations;
    private Class<? extends DbInterface> version;
    private DbInterface instance = null;
    private String filename = null;

    public DataBase(@NonNull Class<? extends DbInterface> version, @NonNull DataEngine.DataMigration... migrations) {
        this.version = version;
        this.migrations = migrations;
    }

    public static void setContext(Context context) {
        DataBase.context = new WeakReference<Context>(context);
    }

    public Class<? extends DbInterface> getVersion() {
        return version;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void close() {
        if (instance != null) ((DataEngine)instance).close();
    }

    @SuppressWarnings("unchecked") // because "<E extends RoomDatabase & DbInterface>" does not allowed in java
    public boolean create(@NonNull String name) {
        try {
            close();
            instance = (DbInterface) DataEngine.databaseBuilder(
                    context.get(), version.asSubclass(DataEngine.class), name)
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
            this.version = version;
        } catch (SecurityException e) {
            e.printStackTrace();
            if (obsolete.exists()) try {
                checkoper(obsolete.renameTo(current));
            } catch (SecurityException g) {
                g.printStackTrace();
                return false;
            }
        } finally {
            result = create(filename);
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
