package com.expertek.tradehouse.documents;

import androidx.annotation.NonNull;

import com.common.extensions.database.SQLiteDatabase;
import com.common.extensions.database.SQLiteMigration;
import com.expertek.tradehouse.documents.sqlite.Documents;
import com.expertek.tradehouse.documents.sqlite.Lines;
import com.expertek.tradehouse.documents.sqlite.Marklines;

public class Documents_v1Sqlite extends SQLiteDatabase implements DBDocuments {
    private final Documents documents;
    private final Lines lines;
    private final Marklines marklines;

    public Documents_v1Sqlite(@NonNull String path) {
        super(path, 1);
        documents = new Documents(this);
        lines = new Lines(this);
        marklines = new Marklines(this);
    }

    public Documents documents() {
        return documents;
    }
    public Lines lines() {
        return lines;
    }
    public Marklines marklines() {
        return marklines;
    }

    /**
     * Migrations through DataBase revisions
     */
    public static class M_0_1 extends SQLiteMigration {
        public M_0_1() {
            super(0, 1);
        }

        @Override
        public void migrate(@NonNull SQLiteDatabase database) {
            database.execSQL("CREATE TABLE MT_MarkLines (LineID INTEGER PRIMARY KEY UNIQUE NOT NULL, DocName TEXT NOT NULL, MarkCode TEXT (150) NOT NULL, PartIDTH TEXT NOT NULL, Sts TEXT (30), MarkParent TEXT, BoxQnty DOUBLE)");
            database.execSQL("CREATE TABLE MT_documents ( DocName TEXT NOT NULL UNIQUE, DocType TEXT, Complete BOOLEAN, Status TEXT, ClientID INTEGER, ClientType TEXT, ObjectID INTEGER, ObjectType TEXT, UserID TEXT, UserName TEXT, FactSum DOUBLE, StartDate DATETIME, Flags INTEGER, PRIMARY KEY ( DocName ) )");
            database.execSQL("CREATE TABLE MT_lines ( LineID INTEGER NOT NULL UNIQUE, DocName TEXT NOT NULL, Pos INTEGER, GoodsID INTEGER NOT NULL, GoodsName TEXT, UnitBC TEXT, BC TEXT NOT NULL, Price DOUBLE, DocQnty DOUBLE, FactQnty DOUBLE, AlcCode TEXT, PartIDTH TEXT, Flags INTEGER, PRIMARY KEY ( LineID ) )");

            database.execSQL("CREATE INDEX DocName ON MT_MarkLines (DocName ASC)");
            database.execSQL("CREATE UNIQUE INDEX DocNamePartId ON MT_MarkLines (DocName ASC, MarkCode ASC, PartIDTH ASC)");
            database.execSQL("CREATE UNIQUE INDEX Line ON MT_MarkLines (LineID ASC, DocName ASC)");
            database.execSQL("CREATE UNIQUE INDEX docNameDoc ON MT_documents ( DocName ASC, DocType ASC )");
            database.execSQL("CREATE INDEX lDocNPart ON MT_lines ( DocName ASC, PartIDTH ASC )");
            database.execSQL("CREATE INDEX lDocName ON MT_lines ( DocName ASC )");
            database.execSQL("CREATE INDEX lDocNameAlc ON MT_lines ( DocName ASC, AlcCode ASC )");
            database.execSQL("CREATE INDEX lDocNameBC ON MT_lines ( DocName ASC, BC ASC )");
            database.execSQL("CREATE INDEX lDocNameGdsUnit ON MT_lines ( DocName ASC, GoodsID ASC, UnitBC ASC )");
            database.execSQL("CREATE UNIQUE INDEX lLine ON MT_lines ( LineID ASC )");
            database.execSQL("CREATE INDEX markCode ON MT_MarkLines (MarkCode ASC)");
            database.execSQL("CREATE UNIQUE INDEX markline ON MT_MarkLines (LineID ASC, MarkCode ASC)");
            database.execSQL("CREATE INDEX mp ON MT_MarkLines (MarkParent)");
            database.execSQL("CREATE INDEX pi ON MT_MarkLines (DocName, MarkCode)");
        }
    }
}
