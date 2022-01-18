package com.common.extensions.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;

import androidx.annotation.NonNull;

import com.common.extensions.Logger;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
            Logger.e(e);
            return false;
        }

        final int actualVersion = ((SQLiteDatabase) instance).getVersion();
        final int requiredVersion = ((SQLiteDatabase) instance).getSchemaVersion();

        try {
            migrate(actualVersion, requiredVersion);
            return true;
        } catch (Exception e) {
            Logger.e(e);
            return false;
        }
    }

    public void close() {
        if (instance == null) return;
        ((SQLiteDatabase) instance).close();
        instance = null;
    }

    public boolean isOpen() {
        return ((SQLiteDatabase) instance).isOpen();
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
            Logger.e(e);
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
            Logger.e(e);
            if (obsolete.exists()) try {
                checkoper(obsolete.renameTo(current));
            } catch (SecurityException g) {
                Logger.e(g);
                return false;
            }
        } finally {
            result = open(local, filename);
        }
        return result;
    }

    private void migrate(int startVersion, int endVersion) throws SQLiteCantOpenDatabaseException {
        if (startVersion == endVersion) return;
        final SQLiteDatabase database = (SQLiteDatabase) instance;

        final List<SQLiteMigration> path = migrationPath(startVersion, endVersion);
        if (path == null) throw new SQLiteCantOpenDatabaseException();

        for (SQLiteMigration migration : path) {
            database.beginTransaction();
            try {
                migration.migrate(database);
                database.setVersion(migration.endVersion);
                database.setTransactionSuccessful();
            } catch (SQLException e) {
                throw (SQLiteCantOpenDatabaseException) new SQLiteCantOpenDatabaseException().initCause(e);
            } finally {
                database.endTransaction();
            }
        }
    }

    protected List<SQLiteMigration> migrationPath(int startVersion, int endVersion) {
        if (startVersion == endVersion) return null;

        final int sgn = (startVersion < endVersion ? 1 : -1);
        List<SQLiteMigration> result = null;

        // Decrease distance of possible transformation
        for (int limitVersion = startVersion; result == null; limitVersion += sgn) {
            SQLiteMigration migration = null;

            // Look for the longest possible transformation
            for (SQLiteMigration m : migrations) {
                if ((m.endVersion != endVersion) || (sgn * m.startVersion < sgn * limitVersion)) continue;
                if ((migration == null) || (sgn * m.startVersion < sgn * migration.startVersion)) migration = m;
            }

            if (migration == null) {
                return null;
            } else if (migration.startVersion == startVersion) {
                result = new ArrayList<SQLiteMigration>();
                result.add(migration);
            } else {
                result = migrationPath(startVersion, migration.startVersion);
                if (result != null) result.add(migration);
            }
        }
        return result;
    }

    public SchemaDAO db() {
        return instance;
    }
}
