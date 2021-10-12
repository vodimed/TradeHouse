package com.common.extensions.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Partial implementation of LimitOffsetDataSource (from Room database)
 */
@SuppressWarnings("deprecation")
public abstract class SQLitePager<Value extends Serializable> extends PositionalDataSource<Value> {
    private final SQLiteDatabase db;
    private final SQLiteStatement counter;
    private final String pager;
    private final Object[] args;

    protected SQLitePager(@NonNull SQLiteDatabase db, @NonNull String query, Object[] args) {
        this.db = db;
        this.pager = "SELECT * FROM ( " + query + " ) LIMIT ? OFFSET ?";
        this.counter = db.compileStatement("SELECT COUNT(*) FROM ( " + query + " )");
        bindArgs(this.counter, args);
        this.args = Arrays.copyOf(args, args.length + 2);
    }

    protected abstract Value convertRow(@NonNull Cursor cursor);

    protected Value convertRow(@NonNull Value object, @NonNull Cursor cursor) {
        final Field[] fields = object.getClass().getFields();

        for (Field field : fields) {
            final int index = cursor.getColumnIndex(field.getName());
            final Class<?> type = field.getType();

            if (index >= 0) try {
                switch (cursor.getType(index)) {
                    case Cursor.FIELD_TYPE_NULL:
                        field.set(object, null);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        if (type.equals(Boolean.TYPE)) {
                            field.setBoolean(object, cursor.getShort(index) != 0);
                        } else if (type.equals(Short.TYPE)) {
                            field.setShort(object, cursor.getShort(index));
                        } else if (type.equals(Integer.TYPE)) {
                            field.setInt(object, cursor.getInt(index));
                        } else {
                            field.setLong(object, cursor.getLong(index));
                        }
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        if (type.equals(Float.TYPE)) {
                            field.setFloat(object, cursor.getFloat(index));
                        } else if (type.equals(Double.TYPE)) {
                            field.setDouble(object, cursor.getDouble(index));
                        }
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        if (type.equals(String.class)) {
                            field.set(object, cursor.getString(index));
                        }
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        field.set(object, cursor.getBlob(index));
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    @Override
    public void loadInitial(@NotNull LoadInitialParams loadInitialParams, @NotNull LoadInitialCallback<Value> loadInitialCallback) {
        // Do Nothing
    }

    @Override
    public void loadRange(@NotNull LoadRangeParams loadRangeParams, @NotNull LoadRangeCallback<Value> loadRangeCallback) {
        loadRangeCallback.onResult(loadRange(loadRangeParams.startPosition, loadRangeParams.loadSize));
    }

    public int countItems() {
        return (int) counter.simpleQueryForLong();
    }

    @NonNull
    public List<Value> loadRange(int startPosition, int loadCount) {
        final ArrayList<Value> result = new ArrayList<Value>();
        args[args.length - 2] = loadCount;
        args[args.length - 1] = startPosition;

        final String[] sagrs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                sagrs[i] = (String) args[i];
            } else if (args[i] instanceof Long) {
                sagrs[i] = String.valueOf((Long) args[i]);
            } else if (args[i] instanceof Integer) {
                sagrs[i] = String.valueOf((Integer) args[i]);
            }
        }

        final Cursor cursor = db.rawQuery(pager, sagrs);
        try {
            while (cursor.moveToNext()) {
                result.add(convertRow(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    private static void bindArgs(SQLiteStatement stmt, Object[] args) {
        for (int i = 1; i <= args.length; i++) {
            final Object arg = args[i - 1];

            if (arg == null) {
                stmt.bindNull(i);
            } else if (arg instanceof String) {
                stmt.bindString(i, (String) arg);
            } else if (arg instanceof Double) {
                stmt.bindDouble(i, (Double) arg);
            } else if (arg instanceof Integer) {
                stmt.bindLong(i, (Integer) arg);
            } else if (arg instanceof Long) {
                stmt.bindLong(i, (Long) arg);
            } else if (arg instanceof Boolean) {
                stmt.bindLong(i, ((Boolean) arg ? 1 : 0));
            } else {
                stmt.bindBlob(i, (byte[]) arg);
            }
        }
    }

    public static class Factory<Value extends Serializable> extends DataSource.Factory<Integer, Value> {
        private final SQLiteDatabase db;
        private final Class<Value> cls;
        private final String query;
        private final Object[] args;

        public Factory(@NonNull SQLiteDatabase db, @NonNull Class<Value> cls, @NonNull String query, Object... args) {
            this.db = db;
            this.cls = cls;
            this.query = query;
            this.args = args;
        }

        protected Value convertRow(@NonNull Value object, @NonNull Cursor cursor) {
            return object;
        }

        @NotNull
        @Override
        public DataSource<Integer, Value> create() {
            return new SQLitePager<Value>(db, query, args) {
                @Override
                protected Value convertRow(@NonNull Cursor cursor) {
                    try {
                        Value value = cls.newInstance();
                        value = super.convertRow(value, cursor);
                        return convertRow(value, cursor);
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }
    }
}
