package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.User;

import java.util.List;

public class Users {
    private final SQLiteDatabase db;

    public Users(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, User> load() {
        return new SQLitePager.Factory<>(db, User.class, "SELECT * FROM TH_users");
    }

    public User get(String ident) {
        final DataSource<Integer, User> source = new SQLitePager.Factory<>(db, User.class,
                "SELECT * FROM TH_users WHERE userID = :ident", ident).create();
        final List<User> result = ((SQLitePager<User>) source).loadRange(0, 1);
        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
