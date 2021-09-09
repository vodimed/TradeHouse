package com.expertek.tradehouse.dictionaries;

import android.database.sqlite.SQLiteDatabase;

import com.expertek.tradehouse.dictionaries.sqlite.Barcodes;
import com.expertek.tradehouse.dictionaries.sqlite.Clients;
import com.expertek.tradehouse.dictionaries.sqlite.Goods;
import com.expertek.tradehouse.dictionaries.sqlite.Objects;
import com.expertek.tradehouse.dictionaries.sqlite.Users;

import java.io.File;

public class Dictionaries_v1Sqlite implements DbDictionaries {
    private final SQLiteDatabase db;
    private final Barcodes barcodes;
    private final Clients clients;
    private final Goods goods;
    private final Objects objects;
    private final Users users;

    public Dictionaries_v1Sqlite(String name) {
        db = SQLiteDatabase.openOrCreateDatabase(new File(name), null);
        barcodes = new Barcodes(db);
        clients = new Clients(db);
        goods = new Goods(db);
        objects = new Objects(db);
        users = new Users(db);
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
}
