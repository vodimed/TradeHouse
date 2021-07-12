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
import com.common.extensions.database.TypeConv;
import com.expertek.tradehouse.documents.entity.document;
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
public final class Documents_Impl implements Documents {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<document> __insertionAdapterOfdocument;

  private final TypeConv __typeConv = new TypeConv();

  private final EntityDeletionOrUpdateAdapter<document> __deletionAdapterOfdocument;

  public Documents_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfdocument = new EntityInsertionAdapter<document>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `MT_documents` (`DocName`,`DocType`,`Complete`,`Status`,`ClientID`,`ClientType`,`ObjectID`,`ObjectType`,`UserID`,`UserName`,`FactSum`,`StartDate`,`Flags`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, document value) {
        if (value.DocName == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.DocName);
        }
        if (value.DocType == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.DocType);
        }
        final int _tmp;
        _tmp = value.Complete ? 1 : 0;
        stmt.bindLong(3, _tmp);
        if (value.Status == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.Status);
        }
        stmt.bindLong(5, value.ClientID);
        if (value.ClientType == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.ClientType);
        }
        stmt.bindLong(7, value.ObjectID);
        if (value.ObjectType == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.ObjectType);
        }
        if (value.UserID == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.UserID);
        }
        if (value.UserName == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.UserName);
        }
        stmt.bindDouble(11, value.FactSum);
        final String _tmp_1;
        _tmp_1 = __typeConv.save(value.StartDate);
        if (_tmp_1 == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindString(12, _tmp_1);
        }
        stmt.bindLong(13, value.Flags);
      }
    };
    this.__deletionAdapterOfdocument = new EntityDeletionOrUpdateAdapter<document>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `MT_documents` WHERE `DocName` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, document value) {
        if (value.DocName == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.DocName);
        }
      }
    };
  }

  @Override
  public void insertAll(final document... objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfdocument.insert(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final document objects) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfdocument.handle(objects);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public DataSource.Factory<Integer, document> getAll() {
    final String _sql = "SELECT `MT_documents`.`DocName` AS `DocName`, `MT_documents`.`DocType` AS `DocType`, `MT_documents`.`Complete` AS `Complete`, `MT_documents`.`Status` AS `Status`, `MT_documents`.`ClientID` AS `ClientID`, `MT_documents`.`ClientType` AS `ClientType`, `MT_documents`.`ObjectID` AS `ObjectID`, `MT_documents`.`ObjectType` AS `ObjectType`, `MT_documents`.`UserID` AS `UserID`, `MT_documents`.`UserName` AS `UserName`, `MT_documents`.`FactSum` AS `FactSum`, `MT_documents`.`StartDate` AS `StartDate`, `MT_documents`.`Flags` AS `Flags` FROM MT_documents";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new DataSource.Factory<Integer, document>() {
      @Override
      public LimitOffsetDataSource<document> create() {
        return new LimitOffsetDataSource<document>(__db, _statement, false, true , "MT_documents") {
          @Override
          protected List<document> convertRows(Cursor cursor) {
            final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(cursor, "DocName");
            final int _cursorIndexOfDocType = CursorUtil.getColumnIndexOrThrow(cursor, "DocType");
            final int _cursorIndexOfComplete = CursorUtil.getColumnIndexOrThrow(cursor, "Complete");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(cursor, "Status");
            final int _cursorIndexOfClientID = CursorUtil.getColumnIndexOrThrow(cursor, "ClientID");
            final int _cursorIndexOfClientType = CursorUtil.getColumnIndexOrThrow(cursor, "ClientType");
            final int _cursorIndexOfObjectID = CursorUtil.getColumnIndexOrThrow(cursor, "ObjectID");
            final int _cursorIndexOfObjectType = CursorUtil.getColumnIndexOrThrow(cursor, "ObjectType");
            final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(cursor, "UserID");
            final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(cursor, "UserName");
            final int _cursorIndexOfFactSum = CursorUtil.getColumnIndexOrThrow(cursor, "FactSum");
            final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(cursor, "StartDate");
            final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(cursor, "Flags");
            final List<document> _res = new ArrayList<document>(cursor.getCount());
            while(cursor.moveToNext()) {
              final document _item;
              _item = new document();
              if (cursor.isNull(_cursorIndexOfDocName)) {
                _item.DocName = null;
              } else {
                _item.DocName = cursor.getString(_cursorIndexOfDocName);
              }
              if (cursor.isNull(_cursorIndexOfDocType)) {
                _item.DocType = null;
              } else {
                _item.DocType = cursor.getString(_cursorIndexOfDocType);
              }
              final int _tmp;
              _tmp = cursor.getInt(_cursorIndexOfComplete);
              _item.Complete = _tmp != 0;
              if (cursor.isNull(_cursorIndexOfStatus)) {
                _item.Status = null;
              } else {
                _item.Status = cursor.getString(_cursorIndexOfStatus);
              }
              _item.ClientID = cursor.getInt(_cursorIndexOfClientID);
              if (cursor.isNull(_cursorIndexOfClientType)) {
                _item.ClientType = null;
              } else {
                _item.ClientType = cursor.getString(_cursorIndexOfClientType);
              }
              _item.ObjectID = cursor.getInt(_cursorIndexOfObjectID);
              if (cursor.isNull(_cursorIndexOfObjectType)) {
                _item.ObjectType = null;
              } else {
                _item.ObjectType = cursor.getString(_cursorIndexOfObjectType);
              }
              if (cursor.isNull(_cursorIndexOfUserID)) {
                _item.UserID = null;
              } else {
                _item.UserID = cursor.getString(_cursorIndexOfUserID);
              }
              if (cursor.isNull(_cursorIndexOfUserName)) {
                _item.UserName = null;
              } else {
                _item.UserName = cursor.getString(_cursorIndexOfUserName);
              }
              _item.FactSum = cursor.getDouble(_cursorIndexOfFactSum);
              final String _tmp_1;
              if (cursor.isNull(_cursorIndexOfStartDate)) {
                _tmp_1 = null;
              } else {
                _tmp_1 = cursor.getString(_cursorIndexOfStartDate);
              }
              _item.StartDate = __typeConv.load(_tmp_1);
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
  public DataSource.Factory<Integer, document> loadAllByIds(final String[] objIds) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM MT_documents WHERE DocName IN (");
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
    return new DataSource.Factory<Integer, document>() {
      @Override
      public LimitOffsetDataSource<document> create() {
        return new LimitOffsetDataSource<document>(__db, _statement, false, true , "MT_documents") {
          @Override
          protected List<document> convertRows(Cursor cursor) {
            final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(cursor, "DocName");
            final int _cursorIndexOfDocType = CursorUtil.getColumnIndexOrThrow(cursor, "DocType");
            final int _cursorIndexOfComplete = CursorUtil.getColumnIndexOrThrow(cursor, "Complete");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(cursor, "Status");
            final int _cursorIndexOfClientID = CursorUtil.getColumnIndexOrThrow(cursor, "ClientID");
            final int _cursorIndexOfClientType = CursorUtil.getColumnIndexOrThrow(cursor, "ClientType");
            final int _cursorIndexOfObjectID = CursorUtil.getColumnIndexOrThrow(cursor, "ObjectID");
            final int _cursorIndexOfObjectType = CursorUtil.getColumnIndexOrThrow(cursor, "ObjectType");
            final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(cursor, "UserID");
            final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(cursor, "UserName");
            final int _cursorIndexOfFactSum = CursorUtil.getColumnIndexOrThrow(cursor, "FactSum");
            final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(cursor, "StartDate");
            final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(cursor, "Flags");
            final List<document> _res = new ArrayList<document>(cursor.getCount());
            while(cursor.moveToNext()) {
              final document _item_1;
              _item_1 = new document();
              if (cursor.isNull(_cursorIndexOfDocName)) {
                _item_1.DocName = null;
              } else {
                _item_1.DocName = cursor.getString(_cursorIndexOfDocName);
              }
              if (cursor.isNull(_cursorIndexOfDocType)) {
                _item_1.DocType = null;
              } else {
                _item_1.DocType = cursor.getString(_cursorIndexOfDocType);
              }
              final int _tmp;
              _tmp = cursor.getInt(_cursorIndexOfComplete);
              _item_1.Complete = _tmp != 0;
              if (cursor.isNull(_cursorIndexOfStatus)) {
                _item_1.Status = null;
              } else {
                _item_1.Status = cursor.getString(_cursorIndexOfStatus);
              }
              _item_1.ClientID = cursor.getInt(_cursorIndexOfClientID);
              if (cursor.isNull(_cursorIndexOfClientType)) {
                _item_1.ClientType = null;
              } else {
                _item_1.ClientType = cursor.getString(_cursorIndexOfClientType);
              }
              _item_1.ObjectID = cursor.getInt(_cursorIndexOfObjectID);
              if (cursor.isNull(_cursorIndexOfObjectType)) {
                _item_1.ObjectType = null;
              } else {
                _item_1.ObjectType = cursor.getString(_cursorIndexOfObjectType);
              }
              if (cursor.isNull(_cursorIndexOfUserID)) {
                _item_1.UserID = null;
              } else {
                _item_1.UserID = cursor.getString(_cursorIndexOfUserID);
              }
              if (cursor.isNull(_cursorIndexOfUserName)) {
                _item_1.UserName = null;
              } else {
                _item_1.UserName = cursor.getString(_cursorIndexOfUserName);
              }
              _item_1.FactSum = cursor.getDouble(_cursorIndexOfFactSum);
              final String _tmp_1;
              if (cursor.isNull(_cursorIndexOfStartDate)) {
                _tmp_1 = null;
              } else {
                _tmp_1 = cursor.getString(_cursorIndexOfStartDate);
              }
              _item_1.StartDate = __typeConv.load(_tmp_1);
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
  public DataSource.Factory<Integer, document> loadByDocType(final String doctype) {
    final String _sql = "SELECT * FROM MT_documents WHERE DocType = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (doctype == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, doctype);
    }
    return new DataSource.Factory<Integer, document>() {
      @Override
      public LimitOffsetDataSource<document> create() {
        return new LimitOffsetDataSource<document>(__db, _statement, false, true , "MT_documents") {
          @Override
          protected List<document> convertRows(Cursor cursor) {
            final int _cursorIndexOfDocName = CursorUtil.getColumnIndexOrThrow(cursor, "DocName");
            final int _cursorIndexOfDocType = CursorUtil.getColumnIndexOrThrow(cursor, "DocType");
            final int _cursorIndexOfComplete = CursorUtil.getColumnIndexOrThrow(cursor, "Complete");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(cursor, "Status");
            final int _cursorIndexOfClientID = CursorUtil.getColumnIndexOrThrow(cursor, "ClientID");
            final int _cursorIndexOfClientType = CursorUtil.getColumnIndexOrThrow(cursor, "ClientType");
            final int _cursorIndexOfObjectID = CursorUtil.getColumnIndexOrThrow(cursor, "ObjectID");
            final int _cursorIndexOfObjectType = CursorUtil.getColumnIndexOrThrow(cursor, "ObjectType");
            final int _cursorIndexOfUserID = CursorUtil.getColumnIndexOrThrow(cursor, "UserID");
            final int _cursorIndexOfUserName = CursorUtil.getColumnIndexOrThrow(cursor, "UserName");
            final int _cursorIndexOfFactSum = CursorUtil.getColumnIndexOrThrow(cursor, "FactSum");
            final int _cursorIndexOfStartDate = CursorUtil.getColumnIndexOrThrow(cursor, "StartDate");
            final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(cursor, "Flags");
            final List<document> _res = new ArrayList<document>(cursor.getCount());
            while(cursor.moveToNext()) {
              final document _item;
              _item = new document();
              if (cursor.isNull(_cursorIndexOfDocName)) {
                _item.DocName = null;
              } else {
                _item.DocName = cursor.getString(_cursorIndexOfDocName);
              }
              if (cursor.isNull(_cursorIndexOfDocType)) {
                _item.DocType = null;
              } else {
                _item.DocType = cursor.getString(_cursorIndexOfDocType);
              }
              final int _tmp;
              _tmp = cursor.getInt(_cursorIndexOfComplete);
              _item.Complete = _tmp != 0;
              if (cursor.isNull(_cursorIndexOfStatus)) {
                _item.Status = null;
              } else {
                _item.Status = cursor.getString(_cursorIndexOfStatus);
              }
              _item.ClientID = cursor.getInt(_cursorIndexOfClientID);
              if (cursor.isNull(_cursorIndexOfClientType)) {
                _item.ClientType = null;
              } else {
                _item.ClientType = cursor.getString(_cursorIndexOfClientType);
              }
              _item.ObjectID = cursor.getInt(_cursorIndexOfObjectID);
              if (cursor.isNull(_cursorIndexOfObjectType)) {
                _item.ObjectType = null;
              } else {
                _item.ObjectType = cursor.getString(_cursorIndexOfObjectType);
              }
              if (cursor.isNull(_cursorIndexOfUserID)) {
                _item.UserID = null;
              } else {
                _item.UserID = cursor.getString(_cursorIndexOfUserID);
              }
              if (cursor.isNull(_cursorIndexOfUserName)) {
                _item.UserName = null;
              } else {
                _item.UserName = cursor.getString(_cursorIndexOfUserName);
              }
              _item.FactSum = cursor.getDouble(_cursorIndexOfFactSum);
              final String _tmp_1;
              if (cursor.isNull(_cursorIndexOfStartDate)) {
                _tmp_1 = null;
              } else {
                _tmp_1 = cursor.getString(_cursorIndexOfStartDate);
              }
              _item.StartDate = __typeConv.load(_tmp_1);
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
