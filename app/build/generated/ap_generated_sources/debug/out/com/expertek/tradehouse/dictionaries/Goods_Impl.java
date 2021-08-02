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
import com.expertek.tradehouse.dictionaries.entity.good;
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
public final class Goods_Impl implements Goods {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<good> __insertionAdapterOfgood;

  private final EntityDeletionOrUpdateAdapter<good> __deletionAdapterOfgood;

  public Goods_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfgood = new EntityInsertionAdapter<good>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `TH_goods` (`GoodsID`,`Name`,`UnitBase`,`PriceBase`,`VAT`,`Country`,`Struct`,`FactQnty`,`FreeQnty`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, good value) {
        stmt.bindLong(1, value.GoodsID);
        if (value.Name == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.Name);
        }
        if (value.UnitBase == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.UnitBase);
        }
        stmt.bindDouble(4, value.PriceBase);
        stmt.bindDouble(5, value.VAT);
        if (value.Country == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.Country);
        }
        if (value.Struct == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.Struct);
        }
        stmt.bindDouble(8, value.FactQnty);
        stmt.bindDouble(9, value.FreeQnty);
      }
    };
    this.__deletionAdapterOfgood = new EntityDeletionOrUpdateAdapter<good>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `TH_goods` WHERE `GoodsID` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, good value) {
        stmt.bindLong(1, value.GoodsID);
      }
    };
  }

  @Override
  public void insertAll(final good... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfgood.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final good objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfgood.handle(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public DataSource.Factory<Integer, good> getAll() {
    final String _sql = "SELECT `TH_goods`.`GoodsID` AS `GoodsID`, `TH_goods`.`Name` AS `Name`, `TH_goods`.`UnitBase` AS `UnitBase`, `TH_goods`.`PriceBase` AS `PriceBase`, `TH_goods`.`VAT` AS `VAT`, `TH_goods`.`Country` AS `Country`, `TH_goods`.`Struct` AS `Struct`, `TH_goods`.`FactQnty` AS `FactQnty`, `TH_goods`.`FreeQnty` AS `FreeQnty` FROM TH_goods";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new DataSource.Factory<Integer, good>() {
      @Override
      public LimitOffsetDataSource<good> create() {
        return new LimitOffsetDataSource<good>(__db, _statement, false, true , "TH_goods") {
          @Override
          protected List<good> convertRows(Cursor cursor) {
            final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsID");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(cursor, "Name");
            final int _cursorIndexOfUnitBase = CursorUtil.getColumnIndexOrThrow(cursor, "UnitBase");
            final int _cursorIndexOfPriceBase = CursorUtil.getColumnIndexOrThrow(cursor, "PriceBase");
            final int _cursorIndexOfVAT = CursorUtil.getColumnIndexOrThrow(cursor, "VAT");
            final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(cursor, "Country");
            final int _cursorIndexOfStruct = CursorUtil.getColumnIndexOrThrow(cursor, "Struct");
            final int _cursorIndexOfFactQnty = CursorUtil.getColumnIndexOrThrow(cursor, "FactQnty");
            final int _cursorIndexOfFreeQnty = CursorUtil.getColumnIndexOrThrow(cursor, "FreeQnty");
            final List<good> _res = new ArrayList<good>(cursor.getCount());
            while(cursor.moveToNext()) {
              final good _item;
              _item = new good();
              _item.GoodsID = cursor.getInt(_cursorIndexOfGoodsID);
              if (cursor.isNull(_cursorIndexOfName)) {
                _item.Name = null;
              } else {
                _item.Name = cursor.getString(_cursorIndexOfName);
              }
              if (cursor.isNull(_cursorIndexOfUnitBase)) {
                _item.UnitBase = null;
              } else {
                _item.UnitBase = cursor.getString(_cursorIndexOfUnitBase);
              }
              _item.PriceBase = cursor.getDouble(_cursorIndexOfPriceBase);
              _item.VAT = cursor.getDouble(_cursorIndexOfVAT);
              if (cursor.isNull(_cursorIndexOfCountry)) {
                _item.Country = null;
              } else {
                _item.Country = cursor.getString(_cursorIndexOfCountry);
              }
              if (cursor.isNull(_cursorIndexOfStruct)) {
                _item.Struct = null;
              } else {
                _item.Struct = cursor.getString(_cursorIndexOfStruct);
              }
              _item.FactQnty = cursor.getDouble(_cursorIndexOfFactQnty);
              _item.FreeQnty = cursor.getDouble(_cursorIndexOfFreeQnty);
              _res.add(_item);
            }
            return _res;
          }
        };
      }
    };
  }

  @Override
  public DataSource.Factory<Integer, good> loadAllByIds(final int[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM TH_goods WHERE GoodsID IN (");
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
    return new DataSource.Factory<Integer, good>() {
      @Override
      public LimitOffsetDataSource<good> create() {
        return new LimitOffsetDataSource<good>(__db, _statement, false, true , "TH_goods") {
          @Override
          protected List<good> convertRows(Cursor cursor) {
            final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsID");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(cursor, "Name");
            final int _cursorIndexOfUnitBase = CursorUtil.getColumnIndexOrThrow(cursor, "UnitBase");
            final int _cursorIndexOfPriceBase = CursorUtil.getColumnIndexOrThrow(cursor, "PriceBase");
            final int _cursorIndexOfVAT = CursorUtil.getColumnIndexOrThrow(cursor, "VAT");
            final int _cursorIndexOfCountry = CursorUtil.getColumnIndexOrThrow(cursor, "Country");
            final int _cursorIndexOfStruct = CursorUtil.getColumnIndexOrThrow(cursor, "Struct");
            final int _cursorIndexOfFactQnty = CursorUtil.getColumnIndexOrThrow(cursor, "FactQnty");
            final int _cursorIndexOfFreeQnty = CursorUtil.getColumnIndexOrThrow(cursor, "FreeQnty");
            final List<good> _res = new ArrayList<good>(cursor.getCount());
            while(cursor.moveToNext()) {
              final good _item_1;
              _item_1 = new good();
              _item_1.GoodsID = cursor.getInt(_cursorIndexOfGoodsID);
              if (cursor.isNull(_cursorIndexOfName)) {
                _item_1.Name = null;
              } else {
                _item_1.Name = cursor.getString(_cursorIndexOfName);
              }
              if (cursor.isNull(_cursorIndexOfUnitBase)) {
                _item_1.UnitBase = null;
              } else {
                _item_1.UnitBase = cursor.getString(_cursorIndexOfUnitBase);
              }
              _item_1.PriceBase = cursor.getDouble(_cursorIndexOfPriceBase);
              _item_1.VAT = cursor.getDouble(_cursorIndexOfVAT);
              if (cursor.isNull(_cursorIndexOfCountry)) {
                _item_1.Country = null;
              } else {
                _item_1.Country = cursor.getString(_cursorIndexOfCountry);
              }
              if (cursor.isNull(_cursorIndexOfStruct)) {
                _item_1.Struct = null;
              } else {
                _item_1.Struct = cursor.getString(_cursorIndexOfStruct);
              }
              _item_1.FactQnty = cursor.getDouble(_cursorIndexOfFactQnty);
              _item_1.FreeQnty = cursor.getDouble(_cursorIndexOfFreeQnty);
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
