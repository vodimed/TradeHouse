package com.expertek.tradehouse.documents;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.expertek.tradehouse.database.DataEngine;
import com.expertek.tradehouse.database.DataMigration;
import com.expertek.tradehouse.database.TypeConv;
import com.expertek.tradehouse.documents.entity.Documents;
import com.expertek.tradehouse.documents.entity.Lines;
import com.expertek.tradehouse.documents.entity.Marklines;

@Database(entities = {Documents.class, Lines.class, Marklines.class}, version = 1, exportSchema = false)
@TypeConverters({TypeConv.class})
public abstract class DBDocuments_v1 extends DataEngine implements DBDocuments {

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
