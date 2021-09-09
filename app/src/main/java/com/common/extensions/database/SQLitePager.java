package com.common.extensions.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PositionalDataSource;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        this.counter.bindAllArgsAsStrings((String[]) args);
        this.args = Arrays.copyOf(args, args.length + 2);
    }

    protected abstract Value convertRow(@NonNull Cursor cursor);

    protected Value convertRow(@NonNull Value object, @NonNull Cursor cursor) {
        final Field[] fields = object.getClass().getFields();

        for (Field field : fields) {
            final int index = cursor.getColumnIndex(field.getName());

            if (index > 0) try {
                switch (cursor.getType(index)) {
                    case Cursor.FIELD_TYPE_NULL:
                        field.set(object, null);
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        field.set(object, cursor.getLong(index));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        field.set(object, cursor.getDouble(index));
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        field.set(object, cursor.getString(index));
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

        final Cursor cursor = db.rawQuery(pager, (String[]) args);
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
        private final String query;
        private final Object[] args;
        private final Value object;

        public Factory(@NonNull Value object, @NonNull SQLiteDatabase db, @NonNull String query, Object... args) {
            this.db = db;
            this.query = query;
            this.args = args;
            this.object = object;
        }

        protected Value convertRow(@NonNull Value object, @NonNull Cursor cursor) {
            return object;
        }

        @NotNull
        @Override
        public DataSource<Integer, Value> create() {
            return new SQLitePager<Value>(db, query, args) {
                @Override
                @SuppressWarnings("unchecked")
                protected Value convertRow(@NonNull Cursor cursor) {
                    Value value = (Value) deepCopy(object);
                    value = super.convertRow(value, cursor);
                    return convertRow(value, cursor);
                }
            };
        }

        private static Object deepCopy(Object original) {
            try {
                ObjectOutputStream oos = null;
                ObjectInputStream ois = null;

                try {
                    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(original);
                    oos.flush();

                    final ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
                    ois = new ObjectInputStream(bin);
                    return ois.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (oos != null) oos.close();
                    if (ois != null) ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
