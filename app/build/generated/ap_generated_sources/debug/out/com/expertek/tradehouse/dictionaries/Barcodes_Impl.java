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
import com.expertek.tradehouse.dictionaries.entity.barcode;
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
public final class Barcodes_Impl implements Barcodes {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<barcode> __insertionAdapterOfbarcode;

  private final EntityDeletionOrUpdateAdapter<barcode> __deletionAdapterOfbarcode;

  public Barcodes_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfbarcode = new EntityInsertionAdapter<barcode>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `TH_barcodes` (`GoodsID`,`BC`,`PriceBC`,`UnitBC`,`UnitRate`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, barcode value) {
        stmt.bindLong(1, value.GoodsID);
        if (value.BC == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.BC);
        }
        stmt.bindDouble(3, value.PriceBC);
        if (value.UnitBC == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.UnitBC);
        }
        stmt.bindDouble(5, value.UnitRate);
      }
    };
    this.__deletionAdapterOfbarcode = new EntityDeletionOrUpdateAdapter<barcode>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `TH_barcodes` WHERE `BC` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, barcode value) {
        if (value.BC == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.BC);
        }
      }
    };
  }

  @Override
  public void insertAll(final barcode... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfbarcode.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final barcode objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfbarcode.handle(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public DataSource.Factory<Integer, barcode> getAll() {
    final String _sql = "SELECT `TH_barcodes`.`GoodsID` AS `GoodsID`, `TH_barcodes`.`BC` AS `BC`, `TH_barcodes`.`PriceBC` AS `PriceBC`, `TH_barcodes`.`UnitBC` AS `UnitBC`, `TH_barcodes`.`UnitRate` AS `UnitRate` FROM TH_barcodes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new DataSource.Factory<Integer, barcode>() {
      @Override
      public LimitOffsetDataSource<barcode> create() {
        return new LimitOffsetDataSource<barcode>(__db, _statement, false, true , "TH_barcodes") {
          @Override
          protected List<barcode> convertRows(Cursor cursor) {
            final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsID");
            final int _cursorIndexOfBC = CursorUtil.getColumnIndexOrThrow(cursor, "BC");
            final int _cursorIndexOfPriceBC = CursorUtil.getColumnIndexOrThrow(cursor, "PriceBC");
            final int _cursorIndexOfUnitBC = CursorUtil.getColumnIndexOrThrow(cursor, "UnitBC");
            final int _cursorIndexOfUnitRate = CursorUtil.getColumnIndexOrThrow(cursor, "UnitRate");
            final List<barcode> _res = new ArrayList<barcode>(cursor.getCount());
            while(cursor.moveToNext()) {
              final barcode _item;
              _item = new barcode();
              _item.GoodsID = cursor.getInt(_cursorIndexOfGoodsID);
              if (cursor.isNull(_cursorIndexOfBC)) {
                _item.BC = null;
              } else {
                _item.BC = cursor.getString(_cursorIndexOfBC);
              }
              _item.PriceBC = cursor.getDouble(_cursorIndexOfPriceBC);
              if (cursor.isNull(_cursorIndexOfUnitBC)) {
                _item.UnitBC = null;
              } else {
                _item.UnitBC = cursor.getString(_cursorIndexOfUnitBC);
              }
              _item.UnitRate = cursor.getDouble(_cursorIndexOfUnitRate);
              _res.add(_item);
            }
            return _res;
          }
        };
      }
    };
  }

  @Override
  public DataSource.Factory<Integer, barcode> loadAllByIds(final String[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM TH_barcodes WHERE BC IN (");
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
    return new DataSource.Factory<Integer, barcode>() {
      @Override
      public LimitOffsetDataSource<barcode> create() {
        return new LimitOffsetDataSource<barcode>(__db, _statement, false, true , "TH_barcodes") {
          @Override
          protected List<barcode> convertRows(Cursor cursor) {
            final int _cursorIndexOfGoodsID = CursorUtil.getColumnIndexOrThrow(cursor, "GoodsID");
            final int _cursorIndexOfBC = CursorUtil.getColumnIndexOrThrow(cursor, "BC");
            final int _cursorIndexOfPriceBC = CursorUtil.getColumnIndexOrThrow(cursor, "PriceBC");
            final int _cursorIndexOfUnitBC = CursorUtil.getColumnIndexOrThrow(cursor, "UnitBC");
            final int _cursorIndexOfUnitRate = CursorUtil.getColumnIndexOrThrow(cursor, "UnitRate");
            final List<barcode> _res = new ArrayList<barcode>(cursor.getCount());
            while(cursor.moveToNext()) {
              final barcode _item_1;
              _item_1 = new barcode();
              _item_1.GoodsID = cursor.getInt(_cursorIndexOfGoodsID);
              if (cursor.isNull(_cursorIndexOfBC)) {
                _item_1.BC = null;
              } else {
                _item_1.BC = cursor.getString(_cursorIndexOfBC);
              }
              _item_1.PriceBC = cursor.getDouble(_cursorIndexOfPriceBC);
              if (cursor.isNull(_cursorIndexOfUnitBC)) {
                _item_1.UnitBC = null;
              } else {
                _item_1.UnitBC = cursor.getString(_cursorIndexOfUnitBC);
              }
              _item_1.UnitRate = cursor.getDouble(_cursorIndexOfUnitRate);
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
