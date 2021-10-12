package com.common.extensions.database;

import androidx.annotation.NonNull;

/**
 * Base class for support of SQLite migrations
 */
public abstract class SQLiteMigration {
    public final int startVersion;
    public final int endVersion;

    // Inheritance: "public SQLiteMigration() { super(0, 1); }"
    protected SQLiteMigration(int startVersion, int endVersion) {
        this.startVersion = startVersion;
        this.endVersion = endVersion;
    }

    public abstract void migrate(@NonNull SQLiteDatabase database);
}
