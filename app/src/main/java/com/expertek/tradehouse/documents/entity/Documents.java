package com.expertek.tradehouse.documents.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "MT_documents")
public class Documents {
    @PrimaryKey
    @ColumnInfo(name = "DocName")
    @NonNull
    public String DocName; // Идентификатор-имя документа

    @ColumnInfo(name = "DocType")
    public String DocType; // Тип документа (приход, расход, возврат)

    @ColumnInfo(name = "Complete")
    public boolean Complete; // Означает загрузить документ в TH (0 – нет 1 – да)

    @ColumnInfo(name = "Status")
    public String Status; // статус ТН

    @ColumnInfo(name = "ClientID")
    public int ClientID; // Код контрагента (cli-code из таблицы спр. клиентов)

    @ColumnInfo(name = "ClientType")
    public String ClientType; // Тип контрагента (cli-type из таблицы спр. клиентов)

    @ColumnInfo(name = "ObjectID")
    public int ObjectID; // Код объекта (obj-code из таблицы спр. объектов)

    @ColumnInfo(name = "ObjectType")
    public String ObjectType; // Тип объекта (obj-type из таблицы спр. объектов)

    @ColumnInfo(name = "UserID")
    public String UserID; // Ид пользователя из TH

    @ColumnInfo(name = "UserName")
    public String UserName; // Имя пользователя в ТН

    @ColumnInfo(name = "FactSum")
    public double FactSum; // Сумма фактическая по документу

    @ColumnInfo(name = "StartDate")
    //@TypeConverters({DateTime.RoomConverter.class}) -- moved to database definition
    public Date StartDate; // Дата документа

    @ColumnInfo(name = "Flags")
    public int Flags; // Битовый флаги означающие различные свойства документа в формате int
}
