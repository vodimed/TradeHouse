package com.expertek.tradehouse.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

import java.io.File;
import java.lang.ref.WeakReference;

public class Database<DbInterface extends Baseface> {
    private final WeakReference<Context> context;
    private final Migration[] migrations;
    private DbInterface instance = null;
    private String filename = null;

    public Database(Context context, @NonNull Migration... migrations) {
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

    @SuppressWarnings("unchecked") // Because "<E extends RoomDatabase & DbInterface>" does not allowed in java
    public <E extends RoomDatabase & Baseface> boolean create(@NonNull Class<E> version, @NonNull String name) {
        try {
            close();
            instance = (DbInterface)androidx.room.Room.databaseBuilder(context.get(), version, name)
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

    public <E extends RoomDatabase & Baseface> boolean replace(@NonNull Class<E> version, @NonNull File source) {
        final Context local = context.get();
        final File current = local.getDatabasePath(filename);
        final File obsolete = local.getDatabasePath(filename + ".bak");

        if (obsolete.exists()) try{
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
