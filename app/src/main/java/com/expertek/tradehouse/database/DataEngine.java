package com.expertek.tradehouse.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adapter to your database. Usage example:
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
public abstract class DataEngine extends RoomDatabase {
    @NonNull public static <T extends DataEngine> RoomDatabase.Builder<T> databaseBuilder(
            @NonNull Context context, @NonNull Class<T> klass, @NonNull String name)
    {
        return androidx.room.Room.databaseBuilder(context, klass, name);
    }

    /**
     * Adapter to Migration classes. Either use auto-migrations,
     * or inherit your own migration classes from this class
     */
    public abstract static class Migration extends androidx.room.migration.Migration {
        /**
         * Creates a new migration between {@code startVersion} and {@code endVersion}.
         *
         * @param startVersion The start version of the database.
         * @param endVersion   The end version of the database after this migration is applied.
         */
        public Migration(int startVersion, int endVersion) {
            super(startVersion, endVersion);
        }

        @Override
        public abstract void migrate(@NonNull SupportSQLiteDatabase database);
    }
}
