package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

/*TODO ROOM:
@Entity(tableName = "MT_lines", indices = {
        @Index(name = "lDocNPart", value = {"DocName", "PartIDTH"}),
        @Index(name = "lDocName", value = {"DocName"}), // redundant
        @Index(name = "lDocNameAlc", value = {"DocName", "AlcCode"}),
        @Index(name = "lDocNameBC", value = {"DocName", "BC"}),
        @Index(name = "lDocNameGdsUnit", value = {"DocName", "GoodsID", "UnitBC"}),
        @Index(name = "lLine", value = {"LineID"}, unique = true)
})
*/
public class line implements Serializable {
    //TODO ROOM: @PrimaryKey(autoGenerate = true)
    public @NonNull int LineID; // Уникальный счетчик
    public @NonNull String DocName = ""; // Идентификатор-имя документа
    public int Pos; // Позиция в документе
    public @NonNull int GoodsID; // Код товара ТН
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
}
