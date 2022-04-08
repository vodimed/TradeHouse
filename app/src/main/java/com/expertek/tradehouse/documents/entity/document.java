package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.common.extensions.database.Entity;
import com.common.extensions.database.Index;
import com.common.extensions.database.PrimaryKey;
import com.expertek.tradehouse.Application;
import com.expertek.tradehouse.R;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "MT_documents", indices = {
        @Index(name = "docNameDoc", value = {"DocName", "DocType"}, unique = true)})
public class document implements Serializable {
    @PrimaryKey
    public @NonNull String DocName = ""; // Идентификатор-имя документа
    public String DocType; // Тип документа (приход, расход, возврат)
    public boolean Complete; // Означает загрузить документ в TH (0 – нет 1 – да)
    public String Status; // статус ТН
    public int ClientID; // Код контрагента (cli-code из таблицы спр. клиентов)
    public String ClientType; // Тип контрагента (cli-type из таблицы спр. клиентов)
    public int ObjectID; // Код объекта (obj-code из таблицы спр. объектов)
    //public String ObjectType; // Тип объекта (obj-type из таблицы спр. объектов)
    public String UserID; // Ид пользователя из TH
    public String UserName; // Имя пользователя в ТН
    public double FactSum; // Сумма фактическая по документу
    //@TypeConverters({DateTime.RoomConverter.class}) -- moved to database definition
    public Date StartDate; // Дата документа
    public int Flags; // Битовый флаги означающие различные свойства документа в формате int

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof document)) return super.equals(obj);
        return DocName.equals(((document) obj).DocName);
    }

    @Override
    @NonNull
    public String toString() {
        String documentType = DocType;
        for (String doctype : Application.app().getResources().getStringArray(R.array.document_types)) {
            if (doctype.startsWith(documentType)) documentType = doctype.split("\\|")[1];
        }
        return "Документ: " + DocName + ", " + documentType;
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
}
