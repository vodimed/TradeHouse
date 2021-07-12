package com.expertek.tradehouse.dictionaries;

import android.database.Cursor;
import androidx.paging.DataSource;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.paging.LimitOffsetDataSource;
import androidx.room.util.CursorUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.expertek.tradehouse.dictionaries.entity.object;
import java.lang.Class;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Objects_Impl implements Objects {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<object> __insertionAdapterOfobject;

  private final EntityDeletionOrUpdateAdapter<object> __deletionAdapterOfobject;

  public Objects_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfobject = new EntityInsertionAdapter<object>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `TH_objects` (`obj_code`,`obj_type`,`Name`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, object value) {
        stmt.bindLong(1, value.obj_code);
        if (value.obj_type == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.obj_type);
        }
        if (value.Name == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.Name);
        }
      }
    };
    this.__deletionAdapterOfobject = new EntityDeletionOrUpdateAdapter<object>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `TH_objects` WHERE `obj_code` = ? AND `obj_type` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, object value) {
        stmt.bindLong(1, value.obj_code);
        if (value.obj_type == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.obj_type);
        }
      }
    };
  }

  @Override
  public void insertAll(final object... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfobject.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final object object) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfobject.handle(object);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public DataSource.Factory<Integer, object> getAll() {
    final String _sql = "SELECT `TH_objects`.`obj_code` AS `obj_code`, `TH_objects`.`obj_type` AS `obj_type`, `TH_objects`.`Name` AS `Name` FROM TH_objects";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new DataSource.Factory<Integer, object>() {
      @Override
      public LimitOffsetDataSource<object> create() {
        return new LimitOffsetDataSource<object>(__db, _statement, false, true , "TH_objects") {
          @Override
          protected List<object> convertRows(Cursor cursor) {
            final int _cursorIndexOfObjCode = CursorUtil.getColumnIndexOrThrow(cursor, "obj_code");
            final int _cursorIndexOfObjType = CursorUtil.getColumnIndexOrThrow(cursor, "obj_type");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(cursor, "Name");
            final List<object> _res = new ArrayList<object>(cursor.getCount());
            while(cursor.moveToNext()) {
              final object _item;
              _item = new object();
              _item.obj_code = cursor.getInt(_cursorIndexOfObjCode);
              if (cursor.isNull(_cursorIndexOfObjType)) {
                _item.obj_type = null;
              } else {
                _item.obj_type = cursor.getString(_cursorIndexOfObjType);
              }
              if (cursor.isNull(_cursorIndexOfName)) {
                _item.Name = null;
              } else {
                _item.Name = cursor.getString(_cursorIndexOfName);
              }
              _res.add(_item);
            }
            return _res;
          }
        };
      }
    };
  }

  @Override
  public DataSource.Factory<Integer, object> loadAllByIds(final int[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM TH_objects WHERE obj_code IN (");
    final int _inputSize = objIds.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int _item : objIds) {
      _statement.bindLong(_argIndex, _item);
      _argIndex ++;
    }
    return new DataSource.Factory<Integer, object>() {
      @Override
      public LimitOffsetDataSource<object> create() {
        return new LimitOffsetDataSource<object>(__db, _statement, false, true , "TH_objects") {
          @Override
          protected List<object> convertRows(Cursor cursor) {
            final int _cursorIndexOfObjCode = CursorUtil.getColumnIndexOrThrow(cursor, "obj_code");
            final int _cursorIndexOfObjType = CursorUtil.getColumnIndexOrThrow(cursor, "obj_type");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(cursor, "Name");
            final List<object> _res = new ArrayList<object>(cursor.getCount());
            while(cursor.moveToNext()) {
              final object _item_1;
              _item_1 = new object();
              _item_1.obj_code = cursor.getInt(_cursorIndexOfObjCode);
              if (cursor.isNull(_cursorIndexOfObjType)) {
                _item_1.obj_type = null;
              } else {
                _item_1.obj_type = cursor.getString(_cursorIndexOfObjType);
              }
              if (cursor.isNull(_cursorIndexOfName)) {
                _item_1.Name = null;
              } else {
                _item_1.Name = cursor.getString(_cursorIndexOfName);
              }
              _res.add(_item_1);
            }
            return _res;
          }
        };
      }
    };
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
