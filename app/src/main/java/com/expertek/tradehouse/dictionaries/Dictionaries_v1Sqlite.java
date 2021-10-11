package com.expertek.tradehouse.dictionaries;

import androidx.annotation.NonNull;

import com.common.extensions.database.SQLiteDatabase;
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
}
