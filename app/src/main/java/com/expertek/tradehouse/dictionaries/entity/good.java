package com.expertek.tradehouse.dictionaries.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

//TODO ROOM: @Entity(tableName = "TH_goods")
public class good implements Serializable {
    //TODO ROOM: @PrimaryKey
    public @NonNull int GoodsID; // идентификатор товара из TH
    public String Name; // название
    public String UnitBase; // основная ед. изм.
    public double PriceBase; // не используется
    public double VAT; // не используется
    public String Country; // страна
    public String Struct; // не используется
    public double FactQnty; // остатки фактическое кол-во
    public double FreeQnty; // остатки свободное кол-во
}
