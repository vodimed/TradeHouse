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
import com.expertek.tradehouse.documents.entity.line;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Lines_Impl implements Lines {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<line> __insertionAdapterOfline;

  private final EntityDeletionOrUpdateAdapter<line> __deletionAdapterOfline;

  public Lines_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfline = new EntityInsertionAdapter<line>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `MT_lines` (`LineID`,`DocName`,`Pos`,`GoodsID`,`GoodsName`,`UnitBC`,`BC`,`Price`,`DocQnty`,`FactQnty`,`AlcCode`,`PartIDTH`,`Flags`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, line value) {
        stmt.bindLong(1, value.LineID);
        if (value.DocName == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.DocName);
        }
        stmt.bindLong(3, value.Pos);
        stmt.bindLong(4, value.GoodsID);
        if (value.GoodsName == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.GoodsName);
        }
        if (value.UnitBC == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.UnitBC);
        }
        if (value.BC == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.BC);
        }
        stmt.bindDouble(8, value.Price);
        stmt.bindDouble(9, value.DocQnty);
        stmt.bindDouble(10, value.FactQnty);
        if (value.AlcCode == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.AlcCode);
        }
        if (value.PartIDTH == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindString(12, value.PartIDTH);
        }
        stmt.bindLong(13, value.Flags);
      }
    };
    this.__deletionAdapterOfline = new EntityDeletionOrUpdateAdapter<line>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `MT_lines` WHERE `LineID` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, line value) {
        stmt.bindLong(1, value.LineID);
      }
    };
  }

  @Override
  public void insertAll(final line... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfline.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final line objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfline.handle(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<line> getAll() {
    final String _sql = "SELECT `MT_lines`.`LineID` AS `LineID`, `MT_lines`.`DocName` AS `DocName`, `MT_lines`.`Pos` AS `Pos`, `MT_lines`.`GoodsID` AS `GoodsID`, `MT_lines`.`GoodsName` AS `GoodsName`, `MT_lines`.`UnitBC` AS `UnitBC`, `MT_lines`.`BC` AS `BC`, `MT_lines`.`Price` AS `Price`, `MT_lines`.`DocQnty` AS `DocQnty`, `MT_lines`.`FactQnty` AS `FactQnty`, `MT_lines`.`AlcCode` AS `AlcCode`, `MT_lines`.`PartIDTH` AS `PartIDTH`, `MT_lines`.`Flags` AS `Flags` FROM MT_lines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfLineID = CursorUtil.getColumnIndexOrThrow(_cursor, "LineID");
      final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(_cursor, "DocName");
      final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "Pos");
      final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(_cursor, "GoodsID");
      final int _cursorIndexOfGoodsName = CursorUtil.getColumnIndexOrThrow(_cursor, "GoodsName");
      final int _cursorIndexOfUnitBC = CursorUtil.getColumnIndexOrThrow(_cursor, "UnitBC");
      final int _cursorIndexOfBC = CursorUtil.getColumnIndexOrThrow(_cursor, "BC");
      final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "Price");
      final int _cursorIndexOfDocQnty = CursorUtil.getColumnIndexOrThrow(_cursor, "DocQnty");
      final int _cursorIndexOfFactQnty = CursorUtil.getColumnIndexOrThrow(_cursor, "FactQnty");
      final int _cursorIndexOfAlcCode = CursorUtil.getColumnIndexOrThrow(_cursor, "AlcCode");
      final int _cursorIndexOfPartIDTH = CursorUtil.getColumnIndexOrThrow(_cursor, "PartIDTH");
      final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(_cursor, "Flags");
      final List<line> _result = new ArrayList<line>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final line _item;
        _item = new line();
        _item.LineID = _cursor.getInt(_cursorIndexOfLineID);
        if (_cursor.isNull(_cursorIndexOfDocName)) {
          _item.DocName = null;
        } else {
          _item.DocName = _cursor.getString(_cursorIndexOfDocName);
        }
        _item.Pos = _cursor.getInt(_cursorIndexOfPos);
        _item.GoodsID = _cursor.getInt(_cursorIndexOfGoodsID);
        if (_cursor.isNull(_cursorIndexOfGoodsName)) {
          _item.GoodsName = null;
        } else {
          _item.GoodsName = _cursor.getString(_cursorIndexOfGoodsName);
        }
        if (_cursor.isNull(_cursorIndexOfUnitBC)) {
          _item.UnitBC = null;
        } else {
          _item.UnitBC = _cursor.getString(_cursorIndexOfUnitBC);
        }
        if (_cursor.isNull(_cursorIndexOfBC)) {
          _item.BC = null;
        } else {
          _item.BC = _cursor.getString(_cursorIndexOfBC);
        }
        _item.Price = _cursor.getDouble(_cursorIndexOfPrice);
        _item.DocQnty = _cursor.getDouble(_cursorIndexOfDocQnty);
        _item.FactQnty = _cursor.getDouble(_cursorIndexOfFactQnty);
        if (_cursor.isNull(_cursorIndexOfAlcCode)) {
          _item.AlcCode = null;
        } else {
          _item.AlcCode = _cursor.getString(_cursorIndexOfAlcCode);
        }
        if (_cursor.isNull(_cursorIndexOfPartIDTH)) {
          _item.PartIDTH = null;
        } else {
          _item.PartIDTH = _cursor.getString(_cursorIndexOfPartIDTH);
        }
        _item.Flags = _cursor.getInt(_cursorIndexOfFlags);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<line> loadAllByIds(final int[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM MT_lines WHERE LineID IN (");
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
      final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(_cursor, "Pos");
      final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(_cursor, "GoodsID");
      final int _cursorIndexOfGoodsName = CursorUtil.getColumnIndexOrThrow(_cursor, "GoodsName");
      final int _cursorIndexOfUnitBC = CursorUtil.getColumnIndexOrThrow(_cursor, "UnitBC");
      final int _cursorIndexOfBC = CursorUtil.getColumnIndexOrThrow(_cursor, "BC");
      final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "Price");
      final int _cursorIndexOfDocQnty = CursorUtil.getColumnIndexOrThrow(_cursor, "DocQnty");
      final int _cursorIndexOfFactQnty = CursorUtil.getColumnIndexOrThrow(_cursor, "FactQnty");
      final int _cursorIndexOfAlcCode = CursorUtil.getColumnIndexOrThrow(_cursor, "AlcCode");
      final int _cursorIndexOfPartIDTH = CursorUtil.getColumnIndexOrThrow(_cursor, "PartIDTH");
      final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(_cursor, "Flags");
      final List<line> _result = new ArrayList<line>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final line _item_1;
        _item_1 = new line();
        _item_1.LineID = _cursor.getInt(_cursorIndexOfLineID);
        if (_cursor.isNull(_cursorIndexOfDocName)) {
          _item_1.DocName = null;
        } else {
          _item_1.DocName = _cursor.getString(_cursorIndexOfDocName);
        }
        _item_1.Pos = _cursor.getInt(_cursorIndexOfPos);
        _item_1.GoodsID = _cursor.getInt(_cursorIndexOfGoodsID);
        if (_cursor.isNull(_cursorIndexOfGoodsName)) {
          _item_1.GoodsName = null;
        } else {
          _item_1.GoodsName = _cursor.getString(_cursorIndexOfGoodsName);
        }
        if (_cursor.isNull(_cursorIndexOfUnitBC)) {
          _item_1.UnitBC = null;
        } else {
          _item_1.UnitBC = _cursor.getString(_cursorIndexOfUnitBC);
        }
        if (_cursor.isNull(_cursorIndexOfBC)) {
          _item_1.BC = null;
        } else {
          _item_1.BC = _cursor.getString(_cursorIndexOfBC);
        }
        _item_1.Price = _cursor.getDouble(_cursorIndexOfPrice);
        _item_1.DocQnty = _cursor.getDouble(_cursorIndexOfDocQnty);
        _item_1.FactQnty = _cursor.getDouble(_cursorIndexOfFactQnty);
        if (_cursor.isNull(_cursorIndexOfAlcCode)) {
          _item_1.AlcCode = null;
        } else {
          _item_1.AlcCode = _cursor.getString(_cursorIndexOfAlcCode);
        }
        if (_cursor.isNull(_cursorIndexOfPartIDTH)) {
          _item_1.PartIDTH = null;
        } else {
          _item_1.PartIDTH = _cursor.getString(_cursorIndexOfPartIDTH);
        }
        _item_1.Flags = _cursor.getInt(_cursorIndexOfFlags);
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
