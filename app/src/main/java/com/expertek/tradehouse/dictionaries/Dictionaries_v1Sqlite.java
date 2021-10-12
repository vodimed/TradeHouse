package com.expertek.tradehouse.dictionaries;

import androidx.annotation.NonNull;

import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLiteMigration;
import com.expertek.tradehouse.dictionaries.sqlite.Barcodes;
import com.expertek.tradehouse.dictionaries.sqlite.Clients;
import com.expertek.tradehouse.dictionaries.sqlite.Goods;
import com.expertek.tradehouse.dictionaries.sqlite.Objects;
import com.expertek.tradehouse.dictionaries.sqlite.Users;

public class Dictionaries_v1Sqlite extends SQLiteDatabase implements DbDictionaries {
    private final Barcodes barcodes;
    private final Clients clients;
    private final Goods goods;
    private final Objects objects;
    private final Users users;

    public Dictionaries_v1Sqlite(@NonNull String path) {
        super(path, 1);
        barcodes = new Barcodes(this);
        clients = new Clients(this);
        goods = new Goods(this);
        objects = new Objects(this);
        users = new Users(this);
    }

    public Barcodes barcodes() {
        return barcodes;
    }
    public Clients clients() {
        return clients;
    }
    public Goods goods() {
        return goods;
    }
    public Objects objects() {
        return objects;
    }
    public Users users() {
        return users;
    }

    /**
     * Migrations through DataBase revisions
     */
    public static class M_0_1 extends SQLiteMigration {
        public M_0_1() {
            super(0, 1);
        }

        @Override
        public void migrate(@NonNull SQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS TH_barcodes ( GoodsID INTEGER NOT NULL, BC TEXT NOT NULL, PriceBC DOUBLE, UnitBC TEXT (10), UnitRate DOUBLE, PRIMARY KEY ( BC ) )");
            database.execSQL("CREATE TABLE IF NOT EXISTS TH_clients ( cli_code INTEGER NOT NULL, cli_type TEXT NOT NULL, Name TEXT, PRIMARY KEY ( cli_code, cli_type ) )");
            database.execSQL("CREATE TABLE IF NOT EXISTS TH_goods ( GoodsID INTEGER NOT NULL UNIQUE, Name TEXT, UnitBase TEXT (10), PriceBase DOUBLE, VAT DOUBLE, Country TEXT, Struct TEXT, FactQnty DOUBLE, FreeQnty DOUBLE, PRIMARY KEY ( GoodsID ) )");
            database.execSQL("CREATE TABLE IF NOT EXISTS TH_objects ( obj_code INTEGER NOT NULL, obj_type TEXT NOT NULL, Name TEXT, PRIMARY KEY ( obj_code, obj_type ) )");
            database.execSQL("CREATE TABLE IF NOT EXISTS TH_users ( userID TEXT NOT NULL UNIQUE, userName TEXT, PRIMARY KEY ( userID ) )");

            database.execSQL("CREATE INDEX IF NOT EXISTS BC ON TH_barcodes ( BC ASC )");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS cli ON TH_clients ( cli_type ASC, cli_code ASC )");
            database.execSQL("CREATE INDEX IF NOT EXISTS gds ON TH_barcodes ( GoodsID ASC )");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS gdsId ON TH_goods ( GoodsID ASC )");
            database.execSQL("CREATE INDEX IF NOT EXISTS obj ON TH_objects ( obj_type ASC, obj_code ASC )");
        }
    }
}
