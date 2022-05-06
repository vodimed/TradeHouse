package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.Client;

import java.util.List;

public class Clients {
    private final SQLiteDatabase db;

    public Clients(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, Client> load() {
        return new SQLitePager.Factory<>(db, Client.class, "SELECT * FROM TH_clients");
    }

    public Client get(int ident) {
        final DataSource<Integer, Client> source = new SQLitePager.Factory<>(db, Client.class,
                "SELECT * FROM TH_clients WHERE cli_code = :ident", ident).create();
        final List<Client> result = ((SQLitePager<Client>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
