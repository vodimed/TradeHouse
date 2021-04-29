package com.expertek.tradehouse.documents;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.expertek.tradehouse.database.DateTime;
import com.expertek.tradehouse.documents.entity.Documents;
import com.expertek.tradehouse.documents.entity.Lines;
import com.expertek.tradehouse.documents.entity.Marklines;

@Database(entities = {Documents.class, Lines.class, Marklines.class}, version = 1, exportSchema = false)
@TypeConverters({DateTime.RoomConverter.class})
public abstract class DBDocuments_v1 extends RoomDatabase implements DBDocuments {
}
