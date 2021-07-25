package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "MT_MarkLines", indices = {
        @Index(name = "DocName", value = "DocName"), // redundant
        @Index(name = "markCode", value = "MarkCode"),
        @Index(name = "mp", value = "MarkParent"),
        @Index(name = "DocNamePartId", value = {"DocName", "MarkCode", "PartIDTH"}, unique = true),
        @Index(name = "line", value = {"LineID", "DocName"}, unique = true),
        @Index(name = "markline", value = {"LineID", "MarkCode"}, unique = true),
        @Index(name = "pi", value = {"DocName", "MarkCode"}) // redundant
})
public class markline implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LineID")
    @NonNull
    public int LineID; // Уникальный счетчик

    @ColumnInfo(name = "DocName")
    @NonNull
    public String DocName = ""; // Идентификатор-имя документа

    @ColumnInfo(name = "MarkCode")
    @NonNull
    public String MarkCode = ""; // Маркировка

    @ColumnInfo(name = "PartIDTH")
    @NonNull
    public String PartIDTH = ""; // ИД партии

    @ColumnInfo(name = "Sts")
    public String Sts; // Статус марки

    @ColumnInfo(name = "MarkParent")
    public String MarkParent; // Родительская марка

    @ColumnInfo(name = "BoxQnty")
    public int BoxQnty; // Кол-во дочерних марок (кол-во в бл., упаков.)
}
