package com.expertek.tradehouse.dictionaries;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.expertek.tradehouse.database.DateTime;
import com.expertek.tradehouse.dictionaries.entity.Barcodes;
import com.expertek.tradehouse.dictionaries.entity.Clients;
import com.expertek.tradehouse.dictionaries.entity.Goods;
import com.expertek.tradehouse.dictionaries.entity.Objects;
import com.expertek.tradehouse.dictionaries.entity.Users;

@Database(entities = {Barcodes.class, Clients.class, Goods.class, Objects.class, Users.class}, version = 1, exportSchema = false)
@TypeConverters({DateTime.RoomConverter.class})
public abstract class DbDictionaries_v1 extends RoomDatabase implements DbDictionaries {

    public static final Migration upgradeFrom_0 = new Migration(0, 1) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    public static final Migration downgradeTo_0 = new Migration(1, 0) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };
}
