package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

/*TODO ROOM:
@Entity(tableName = "MT_MarkLines", indices = {
        @Index(name = "DocName", value = "DocName"), // redundant
        @Index(name = "markCode", value = "MarkCode"),
        @Index(name = "mp", value = "MarkParent"),
        @Index(name = "DocNamePartId", value = {"DocName", "MarkCode", "PartIDTH"}, unique = true),
        @Index(name = "line", value = {"LineID", "DocName"}, unique = true),
        @Index(name = "markline", value = {"LineID", "MarkCode"}, unique = true),
        @Index(name = "pi", value = {"DocName", "MarkCode"}) // redundant
})
*/
public class markline implements Serializable {
    //TODO ROOM: @PrimaryKey(autoGenerate = true)
    public @NonNull int LineID; // Уникальный счетчик
    public @NonNull String DocName = ""; // Идентификатор-имя документа
    public @NonNull String MarkCode = ""; // Маркировка
    public @NonNull String PartIDTH = ""; // ИД партии
    public String Sts; // Статус марки
    public String MarkParent; // Родительская марка
    public int BoxQnty; // Кол-во дочерних марок (кол-во в бл., упаков.)
}
