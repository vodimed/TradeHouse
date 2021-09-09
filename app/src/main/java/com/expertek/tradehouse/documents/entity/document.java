package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

/*TODO ROOM:
@Entity(tableName = "MT_documents", primaryKeys = {"DocName"}, indices = {
        @Index(name = "docNameDoc", value = {"DocName", "DocType"}, unique = true)
})
*/
public class document implements Serializable {
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
}
