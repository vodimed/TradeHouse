package com.expertek.tradehouse.dictionaries.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TH_goods")
public class Goods {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "GoodsID")
    public int GoodsID; // идентификатор товара из TH

    @ColumnInfo(name = "Name")
    public String Name; // название

    @ColumnInfo(name = "UnitBase")
    public int UnitBase; // основная ед. изм.

    @ColumnInfo(name = "PriceBase")
    public float PriceBase; // не используется

    @ColumnInfo(name = "VAT")
    public int VAT; // не используется

    @ColumnInfo(name = "Country")
    public String Country; // страна

    @ColumnInfo(name = "Struct")
    public int Struct; // не используется

    @ColumnInfo(name = "FactQnty")
    public int FactQnty; // остатки фактическое кол-во

    @ColumnInfo(name = "FreeQnty")
    public int FreeQnty; // остатки свободное кол-во
}
