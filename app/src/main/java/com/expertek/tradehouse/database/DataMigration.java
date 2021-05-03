package com.expertek.tradehouse.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adapter to Migration classes, inherit your migrations from this
 */
public abstract class DataMigration extends Migration {
    /**
     * Creates a new migration between {@code startVersion} and {@code endVersion}.
     *
     * @param startVersion The start version of the database.
     * @param endVersion   The end version of the database after this migration is applied.
     */
    public DataMigration(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public abstract void migrate(@NonNull SupportSQLiteDatabase database);
}
