package com.expertek.tradehouse.documents;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.expertek.tradehouse.database.DateTime;
import com.expertek.tradehouse.documents.entity.Documents;
import com.expertek.tradehouse.documents.entity.Lines;
import com.expertek.tradehouse.documents.entity.Marklines;

@Database(entities = {Documents.class, Lines.class, Marklines.class}, version = 1, exportSchema = false)
@TypeConverters({DateTime.RoomConverter.class})
public abstract class DBDocuments_v1 extends RoomDatabase implements DBDocuments {

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
