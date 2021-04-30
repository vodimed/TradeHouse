package com.expertek.tradehouse.database;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

/**
 * Copy necessary methods from RoomDatabase class,
 * and create interface to your room-database class,
 * describing DAO methods, based on this interface:
 * public interface DBDocuments extends BaseFace {
 *     // Describe your DAO methods here
 * }
 * public abstract class DBDocuments_v1 extends
 *     RoomDatabase implements DBDocuments
 * {
 *     // No DAO descriptions here anymore
 * }
 */
public interface BaseFace {
    void runInTransaction(@NonNull Runnable body);
    <V> V runInTransaction(@NonNull Callable<V> body);
    boolean inTransaction();
}
