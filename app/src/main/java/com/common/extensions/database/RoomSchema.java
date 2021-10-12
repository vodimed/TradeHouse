package com.common.extensions.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

import java.lang.ref.WeakReference;

/**
 * Room Database engine
 * @param <SchemaDAO>
 */
public class RoomSchema<SchemaDAO> extends SQLiteSchema<SchemaDAO> {
    private final Migration[] migrations;

    public <VersionDAO extends SchemaDAO> RoomSchema(Class<VersionDAO> schema, Migration... migrations) {
        super(schema);
        this.migrations = migrations;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean open(Context context, @NonNull String name) {
        this.context = new WeakReference<Context>(context);
        try {
            close();
            instance = (SchemaDAO) Room.databaseBuilder(context, schema.asSubclass(RoomDatabase.class), name)
                    .allowMainThreadQueries().enableMultiInstanceInvalidation()
                    .addMigrations((Migration[]) migrations)
                    .build();
            filename = name;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {
        if (instance == null) return;
        ((RoomDatabase) instance).close();
        instance = null;
    }

    @Override
    public boolean isOpen() {
        return ((RoomDatabase) instance).isOpen();
    }
}
