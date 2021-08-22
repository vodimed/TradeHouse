package com.expertek.tradehouse.documents;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Documents_v1Room_Impl extends Documents_v1Room {
  private volatile Documents _documents;

  private volatile Lines _lines;

  private volatile Marklines _marklines;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `MT_documents` (`DocName` TEXT NOT NULL, `DocType` TEXT, `Complete` INTEGER NOT NULL, `Status` TEXT, `ClientID` INTEGER NOT NULL, `ClientType` TEXT, `ObjectID` INTEGER NOT NULL, `UserID` TEXT, `UserName` TEXT, `FactSum` REAL NOT NULL, `StartDate` TEXT, `Flags` INTEGER NOT NULL, PRIMARY KEY(`DocName`))");
        _db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `docNameDoc` ON `MT_documents` (`DocName`, `DocType`)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `MT_lines` (`LineID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `DocName` TEXT NOT NULL, `Pos` INTEGER NOT NULL, `GoodsID` INTEGER NOT NULL, `GoodsName` TEXT, `UnitBC` TEXT, `BC` TEXT NOT NULL, `Price` REAL NOT NULL, `DocQnty` REAL NOT NULL, `FactQnty` REAL NOT NULL, `AlcCode` TEXT, `PartIDTH` TEXT, `Flags` INTEGER NOT NULL)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `lDocNPart` ON `MT_lines` (`DocName`, `PartIDTH`)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `lDocName` ON `MT_lines` (`DocName`)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `lDocNameAlc` ON `MT_lines` (`DocName`, `AlcCode`)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `lDocNameBC` ON `MT_lines` (`DocName`, `BC`)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `lDocNameGdsUnit` ON `MT_lines` (`DocName`, `GoodsID`, `UnitBC`)");
        _db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `lLine` ON `MT_lines` (`LineID`)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `MT_MarkLines` (`LineID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `DocName` TEXT NOT NULL, `MarkCode` TEXT NOT NULL, `PartIDTH` TEXT NOT NULL, `Sts` TEXT, `MarkParent` TEXT, `BoxQnty` INTEGER NOT NULL)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `DocName` ON `MT_MarkLines` (`DocName`)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `markCode` ON `MT_MarkLines` (`MarkCode`)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `mp` ON `MT_MarkLines` (`MarkParent`)");
        _db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `DocNamePartId` ON `MT_MarkLines` (`DocName`, `MarkCode`, `PartIDTH`)");
        _db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `line` ON `MT_MarkLines` (`LineID`, `DocName`)");
        _db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `markline` ON `MT_MarkLines` (`LineID`, `MarkCode`)");
        _db.execSQL("CREATE INDEX IF NOT EXISTS `pi` ON `MT_MarkLines` (`DocName`, `MarkCode`)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd2579a879fc994c7d3a04cb93de5d64f')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `MT_documents`");
        _db.execSQL("DROP TABLE IF EXISTS `MT_lines`");
        _db.execSQL("DROP TABLE IF EXISTS `MT_MarkLines`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsMTDocuments = new HashMap<String, TableInfo.Column>(12);
        _columnsMTDocuments.put("DocName", new TableInfo.Column("DocName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("DocType", new TableInfo.Column("DocType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("Complete", new TableInfo.Column("Complete", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("Status", new TableInfo.Column("Status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("ClientID", new TableInfo.Column("ClientID", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("ClientType", new TableInfo.Column("ClientType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("ObjectID", new TableInfo.Column("ObjectID", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("UserID", new TableInfo.Column("UserID", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("UserName", new TableInfo.Column("UserName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("FactSum", new TableInfo.Column("FactSum", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("StartDate", new TableInfo.Column("StartDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTDocuments.put("Flags", new TableInfo.Column("Flags", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMTDocuments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMTDocuments = new HashSet<TableInfo.Index>(1);
        _indicesMTDocuments.add(new TableInfo.Index("docNameDoc", true, Arrays.asList("DocName","DocType")));
        final TableInfo _infoMTDocuments = new TableInfo("MT_documents", _columnsMTDocuments, _foreignKeysMTDocuments, _indicesMTDocuments);
        final TableInfo _existingMTDocuments = TableInfo.read(_db, "MT_documents");
        if (! _infoMTDocuments.equals(_existingMTDocuments)) {
          return new RoomOpenHelper.ValidationResult(false, "MT_documents(com.expertek.tradehouse.documents.entity.document).\n"
                  + " Expected:\n" + _infoMTDocuments + "\n"
                  + " Found:\n" + _existingMTDocuments);
        }
        final HashMap<String, TableInfo.Column> _columnsMTLines = new HashMap<String, TableInfo.Column>(13);
        _columnsMTLines.put("LineID", new TableInfo.Column("LineID", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("DocName", new TableInfo.Column("DocName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("Pos", new TableInfo.Column("Pos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("GoodsID", new TableInfo.Column("GoodsID", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("GoodsName", new TableInfo.Column("GoodsName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("UnitBC", new TableInfo.Column("UnitBC", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("BC", new TableInfo.Column("BC", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("Price", new TableInfo.Column("Price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("DocQnty", new TableInfo.Column("DocQnty", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("FactQnty", new TableInfo.Column("FactQnty", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("AlcCode", new TableInfo.Column("AlcCode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("PartIDTH", new TableInfo.Column("PartIDTH", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTLines.put("Flags", new TableInfo.Column("Flags", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMTLines = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMTLines = new HashSet<TableInfo.Index>(6);
        _indicesMTLines.add(new TableInfo.Index("lDocNPart", false, Arrays.asList("DocName","PartIDTH")));
        _indicesMTLines.add(new TableInfo.Index("lDocName", false, Arrays.asList("DocName")));
        _indicesMTLines.add(new TableInfo.Index("lDocNameAlc", false, Arrays.asList("DocName","AlcCode")));
        _indicesMTLines.add(new TableInfo.Index("lDocNameBC", false, Arrays.asList("DocName","BC")));
        _indicesMTLines.add(new TableInfo.Index("lDocNameGdsUnit", false, Arrays.asList("DocName","GoodsID","UnitBC")));
        _indicesMTLines.add(new TableInfo.Index("lLine", true, Arrays.asList("LineID")));
        final TableInfo _infoMTLines = new TableInfo("MT_lines", _columnsMTLines, _foreignKeysMTLines, _indicesMTLines);
        final TableInfo _existingMTLines = TableInfo.read(_db, "MT_lines");
        if (! _infoMTLines.equals(_existingMTLines)) {
          return new RoomOpenHelper.ValidationResult(false, "MT_lines(com.expertek.tradehouse.documents.entity.line).\n"
                  + " Expected:\n" + _infoMTLines + "\n"
                  + " Found:\n" + _existingMTLines);
        }
        final HashMap<String, TableInfo.Column> _columnsMTMarkLines = new HashMap<String, TableInfo.Column>(7);
        _columnsMTMarkLines.put("LineID", new TableInfo.Column("LineID", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTMarkLines.put("DocName", new TableInfo.Column("DocName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTMarkLines.put("MarkCode", new TableInfo.Column("MarkCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTMarkLines.put("PartIDTH", new TableInfo.Column("PartIDTH", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTMarkLines.put("Sts", new TableInfo.Column("Sts", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTMarkLines.put("MarkParent", new TableInfo.Column("MarkParent", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMTMarkLines.put("BoxQnty", new TableInfo.Column("BoxQnty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMTMarkLines = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMTMarkLines = new HashSet<TableInfo.Index>(7);
        _indicesMTMarkLines.add(new TableInfo.Index("DocName", false, Arrays.asList("DocName")));
        _indicesMTMarkLines.add(new TableInfo.Index("markCode", false, Arrays.asList("MarkCode")));
        _indicesMTMarkLines.add(new TableInfo.Index("mp", false, Arrays.asList("MarkParent")));
        _indicesMTMarkLines.add(new TableInfo.Index("DocNamePartId", true, Arrays.asList("DocName","MarkCode","PartIDTH")));
        _indicesMTMarkLines.add(new TableInfo.Index("line", true, Arrays.asList("LineID","DocName")));
        _indicesMTMarkLines.add(new TableInfo.Index("markline", true, Arrays.asList("LineID","MarkCode")));
        _indicesMTMarkLines.add(new TableInfo.Index("pi", false, Arrays.asList("DocName","MarkCode")));
        final TableInfo _infoMTMarkLines = new TableInfo("MT_MarkLines", _columnsMTMarkLines, _foreignKeysMTMarkLines, _indicesMTMarkLines);
        final TableInfo _existingMTMarkLines = TableInfo.read(_db, "MT_MarkLines");
        if (! _infoMTMarkLines.equals(_existingMTMarkLines)) {
          return new RoomOpenHelper.ValidationResult(false, "MT_MarkLines(com.expertek.tradehouse.documents.entity.markline).\n"
                  + " Expected:\n" + _infoMTMarkLines + "\n"
                  + " Found:\n" + _existingMTMarkLines);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d2579a879fc994c7d3a04cb93de5d64f", "04aeb2d4bac3bbe1d3d79276d91af2aa");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "MT_documents","MT_lines","MT_MarkLines");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `MT_documents`");
      _db.execSQL("DELETE FROM `MT_lines`");
      _db.execSQL("DELETE FROM `MT_MarkLines`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(Documents.class, Documents_Impl.getRequiredConverters());
    _typeConvertersMap.put(Lines.class, Lines_Impl.getRequiredConverters());
    _typeConvertersMap.put(Marklines.class, Marklines_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  protected Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  protected List<Migration> getAutoMigrations() {
    return Arrays.asList();
  }

  @Override
  public Documents documents() {
    if (_documents != null) {
      return _documents;
    } else {
      synchronized(this) {
        if(_documents == null) {
          _documents = new Documents_Impl(this);
        }
        return _documents;
      }
    }
  }

  @Override
  public Lines lines() {
    if (_lines != null) {
      return _lines;
    } else {
      synchronized(this) {
        if(_lines == null) {
          _lines = new Lines_Impl(this);
        }
        return _lines;
      }
    }
  }

  @Override
  public Marklines marklines() {
    if (_marklines != null) {
      return _marklines;
    } else {
      synchronized(this) {
        if(_marklines == null) {
          _marklines = new Marklines_Impl(this);
        }
        return _marklines;
      }
    }
  }
}
