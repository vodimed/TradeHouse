package com.common.extensions.database;

import android.content.Context;
import android.database.sqlite.SQLiteCantOpenDatabaseException;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * SQLite Database engine
 * @param <SchemaDAO>
 */
public class SQLiteSchema<SchemaDAO> {
    protected WeakReference<Context> context = null;
    protected Class<? extends SchemaDAO> schema;
    protected SchemaDAO instance = null;
    protected String filename = null;
    protected final SQLiteMigration[] migrations;

    public <VersionDAO extends SchemaDAO> SQLiteSchema(Class<VersionDAO> schema, SQLiteMigration... migrations) {
        this.schema = schema;
        this.migrations = migrations;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public boolean open(Context context, @NonNull String name) {
        this.context = new WeakReference<Context>(context);
        this.filename = name;
        final SQLiteDatabase db;

        try {
            final String path = context.getDatabasePath(filename).getPath();
            instance = schema.getConstructor(String.class).newInstance(path);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }

        final int actualVersion = ((SQLiteDatabase) instance).getVersion();
        final int requiredVersion = ((SQLiteDatabase) instance).getSchemaVersion();

        try {
            migrate(actualVersion, requiredVersion);
            ((SQLiteDatabase) instance).setVersion(requiredVersion);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        if (instance == null) return;
        ((SQLiteDatabase) instance).close();
        instance = null;
    }

    public boolean isOpen() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public <VersionDAO extends SchemaDAO> Class<VersionDAO> version() {
        return (Class<VersionDAO>) schema;
    }

    private void checkoper(boolean result) throws SecurityException {
        if (!result) throw new SecurityException();
    }

    public <VersionDAO extends SchemaDAO> boolean replace(@NonNull String name, Class<VersionDAO> schema) {
        final Context local = context.get();
        final File current = local.getDatabasePath(filename);
        final File obsolete = local.getDatabasePath(filename + ".bak");

        if (obsolete.exists()) try {
            if (!obsolete.delete()) throw new SecurityException();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }

        final File source = local.getDatabasePath(name);
        boolean result = true;

        if (source.exists()) try {
            close();
            checkoper(current.renameTo(obsolete));
            checkoper(source.renameTo(current));
            this.schema = schema;
        } catch (SecurityException e) {
            e.printStackTrace();
            if (obsolete.exists()) try {
                checkoper(obsolete.renameTo(current));
            } catch (SecurityException g) {
                g.printStackTrace();
                return false;
            }
        } finally {
            result = open(local, filename);
        }
        return result;
    }

    private void migrate(int startVersion, int endVersion) throws SQLiteCantOpenDatabaseException {
        if (startVersion == endVersion) return;
        // TODO: find the best SQLiteMigration path and execute SQLiteMigration's
        //throw new SQLiteCantOpenDatabaseException();
    }

    public SchemaDAO db() {
        return instance;
    }

    /**
     * Migration interface
     */
    public interface SQLiteMigration {
        void migrate (SQLiteDatabase database);
        int startVersion();
        int endVersion();
    }
}
