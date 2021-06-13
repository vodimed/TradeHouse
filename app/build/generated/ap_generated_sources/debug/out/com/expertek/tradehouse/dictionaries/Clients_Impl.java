package com.expertek.tradehouse.dictionaries;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.expertek.tradehouse.dictionaries.entity.client;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Clients_Impl implements Clients {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<client> __insertionAdapterOfclient;

  private final EntityDeletionOrUpdateAdapter<client> __deletionAdapterOfclient;

  public Clients_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfclient = new EntityInsertionAdapter<client>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `TH_clients` (`cli_code`,`cli_type`,`Name`) VALUES (?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, client value) {
        stmt.bindLong(1, value.cli_code);
        stmt.bindLong(2, value.cli_type);
        if (value.Name == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.Name);
        }
      }
    };
    this.__deletionAdapterOfclient = new EntityDeletionOrUpdateAdapter<client>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `TH_clients` WHERE `cli_code` = ? AND `cli_type` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, client value) {
        stmt.bindLong(1, value.cli_code);
        stmt.bindLong(2, value.cli_type);
      }
    };
  }

  @Override
  public void insertAll(final client... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfclient.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final client objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfclient.handle(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<client> getAll() {
    final String _sql = "SELECT `TH_clients`.`cli_code` AS `cli_code`, `TH_clients`.`cli_type` AS `cli_type`, `TH_clients`.`Name` AS `Name` FROM TH_clients";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfCliCode = CursorUtil.getColumnIndexOrThrow(_cursor, "cli_code");
      final int _cursorIndexOfCliType = CursorUtil.getColumnIndexOrThrow(_cursor, "cli_type");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "Name");
      final List<client> _result = new ArrayList<client>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final client _item;
        _item = new client();
        _item.cli_code = _cursor.getInt(_cursorIndexOfCliCode);
        _item.cli_type = _cursor.getInt(_cursorIndexOfCliType);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.Name = null;
        } else {
          _item.Name = _cursor.getString(_cursorIndexOfName);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<client> loadAllByIds(final int[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM TH_clients WHERE cli_code IN (");
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
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfCliCode = CursorUtil.getColumnIndexOrThrow(_cursor, "cli_code");
      final int _cursorIndexOfCliType = CursorUtil.getColumnIndexOrThrow(_cursor, "cli_type");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "Name");
      final List<client> _result = new ArrayList<client>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final client _item_1;
        _item_1 = new client();
        _item_1.cli_code = _cursor.getInt(_cursorIndexOfCliCode);
        _item_1.cli_type = _cursor.getInt(_cursorIndexOfCliType);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item_1.Name = null;
        } else {
          _item_1.Name = _cursor.getString(_cursorIndexOfName);
        }
        _result.add(_item_1);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
