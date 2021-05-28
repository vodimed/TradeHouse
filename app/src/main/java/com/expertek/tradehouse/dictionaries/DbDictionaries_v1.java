package com.expertek.tradehouse.dictionaries;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.common.extensions.database.DataEngine;
import com.common.extensions.database.TypeConv;
import com.expertek.tradehouse.dictionaries.entity.barcode;
import com.expertek.tradehouse.dictionaries.entity.client;
import com.expertek.tradehouse.dictionaries.entity.good;
import com.expertek.tradehouse.dictionaries.entity.object;
import com.expertek.tradehouse.dictionaries.entity.user;

@TypeConverters({TypeConv.class})
@Database(version = 1,
        entities = {barcode.class, client.class, good.class, object.class, user.class},
        autoMigrations = {
        //@AutoMigration(from = -0, to = 0, spec = DbDictionaries_v1.AutoMigration.class)
})
public abstract class DbDictionaries_v1 extends DataEngine implements DbDictionaries {

    public static final DataMigration upgradeFrom_0 = new DataMigration(-0, 0) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    public static final DataMigration downgradeTo_0 = new DataMigration(0, -0) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    // https://developer.android.com/reference/androidx/room/AutoMigration
    // https://medium.com/androiddevelopers/room-auto-migrations-d5370b0ca6eb
    static class AutoMigration implements AutoMigrationSpec {
        @Override
        public void onPostMigrate(@NonNull SupportSQLiteDatabase db) {
        }
    }
}
