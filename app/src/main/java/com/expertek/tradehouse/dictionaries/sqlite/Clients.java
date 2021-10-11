package com.expertek.tradehouse.dictionaries.sqlite;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.client;

public class Clients {
    private final SQLiteDatabase db;

    public Clients(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, client> load() {
        return new SQLitePager.Factory<>(db, client.class, "SELECT * FROM TH_clients");
    }

    public DataSource.Factory<Integer, client> get(int... ident) {
        return new SQLitePager.Factory<>(db, client.class, "SELECT * FROM TH_clients WHERE cli_code IN (:ident)", ident);
    }

    //@Query("SELECT * FROM TH_clients WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //client findByName(String first, String last);

    public void insert(client... objects) {

    }

    public void delete(client... objects) {

    }
}
