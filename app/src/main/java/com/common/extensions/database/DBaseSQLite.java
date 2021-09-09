package com.common.extensions.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * SQLite Database engine
 * @param <SchemaDAO>
 */
public class DBaseSQLite<SchemaDAO> implements DBaseInterface<SchemaDAO> {
    protected WeakReference<Context> context = null;
    protected Class<? extends SchemaDAO> schema;
    protected SchemaDAO instance = null;
    protected String filename = null;

    public <VersionDAO extends SchemaDAO> DBaseSQLite(Class<VersionDAO> schema) {
        this.schema = schema;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    @Override
    public boolean open(Context context, @NonNull String name) {
        this.context = new WeakReference<Context>(context);
        this.filename = name;
        try {
            final File current = context.getDatabasePath(filename);
            instance = schema.getConstructor(String.class).newInstance(current.toString());
            return true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {
        if (instance == null) return;
        ((SQLiteDatabase) instance).close();
        instance = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <VersionDAO extends SchemaDAO> Class<VersionDAO> version() {
        return (Class<VersionDAO>) schema;
    }

    private void checkoper(boolean result) throws SecurityException {
        if (!result) throw new SecurityException();
    }

    @Override
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

    @Override
    public SchemaDAO db() {
        return instance;
    }
}
