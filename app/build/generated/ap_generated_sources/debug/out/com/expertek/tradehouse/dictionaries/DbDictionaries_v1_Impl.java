package com.expertek.tradehouse.dictionaries;

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
public final class DbDictionaries_v1_Impl extends DbDictionaries_v1 {
  private volatile Barcodes _barcodes;

  private volatile Clients _clients;

  private volatile Goods _goods;

  private volatile Objects _objects;

  private volatile Users _users;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `TH_barcodes` (`GoodsID` INTEGER NOT NULL, `BC` TEXT NOT NULL, `PriceBC` REAL NOT NULL, `UnitBC` TEXT, `UnitRate` REAL NOT NULL, PRIMARY KEY(`BC`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `TH_clients` (`cli_code` INTEGER NOT NULL, `cli_type` INTEGER NOT NULL, `Name` TEXT, PRIMARY KEY(`cli_code`, `cli_type`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `TH_goods` (`GoodsID` INTEGER NOT NULL, `Name` TEXT, `UnitBase` TEXT, `PriceBase` REAL NOT NULL, `VAT` REAL NOT NULL, `Country` TEXT, `Struct` TEXT, `FactQnty` REAL NOT NULL, `FreeQnty` REAL NOT NULL, PRIMARY KEY(`GoodsID`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `TH_objects` (`obj_code` INTEGER NOT NULL, `obj_type` TEXT NOT NULL, `Name` TEXT, PRIMARY KEY(`obj_code`, `obj_type`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `TH_users` (`userID` TEXT NOT NULL, `userName` TEXT, PRIMARY KEY(`userID`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'dcfd48c128475765e0bc83b06a15d63f')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `TH_barcodes`");
        _db.execSQL("DROP TABLE IF EXISTS `TH_clients`");
        _db.execSQL("DROP TABLE IF EXISTS `TH_goods`");
        _db.execSQL("DROP TABLE IF EXISTS `TH_objects`");
        _db.execSQL("DROP TABLE IF EXISTS `TH_users`");
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
        final HashMap<String, TableInfo.Column> _columnsTHBarcodes = new HashMap<String, TableInfo.Column>(5);
        _columnsTHBarcodes.put("GoodsID", new TableInfo.Column("GoodsID", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHBarcodes.put("BC", new TableInfo.Column("BC", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHBarcodes.put("PriceBC", new TableInfo.Column("PriceBC", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHBarcodes.put("UnitBC", new TableInfo.Column("UnitBC", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHBarcodes.put("UnitRate", new TableInfo.Column("UnitRate", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTHBarcodes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTHBarcodes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTHBarcodes = new TableInfo("TH_barcodes", _columnsTHBarcodes, _foreignKeysTHBarcodes, _indicesTHBarcodes);
        final TableInfo _existingTHBarcodes = TableInfo.read(_db, "TH_barcodes");
        if (! _infoTHBarcodes.equals(_existingTHBarcodes)) {
          return new RoomOpenHelper.ValidationResult(false, "TH_barcodes(com.expertek.tradehouse.dictionaries.entity.barcode).\n"
                  + " Expected:\n" + _infoTHBarcodes + "\n"
                  + " Found:\n" + _existingTHBarcodes);
        }
        final HashMap<String, TableInfo.Column> _columnsTHClients = new HashMap<String, TableInfo.Column>(3);
        _columnsTHClients.put("cli_code", new TableInfo.Column("cli_code", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHClients.put("cli_type", new TableInfo.Column("cli_type", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHClients.put("Name", new TableInfo.Column("Name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTHClients = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTHClients = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTHClients = new TableInfo("TH_clients", _columnsTHClients, _foreignKeysTHClients, _indicesTHClients);
        final TableInfo _existingTHClients = TableInfo.read(_db, "TH_clients");
        if (! _infoTHClients.equals(_existingTHClients)) {
          return new RoomOpenHelper.ValidationResult(false, "TH_clients(com.expertek.tradehouse.dictionaries.entity.client).\n"
                  + " Expected:\n" + _infoTHClients + "\n"
                  + " Found:\n" + _existingTHClients);
        }
        final HashMap<String, TableInfo.Column> _columnsTHGoods = new HashMap<String, TableInfo.Column>(9);
        _columnsTHGoods.put("GoodsID", new TableInfo.Column("GoodsID", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("Name", new TableInfo.Column("Name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("UnitBase", new TableInfo.Column("UnitBase", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("PriceBase", new TableInfo.Column("PriceBase", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("VAT", new TableInfo.Column("VAT", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("Country", new TableInfo.Column("Country", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("Struct", new TableInfo.Column("Struct", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("FactQnty", new TableInfo.Column("FactQnty", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHGoods.put("FreeQnty", new TableInfo.Column("FreeQnty", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTHGoods = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTHGoods = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTHGoods = new TableInfo("TH_goods", _columnsTHGoods, _foreignKeysTHGoods, _indicesTHGoods);
        final TableInfo _existingTHGoods = TableInfo.read(_db, "TH_goods");
        if (! _infoTHGoods.equals(_existingTHGoods)) {
          return new RoomOpenHelper.ValidationResult(false, "TH_goods(com.expertek.tradehouse.dictionaries.entity.good).\n"
                  + " Expected:\n" + _infoTHGoods + "\n"
                  + " Found:\n" + _existingTHGoods);
        }
        final HashMap<String, TableInfo.Column> _columnsTHObjects = new HashMap<String, TableInfo.Column>(3);
        _columnsTHObjects.put("obj_code", new TableInfo.Column("obj_code", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHObjects.put("obj_type", new TableInfo.Column("obj_type", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHObjects.put("Name", new TableInfo.Column("Name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTHObjects = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTHObjects = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTHObjects = new TableInfo("TH_objects", _columnsTHObjects, _foreignKeysTHObjects, _indicesTHObjects);
        final TableInfo _existingTHObjects = TableInfo.read(_db, "TH_objects");
        if (! _infoTHObjects.equals(_existingTHObjects)) {
          return new RoomOpenHelper.ValidationResult(false, "TH_objects(com.expertek.tradehouse.dictionaries.entity.object).\n"
                  + " Expected:\n" + _infoTHObjects + "\n"
                  + " Found:\n" + _existingTHObjects);
        }
        final HashMap<String, TableInfo.Column> _columnsTHUsers = new HashMap<String, TableInfo.Column>(2);
        _columnsTHUsers.put("userID", new TableInfo.Column("userID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTHUsers.put("userName", new TableInfo.Column("userName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTHUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTHUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTHUsers = new TableInfo("TH_users", _columnsTHUsers, _foreignKeysTHUsers, _indicesTHUsers);
        final TableInfo _existingTHUsers = TableInfo.read(_db, "TH_users");
        if (! _infoTHUsers.equals(_existingTHUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "TH_users(com.expertek.tradehouse.dictionaries.entity.user).\n"
                  + " Expected:\n" + _infoTHUsers + "\n"
                  + " Found:\n" + _existingTHUsers);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "dcfd48c128475765e0bc83b06a15d63f", "95820dfe73fbb81c60d2c79e903f9119");
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
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "TH_barcodes","TH_clients","TH_goods","TH_objects","TH_users");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `TH_barcodes`");
      _db.execSQL("DELETE FROM `TH_clients`");
      _db.execSQL("DELETE FROM `TH_goods`");
      _db.execSQL("DELETE FROM `TH_objects`");
      _db.execSQL("DELETE FROM `TH_users`");
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
    _typeConvertersMap.put(Barcodes.class, Barcodes_Impl.getRequiredConverters());
    _typeConvertersMap.put(Clients.class, Clients_Impl.getRequiredConverters());
    _typeConvertersMap.put(Goods.class, Goods_Impl.getRequiredConverters());
    _typeConvertersMap.put(Objects.class, Objects_Impl.getRequiredConverters());
    _typeConvertersMap.put(Users.class, Users_Impl.getRequiredConverters());
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
  public Barcodes barcodes() {
    if (_barcodes != null) {
      return _barcodes;
    } else {
      synchronized(this) {
        if(_barcodes == null) {
          _barcodes = new Barcodes_Impl(this);
        }
        return _barcodes;
      }
    }
  }

  @Override
  public Clients clients() {
    if (_clients != null) {
      return _clients;
    } else {
      synchronized(this) {
        if(_clients == null) {
          _clients = new Clients_Impl(this);
        }
        return _clients;
      }
    }
  }

  @Override
  public Goods goods() {
    if (_goods != null) {
      return _goods;
    } else {
      synchronized(this) {
        if(_goods == null) {
          _goods = new Goods_Impl(this);
        }
        return _goods;
      }
    }
  }

  @Override
  public Objects objects() {
    if (_objects != null) {
      return _objects;
    } else {
      synchronized(this) {
        if(_objects == null) {
          _objects = new Objects_Impl(this);
        }
        return _objects;
      }
    }
  }

  @Override
  public Users users() {
    if (_users != null) {
      return _users;
    } else {
      synchronized(this) {
        if(_users == null) {
          _users = new Users_Impl(this);
        }
        return _users;
      }
    }
  }
}
