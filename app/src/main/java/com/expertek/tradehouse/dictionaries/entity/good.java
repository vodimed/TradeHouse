package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TH_goods")
public class good implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "GoodsID")
    @NonNull
    public int GoodsID; // идентификатор товара из TH

    @ColumnInfo(name = "Name")
    public String Name; // название

    @ColumnInfo(name = "UnitBase")
    public String UnitBase; // основная ед. изм.

    @ColumnInfo(name = "PriceBase")
    public double PriceBase; // не используется

    @ColumnInfo(name = "VAT")
    public double VAT; // не используется

    @ColumnInfo(name = "Country")
    public String Country; // страна

    @ColumnInfo(name = "Struct")
    public String Struct; // не используется

    @ColumnInfo(name = "FactQnty")
    public double FactQnty; // остатки фактическое кол-во

    @ColumnInfo(name = "FreeQnty")
    public double FreeQnty; // остатки свободное кол-во
}
