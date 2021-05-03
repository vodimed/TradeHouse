package com.expertek.tradehouse.dictionaries;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.expertek.tradehouse.database.DataEngine;
import com.expertek.tradehouse.database.DataMigration;
import com.expertek.tradehouse.database.TypeConv;
import com.expertek.tradehouse.dictionaries.entity.Barcodes;
import com.expertek.tradehouse.dictionaries.entity.Clients;
import com.expertek.tradehouse.dictionaries.entity.Goods;
import com.expertek.tradehouse.dictionaries.entity.Objects;
import com.expertek.tradehouse.dictionaries.entity.Users;

@Database(entities = {Barcodes.class, Clients.class, Goods.class, Objects.class, Users.class}, version = 1, exportSchema = false)
@TypeConverters({TypeConv.class})
public abstract class DbDictionaries_v1 extends DataEngine implements DbDictionaries {

    public static final DataMigration upgradeFrom_0 = new DataMigration(0, 1) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    public static final DataMigration downgradeTo_0 = new DataMigration(1, 0) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };
}
