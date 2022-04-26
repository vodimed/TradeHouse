package com.common.extensions.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.expertek.tradehouse.components.Logger;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        try {
            final String path = context.getDatabasePath(filename).getPath();
            instance = schema.getConstructor(String.class).newInstance(path);
            if (!(instance instanceof SQLiteSchema.DB)) throw new ReflectiveOperationException();
        } catch (ReflectiveOperationException e) {
            Logger.e(e);
            return false;
        }

        final int actualVersion = ((DB) instance).db.getVersion();
        final int requiredVersion = ((DB) instance).schema_version;

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
        ((DB) instance).db.close();
        instance = null;
    }

    public boolean isOpen() {
        return ((DB) instance).db.isOpen();
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
        final SQLiteDatabase db = ((DB) instance).db;

        final List<SQLiteMigration> path = migrationPath(startVersion, endVersion);
        if (path == null) throw new SQLiteCantOpenDatabaseException();

        for (SQLiteMigration migration : path) {
            db.beginTransaction();
            try {
                migration.migrate(db);
                db.setVersion(migration.endVersion);
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                throw (SQLiteCantOpenDatabaseException) new SQLiteCantOpenDatabaseException().initCause(e);
            } finally {
                db.endTransaction();
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

    /**
     * Base class for all DAO Databases
     */
    public static abstract class DB {
        protected final SQLiteDatabase db;
        protected final int schema_version;

        protected DB(@NonNull String path, int version) {
            this.db = SQLiteDatabase.openOrCreateDatabase(path, null);
            this.schema_version = version;
        }
    }

    /**
     * Datetime converter
     */
    public static class DateConverter {
        private static final String sqlDateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        @SuppressLint("SimpleDateFormat")
        private static final DateFormat template = new SimpleDateFormat(sqlDateTimeFormat);

        public static String save(java.util.Date value) {
            if (value != null) {
                return template.format(value);
            } else {
                return null;
            }
        }

        public static java.util.Date load(String value) {
            if (value != null) try {
                return template.parse(value);
            } catch (ParseException e) {
                return null;
            } else {
                return null;
            }
        }
    }
}
