package com.expertek.tradehouse.dictionaries;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.common.extensions.database.RoomSchema;
import com.expertek.tradehouse.dictionaries.entity.Barcode;
import com.expertek.tradehouse.dictionaries.entity.Client;
import com.expertek.tradehouse.dictionaries.entity.Good;
import com.expertek.tradehouse.dictionaries.entity.Obj;
import com.expertek.tradehouse.dictionaries.entity.User;

@TypeConverters({RoomSchema.DateConverter.class})
@Database(version = 1,
        entities = {Barcode.class, Client.class, Good.class, Obj.class, User.class},
        autoMigrations = {
        //@AutoMigration(from = -0, to = 0, spec = Dictionaries_v1Room.AutoMigration.class)
})
public abstract class Dictionaries_v1Room extends RoomDatabase implements DbDictionaries {
}
