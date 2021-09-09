package com.expertek.tradehouse.documents;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.common.extensions.database.DateRoomConverter;
import com.expertek.tradehouse.documents.entity.document;
import com.expertek.tradehouse.documents.entity.line;
import com.expertek.tradehouse.documents.entity.markline;

@TypeConverters({DateRoomConverter.class})
@Database(version = 2,
        entities = {document.class, line.class, markline.class},
        autoMigrations = {
        //@AutoMigration(from = -0, to = 0, spec = Documents_v1Room.AutoMigration.class)
})
public abstract class Documents_v1Room extends RoomDatabase implements DBDocuments {
}
