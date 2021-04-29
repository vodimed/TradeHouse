package com.expertek.tradehouse.dictionaries;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.expertek.tradehouse.database.DateTime;
import com.expertek.tradehouse.dictionaries.entity.Barcodes;
import com.expertek.tradehouse.dictionaries.entity.Clients;
import com.expertek.tradehouse.dictionaries.entity.Goods;
import com.expertek.tradehouse.dictionaries.entity.Objects;
import com.expertek.tradehouse.dictionaries.entity.Users;

@Database(entities = {Barcodes.class, Clients.class, Goods.class, Objects.class, Users.class}, version = 1, exportSchema = false)
@TypeConverters({DateTime.RoomConverter.class})
public abstract class DbDictionaries_v1 extends RoomDatabase implements DbDictionaries {
}
