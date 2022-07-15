package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.extensions.database.Entity;
import com.common.extensions.database.Index;
import com.common.extensions.database.PrimaryKey;
import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.R;
import com.expertek.tradehouse.components.MainSettings;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity(tableName = "MT_documents", indices = {
        @Index(name = "docNameDoc", value = {"DocName", "DocType"}, unique = true)})
public class Document implements Serializable {
    @PrimaryKey
    public @NonNull String DocName = ""; // Идентификатор-имя документа
    public String DocType; // Тип документа (приход, расход, возврат)
    public boolean Complete; // Означает загрузить документ в TH (0 – нет 1 – да)
    public String Status; // статус ТН
    public int ClientID; // Код контрагента (cli-code из таблицы спр. клиентов)
    public String ClientType; // Тип контрагента (cli-type из таблицы спр. клиентов)
    public int ObjectID; // Код объекта (obj-code из таблицы спр. объектов)
    public String ObjectType; // Тип объекта (obj-type из таблицы спр. объектов)
    public String UserID; // Ид пользователя из TH
    public String UserName; // Имя пользователя в ТН
    public double FactSum; // Сумма фактическая по документу
    //@TypeConverters({DateTime.RoomConverter.class}) -- moved to database definition
    public Date StartDate; // Дата документа
    public int Flags; // Битовый флаги означающие различные свойства документа в формате int

    public static final String INV = "Inv";
    public static final String INV_MARKS = "InvMarks";
    public static final String INV_INTRODUCE = "InvIntroduce";
    public static final String INTRODUCE = "Introduce";
    public static final String UTD = "UTD";
    private static final List<String> readonlytype = Arrays.asList(UTD, INV_MARKS, INV_INTRODUCE);
    private static final List<String> readonlystat = Arrays.asList("разрешен+", "накл+");
    private static final List<String> thousestat = Arrays.asList("НАКЛ+", "РАЗР+", "накл+", "накл-", "разр+");

    @Override
    @NonNull
    public String toString() {
        String documentType = DocType;
        for (String doctype : Application.app().getResources().getStringArray(R.array.invoice_types)) {
            if (doctype.startsWith(documentType)) documentType = doctype.split("\\|")[1];
        }
        return "Документ: " + DocName + ", " + documentType;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        assert (obj instanceof Document);
        return DocName.equals(((Document) obj).DocName);
    }

    public String getNextId(String lastId) {
        final char[] nextId = lastId.toCharArray();
        for (int i = nextId.length - 1; i >= 0; i--) {
            if (nextId[i] >= '0' && nextId[i] <= '9') {
                if (++nextId[i] <= '9') return new String(nextId);
                nextId[i] = '0';
            }
        }
        return "1" + (new String(nextId));
    }

    public boolean isComplete() {
        return Complete;
    }

    public boolean isMarked() {
        return MainSettings.CheckMarks && ((Flags & 0x1) == 0x1);
    }

    public boolean isInventory() {
        return DocType.startsWith("Inv");
    }

    public boolean isInvoice() {
        return DocType.endsWith("WB");
    }

    public boolean isReadonly() {
        return readonlytype.contains(DocType) || readonlystat.contains(Status);
    }

    public boolean isTreadHouse() {
        return DocType.equals(INV) || thousestat.contains(Status);
    }
}
