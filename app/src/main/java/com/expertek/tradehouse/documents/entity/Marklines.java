package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MT_MarkLines")
public class Marklines {
    @PrimaryKey
    @ColumnInfo(name = "LineID")
    @NonNull
    public int LineID; // Уникальный счетчик

    @ColumnInfo(name = "DocName")
    @NonNull
    public String DocName; // Идентификатор-имя документа

    @ColumnInfo(name = "MarkCode")
    @NonNull
    public String MarkCode; // Маркировка

    @ColumnInfo(name = "PartIDTH")
    @NonNull
    public String PartIDTH; // ИД партии

    @ColumnInfo(name = "Sts")
    public String Sts; // Статус марки

    @ColumnInfo(name = "MarkParent")
    public String MarkParent; // Родительская марка

    @ColumnInfo(name = "BoxQnty")
    public int BoxQnty; // Кол-во дочерних марок (кол-во в бл., упаков.)
}
