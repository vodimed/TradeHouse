package com.expertek.tradehouse.dictionaries;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.common.extensions.database.RoomSchema;
import com.expertek.tradehouse.dictionaries.entity.barcode;
import com.expertek.tradehouse.dictionaries.entity.client;
import com.expertek.tradehouse.dictionaries.entity.good;
import com.expertek.tradehouse.dictionaries.entity.object;
import com.expertek.tradehouse.dictionaries.entity.user;

@TypeConverters({RoomSchema.DateConverter.class})
@Database(version = 1,
        entities = {barcode.class, client.class, good.class, object.class, user.class},
        autoMigrations = {
        //@AutoMigration(from = -0, to = 0, spec = Dictionaries_v1Room.AutoMigration.class)
})
public abstract class Dictionaries_v1Room extends RoomDatabase implements DbDictionaries {
}
