package com.expertek.tradehouse.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;

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
}
