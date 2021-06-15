package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "MT_lines", indices = {
        @Index(name = "lDocNPart",      value = {"DocName", "PartIDTH"}),
        @Index(name = "lDocName",       value = {"DocName"}), // redundant
        @Index(name = "lDocNameAlc",    value = {"DocName", "AlcCode"}),
        @Index(name = "lDocNameBC",     value = {"DocName", "BC"}),
        @Index(name = "lDocNameGdsUnit",value = {"DocName", "GoodsID", "UnitBC"}),
        @Index(name = "lLine",          value = {"LineID"}, unique = true)
})
public class line {
    @PrimaryKey
    @ColumnInfo(name = "LineID")
    @NonNull
    public int LineID; // Уникальный счетчик

    @ColumnInfo(name = "DocName")
    @NonNull
    public String DocName; // Идентификатор-имя документа

    @ColumnInfo(name = "Pos")
    public int Pos; // Позиция в документе

    @ColumnInfo(name = "GoodsID")
    @NonNull
    public int GoodsID; // Код товара ТН

    @ColumnInfo(name = "GoodsName")
    public String GoodsName; // Имя товара ТН

    @ColumnInfo(name = "UnitBC")
    public String UnitBC; // Ед. изм. товара ТН

    @ColumnInfo(name = "BC")
    @NonNull
    public String BC; // Бар-код товара ТН

    @ColumnInfo(name = "Price")
    public double Price; // Цена по линии

    @ColumnInfo(name = "DocQnty")
    public double DocQnty; // Документарное кол-во

    @ColumnInfo(name = "FactQnty")
    public double FactQnty; // Фактическое кол-во

    @ColumnInfo(name = "AlcCode")
    public String AlcCode; // Дополнительный код

    @ColumnInfo(name = "PartIDTH")
    public String PartIDTH; // ИД партии

    @ColumnInfo(name = "Flags")
    public int Flags; // Битовый флаги означающие различные свойства документа в формате int
}