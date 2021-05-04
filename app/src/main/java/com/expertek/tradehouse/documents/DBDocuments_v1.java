package com.expertek.tradehouse.documents;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.common.extensions.database.DataEngine;
import com.common.extensions.database.TypeConv;
import com.expertek.tradehouse.documents.entity.Documents;
import com.expertek.tradehouse.documents.entity.Lines;
import com.expertek.tradehouse.documents.entity.Marklines;

@TypeConverters({TypeConv.class})
@Database(version = 1,
        entities = {Documents.class, Lines.class, Marklines.class},
        autoMigrations = {
        //@AutoMigration(from = -0, to = 0, spec = DBDocuments_v1.AutoMigration.class)
})
public abstract class DBDocuments_v1 extends DataEngine implements DBDocuments {

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
