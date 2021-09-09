package com.expertek.tradehouse.dictionaries.sqlite;

import android.database.sqlite.SQLiteDatabase;

import androidx.paging.DataSource;

import com.common.extensions.database.SQLitePager;
import com.expertek.tradehouse.dictionaries.entity.user;

public class Users {
    private final SQLiteDatabase db;

    public Users(SQLiteDatabase db) {
        this.db = db;
    }

    public DataSource.Factory<Integer, user> load() {
        return new SQLitePager.Factory<user>(new user(), db, "SELECT * FROM TH_users");
    }

    public DataSource.Factory<Integer, user> get(String... ident) {
        return new SQLitePager.Factory<user>(new user(), db, "SELECT * FROM TH_users WHERE userID IN (:ident)", (Object) ident);
    }

    //@Query("SELECT * FROM TH_users WHERE first_name LIKE :first AND " +
    //        "last_name LIKE :last LIMIT 1")
    //user findByName(String first, String last);

    public void insert(user... objects) {
        
    }

    public void delete(user... objects) {
        
    }
}
