package com.expertek.tradehouse.documents;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.common.extensions.database.RoomSchema;
import com.expertek.tradehouse.documents.entity.Document;
import com.expertek.tradehouse.documents.entity.Line;
import com.expertek.tradehouse.documents.entity.Markline;

@TypeConverters({RoomSchema.DateConverter.class})
@Database(version = 1,
        entities = {Document.class, Line.class, Markline.class},
        autoMigrations = {
        //@AutoMigration(from = -0, to = 0, spec = Documents_v1Room.AutoMigration.class)
})
public abstract class Documents_v1Room extends RoomDatabase implements DBDocuments {
}
