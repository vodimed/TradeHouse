package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.extensions.database.Entity;
import com.common.extensions.database.Index;
import com.common.extensions.database.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "MT_lines", indices = {
        @Index(name = "lDocNPart", value = {"DocName", "PartIDTH"}),
        @Index(name = "lDocName", value = {"DocName"}), // redundant
        @Index(name = "lDocNameAlc", value = {"DocName", "AlcCode"}),
        @Index(name = "lDocNameBC", value = {"DocName", "BC"}),
        @Index(name = "lDocNameGdsUnit", value = {"DocName", "GoodsID", "UnitBC"}),
        @Index(name = "lLine", value = {"LineID"}, unique = true)})
public class Line implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public @NonNull int LineID; // Уникальный счетчик
    public @NonNull String DocName = ""; // Идентификатор-имя документа
    public int Pos; // Позиция в документе
    public @NonNull int GoodsID = 0; // Код товара ТН
    public String GoodsName; // Имя товара ТН
    public String UnitBC; // Ед. изм. товара ТН
    public @NonNull String BC = ""; // Бар-код товара ТН
    public double Price; // Цена по линии
    public double DocQnty; // Документарное кол-во
    public double FactQnty; // Фактическое кол-во
    public String AlcCode; // Дополнительный код
    public String PartIDTH; // ИД партии
    public int Flags; // Битовый флаги означающие различные свойства документа в формате int

    @Override
    @NonNull
    public String toString() {
        return "Позиция: " + GoodsName + ", " + FactQnty + " " + UnitBC;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        assert (obj instanceof Line);
        return (LineID == ((Line) obj).LineID);
    }
}
