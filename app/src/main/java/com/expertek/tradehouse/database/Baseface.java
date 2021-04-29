package com.expertek.tradehouse.database;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

/**
 * Copy necessary methods from RoomDatabase class
 */
public interface Baseface {
    void runInTransaction(@NonNull Runnable body);
    <V> V runInTransaction(@NonNull Callable<V> body);
    boolean inTransaction();
}
