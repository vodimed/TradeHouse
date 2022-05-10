package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.extensions.database.Entity;
import com.common.extensions.database.Index;
import com.common.extensions.database.PrimaryKey;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "MT_MarkLines", indices = {
        @Index(name = "DocName", value = "DocName"), // redundant
        @Index(name = "markCode", value = "MarkCode"),
        @Index(name = "mp", value = "MarkParent"),
        @Index(name = "DocNamePartId", value = {"DocName", "MarkCode", "PartIDTH"}, unique = true),
        @Index(name = "line", value = {"LineID", "DocName"}, unique = true),
        @Index(name = "markline", value = {"LineID", "MarkCode"}, unique = true),
        @Index(name = "pi", value = {"DocName", "MarkCode"}) // redundant
})
public class Markline implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public @NonNull int LineID; // Уникальный счетчик
    public @NonNull String DocName = ""; // Идентификатор-имя документа
    public @NonNull String MarkCode = ""; // Маркировка
    public @NonNull String PartIDTH = ""; // ИД партии
    public String Sts; // Статус марки
    public String MarkParent; // Родительская марка
    public int BoxQnty; // Кол-во дочерних марок (кол-во в бл., упаков.)

    public static final String UNGROUPED = "Ungrouped";
    public static final String NOT_CORRECT = "NotCorrect";
    public static final String GRAY_ZONE = "GrayZone";
    public static final String UTD = "UTD";
    public static final String TSD = "TSD";
    public static final String CR_TSD = "CrTSD";
    public static final String CHECK = "Check";
    public static final String CHECK_SCAN = "CheckScan";
    private static final List<String> scannedstat = Arrays.asList(TSD, CR_TSD, CHECK, CHECK_SCAN);

    @Override
    public boolean equals(@Nullable Object obj) {
        assert (obj instanceof Markline);
        return (LineID == ((Markline) obj).LineID);
    }

    public boolean isScanned() {
        return scannedstat.contains(Sts);
    }

    public boolean isParent() {
        return (BoxQnty > 1);
    }
}
