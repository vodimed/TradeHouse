package com.common.extensions.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteClosable;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Pair;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * SQLiteDatabase wrapper for inheritance
 */
public class SQLiteDatabase extends SQLiteClosable {
    private final android.database.sqlite.SQLiteDatabase db;
    private final int schema_version;

    public SQLiteDatabase(@NonNull String path, int version) {
        this.db = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(path, null);
        this.schema_version = version;
    }

    public int getSchemaVersion() {
        return schema_version;
    }

    @Override
    protected void finalize() throws Throwable {
        final Method method = db.getClass().getDeclaredMethod("finalize");
        method.setAccessible(true);
        method.invoke(db);
    }

    @Override
    protected void onAllReferencesReleased() {
        try {
            final Method method = db.getClass().getDeclaredMethod("onAllReferencesReleased");
            method.setAccessible(true);
            method.invoke(db);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void beginTransactionNonExclusive() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            db.beginTransactionNonExclusive();
        } else {
            db.beginTransaction();
        }
    }

    public void beginTransactionWithListener(SQLiteTransactionListener transactionListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            db.beginTransactionWithListener(transactionListener);
        }
    }

    public void beginTransactionWithListenerNonExclusive(SQLiteTransactionListener transactionListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            db.beginTransactionWithListenerNonExclusive(transactionListener);
        }
    }

    public SQLiteStatement compileStatement(String sql) {
        return db.compileStatement(sql);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs);
    }

    public static boolean deleteDatabase(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return android.database.sqlite.SQLiteDatabase.deleteDatabase(file);
        } else {
            return false;
        }
    }

    public void disableWriteAheadLogging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.disableWriteAheadLogging();
        }
    }

    public boolean enableWriteAheadLogging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return db.enableWriteAheadLogging();
        } else {
            return false;
        }
    }

    public void endTransaction() {
        db.endTransaction();
    }

    public void execPerConnectionSQL(String sql, Object[] bindArgs) {
        db.execPerConnectionSQL(sql, bindArgs);
    }

    public void execSQL(String sql) {
        db.execSQL(sql);
    }

    public void execSQL(String sql, Object[] bindArgs) {
        db.execSQL(sql, bindArgs);
    }

    public static String findEditTable(String tables) {
        return android.database.sqlite.SQLiteDatabase.findEditTable(tables);
    }

    public List<Pair<String, String>> getAttachedDbs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return db.getAttachedDbs();
        } else {
            return new ArrayList<Pair<String, String>>();
        }
    }

    public long getMaximumSize() {
        return db.getMaximumSize();
    }

    public long getPageSize() {
        return db.getPageSize();
    }

    public String getPath() {
        return db.getPath();
    }

    @SuppressWarnings("deprecation")
    public Map<String, String> getSyncedTables() {
        return db.getSyncedTables();
    }

    public int getVersion() {
        return db.getVersion();
    }

    public boolean inTransaction() {
        return db.inTransaction();
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        return db.insert(table, nullColumnHack, values);
    }

    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        return db.insertOrThrow(table, nullColumnHack, values);
    }

    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return db.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
        } else {
            return db.insert(table, nullColumnHack, initialValues);
        }
    }

    public boolean isDatabaseIntegrityOk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return db.isDatabaseIntegrityOk();
        } else {
            return true;
        }
    }

    public boolean isDbLockedByCurrentThread() {
        return db.isDbLockedByCurrentThread();
    }

    @SuppressWarnings("deprecation")
    public boolean isDbLockedByOtherThreads() {
        return db.isDbLockedByOtherThreads();
    }

    public boolean isOpen() {
        return db.isOpen();
    }

    public boolean isReadOnly() {
        return db.isReadOnly();
    }

    public boolean isWriteAheadLoggingEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return db.isWriteAheadLoggingEnabled();
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public void markTableSyncable(String table, String deletedTable) {
        db.markTableSyncable(table, deletedTable);
    }

    @SuppressWarnings("deprecation")
    public void markTableSyncable(String table, String foreignKey, String updateTable) {
        db.markTableSyncable(table, foreignKey, updateTable);
    }

    public boolean needUpgrade(int newVersion) {
        return db.needUpgrade(newVersion);
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
        } else {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public Cursor queryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return db.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
        } else {
            return db.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        }
    }

    public Cursor queryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory cursorFactory, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return db.queryWithFactory(cursorFactory, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return db.rawQuery(sql, selectionArgs, cancellationSignal);
        } else {
            return db.rawQuery(sql, selectionArgs);
        }
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    public Cursor rawQueryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable, CancellationSignal cancellationSignal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return db.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable, cancellationSignal);
        } else {
            return db.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable);
        }
    }

    public Cursor rawQueryWithFactory(android.database.sqlite.SQLiteDatabase.CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable) {
        return db.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable);
    }

    public static int releaseMemory() {
        return android.database.sqlite.SQLiteDatabase.releaseMemory();
    }

    public long replace(String table, String nullColumnHack, ContentValues initialValues) {
        return db.replace(table, nullColumnHack, initialValues);
    }

    public long replaceOrThrow(String table, String nullColumnHack, ContentValues initialValues) {
        return db.replaceOrThrow(table, nullColumnHack, initialValues);
    }

    public void setCustomAggregateFunction(String functionName, BinaryOperator<String> aggregateFunction) {
        db.setCustomAggregateFunction(functionName, aggregateFunction);
    }

    public void setCustomScalarFunction(String functionName, UnaryOperator<String> scalarFunction) {
        db.setCustomScalarFunction(functionName, scalarFunction);
    }

    public void setForeignKeyConstraintsEnabled(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(enable);
        }
    }

    public void setLocale(Locale locale) {
        db.setLocale(locale);
    }

    @SuppressWarnings("deprecation")
    public void setLockingEnabled(boolean lockingEnabled) {
        db.setLockingEnabled(lockingEnabled);
    }

    public void setMaxSqlCacheSize(int cacheSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            db.setMaxSqlCacheSize(cacheSize);
        }
    }

    public long setMaximumSize(long numBytes) {
        return db.setMaximumSize(numBytes);
    }

    public void setPageSize(long numBytes) {
        db.setPageSize(numBytes);
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void setVersion(int version) {
        db.setVersion(version);
    }

    @NotNull
    @Override
    public String toString() {
        return db.toString();
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    public int updateWithOnConflict(String table, ContentValues values, String whereClause, String[] whereArgs, int conflictAlgorithm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return db.updateWithOnConflict(table, values, whereClause, whereArgs, conflictAlgorithm);
        } else {
            return db.update(table, values, whereClause, whereArgs);
        }
    }

    public void validateSql(String sql, CancellationSignal cancellationSignal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            db.validateSql(sql, cancellationSignal);
        }
    }

    @SuppressWarnings("deprecation")
    public boolean yieldIfContended() {
        return db.yieldIfContended();
    }

    @SuppressWarnings("deprecation")
    public boolean yieldIfContendedSafely() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            return db.yieldIfContendedSafely();
        } else {
            return db.yieldIfContended();
        }
    }

    @SuppressWarnings("deprecation")
    public boolean yieldIfContendedSafely(long sleepAfterYieldDelay) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            return db.yieldIfContendedSafely(sleepAfterYieldDelay);
        } else {
            return db.yieldIfContended();
        }
    }

    /**
     * Constructor candidates
     *
    public static SQLiteDatabase create(android.database.sqlite.SQLiteDatabase.CursorFactory factory) {
        return android.database.sqlite.SQLiteDatabase.create(factory);
    }

    public static SQLiteDatabase createInMemory(android.database.sqlite.SQLiteDatabase.OpenParams openParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return android.database.sqlite.SQLiteDatabase.createInMemory(openParams);
        }
    }

    public static SQLiteDatabase openDatabase(String path, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int flags) {
        return android.database.sqlite.SQLiteDatabase.openDatabase(path, factory, flags);
    }

    public static SQLiteDatabase openDatabase(File path, android.database.sqlite.SQLiteDatabase.OpenParams openParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            return android.database.sqlite.SQLiteDatabase.openDatabase(path, openParams);
        }
    }

    public static SQLiteDatabase openDatabase(String path, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int flags, DatabaseErrorHandler errorHandler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.database.sqlite.SQLiteDatabase.openDatabase(path, factory, flags, errorHandler);
        }
    }

    public static SQLiteDatabase openOrCreateDatabase(File file, android.database.sqlite.SQLiteDatabase.CursorFactory factory) {
        return android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(file, factory);
    }

    public static SQLiteDatabase openOrCreateDatabase(String path, android.database.sqlite.SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(path, factory, errorHandler);
        }
    }

    public static SQLiteDatabase openOrCreateDatabase(String path, android.database.sqlite.SQLiteDatabase.CursorFactory factory) {
        return android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(path, factory);
    }
    */
}
