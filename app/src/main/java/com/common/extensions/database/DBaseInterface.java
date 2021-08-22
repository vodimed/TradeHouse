package com.common.extensions.database;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Database engine interface
 * @param <SchemaDAO>
 */
public interface DBaseInterface<SchemaDAO> {
    // <VersionDAO extends SchemaDAO> DBaseInterface(Class<VersionDAO> schema);
    boolean open(Context context, @NonNull String name);
    void close();
    <VersionDAO extends SchemaDAO> Class<VersionDAO> version();
    <VersionDAO extends SchemaDAO> boolean replace(@NonNull String name, Class<VersionDAO> schema);
    SchemaDAO db();
}
