package com.common.extensions.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import com.common.extensions.Logger;

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
    private final String[] args;

    protected SQLitePager(@NonNull SQLiteDatabase db, @NonNull String query, Object[] args) {
        this.db = db;
        this.pager = "SELECT * FROM ( " + query + " ) LIMIT ? OFFSET ?";
        this.counter = db.compileStatement("SELECT COUNT(*) FROM ( " + query + " )");
        this.counter.bindAllArgsAsStrings(strargs(args));
        if (args != null) {
            this.args = strargs(Arrays.copyOf(args, args.length + 2));
        } else {
            this.args = new String[2];
        }
    }

    private static String[] strargs(Object[] args) {
        if (args instanceof String[]) {
            return (String[]) args;
        } else if (args == null) {
            return null;
        } else {
            final String[] strargs = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                strargs[i] = String.valueOf(args[i]);
            }
            return strargs;
        }
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
                        } else if (type.equals(java.util.Date.class)) {
                            field.set(object, SQLiteSchema.DateConverter.load(cursor.getString(index)));
                        }
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        field.set(object, cursor.getBlob(index));
                        break;
                }
            } catch (IllegalAccessException e) {
                Logger.e(e);
            }
        }
        return object;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams loadInitialParams, @NonNull LoadInitialCallback<Value> loadInitialCallback) {
        // Do Nothing
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams loadRangeParams, @NonNull LoadRangeCallback<Value> loadRangeCallback) {
        loadRangeCallback.onResult(loadRange(loadRangeParams.startPosition, loadRangeParams.loadSize));
    }

    public int countItems() {
        return (int) counter.simpleQueryForLong();
    }

    @NonNull
    public List<Value> loadRange(int startPosition, int loadCount) {
        final ArrayList<Value> result = new ArrayList<Value>();
        args[args.length - 2] = String.valueOf(loadCount);
        args[args.length - 1] = String.valueOf(startPosition);

        final Cursor cursor = db.rawQuery(pager, args);
        try {
            while (cursor.moveToNext()) {
                result.add(convertRow(cursor));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public static class Factory<Value extends Serializable> extends DataSource.Factory<Integer, Value> {
        private final SQLiteDatabase db;
        private final Class<Value> cls;
        private final String query;
        private final Object[] args;

        public Factory(@NonNull SQLiteDatabase db, @NonNull Class<Value> cls, @NonNull String query) {
            this(db, cls, query, null);
        }

        public Factory(@NonNull SQLiteDatabase db, @NonNull Class<Value> cls, @NonNull String query, Object args) {
            this(db, cls, query, new Object[]{args});
        }

        public Factory(@NonNull SQLiteDatabase db, @NonNull Class<Value> cls, @NonNull String query, Object[] args) {
            this.db = db;
            this.cls = cls;
            this.query = query;
            this.args = args;
        }

        protected Value convertRow(@NonNull Value object, @NonNull Cursor cursor) {
            return object;
        }

        @NonNull
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
                        Logger.e(e);
                        return null;
                    }
                }
            };
        }
    }
}
