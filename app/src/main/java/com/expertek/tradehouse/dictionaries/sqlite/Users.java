package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.user;

import java.util.List;

public class Users {
    private final SQLiteDatabase db;

    public Users(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, user> load() {
        return new SQLitePager.Factory<>(db, user.class, "SELECT * FROM TH_users");
    }

    public user get(String ident) {
        final DataSource<Integer, user> source = new SQLitePager.Factory<>(db, user.class,
                "SELECT * FROM TH_users WHERE userID = :ident", ident).create();
        final List<user> result = ((SQLitePager<user>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
