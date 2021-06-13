package com.expertek.tradehouse.documents;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.expertek.tradehouse.documents.entity.markline;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Marklines_Impl implements Marklines {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<markline> __insertionAdapterOfmarkline;

  private final EntityDeletionOrUpdateAdapter<markline> __deletionAdapterOfmarkline;

  public Marklines_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfmarkline = new EntityInsertionAdapter<markline>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `MT_MarkLines` (`LineID`,`DocName`,`MarkCode`,`PartIDTH`,`Sts`,`MarkParent`,`BoxQnty`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, markline value) {
        stmt.bindLong(1, value.LineID);
        if (value.DocName == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.DocName);
        }
        if (value.MarkCode == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.MarkCode);
        }
        if (value.PartIDTH == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.PartIDTH);
        }
        if (value.Sts == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.Sts);
        }
        if (value.MarkParent == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.MarkParent);
        }
        stmt.bindLong(7, value.BoxQnty);
      }
    };
    this.__deletionAdapterOfmarkline = new EntityDeletionOrUpdateAdapter<markline>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `MT_MarkLines` WHERE `LineID` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, markline value) {
        stmt.bindLong(1, value.LineID);
      }
    };
  }

  @Override
  public void insertAll(final markline... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfmarkline.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final markline objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfmarkline.handle(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<markline> getAll() {
    final String _sql = "SELECT `MT_MarkLines`.`LineID` AS `LineID`, `MT_MarkLines`.`DocName` AS `DocName`, `MT_MarkLines`.`MarkCode` AS `MarkCode`, `MT_MarkLines`.`PartIDTH` AS `PartIDTH`, `MT_MarkLines`.`Sts` AS `Sts`, `MT_MarkLines`.`MarkParent` AS `MarkParent`, `MT_MarkLines`.`BoxQnty` AS `BoxQnty` FROM MT_MarkLines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfLineID = CursorUtil.getColumnIndexOrThrow(_cursor, "LineID");
      final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(_cursor, "DocName");
      final int _cursorIndexOfMarkCode = CursorUtil.getColumnIndexOrThrow(_cursor, "MarkCode");
      final int _cursorIndexOfPartIDTH = CursorUtil.getColumnIndexOrThrow(_cursor, "PartIDTH");
      final int _cursorIndexOfSts = CursorUtil.getColumnIndexOrThrow(_cursor, "Sts");
      final int _cursorIndexOfMarkParent = CursorUtil.getColumnIndexOrThrow(_cursor, "MarkParent");
      final int _cursorIndexOfBoxQnty = CursorUtil.getColumnIndexOrThrow(_cursor, "BoxQnty");
      final List<markline> _result = new ArrayList<markline>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final markline _item;
        _item = new markline();
        _item.LineID = _cursor.getInt(_cursorIndexOfLineID);
        if (_cursor.isNull(_cursorIndexOfDocName)) {
          _item.DocName = null;
        } else {
          _item.DocName = _cursor.getString(_cursorIndexOfDocName);
        }
        if (_cursor.isNull(_cursorIndexOfMarkCode)) {
          _item.MarkCode = null;
        } else {
          _item.MarkCode = _cursor.getString(_cursorIndexOfMarkCode);
        }
        if (_cursor.isNull(_cursorIndexOfPartIDTH)) {
          _item.PartIDTH = null;
        } else {
          _item.PartIDTH = _cursor.getString(_cursorIndexOfPartIDTH);
        }
        if (_cursor.isNull(_cursorIndexOfSts)) {
          _item.Sts = null;
        } else {
          _item.Sts = _cursor.getString(_cursorIndexOfSts);
        }
        if (_cursor.isNull(_cursorIndexOfMarkParent)) {
          _item.MarkParent = null;
        } else {
          _item.MarkParent = _cursor.getString(_cursorIndexOfMarkParent);
        }
        _item.BoxQnty = _cursor.getInt(_cursorIndexOfBoxQnty);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<markline> loadAllByIds(final int[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM MT_MarkLines WHERE LineID IN (");
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
      final int _cursorIndexOfLineID = CursorUtil.getColumnIndexOrThrow(_cursor, "LineID");
      final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(_cursor, "DocName");
      final int _cursorIndexOfMarkCode = CursorUtil.getColumnIndexOrThrow(_cursor, "MarkCode");
      final int _cursorIndexOfPartIDTH = CursorUtil.getColumnIndexOrThrow(_cursor, "PartIDTH");
      final int _cursorIndexOfSts = CursorUtil.getColumnIndexOrThrow(_cursor, "Sts");
      final int _cursorIndexOfMarkParent = CursorUtil.getColumnIndexOrThrow(_cursor, "MarkParent");
      final int _cursorIndexOfBoxQnty = CursorUtil.getColumnIndexOrThrow(_cursor, "BoxQnty");
      final List<markline> _result = new ArrayList<markline>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final markline _item_1;
        _item_1 = new markline();
        _item_1.LineID = _cursor.getInt(_cursorIndexOfLineID);
        if (_cursor.isNull(_cursorIndexOfDocName)) {
          _item_1.DocName = null;
        } else {
          _item_1.DocName = _cursor.getString(_cursorIndexOfDocName);
        }
        if (_cursor.isNull(_cursorIndexOfMarkCode)) {
          _item_1.MarkCode = null;
        } else {
          _item_1.MarkCode = _cursor.getString(_cursorIndexOfMarkCode);
        }
        if (_cursor.isNull(_cursorIndexOfPartIDTH)) {
          _item_1.PartIDTH = null;
        } else {
          _item_1.PartIDTH = _cursor.getString(_cursorIndexOfPartIDTH);
        }
        if (_cursor.isNull(_cursorIndexOfSts)) {
          _item_1.Sts = null;
        } else {
          _item_1.Sts = _cursor.getString(_cursorIndexOfSts);
        }
        if (_cursor.isNull(_cursorIndexOfMarkParent)) {
          _item_1.MarkParent = null;
        } else {
          _item_1.MarkParent = _cursor.getString(_cursorIndexOfMarkParent);
        }
        _item_1.BoxQnty = _cursor.getInt(_cursorIndexOfBoxQnty);
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
