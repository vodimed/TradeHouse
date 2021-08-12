package com.expertek.tradehouse.documents;

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
import com.expertek.tradehouse.documents.entity.line;
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
public final class Lines_Impl implements Lines {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<line> __insertionAdapterOfline;

  private final EntityDeletionOrUpdateAdapter<line> __deletionAdapterOfline;

  public Lines_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfline = new EntityInsertionAdapter<line>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `MT_lines` (`LineID`,`DocName`,`Pos`,`GoodsID`,`GoodsName`,`UnitBC`,`BC`,`Price`,`DocQnty`,`FactQnty`,`AlcCode`,`PartIDTH`,`Flags`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
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
  public void insert(final line... objects) {
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
  public void delete(final line... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfline.handleMultiple(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public DataSource.Factory<Integer, line> getAll() {
    final String _sql = "SELECT `MT_lines`.`LineID` AS `LineID`, `MT_lines`.`DocName` AS `DocName`, `MT_lines`.`Pos` AS `Pos`, `MT_lines`.`GoodsID` AS `GoodsID`, `MT_lines`.`GoodsName` AS `GoodsName`, `MT_lines`.`UnitBC` AS `UnitBC`, `MT_lines`.`BC` AS `BC`, `MT_lines`.`Price` AS `Price`, `MT_lines`.`DocQnty` AS `DocQnty`, `MT_lines`.`FactQnty` AS `FactQnty`, `MT_lines`.`AlcCode` AS `AlcCode`, `MT_lines`.`PartIDTH` AS `PartIDTH`, `MT_lines`.`Flags` AS `Flags` FROM MT_lines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new DataSource.Factory<Integer, line>() {
      @Override
      public LimitOffsetDataSource<line> create() {
        return new LimitOffsetDataSource<line>(__db, _statement, false, true , "MT_lines") {
          @Override
          protected List<line> convertRows(Cursor cursor) {
            final int _cursorIndexOfLineID = CursorUtil.getColumnIndexOrThrow(cursor, "LineID");
            final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(cursor, "DocName");
            final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(cursor, "Pos");
            final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsID");
            final int _cursorIndexOfGoodsName = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsName");
            final int _cursorIndexOfUnitBC = CursorUtil.getColumnIndexOrThrow(cursor, "UnitBC");
            final int _cursorIndexOfBC = CursorUtil.getColumnIndexOrThrow(cursor, "BC");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(cursor, "Price");
            final int _cursorIndexOfDocQnty = CursorUtil.getColumnIndexOrThrow(cursor, "DocQnty");
            final int _cursorIndexOfFactQnty = CursorUtil.getColumnIndexOrThrow(cursor, "FactQnty");
            final int _cursorIndexOfAlcCode = CursorUtil.getColumnIndexOrThrow(cursor, "AlcCode");
            final int _cursorIndexOfPartIDTH = CursorUtil.getColumnIndexOrThrow(cursor, "PartIDTH");
            final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(cursor, "Flags");
            final List<line> _res = new ArrayList<line>(cursor.getCount());
            while(cursor.moveToNext()) {
              final line _item;
              _item = new line();
              _item.LineID = cursor.getInt(_cursorIndexOfLineID);
              if (cursor.isNull(_cursorIndexOfDocName)) {
                _item.DocName = null;
              } else {
                _item.DocName = cursor.getString(_cursorIndexOfDocName);
              }
              _item.Pos = cursor.getInt(_cursorIndexOfPos);
              _item.GoodsID = cursor.getInt(_cursorIndexOfGoodsID);
              if (cursor.isNull(_cursorIndexOfGoodsName)) {
                _item.GoodsName = null;
              } else {
                _item.GoodsName = cursor.getString(_cursorIndexOfGoodsName);
              }
              if (cursor.isNull(_cursorIndexOfUnitBC)) {
                _item.UnitBC = null;
              } else {
                _item.UnitBC = cursor.getString(_cursorIndexOfUnitBC);
              }
              if (cursor.isNull(_cursorIndexOfBC)) {
                _item.BC = null;
              } else {
                _item.BC = cursor.getString(_cursorIndexOfBC);
              }
              _item.Price = cursor.getDouble(_cursorIndexOfPrice);
              _item.DocQnty = cursor.getDouble(_cursorIndexOfDocQnty);
              _item.FactQnty = cursor.getDouble(_cursorIndexOfFactQnty);
              if (cursor.isNull(_cursorIndexOfAlcCode)) {
                _item.AlcCode = null;
              } else {
                _item.AlcCode = cursor.getString(_cursorIndexOfAlcCode);
              }
              if (cursor.isNull(_cursorIndexOfPartIDTH)) {
                _item.PartIDTH = null;
              } else {
                _item.PartIDTH = cursor.getString(_cursorIndexOfPartIDTH);
              }
              _item.Flags = cursor.getInt(_cursorIndexOfFlags);
              _res.add(_item);
            }
            return _res;
          }
        };
      }
    };
  }

  @Override
  public DataSource.Factory<Integer, line> loadAllByIds(final int[] objIds) {
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
    return new DataSource.Factory<Integer, line>() {
      @Override
      public LimitOffsetDataSource<line> create() {
        return new LimitOffsetDataSource<line>(__db, _statement, false, true , "MT_lines") {
          @Override
          protected List<line> convertRows(Cursor cursor) {
            final int _cursorIndexOfLineID = CursorUtil.getColumnIndexOrThrow(cursor, "LineID");
            final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(cursor, "DocName");
            final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(cursor, "Pos");
            final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsID");
            final int _cursorIndexOfGoodsName = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsName");
            final int _cursorIndexOfUnitBC = CursorUtil.getColumnIndexOrThrow(cursor, "UnitBC");
            final int _cursorIndexOfBC = CursorUtil.getColumnIndexOrThrow(cursor, "BC");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(cursor, "Price");
            final int _cursorIndexOfDocQnty = CursorUtil.getColumnIndexOrThrow(cursor, "DocQnty");
            final int _cursorIndexOfFactQnty = CursorUtil.getColumnIndexOrThrow(cursor, "FactQnty");
            final int _cursorIndexOfAlcCode = CursorUtil.getColumnIndexOrThrow(cursor, "AlcCode");
            final int _cursorIndexOfPartIDTH = CursorUtil.getColumnIndexOrThrow(cursor, "PartIDTH");
            final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(cursor, "Flags");
            final List<line> _res = new ArrayList<line>(cursor.getCount());
            while(cursor.moveToNext()) {
              final line _item_1;
              _item_1 = new line();
              _item_1.LineID = cursor.getInt(_cursorIndexOfLineID);
              if (cursor.isNull(_cursorIndexOfDocName)) {
                _item_1.DocName = null;
              } else {
                _item_1.DocName = cursor.getString(_cursorIndexOfDocName);
              }
              _item_1.Pos = cursor.getInt(_cursorIndexOfPos);
              _item_1.GoodsID = cursor.getInt(_cursorIndexOfGoodsID);
              if (cursor.isNull(_cursorIndexOfGoodsName)) {
                _item_1.GoodsName = null;
              } else {
                _item_1.GoodsName = cursor.getString(_cursorIndexOfGoodsName);
              }
              if (cursor.isNull(_cursorIndexOfUnitBC)) {
                _item_1.UnitBC = null;
              } else {
                _item_1.UnitBC = cursor.getString(_cursorIndexOfUnitBC);
              }
              if (cursor.isNull(_cursorIndexOfBC)) {
                _item_1.BC = null;
              } else {
                _item_1.BC = cursor.getString(_cursorIndexOfBC);
              }
              _item_1.Price = cursor.getDouble(_cursorIndexOfPrice);
              _item_1.DocQnty = cursor.getDouble(_cursorIndexOfDocQnty);
              _item_1.FactQnty = cursor.getDouble(_cursorIndexOfFactQnty);
              if (cursor.isNull(_cursorIndexOfAlcCode)) {
                _item_1.AlcCode = null;
              } else {
                _item_1.AlcCode = cursor.getString(_cursorIndexOfAlcCode);
              }
              if (cursor.isNull(_cursorIndexOfPartIDTH)) {
                _item_1.PartIDTH = null;
              } else {
                _item_1.PartIDTH = cursor.getString(_cursorIndexOfPartIDTH);
              }
              _item_1.Flags = cursor.getInt(_cursorIndexOfFlags);
              _res.add(_item_1);
            }
            return _res;
          }
        };
      }
    };
  }

  @Override
  public DataSource.Factory<Integer, line> loadByDocument(final String docName) {
    final String _sql = "SELECT * FROM MT_lines WHERE DocName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (docName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, docName);
    }
    return new DataSource.Factory<Integer, line>() {
      @Override
      public LimitOffsetDataSource<line> create() {
        return new LimitOffsetDataSource<line>(__db, _statement, false, true , "MT_lines") {
          @Override
          protected List<line> convertRows(Cursor cursor) {
            final int _cursorIndexOfLineID = CursorUtil.getColumnIndexOrThrow(cursor, "LineID");
            final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(cursor, "DocName");
            final int _cursorIndexOfPos = CursorUtil.getColumnIndexOrThrow(cursor, "Pos");
            final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsID");
            final int _cursorIndexOfGoodsName = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsName");
            final int _cursorIndexOfUnitBC = CursorUtil.getColumnIndexOrThrow(cursor, "UnitBC");
            final int _cursorIndexOfBC = CursorUtil.getColumnIndexOrThrow(cursor, "BC");
            final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(cursor, "Price");
            final int _cursorIndexOfDocQnty = CursorUtil.getColumnIndexOrThrow(cursor, "DocQnty");
            final int _cursorIndexOfFactQnty = CursorUtil.getColumnIndexOrThrow(cursor, "FactQnty");
            final int _cursorIndexOfAlcCode = CursorUtil.getColumnIndexOrThrow(cursor, "AlcCode");
            final int _cursorIndexOfPartIDTH = CursorUtil.getColumnIndexOrThrow(cursor, "PartIDTH");
            final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(cursor, "Flags");
            final List<line> _res = new ArrayList<line>(cursor.getCount());
            while(cursor.moveToNext()) {
              final line _item;
              _item = new line();
              _item.LineID = cursor.getInt(_cursorIndexOfLineID);
              if (cursor.isNull(_cursorIndexOfDocName)) {
                _item.DocName = null;
              } else {
                _item.DocName = cursor.getString(_cursorIndexOfDocName);
              }
              _item.Pos = cursor.getInt(_cursorIndexOfPos);
              _item.GoodsID = cursor.getInt(_cursorIndexOfGoodsID);
              if (cursor.isNull(_cursorIndexOfGoodsName)) {
                _item.GoodsName = null;
              } else {
                _item.GoodsName = cursor.getString(_cursorIndexOfGoodsName);
              }
              if (cursor.isNull(_cursorIndexOfUnitBC)) {
                _item.UnitBC = null;
              } else {
                _item.UnitBC = cursor.getString(_cursorIndexOfUnitBC);
              }
              if (cursor.isNull(_cursorIndexOfBC)) {
                _item.BC = null;
              } else {
                _item.BC = cursor.getString(_cursorIndexOfBC);
              }
              _item.Price = cursor.getDouble(_cursorIndexOfPrice);
              _item.DocQnty = cursor.getDouble(_cursorIndexOfDocQnty);
              _item.FactQnty = cursor.getDouble(_cursorIndexOfFactQnty);
              if (cursor.isNull(_cursorIndexOfAlcCode)) {
                _item.AlcCode = null;
              } else {
                _item.AlcCode = cursor.getString(_cursorIndexOfAlcCode);
              }
              if (cursor.isNull(_cursorIndexOfPartIDTH)) {
                _item.PartIDTH = null;
              } else {
                _item.PartIDTH = cursor.getString(_cursorIndexOfPartIDTH);
              }
              _item.Flags = cursor.getInt(_cursorIndexOfFlags);
              _res.add(_item);
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
