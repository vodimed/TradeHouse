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
import com.expertek.tradehouse.dictionaries.entity.user;
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
public final class Users_Impl implements Users {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<user> __insertionAdapterOfuser;

  private final EntityDeletionOrUpdateAdapter<user> __deletionAdapterOfuser;

  public Users_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfuser = new EntityInsertionAdapter<user>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `TH_users` (`userID`,`userName`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, user value) {
        if (value.userID == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.userID);
        }
        if (value.userName == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.userName);
        }
      }
    };
    this.__deletionAdapterOfuser = new EntityDeletionOrUpdateAdapter<user>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `TH_users` WHERE `userID` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, user value) {
        if (value.userID == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.userID);
        }
      }
    };
  }

  @Override
  public void insertAll(final user... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfuser.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final user objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfuser.handle(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public DataSource.Factory<Integer, user> getAll() {
    final String _sql = "SELECT `TH_users`.`userID` AS `userID`, `TH_users`.`userName` AS `userName` FROM TH_users";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new DataSource.Factory<Integer, user>() {
      @Override
      public LimitOffsetDataSource<user> create() {
        return new LimitOffsetDataSource<user>(__db, _statement, false, true , "TH_users") {
          @Override
          protected List<user> convertRows(Cursor cursor) {
            final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(cursor, "userID");
            final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(cursor, "userName");
            final List<user> _res = new ArrayList<user>(cursor.getCount());
            while(cursor.moveToNext()) {
              final user _item;
              _item = new user();
              if (cursor.isNull(_cursorIndexOfUserID)) {
                _item.userID = null;
              } else {
                _item.userID = cursor.getString(_cursorIndexOfUserID);
              }
              if (cursor.isNull(_cursorIndexOfUserName)) {
                _item.userName = null;
              } else {
                _item.userName = cursor.getString(_cursorIndexOfUserName);
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
  public DataSource.Factory<Integer, user> loadAllByIds(final String[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM TH_users WHERE userID IN (");
    final int _inputSize = objIds.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : objIds) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    return new DataSource.Factory<Integer, user>() {
      @Override
      public LimitOffsetDataSource<user> create() {
        return new LimitOffsetDataSource<user>(__db, _statement, false, true , "TH_users") {
          @Override
          protected List<user> convertRows(Cursor cursor) {
            final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(cursor, "userID");
            final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(cursor, "userName");
            final List<user> _res = new ArrayList<user>(cursor.getCount());
            while(cursor.moveToNext()) {
              final user _item_1;
              _item_1 = new user();
              if (cursor.isNull(_cursorIndexOfUserID)) {
                _item_1.userID = null;
              } else {
                _item_1.userID = cursor.getString(_cursorIndexOfUserID);
              }
              if (cursor.isNull(_cursorIndexOfUserName)) {
                _item_1.userName = null;
              } else {
                _item_1.userName = cursor.getString(_cursorIndexOfUserName);
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
